package oversampling;

import java.text.DecimalFormat;

public class SC {
	private static int feedbackCR = 100;
	
	public static String DLMT = ","; 
	public static String DOT = ".";
	public static String COLON = ":";
	public static String SPACE = " ";
	public static String QUOTE = "\"";
	public static String NL = "\n";	
	public static String NL2 = NL + NL;	
	public static String NL3 = NL + NL + NL; 	
	public static String TB = "\t";	
	public static String TB2 = TB + TB;	
	public static String TB3 = TB + TB + TB;	
	public static String TB4 = TB + TB + TB + TB;	
	public static String MiddleBracketS = "[";
	public static String MiddleBracketE = "]";
	public static String BBracketS = "{";
	public static String BBracketE = "}";	
	public static String ZeroSpace = "";
	public static String FSlash = "/";
	public static String BSlash = "\\";
	public static String sCOLON = ";";
	public static String US = "_";
	public static String PIPE = "|";
	public static String GT = ">";
	public static String ST = "<";
	public static String EQ = "=";
	public static String Hyphen = "-";
	
		
		
	public static void main(String[] args) {
		System.out.println("Testing maven output.....");
	}
	
	SC () {
		// default constructor
	}
	
	///////////////////////////////////////////////////////////////////
	public static String Formate (double d) {
		String pattern = "##.##";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
		String ret = decimalFormat.format(d);
		return ret;
	}
	///////////////////////////////////////////////////////////////////

	public static String Formate (float d) {
		String pattern = "##.##";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
		String ret = decimalFormat.format((double)d);
		return ret;
	}
	///////////////////////////////////////////////////////////////////

	public static void displayFeedback(int i, String f, String s) {
		if ((i % feedbackCR) == 0) System.out.print(SC.NL);
		String p = ((i % 2) == 0) ?  f : s;
		System.out.print(p);			
			
	}
	 
}
