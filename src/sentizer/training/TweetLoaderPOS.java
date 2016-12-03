package sentizer.training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import sentizer.util.Tagger.TaggedToken;
import sentizer.util.IntValueComparator;
import sentizer.util.Tagger;

public class TweetLoaderPOS {
	
	public static void main(String args[]) throws Exception{
		
		String pathTraining = "D:\\project2nd\\dataset_sentizer\\training_tag.txt";
		
		//String pathTesting = "D:\\project2nd\\dataset\\sentiment-tweet\\testing.csv";
		
		Map<String, Integer> posWordCount = new HashMap<String, Integer>();
		Map<String, Integer> negWordCount = new HashMap<String, Integer>();
		
		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fw_neg = new FileWriter(new File("D:\\project2nd\\dataset\\training_negTag.txt"));
		BufferedWriter bw_neg = new BufferedWriter(fw_neg);
		
		FileWriter fw_pos = new FileWriter(new File("D:\\project2nd\\dataset\\training_posTag.txt"));
		BufferedWriter bw_pos = new BufferedWriter(fw_pos);
		
		String negStartStr = "1467822272";
		String line = "";
		boolean isNegStart = false;
		int ct = 0;
		while(true){
			if(ct % 10000 == 0) System.out.println("progress : " + ct);
			
			line = br.readLine();
			if(line == null) break;
			
			String[] splitStr = line.split("\t");
			if(splitStr[0].equals(negStartStr)){
				isNegStart = true;
			}
			
			for(int i=1; i < splitStr.length; i+=2 ){
				String strTag = splitStr[i] + " " + splitStr[i+1];
				if(isNegStart){
					//Neg
					if(negWordCount.containsKey(strTag)){
						negWordCount.put(strTag, negWordCount.get(strTag)+1);
					}else{
						negWordCount.put(strTag, 1);
					}
				}else{
					//Pos
					if(posWordCount.containsKey(strTag)){
						posWordCount.put(strTag, posWordCount.get(strTag)+1);
					}else{
						posWordCount.put(strTag, 1);
					}
				}
				
			}

			ct++;
		}
		
		br.close();
		fr.close();
		
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
	    
		System.out.println("complete : "+ct);

	}

}
