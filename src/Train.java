import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class Train {
	public static void main(String[] args) throws IOException{	
		add_country_list();
		Bootstrapping bs=new Bootstrapping("sd_country.txt", "country.pat");
		bs.bootstrapping(3, 3, 7);
	/*	bs.good_pattern_generation(2);
		System.out.println("Done with pattern generation");
		bs.good_pair_generation(2);*/
		System.out.println(bs.good_pairs);
		System.out.println(bs.good_pairs.size());
	}
	
	public static void add_country_list() throws IOException{
		Pattern.country_names=new HashSet<String>();
		Scanner sc=new Scanner(new File("D:\\My Java Workspace\\NLP_Final\\country_names.txt"));
		while(sc.hasNext()){
			Pattern.country_names.add(sc.next());
		}
		sc.close();
	}
}


