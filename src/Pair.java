import java.util.HashSet;


public class Pair {
	public String token1;
	public String token2;
	
	public Pair(String t1, String t2){
		token1=t1;
		token2=t2;
	}		
	
	public boolean equals(Object obj){
		Pair p=(Pair)obj;
		return token1.equals(p.token1)&&token2.equals(p.token2);
	}
	
	public int hashCode(){
		return token1.hashCode()*Integer.MAX_VALUE+token2.hashCode();
	}
	
	public String toString(){
		return "{"+token1+","+token2+"}";
	}
}
