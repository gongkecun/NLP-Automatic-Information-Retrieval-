import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Test {
	public static void main(String[] args) throws IOException{
		String pattern_filename="country.pat";
		String test_filename="country.txt";
		ArrayList<Pattern> pts=read_patterns(pattern_filename);
		ArrayList<Sentence> test_data=read_test_data(test_filename);
		evaluate(pts, test_data);
	}
	
	public static void evaluate(ArrayList<Pattern> pts, ArrayList<Sentence> test_data){	
		int total_pairs=0;
		int joint_total_pairs=0;
		int correct=0;
		for(int i=0;i<test_data.size();i++){
			Sentence sc=test_data.get(i);
			if(sc.pairs!=null){
				total_pairs=total_pairs+sc.pairs.size();
			}else{
				total_pairs++;
			}
			ArrayList<Pair> obtained_pairs=new ArrayList<Pair>(2);
			for(int j=0;j<pts.size();j++){
				Pair p=pts.get(j).getPairs(sc.sentence);
				if((!p.token1.equals(""))&&(!p.token2.equals(""))){
					obtained_pairs.add(p);
				}
			}
			ArrayList<Pair> true_pairs=sc.pairs;	
			if(true_pairs==null){
				joint_total_pairs++;
				if(obtained_pairs.size()==0){					
					correct++;					
				}
				continue;
			}
			if(obtained_pairs.size()>0){
				joint_total_pairs++;
			}
			for(int j=0;j<obtained_pairs.size();j++){
				Pair p=obtained_pairs.get(j);
				boolean breakout=false;
				for(int k=0;k<true_pairs.size();k++){
					Pair p2=true_pairs.get(k);
					if((p.token1.equals(p2.token1))&&(p.token2.equals(p2.token2))){
						correct++;
						breakout=true;
						break;
					}
				}
				if(breakout){break;}
			}		
		}
		System.out.println("Precision is "+(double)correct/joint_total_pairs);
		System.out.println("Recall is "+(double)correct/total_pairs);
	}
	
	public static ArrayList<Pattern> read_patterns(String filename) throws IOException{
		Scanner sc=new Scanner(new File("D:\\My Java Workspace\\NLP_Final\\"+filename));
		ArrayList<Pattern> pts=new ArrayList<Pattern>();
		while(sc.hasNextLine()){
			String line=sc.nextLine();
			pts.add(new Pattern("", line.substring(2, line.length()-2), ""));
		}
		sc.close();
		return pts;
	}
	
	public static ArrayList<Sentence> read_test_data(String filename) throws IOException{
		Scanner reader=new Scanner(new File("D:\\My Java Workspace\\NLP_Final\\test_data\\"+filename));
		ArrayList<Sentence> scs=new ArrayList<Sentence>();
		String line=reader.nextLine();
		while(reader.hasNextLine()){
			Sentence sc=new Sentence(line);
			line=reader.nextLine();
			if(line.charAt(0)=='/'){
				scs.add(sc);
				if(!reader.hasNextLine()){
					break;
				}
				line=reader.nextLine();
				continue;
			}
			sc.pairs=new ArrayList<Pair>();
			while(line.charAt(0)=='+'){			
				String[] splits=line.split(" ");
				int i=1;
				String left="";
				while(splits[i].charAt(0)!='|'){
					left=left+splits[i]+" ";
					i++;
				}
				i++;
				while(splits[i].charAt(0)!='|'){
					i++;
				}
				i++;
				String right="";
				while(i<splits.length){
					right=right+splits[i]+" ";
					i++;
				}
				Pair p=new Pair(truncateString(left), truncateString(right));
				sc.pairs.add(p);				
				line=reader.nextLine();	
			}
			scs.add(sc);
		}
		reader.close();
		return scs;
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
}
