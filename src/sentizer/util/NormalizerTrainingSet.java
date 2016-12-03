package sentizer.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import sentizer.util.Tagger;

public class NormalizerTrainingSet {

	public static void main(String args[]) throws Exception{
		
		
		Map<String, Integer> posWordCount;
		Map<String, Integer> negWordCount;

		
		FileReader fr_neg;
		BufferedReader br_neg;
			
		FileReader fr_pos;
		BufferedReader br_pos;
		
		posWordCount = new HashMap<String, Integer>();
		negWordCount = new HashMap<String, Integer>();

		fr_neg = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_neg2.txt"));
		br_neg = new BufferedReader(fr_neg);
		
		String line = "";
		while(true){
			line = br_neg.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				key = key.replaceAll("([a-z])\\1+", "$1$1");

				int value = Integer.parseInt( st.nextToken() );
				if(negWordCount.containsKey(key)){
					negWordCount.put(key, negWordCount.get(key) + value);
				}else{
					negWordCount.put(key, value);
				}
				
			}
		}
		
		br_neg.close();
		fr_neg.close();
		
		fr_pos = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_pos2.txt"));
		br_pos = new BufferedReader(fr_pos);
		line = "";
		
		while(true){
			line = br_pos.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				key = key.replaceAll("([a-z])\\1+", "$1$1");

				int value = Integer.parseInt( st.nextToken() );
				if(posWordCount.containsKey(key)){
					posWordCount.put(key, posWordCount.get(key) + value);
				}else{
					posWordCount.put(key, value);
				}
			}
		}
		
		br_pos.close();
		fr_pos.close();
		
		
		FileWriter fw_neg = new FileWriter(new File("D:\\project2nd\\dataset_sentizer\\training_neg.txt"));
		BufferedWriter bw_neg = new BufferedWriter(fw_neg);
		
		FileWriter fw_pos = new FileWriter(new File("D:\\project2nd\\dataset_sentizer\\training_pos.txt"));
		BufferedWriter bw_pos = new BufferedWriter(fw_pos);

		IntValueComparator bvc = new IntValueComparator(negWordCount);
	    TreeMap<String, Integer> tMap = new TreeMap<String, Integer>(bvc);
	    tMap.putAll(negWordCount);

	    for(Map.Entry<String, Integer> entry : tMap.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	//System.out.println(key + ":" + value);
	    	
	    	bw_neg.write(key + "\t" + value);
	    	bw_neg.newLine();
	    	
	    	
	    }
	    
	    bw_neg.close();
	    fw_neg.close();
	    
    	System.out.println();

	    IntValueComparator bvc2 = new IntValueComparator(posWordCount);
	    TreeMap<String, Integer> tMap2 = new TreeMap<String, Integer>(bvc2);
	    tMap2.putAll(posWordCount);
		
	    for(Map.Entry<String, Integer> entry : tMap2.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	//System.out.println(key + ":" + value);
	    	
	    	bw_pos.write(key + "\t" + value);
	    	bw_pos.newLine();
	    	
	    }
	    
	    bw_pos.close();
	    fw_pos.close();
	    
	}
	
}
