import java.util.ArrayList;
import java.util.HashSet;


public class Pattern {
	String left;
	String middle;
	String right;
	public static HashSet<String> country_names;
	
	public Pattern(String l, String m, String r){
		left=l;
		middle=m;
		right=r;
	}
	
	// Get pair of tokens using this current pattern. Increment num_of_matches if valid match found.
	// Return null if current sentence doesn't match this pattern
	public Pair getPairs(String sentence){
		String[] mid_sp=Pattern.my_split(middle);
		String[] splits=sentence.split(" ");
		int start=Pattern.getIndex(splits, mid_sp);
		if(start==-1){
			return new Pair("", "");
		}
		String left="";
		boolean started=false;
		for(int i=start;i>=0;i--){
			if((!Pattern.isValidToken2(splits[i]))&&(started)){
				break;
			}else if(Pattern.isValidToken2(splits[i])){
				started=true;
				left=splits[i]+" "+left;
			}
		}
		String right="";
		started=false;
		for(int i=start+mid_sp.length;i<splits.length;i++){
			if((!Pattern.isValidToken(splits[i]))&&(started)){
				break;
			}else if(Pattern.isValidToken(splits[i])){
				started=true;
				right=right+" "+splits[i];
			}
		}
		if(left.equals("")||right.equals("")){
			return null;
		}
		return new Pair(truncateString(left), truncateString(right));
	}
	
	public static String truncateString(String in){
		String left=in;
		if(left.length()>0){
			if(left.charAt(0)==' '){
				left=left.substring(1);
			}
			if(left.charAt(left.length()-1)==' '){
				left=left.substring(0, left.length()-1);
			}
		}
		return left;
	}
	
	public static boolean isValidToken(String t){
	// Use for country, state and leader
		if(country_names!=null){
			if(!country_names.contains(t)){
				return false;
			}
		}
		char ch=t.charAt(0);
		return (ch>='A')&&(ch<='Z');
	// Use for population
	/*	if((t.equalsIgnoreCase("thousand"))||(t.equalsIgnoreCase("hundred"))||
				(t.equalsIgnoreCase("million"))||(t.equalsIgnoreCase("billion"))){
			return true;
		}else if(t.charAt(0)=='1'){
			return true;
		}
		return false;*/
	}
	
	public static boolean isValidToken2(String t){
		// Use for country, state and leader	
		char ch=t.charAt(0);
		return (ch>='A')&&(ch<='Z');
	}
	
	// Generate a pattern given a sentence and left, right token pairs. bound is used to indicate the maximum tokens to 
	// include in left and right. Return null if current sentence doesn't contain left_token and right_token
	//
	// Current: Only generate mid. Left=right="". If left_idx and right_idx are 7 more indices apart, return null
	public static ArrayList<Pattern> genPatterns(String sentence, String left_token, String right_token, int bound){
		String[] splits=sentence.split(" ");
		String[] sp_left=my_split(left_token);
		String[] sp_right=my_split(right_token);
		int left_idx=getIndex(splits, sp_left);
		int right_idx=getIndex(splits, sp_right);
		if((left_idx==-1)||(right_idx==-1)||(left_idx>right_idx)||(right_idx-left_idx>7)){
			return genMultipleMidPattern(splits, left_idx+sp_left.length-1, right_idx);
		}
		String mid="";
		if(splits[left_idx+1].equals("Vuylsteke")){
			System.out.println();
		}
		for(int i=left_idx+sp_left.length;i<right_idx;i++){
			mid=mid+splits[i]+" ";
		}
		String left="";
		if(bound==0){left="";};
		for(int i=Math.max(0, left_idx-bound+1);i<left_idx;i++){
			left=left+splits[i]+" ";
		}
		String right="";
		if(bound==0){right="";};
		for(int i=right_idx+1;i<Math.min(splits.length, right_idx+bound);i++){
			right=right+splits[i]+" ";
		}
		ArrayList<Pattern> pts=new ArrayList<Pattern>();
		pts.add(new Pattern(left, mid, right));
		return pts;
	}
	
	public static ArrayList<Pattern> genMultipleMidPattern(String[] splits, int start, int end){
		ArrayList<Pattern> ret=new ArrayList<Pattern>();
		for(int i=2;i<=6;i++){		
			for(int j=start+1;j<=end-i;j++){
				String mid="";
				for(int k=0;k<i;k++){
					mid=mid+splits[j+k];
				}
				Pattern temp=new Pattern("",mid,"");
				ret.add(temp);
			}
		}
		return ret;
	}
	
	// Gets the index in sentence[index] where token[0] appears, 
	// and sentence[index+i] matches token[i], for 0<=i<=token.length-1
	// return -1 if no match
	public static int getIndex(String[] sentence, String[] token){
		for(int i=0;i<sentence.length;i++){
			if(sentence[i].equals(token[0])){
				boolean flag=true;
				for(int j=1;j<token.length;j++){
					if((i+j>=sentence.length)||(!sentence[i+j].equals(token[j]))){
						flag=false;
						break;
					}
				}
				if(flag){
					return i;
				}else{
					continue;
				}
			}
		}
		return -1;
	}
	
	public static String[] my_split(String x){
		ArrayList<String> l=new ArrayList<String>();
		String token="";
		for(int i=0;i<x.length();i++){
			if((x.charAt(i)==' ')||(x.charAt(i)=='.')){
				l.add(token);
				token="";
			}else{
				token=token+x.charAt(i);
			}
		}
		l.add(token);
		for(int i=0;i<l.size();i++){
			if(l.get(i).equals("")){
				l.remove(i);
			}
		}
		String[] waste=new String[0];
		return l.toArray(waste);
	}
	
	public String toString(){
		if((left.equals(""))&&(right.equals(""))){
			return "[ "+middle+" ]";
		}
		return "";
	}
	
	public boolean equals(Object obj){
		Pattern t=(Pattern)obj;
		return left.equals(t.left)&&middle.equals(t.middle)&&right.equals(t.right);
	}
	
	public int hashCode(){
		return middle.hashCode();
	}
}
