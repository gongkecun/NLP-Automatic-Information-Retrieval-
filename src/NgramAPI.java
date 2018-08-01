import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple Java API for the web-based n-gram search engine. As long as 
 * the user have Internet access, he or she should be able to query the n-gram search
 * engine through this API
 * <p>
 * Current version only support sentence output style, and text output format.
 * The user can specify the query and set the number of output and frequency threshold
 * through this API. Call the constructor NgramAPI("can not * * because of", 1000, 10) 
 * will tell the N-gram engine to search for pattern can not * * because of with maximum number of 
 * output 1000 and frequency threshold 10.
 * <p>
 * @author Jiexun Xu
 */
 
public class NgramAPI {
	private String pattern;
	private int num_of_output;
	private int frequency_threshold;
	private String body;
	private int ch_pointer;
	
	/**
	 * Constructor. 
	 */
	 
	public NgramAPI() throws IOException{
	}
	
	/*
	 * Establishes a connection to the server. IOException is be thrown if connection
	 * is failed.
	 * <p> 
	 * pattern specifies the pattern to query to the engine. num_of_output specifies the 
	 * maximum number of output. frequency_threshold specifies the frequency threshold.
	 * 
	 * @param pattern
	 * @param num_of_output
	 * @param frequency_threshold
	 */
	 
	public void connect(String pattern, int num_of_output, int frequency_threshold) throws IOException{
		this.pattern=pattern;
		this.num_of_output=num_of_output;
		this.frequency_threshold=frequency_threshold;
		ch_pointer=0;
		URL url=new URL(getSearchArgs());
		URLConnection url_c = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(url_c.getInputStream()));
        String inputLine;
        int count=0;
        while ((inputLine = in.readLine()) != null) {
        	if(count==4){
        		body=inputLine;
        	}
        	count++;
        }        
        in.close();
	}
	
	/**
	 * Returns the next sentence of this query. Returns null if last sentence has been returned, or if this query 
	 * results in zero output sentences.  
	 * 
	 * @return String
	 */
	 
	public String nextSentence(){
		if(body==null){
			return null;
		}
		if((body.charAt(ch_pointer)=='<')&&(body.charAt(ch_pointer+1)=='/')&&(body.charAt(ch_pointer+2)=='b')&&
		(body.charAt(ch_pointer+3)=='o')&&(body.charAt(ch_pointer+4)=='d')&&(body.charAt(ch_pointer+5)=='y')&&(body.charAt(ch_pointer+6)=='>')){
			return null;
		}		
		int start_point=ch_pointer;
		int end_point=-1;
		while(true){
			if(body.charAt(ch_pointer)=='<'){
				if((body.charAt(ch_pointer+1)=='b')&&(body.charAt(ch_pointer+2)=='r')&&(body.charAt(ch_pointer+3)=='>')){
					end_point=ch_pointer;
					ch_pointer=ch_pointer+4;
					break;
				}
			}
			ch_pointer++;
		}
		return body.substring(start_point, end_point);
	}
	
	/**
	 * Checks if this query actually results in zero output sentences or not. Return true if non-zero sentences is returned.
	 * 
	 * @return boolean
	 */
	 
	public boolean hasSentences(){
		return body!=null;
	}		
	
	private String getSearchArgs(){
		String out="http://linserv1.cims.nyu.edu:23232/cgi-bin/ngram/news86.cgi?key=";
		String[] splits=pattern.split(" ");
		for(int i=0;i<splits.length-1;i++){
			out=out+splits[i]+"+";
		}
		out=out+splits[splits.length-1]+"&print_max="+num_of_output+"&freq_threshold="+frequency_threshold;
		return out+"&output_style=sentence&print_format=text&sort=frequency";
	}
}
