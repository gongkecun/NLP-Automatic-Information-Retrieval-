import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/*
 * 1.  Initialization.  Start with high-precision pattern.  Generate set of seed pairs.  If necessary, prune seed pairs by hand.

Loop:

2.  Find sentences containing selected pairs (initially, seed pairs).

3.  From each sentence generate one or more patterns.*

4.  Prune #1.  Keep patterns matching more than n pairs.

5.  Collect pairs matching each pattern.

6.  Prune #2.  Rate patterns by precision using Agichtein's formula, assuming attributes are single-valued.

7.  Rate new pairs using Agichtein's formula, add best to set of selected pairs(Currently good pairs are pairs that match at
least k patterns, for some k)

Repeat

 */
public class Bootstrapping {
	public ArrayList<Pattern> good_patterns;
	public ArrayList<Pair> good_pairs;
	private PrintWriter pt;
	
	public Bootstrapping(String filename, String output_name) throws FileNotFoundException{
		good_patterns=new ArrayList<Pattern>();
		good_pairs=new ArrayList<Pair>();
		pt=new PrintWriter(new File("D:\\My Java Workspace\\NLP_Final\\"+output_name));
		init(filename);
	}
	
	// Phase 1. Put several seed pairs into pairs list. Let patterns list be empty.
	public void init(String filename) throws FileNotFoundException{		
		Scanner reader=new Scanner(new File("D:\\My Java Workspace\\NLP_Final\\"+filename));
		while(reader.hasNextLine()){
			Pair p=new Pair(reader.nextLine(), reader.nextLine());
			exclusive_add(good_pairs, p);
		}		
	}
	
	public void bootstrapping(int p, int q, int threshold) throws IOException{
		int pattern_count=good_patterns.size();	
		while(pattern_count<threshold){
			good_pattern_generation(p);
			if(pattern_count==good_patterns.size()){
				break;
			}
			good_pair_generation(q);
			pattern_count=good_patterns.size();
		}
		output_patterns();
	}
	
	public void output_patterns(){
		for(int i=0;i<good_patterns.size();i++){
			pt.println(good_patterns.get(i));
		}
		pt.close();
	}
	// Phase 2, 3, 4
	// Put a list of good patterns (patterns matching at least k pairs) into good_patterns
	public void good_pattern_generation(int k) throws IOException{
		//Phase 2
		ArrayList<Sentence> sentences=findSentencesFromPairs();
		//Phase 3 and 4
		HashMap<Pattern, Integer> map=new HashMap<Pattern, Integer>();
		for(int i=0;i<sentences.size();i++){
			Sentence sc=sentences.get(i);
			ArrayList<Pattern> pts=Pattern.genPatterns(sc.sentence, sc.tokens.token1, sc.tokens.token2, 0);
			if(pts==null){continue;}
			for(int j=0;j<pts.size();j++){
				Pattern p=pts.get(j);
				if(map.containsKey(p)){
					int count=map.remove(p).intValue();
					if(count+1>=k){
						exclusive_add(good_patterns, p);
					}else{
						map.put(p, Integer.valueOf(count+1));
					}
				}else{
					if(k<=1){
						exclusive_add(good_patterns, p);
					}else{
						map.put(p, Integer.valueOf(1));
					}
				}
			}
		}
	}
	
	private <T> void exclusive_add(ArrayList<T> list, T x){
		for(int i=0;i<list.size();i++){
			if(list.get(i).equals(x)){
				return;
			}
		}
		list.add(x);
	}
	
	//Phase 5 and 7(currently ignore phase 6)
	public void good_pair_generation(int k) throws IOException{
		//Phase 5
		ArrayList<Sentence> sentences=findSentencesFromPatterns();
		System.out.println("Done with finding sentences from pattern");
		//Phase 7
		HashMap<Pair, HashSet<Pattern>> map=new HashMap<Pair, HashSet<Pattern>>();
		for(int i=0;i<sentences.size();i++){
			Sentence sc=sentences.get(i);
			Pair p=sc.tokens;
			if(p==null){
				continue;
			}
			if(!map.containsKey(p)){
				HashSet<Pattern> set=new HashSet<Pattern>();
				set.add(sc.pattern);
				map.put(p, set);
			}else{
				HashSet<Pattern> set=map.remove(p);
				if(set.size()>=k){
					exclusive_add(good_pairs, p);
				}else{
					set.add(sc.pattern);
					map.put(p, set);
				}
			}
		}
	}
	
	// Find and return sentences containing at least one element in the pairs list
	private ArrayList<Sentence> findSentencesFromPairs() throws IOException{
		ArrayList<Sentence> valid_sentences=new ArrayList<Sentence>();
		NgramAPI api=new NgramAPI();
		for(int i=0;i<good_pairs.size();i++){
			Pair p=good_pairs.get(i);
			String query=generateQueriesFromPair(p);			
			api.connect(query, 10000, 0);
			if(api.hasSentences()){
				String line=api.nextSentence();
				while(line!=null){
					Sentence sc=new Sentence(line);
					sc.tokens=p;
					valid_sentences.add(sc);
					line=api.nextSentence();
				}
			}		
		}
		return valid_sentences;
	}	
	
	// Find and return sentences matching at least one pattern in the patterns list
	private ArrayList<Sentence> findSentencesFromPatterns() throws IOException{
		ArrayList<Sentence> sentences=new ArrayList<Sentence>();
		NgramAPI api=new NgramAPI();
		for(int i=0;i<good_patterns.size();i++){
			Pattern p=good_patterns.get(i);
			String query=generateQueriesFromPattern(p);
			api.connect(query, 10000, 0);
			if(api.hasSentences()){
				String line=api.nextSentence();
				while(line!=null){
					Sentence sc=new Sentence(line);
					sc.pattern=p;
					sc.tokens=getPairFromSentenceWithPattern(sc);
					sentences.add(sc);
					line=api.nextSentence();
				}
			}
		}
		return sentences;
	}
	
	private Pair getPairFromSentenceWithPattern(Sentence s){
		String[] mid_sp=Pattern.my_split(s.pattern.middle);
		String[] splits=s.sentence.split(" ");
		int start=Pattern.getIndex(splits, mid_sp);
		if(start==-1){
			System.out.println("What the hell are you doing?");
			System.exit(1);
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
	
	public String generateQueriesFromPair(Pair p){
		return p.token1+" * * * * * * "+p.token2;
	}
	
	public String generateQueriesFromPattern(Pattern p){
		if((p.left.equals(""))&&(p.right.equals(""))){
			return "* "+p.middle+" *";
		}
		return null;
	}
}
