package sentizer.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import sentizer.util.IntValueComparator;

public class Test {

	public static void main(String args[]) throws Exception{
		
	     //String s = "hellllooooo howwwwwww areeeeeee youuuuuuu";
	     //s = s.replaceAll("([a-z])\\1+", "$1$1");
	     //System.out.println(s);
	     
	     FileReader fr = new FileReader(new File("D:\\project2nd\\dataset\\training_negN.txt"));
	     BufferedReader br = new BufferedReader(fr);

	     FileWriter fw = new FileWriter(new File("D:\\project2nd\\dataset\\training_negN_sorted.txt"));
	     BufferedWriter bw = new BufferedWriter(fw);

	     Map<String, Integer> wordCount = new HashMap<String, Integer>();
	     
	     String line = "";
	     
	     while(true){
	    	 
	    	 line = br.readLine();
	    	 if(line == null) break;
	    	 
	    	 StringTokenizer st = new StringTokenizer(line, "\t");
	    	 if(st.countTokens()==2){
	    		 
	    		 String key = st.nextToken();
	    		 int value = Integer.parseInt( st.nextToken() );
	    		 
	    		 wordCount.put(key, value);
	    		 
	    	 }
	    	 
	     }
	     
	     br.close();
	     fr.close();
	     
	 	IntValueComparator bvc = new IntValueComparator(wordCount);
	    TreeMap<String, Integer> tMap = new TreeMap<String, Integer>(bvc);
	    tMap.putAll(wordCount);

	    for(Map.Entry<String, Integer> entry : tMap.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	bw.write(key + "\t" + value);
	    	bw.newLine();
	    	
	    }
	    
	    bw.close();
	    bw.close();
	    
	    System.out.println("Completed!");
	    

	}
}
