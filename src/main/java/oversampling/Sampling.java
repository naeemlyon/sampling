package oversampling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Sampling {
	private static MathUtil MU = new MathUtil();
	private static KNN knn = null;
	private static Map<Long, String> mapCoI = new HashMap <Long, String>();
	private static ArrayList<String> List = new ArrayList<String>();
	private static String FileToSample = "/home/mnaeem/workspace/data/sampleFolder/" + "dermatology";
	
	private static String OverSampleFile = "/home/mnaeem/workspace/data/sampleFolder/"+ "oversampled.csv";
	private static String basicInfo = "";

	private static Integer totalVectors = 0;
	private static boolean copyHeaderInOverSample = true;

	private static BufferStore store = new BufferStore();
	private static int windowSize = 200;
	
	/*
	 * Class of Interest
	 * if zero length, system will identify class
	 * with lowest frequency as CoI
	 */
    private static String CoI = "";
	
    /*
	 * factor 0.0 -> 50% CoI & 50% non.CoI approximately
	 * factor 1.0 -> 100% increase in CoI records
	 * factor 1.5 -> 150% increase in CoI records
	 * factor 0.75 -> 750% increase in CoI records
	 */	
    private static Double factor = 0.0;
    
    private static int K = 5; //Integer.MAX_VALUE;  
	
    /*
     * false -> Min...randomNum .... Max
     * true  -> mean...Gaussian Sample...sd
     */
    private static boolean useGaussianDist = true;
    ///////////////////////////////////////////////////////////////////////////
	public static String getFileToSample() {
		return FileToSample;
	}
	public void setFileToSample(String val) {
		FileToSample = val;
	}

    ///////////////////////////////////////////////////////////////////////////
	public static String getOverSampleFile() {
		return OverSampleFile;
	}
	public void setOverSampleFile(String val) {
		OverSampleFile = val;
	}

    ///////////////////////////////////////////////////////////////////////////
	public static String getCoI() {
		return CoI;
	}
	public void setCoI(String val) {
		CoI = val;
	}
	
	///////////////////////////////////////////////////////////////////////////
	public static boolean getcopyHeaderInOverSample() {
		return copyHeaderInOverSample;
	}
	public void setcopyHeaderInOverSample(boolean val) {
		copyHeaderInOverSample = val;
	}

	///////////////////////////////////////////////////////////////////////////
	public static boolean getuseGaussianDist() {
		return useGaussianDist;
	}
	public void setuseGaussianDist(boolean val) {
		useGaussianDist = val;
	}

	///////////////////////////////////////////////////////////////////////////
	public static int getK() {
		return K;
	}
	public void setK(int val) {		
		if (val < 1 )
			// lead to set value into count of CoI
			K = Integer.MAX_VALUE; 
		else
			K = val;
	}

	///////////////////////////////////////////////////////////////////////////
	public static double getIncreaseFactor() {
		return factor;
	}
	public void setIncreaseFactor(double val) {
		factor = val;
	}

	///////////////////////////////////////////////////////////////////////////
		
	public void setWindowSize(int val) {
		windowSize = val;
	}	
	public int getWindowSize() {
		return windowSize;
	}

	//////////////////////////////////////////////////////////////////////
	
	public String[] ListParameters() {
		   String[] val = new String[8];
		   val[0] = "FileToSample = " + getFileToSample();
		   val[1] = "OverSampleFile = " + getOverSampleFile();
		   val[2] = "HeaderInOverSample = " + String.valueOf(getcopyHeaderInOverSample());
		   val[3] = "useGaussianDist = " + String.valueOf(getuseGaussianDist());
		   val[4] = "K = " + String.valueOf(getK());
		   val[5] = "IncreaseFactor = " + String.valueOf(getIncreaseFactor());
		   val[6] = "Class of Interest = " + String.valueOf(getCoI());
		   val[7] = "WindowSize = " + String.valueOf(getWindowSize());
		   
	       return val;
	   }
	
	///////////////////////////////////////////////////////////////////////////
	

	public static void main(String[] args) {
		Run();			
	 } 
	
	 //////////////////////////////////////////////////////////////////////////
	 public static String[] Run() {
		 String ret = Exec();
		 return ret.split(SC.NL);
	 }
	 //////////////////////////////////////////////////////////////////////////
	 public static String Exec() {
		 String ret = "";
		 CoI = FindCoI(FileToSample, CoI);
		 Long Rec = SeparateCoI(FileToSample);
		 /*
		  * K > Counts(CoI) --> K=Counts(CoI)
		  * K < Counts(CoI) --> K=K
		 */
		 K = (K > mapCoI.size() ) ? mapCoI.size() : K; 
		 
		 if (K < 3 ) {
			 ret = ("There are only " + K + " instance of Class of Interest.." 
					 + SC.NL + "these might be noise/outliar" + SC.NL +  " No processing performed");
			 return ret;
		 }
		 
		 
		 knn = new KNN(K, useGaussianDist); 
		 knn.setTempStoreObject(store);
		 knn.setWindowSize(windowSize);		 
		  
		 totalVectors = knn.Run(mapCoI);
		 factor = SetPriorRatio(Rec);
		 Map<Integer, String> FT = knn.FT;
		 knn = null;
		 GenerateSynthethicData(FT);
		 ret = DisplayResult(Rec);
		 return ret;
	 }
	  
	///////////////////////////////////////////////////////////////////////////
	 /*
	  * Separate records of CoI (Class of Interest) from the main file
	  * Populates two structures
	  * map: holds the position of records of CoI from main file
	  * ArrayList: holds the actual records related to only CoI
	  * input: path of the main file
	  * 
	  */
	 private static Long SeparateCoI(String fPath) {
		String  Line = null;
		String[] arr = null;
		Long rec = 0L; // total records
		int CI = -1; // Class Index, -1=Last Column
		Reader filePath;
		File inputFile = new File(fPath);
		
		try {
			filePath = new FileReader((inputFile));
		      try {
		         // open input stream file for reading purpose.
		         BufferedReader br = new BufferedReader(filePath);
		         Line = br.readLine(); // header 
		         
		         arr = Line.split(SC.DLMT);
		         if (CI == -1)
		        	 CI = arr.length-1;
		         
		         while ((Line = br.readLine()) != null) {
		        	Line = Line.trim();
		        	arr = Line.split(SC.DLMT);
		        	if (arr[CI].trim().contentEquals(CoI)==true) {
		        		mapCoI.put(rec, Line);
		        		List.add(Line);
		        	}
		        	rec++;
		         }
		           
		         br.close();
		      }catch(Exception e){
		         e.printStackTrace();
		     }		
		} 
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}		
		return rec;
	 }	 
	 	 
	///////////////////////////////////////////////////////////////////////////
	 /*
	  * If Class of Interest is already given, do nothing, just return CoI
	  * else
	  * find out the CoI (class of interest = minimum frequent class
	  * map: holds the counts of every class
	  * input: path of the main file
	  * input: CoI
	  */
	 private static String FindCoI(String fPath, String coi) {
	
		if (coi.trim().length() > 0)
			return coi;
		 
		String  Line = null;
		String[] arr = null;
		int CI = -1; // Class Index, -1=Last Column
		Reader filePath;
		File inputFile = new File(fPath);
		Map<String, Long> map = new HashMap<String, Long>();
		try {
			filePath = new FileReader((inputFile));
		      try {
		         // open input stream test.txt for reading purpose.
		         BufferedReader br = new BufferedReader(filePath);
		         Line = br.readLine(); // header 
		         arr = Line.split(SC.DLMT);
		         if (CI == -1)
		        	 CI = arr.length-1;
		         
		         while ((Line = br.readLine()) != null) {
		        	Line = Line.trim();
		        	arr = Line.split(SC.DLMT);
		        	String key = arr[CI].trim();
		        	map.put(key, map.containsKey(key) ? (map.get(key) + 1) :  1L);
		         }
		         br.close();
		      } catch(Exception e){
		         e.printStackTrace();
		     }		
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		///////////////////////////////
		Long current = 1L, mn = Long.MAX_VALUE;
		String ret = "";
		Iterator<Map.Entry<String,Long>> it = map.entrySet().iterator();
		while (it.hasNext()) {
	        Entry<String, Long> pair = it.next();
	        current = pair.getValue();
	        ret = (current < mn) ? pair.getKey() : ret;
			mn = (current < mn) ? current : mn;
	  }
		
		basicInfo = "Classes: " + map.toString() + SC.NL 
					+ "Class of Interest: '" + ret +"'"
					+ " , Count: " + mn + SC.NL;
		
		return ret; // CoI = least frequent class
	 }
	 
	 ////////////////////////////////////////////////////////////////////////
	 
	 private static void CopyOriginalFile() {
		 if (copyHeaderInOverSample)
			 CopyFileWithHeader();
		 else
			 CopyFileWithoutHeader();
	 }
	 ////////////////////////////////////////////////////////////////////////
	 
	 private static void CopyFileWithHeader() {
		 try {	 
			 // delete previous file "OverSample"
			 File f = new File(OverSampleFile);
			 if (f.exists())
					 f.delete();
			 
			 // take actual file to be appended
			 FileUtils.copyFile(new File (FileToSample), f);
			 
			} catch (IOException e) {
				e.printStackTrace();
			}
	 }

	 ////////////////////////////////////////////////////////////////////////
	 
	 private static void CopyFileWithoutHeader() {
		File inputFile = new File(FileToSample);
		String  Line = null;		
		Reader filePath;
		
		try {
			// delete previous file "OverSample"
			File file = new File(OverSampleFile);
			if (file.exists())
					 file.delete();
			
			// populate "OverSample" with input file
			// line by line skipping header			
			filePath = new FileReader((inputFile));
		      try {
		    	  
		    	// create a new file "OverSample"
				file =new File(OverSampleFile);
				file.createNewFile();
				  
		         // open input stream file for reading purpose.
		         BufferedReader br = new BufferedReader(filePath);
		         Line = br.readLine(); // skip header 
		          
		         while ((Line = br.readLine()) != null) {
		        	AppendTofile(Line.trim() + SC.NL , OverSampleFile);
		         }		           
		         br.close();
		      } catch(Exception e){
		         e.printStackTrace();
		     }		
		}	catch (FileNotFoundException e1) {
				e1.printStackTrace();
		}		
			
	 }

	 ////////////////////////////////////////////////////////////////////////
	 
	 private static void GenerateSynthethicData( Map<Integer, String> FT) {
		 int i=0; //, sz = oVector.size();
		 int ind = 0;
		 String S = "", Line = "";
		 
		 double fac = (factor * mapCoI.size());
		 Long req = (long)Math.round(fac) ;		 
		 CopyOriginalFile();		 
		 System.out.println(SC.NL + "Writing '" + req + "' synthetic records");
		 
		 while (req-- > 0) {
			ind = (int) (req % totalVectors);
		// 	System.out.println("ind = req / sz: " + ind + "=" + req + "/" + sz);
			
			S = store.DeserializeVectorWithMinMax(("vMM" + ind)).toString();
		//	S = oVector.get(ind).toString();
			S = S.replace("{",""); // start bracket
			S = S.replace("}",""); // closing bracket
		//	System.out.println(S);
			
			String[] arr = S.split("="); 
			S = arr[1];
			
			arr = S.split(SC.DLMT);
			Line = "";
			for (i=0; i<arr.length; i++) {
				if (FT.get(i) == "Numeric") {
					Line += ProduceNumericData(arr[i]) + SC.DLMT;
				} else {
					Line += ProduceDiscreteData(arr[i]) + SC.DLMT;
				}	
			}
			
			Line = SC.NL + Line + CoI;
			// write it to file
			
			AppendTofile(Line, OverSampleFile);
			
		 }
			
		 
	 }
	 ////////////////////////////////////////////////////////////////////////
	 
	 private static String ProduceNumericData(String inp) {
		 String numericVal = "";
		 if (useGaussianDist)
			 numericVal = getGaussianSample(inp);
		 else
			 numericVal =  getMinMaxRandomSample(inp);
		 
		 return numericVal;
	}
	
	 ////////////////////////////////////////////////////////////////////////
	 /*
	  * Input:  Min:Max (string format)
	  * Output: random number between range (min to max) 
	  */	 
	 private static String getMinMaxRandomSample(String inp) {
		 String[] arr = inp.split(SC.COLON);
		 
		 Double min = Double.parseDouble(arr[0]);
		 Double max = Double.parseDouble(arr[1]);
		 
		 Random rand = new Random();
		 double randomNum = min + (max - min) * rand.nextDouble();
		 randomNum = MU.RoundUp(randomNum, 2);
		 	     
	     // System.out.println("<"  + min + " : " + max + "> : " + randomNum );	     
		 return String.valueOf(randomNum);
	}
	
	 
	 //////////////////////////////////////////////////////////////////////////
	 /*
	  * Input:  mean:standardDeviation (string format)
	  * Output: Gaussian Sample 
	  */
	 
	 private static String getGaussianSample(String inp) {
		 String[] arr = inp.split(SC.COLON);
		 Double mean = Double.parseDouble(arr[0]);
		 Double sd = Double.parseDouble(arr[1]);
				 
		// System.out.print("mean: " + mean + " sd: " + sd);
		 
		 // every input sample is same
		 if (sd == 0.0) 
			 return String.valueOf(mean);
		 
		 NormalDistribution nd =  new NormalDistribution(mean, sd);
		 double prob = nd.sample();
		 prob = MU.RoundUp(prob, 2);
		 
		 // System.out.print(" --> GaussianSample: " + prob + SC.NL);
		 return String.valueOf(prob);
	 }
   
	 ////////////////////////////////////////////////////////////////////////
	 /*
	  * Input: array of unique itesms separated by colon
	  *        e.g.,  a:b:c:e:r:s
	  * Output: randomly select any one of them and return one item
	  * Problem: input carries duplicate values.  
	  *          for large size, it may consume all space
	  *          possible solution is to convert 
	  *          a:b:b:b:b:b:b:a:d:c:d:d
	  *          into
	  *          a-2:b-6:c-1:d-3
	  *          and then expand in processing
	  */
	 private static String ProduceDiscreteData(String inp) {
		 String[] arr = inp.split(SC.COLON);
		 Random rand = new Random();
		 int randomNum = rand.nextInt(arr.length);
		 return arr[randomNum];
	 }	
	
	////////////////////////////////////////////////////////////////////////
	/*
	* input: data to be appended
	* input: path of the file  
	*/
	private static void AppendTofile(String Line, String fPath ) {
		try {			
			//'true'  -> append the new data
			//'false' -> overwrite previous data
			FileWriter fw = new FileWriter(fPath, true); 
			fw.write(Line);//appends the string to the file
			fw.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	 //////////////////////////////////////////////////////////////////////////
    /*
	  * factor 0.0 -> 50% CoI & 50% non.CoI approximately
	  * factor 1.0 -> 100% increase in CoI records
	  * factor 1.5 -> 150% increase in CoI records
	  * factor 0.75 -> 750% increase in CoI records
	 */
	 private static double SetPriorRatio(Long Rec) {
		 if (factor > 0.0 )
			 return factor;
		 
		 double f = Rec - totalVectors;
		 f = f / totalVectors;
		 return f;
	 }
	 
	 
	 //////////////////////////////////////////////////////////////////////////
    /*
 	  * display final output (self explanatory)
 	  * last call of main function of this class
	 */	
	 private static String DisplayResult(Long Rec) {
		String ret = "";
		double ratio = (totalVectors * factor);
		double total = Rec +  ratio;
		ratio += totalVectors; 
		ratio = 100.0 * ( ratio/total);		
		ratio = MU.RoundUp(ratio, 2);
		 
	    String o = "<<Total Records>>" + SC.NL;
	    o += "Before Oversaming: " + Rec + SC.NL; 
 		o += "After Oversaming: " + (long) total + SC.NL2;    
		o += "ratio: " + ratio + "%" + SC.NL;  
		o += "factor: " + MU.RoundUp(factor,2) +  SC.NL;
		o += "KNN Value: " +  K + SC.NL;
		o += OverSampleFile + " created";				
		
		ret =  basicInfo + o + SC.NL + "Done";
		// System.out.println(ret);
		
		return ret;
	 }
	 
	

}
