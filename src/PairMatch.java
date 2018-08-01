import java.util.HashSet;


public class PairMatch {
	public Pair p;
	public HashSet<Pattern> matched_patterns;
	public PairMatch(Pair p){
		this.p=p;
	}
	
	public void addMatchedPattern(Pattern p){
		if(matched_patterns==null){
			matched_patterns=new HashSet<Pattern>();
		}
		if(!matched_patterns.contains(p)){
			matched_patterns.add(p);
		}
	}
	
	public boolean equals(Object obj){
		PairMatch temp=(PairMatch)obj;
		return p.token1.equalsIgnoreCase(temp.p.token1)&&p.token2.equalsIgnoreCase(temp.p.token2);
	}
	
	public int hashCode(){
		return p.token1.hashCode()*Integer.MAX_VALUE+p.token2.hashCode();
	}
}
