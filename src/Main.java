import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException{
		NgramAPI ap=new NgramAPI();
		ap.connect("* head of *", 20, 0);
		String line=ap.nextSentence();
		while(line!=null){
			System.out.println(line);
			System.out.println("-----------------------------------");
			line=ap.nextSentence();
		}
	}
}


