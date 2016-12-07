package oversampling;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

class KNN
{

	private static MathUtil MU = new MathUtil();	
	private static Map<Long, String> mapCoI = new HashMap <Long, String>(); 
	private static BufferStore store = new BufferStore();
	public Map<Integer, String> FT = new HashMap<Integer, String>();
	private int K; // value set when class instance is invoked
	private boolean GuassianDist;
	private static int windowSize = 3000;

	////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {

	}
	
	////////////////////////////////////////////////////////////////////
	KNN (int k, boolean useGuassian) {
		setK (k);
		setGuassian(useGuassian);
	}

	////////////////////////////////////////////////////////////////////
	
	public Integer Run(Map<Long, String> map) {
		// create temporary folder to hold hash map temporarily
		File tmpFolder = store.createTempDirectory();
		store.setTempFolder(tmpFolder.getAbsolutePath() + SC.FSlash);
	
		setmapCoI(map);
		
		System.out.print("Feature Identification started");
		FT = IdentifyFeatureType();
				 
		System.out.print(SC.NL2 + "Nearest Matrix started");	
		formulateNearestMatrix();
			
		System.out.print(SC.NL2 + "Matrix -> Ordered Vector started" + SC.NL);
		formulateOrderedVector();
					
		System.out.print(SC.NL2 + "Developing Vector with (MinMax...) started"); 
		int totalVectors = PopulateVectorWithMinMax();			
		
		tmpFolder.delete(); // delete temporary folder... no more required
		return totalVectors;
	}
	
	
	////////////////////////////////////////////////////////////////////
	private void formulateOrderedVector() {		
		List <Long> rec = new ArrayList<Long>(mapCoI.keySet());		
		int i = 0, j=0;
		int sz = rec.size()-1;
		String ky = "";	
		
		////////////////////////////////////		
		int  winSz = (sz+1 < getWindowSize()) ? sz+1 : getWindowSize();
		
		List <Map<String, Double>> window = new ArrayList<Map<String, Double>>();
		for (i=0; i< winSz; i++) {
			 window.add(store.DeserializeMx(i +  ".matrix"));
			 // System.out.println("populating window. " + i);
		 }		
		///////////////////////////////////
		
		Map<String, Double> mapOutward = new HashMap<String, Double>();
		Map<String, Double> mapInward = new HashMap<String, Double>();
		
		for (i=sz; i>=0; i--) {
			
		//	System.out.print(SC.NL + "processing: " + i +  "/" + winSz);
			mapOutward.clear();
			if (i < winSz) {			
				mapOutward.putAll(window.get(i));
				window.get(i).clear();		window.remove(i);
				
			} else {
				mapOutward = store.DeserializeMx(i +  ".matrix");	
			}
			
			List <String> k = new ArrayList<String>(mapOutward.keySet());
			List <Double> v = new ArrayList<Double>(mapOutward.values());
			ky = rec.get(i).toString();									
		    				
			
			for (j=i-1;  j >= 0; j--) {
			
				if (j < winSz) {
					mapInward = window.get(j);
				} else {
					mapInward = store.DeserializeMx(j +  ".matrix");						
				}
				k.add(rec.get(j).toString());
				v.add((Double) mapInward.get(ky) );				
			}
			//System.out.println(rec.get(i) + " : " +  k.size());
			
			SC.displayFeedback(i, SC.DOT, SC.DOT);
			
			store.SerializeOv(("v." + i), bubbleSort(k, v, ky));
		}
		
	}
	
	
	////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////
	private Map<String, String> bubbleSort(List<String>  arrRec, List<Double> arrDist, String ky) {
		int i = 0, j=0;
		int sz = arrRec.size();
		
		double swapD = 0.0;
		String swapS = "";
		for (i = 0; i < (sz-1 ); i++) {
		      for (j = i+1; j < sz; j++) {
		        if  (  (Double) arrDist.get(i) > (Double) arrDist.get(j)) // For descending order use < */
		        {
		        	  swapD = (Double) arrDist.get(i);
			          arrDist.set(i, (Double) arrDist.get(j)); ;
			          arrDist.set(j, swapD);
			        
			          swapS = (String) arrRec.get(i);
			          arrRec.set(i , (String) arrRec.get(j));
			          arrRec.set(j, swapS);
		        }
		      }
		}
		
		////////////////////////////////
		int limit = sz < getK() ? sz : getK() ;		
		String vl ="";
		for (i = 0; i < limit; i++) {
			ky += SC.COLON + arrRec.get(i).toString() ;
			vl += SC.COLON + arrDist.get(i).toString() ;
		}
		vl = vl.substring(1);
		
		Map<String, String> ret = new HashMap<String, String>();
		ret.put(ky, vl);
		
		return ret;
		
	}
	
	////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////
	
	private void formulateNearestMatrix() {
		
		Map<String, Double> F = new HashMap<String, Double>();
		
		List <Long> rec = new ArrayList<Long>(mapCoI.keySet());
		String t = "";
		List <String> list = new ArrayList<String>(mapCoI.values());
		
	//	System.out.println(mapCoI.toString() + SC.NL2 );		
	//	System.out.println(list.toString());
	//	System.out.println(rec.toString());
		
		// putting a dot for processing each record
		int total = list.size();  
		int i=0, j=0; 
		double dist = 0.0;
		for (i=0; i<total; i++) {
			List <Double> outward = Vectorize(list.get(i));
		//	System.out.print(SC.NL + "processing: " + (i +1) +  "/" + total + " ");
		//	t = rec.get(i).toString();
		//	F.put(t,  0.0);
			for (j=i+1; j<list.size(); j++) {
				List <Double> inward = Vectorize(list.get(j));
				dist = FindDistance(outward, inward, 2.0); // 2.0 --> Euclidean Distance
				inward.clear();
				t = rec.get(j).toString();
				F.put(t,  dist);
			}		
			
			SC.displayFeedback(i,SC.PIPE,SC.Hyphen);
			store.SerializeMx( (i + ".matrix"), F);			
			F.clear();
		}				
	}
	
	///////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////
	private List <Double> Vectorize(String inp) {
		// System.out.println("inp.size : " + inp.length());
			
		List <Double> ret = new ArrayList<Double>();
		String[] arr = inp.split(SC.DLMT);
		for (int i=0; i<arr.length; i++) {
			if (FT.get(i) == "Numeric")
				ret.add(Double.parseDouble(arr[i]));
		}		
		// System.out.println(ret.toString());
		return ret;
	}
	
	////////////////////////////////////////////////////////////////////
	/*
	 * Chebyshev distance
	 * p=2 --> Euclidean (special case of Chebyshev distance)
	 */
	private double FindDistance(List <Double> a, List <Double> b, double p) {
		double sum = 0.0;
		int sz = a.size();
		
		for (int i=0; i<sz; i++) {
			sum += Math.pow((a.get(i) - b.get(i)), p);
		}
		
		sum = Math.pow(sum, (1/p));
		return (sum);
	}
	
	////////////////////////////////////////////////////////////////////

	 /*
	  * Input: Map of CoI (record number and record itself
	  * Output: Map <ColNumber, ColDataType{ Numeric, Text} )
	  */
	 private Map<Integer, String> IdentifyFeatureType() {
		 Map<Integer, String> map = new HashMap<Integer, String>();
		 Iterator <Entry <Long, String>> it = mapCoI.entrySet().iterator();
		// System.out.println(mapCoI.toString() + SC.NL);
		 Entry <Long, String> pair = it.next();
		 String[] arr = pair.getValue().split(SC.DLMT);
		 
		 for (int i = 0; i<arr.length; i++ ) { 
			 if (MU.isNumeric(arr[i])) {
				 map.put(i, "Numeric");
			 } else {
				 map.put(i, "Text");
			 }
			 
			 SC.displayFeedback(i,"*","*");
			 
		 }		 
		 return map;
	 }

	////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private  Map<Long, String> getmapCoI() {
			return mapCoI;
	}

	public void setmapCoI( Map<Long, String> map) {
			KNN.mapCoI = map;
	}

	////////////////////////////////////////////////////////////////////
	public Integer getK () {
		return this.K;
	}
	
	public void setK(int k) {
		this.K = k;
	}
	////////////////////////////////////////////////////////////////////
	public boolean getGuassian () {
		return this.GuassianDist;
	}
	
	public void setGuassian(boolean  g) {
		this.GuassianDist = g;
	}
	////////////////////////////////////////////////////////////////////

	
	////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////
	private Integer PopulateVectorWithMinMax() {
	
		int i=0, j=0; 
		int sz = mapCoI.keySet().size();
		Map<String, String> map = new HashMap<String,String>();
		for (i=0; i<sz; i++) {
			//Map<String, String> map = oVector.get(i);
			map = store.DeserializeOv("v." + i);
			String S = map.keySet().toString();
			S = S.replace(SC.MiddleBracketS, SC.ZeroSpace);
			S = S.replace(SC.MiddleBracketE, SC.ZeroSpace);
			String[] Rec = S.split(SC.COLON); 
			//System.out.println(S);
			
			
			// find the distance vector in order 
			// length of vector is either K or CoI record count (whichever is less)
			int ul = (K >= Rec.length ? (Rec.length) : K ); 
			int ul2 = -1 + mapCoI.get(Long.parseLong(Rec[0])).split(SC.DLMT).length;
			
			//System.out.println(mapCoI.get(Long.parseLong(Rec[0])).toString());
			
			String[][] R = new String[ul+1][ul2+1];
			
			//System.out.println((ul+1) + " X " + (ul2+1));
			
			
			for (j=0; j<ul; j++) {
				S = mapCoI.get(Long.parseLong( Rec[j]));
				String[] arr = S.split(SC.DLMT);
				ul2 = arr.length-1;
				for (int x=0; x<ul2; x++) {
					R[j][x] = arr[x];
				}
			}
			
			//////////////////////
			R = traspose2DArray(R);
			//////////////////////
			S="";
			Map<Long, String> MinMaxDisc = new HashMap<Long,String> ();
			for (j=0; j<ul2; j++) {
				if (FT.get(j) == "Numeric") {
					S += SC.DLMT + FindNumericParam(R[j]);
				} else {
					S += SC.DLMT + FindDiscreteSet(R[j]);
				}
			}
			/////////////////////
			S = S.substring(1);
			MinMaxDisc.put(Long.parseLong(Rec[0]), S);
		
			SC.displayFeedback(i,SC.FSlash,SC.BSlash);
			store.SerializeVectorWithMinMax(("vMM" + i), MinMaxDisc);
		}
		
		// System.out.println(oV.toString());
		return sz;
	}
	
	
	/////////////////////////////////////////////////////
	private String FindNumericParam(String[] inp) {
		String ret = new String();
		if (getGuassian())
			ret = FindGuassianParam(inp);
		else
			ret = FindMinMax(inp);
		
		return ret;
	}
	
	
	/////////////////////////////////////////////////////
		
	private String FindGuassianParam(String[] inp) {
		String ret = new String();
		int i, sz = -1 + inp.length;
		
		Double sum =0.0; 
		for (i=0; i<sz; i++) {
			sum += Double.parseDouble(inp[i]);
		}
		Double mean = sum / sz; 
		
		double variance = 0.0, sd = 0.0; 
		double cur = 0.0;
		for (i=0; i<sz; i++) {
			cur = Double.parseDouble(inp[i]);
			cur -= mean;
			cur *= cur;
			variance += cur;
		}
		variance /= sz;
		sd = Math.sqrt(variance);
				
		mean = MU.RoundUp(mean, 2);
		sd = MU.RoundUp(sd, 2);
//		System.out.print("mean: " + mean + SC.TB + " sd: " + sd + SC.NL);
		ret = String.valueOf(mean) + SC.COLON + String.valueOf(sd);		
		return ret;
	}

	/////////////////////////////////////////////////////
			
	private String FindMinMax(String[] inp) {
		String ret = new String();
		int i, sz = -1 + inp.length;
		
		Double Min = Double.MAX_VALUE; 
		Double Max = -Double.MAX_VALUE; 
		double cur = 0.0;
		for (i=0; i<sz; i++) {
			cur = Double.parseDouble(inp[i]);
			Min = (cur <= Min) ? cur : Min;
		    Max = (cur >= Max) ? cur : Max;
		    //System.out.println(" cur: " + cur + "  mx:" + Max);
		}
		// System.out.print(Min + ":::" + Max + SC.NL);
		ret = String.valueOf(Min) + SC.COLON + String.valueOf(Max);		
		return ret;
	}
	
	///////////////////////////////////////////////////////////
	private String FindDiscreteSet(String[] inp) {
		String ret = new String();
		int i, sz = -1 + inp.length;
		
		for (i=0; i<sz; i++) {
		//	System.out.print(inp[i] + "|");
			ret = SC.PIPE + (inp[i]);		
		}
		ret = ret.substring(1); // remove first delimiter
		// System.out.print(SC.NL);
		return ret;
	}

	
	/////////////////////////////////////////////////////
	public static String[][] traspose2DArray(String[][] matrix)
	{
	    int m = matrix.length;
	    int n = matrix[0].length;

	    String[][] trasposedMatrix = new String[n][m];

	    for(int x = 0; x < n; x++)
	    {
	        for(int y = 0; y < m; y++)
	        {
	            trasposedMatrix[x][y] = matrix[y][x];
	        }
	    }

	    return trasposedMatrix;
	}
	/////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////
	public void setTempStoreObject(BufferStore val) {
		store = val;
	}
	

	public BufferStore getTempStoreObject() {
		return store;
	} 		
	
	//////////////////////////////////////////////////////////////////////
	
	public void setWindowSize(int val) {
		windowSize = val;
	}

	public int getWindowSize() {
		return windowSize;
	}

	
}
