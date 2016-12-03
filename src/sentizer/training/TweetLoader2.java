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

public class TweetLoader2 {
	
	
	
	static String[] negation_cues= { "aint","cannot","cant","darent","didnt",
			"doesnt","dont","hadnt","hardly","hasnt",
			"havent","havnt","isnt","lack","lacking",
			"lacks","neither","never","no","nobody",
			"none","nor","not","nothing","nowhere",
			"mightnt","mustnt","neednt","oughtnt","shant",
			"shouldnt","wasnt","without","wouldnt" };
	
	
	public static void main(String args[]) throws Exception{
		
		
		String pathTraining = "D:\\project2nd\\dataset\\sentiment-tweet\\training.csv";
		//String pathTesting = "D:\\project2nd\\dataset\\sentiment-tweet\\testing.csv";
		
		String modelFilename = "D:\\project2nd\\library\\model.20120919";
		Tagger tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		Map<String, Integer> posWordCount = new HashMap<String, Integer>();
		Map<String, Integer> negWordCount = new HashMap<String, Integer>();
		
		Map<String, Integer> posWordCountN = new HashMap<String, Integer>();
		Map<String, Integer> negWordCountN = new HashMap<String, Integer>();
		
		Map<String, Integer> posWordCountA = new HashMap<String, Integer>();
		Map<String, Integer> negWordCountA = new HashMap<String, Integer>();
		
		
		List<String> wordList = new ArrayList<String>();
		
		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fw_neg = new FileWriter(new File("D:\\project2nd\\dataset\\training_neg.txt"));
		BufferedWriter bw_neg = new BufferedWriter(fw_neg);
		
		FileWriter fw_pos = new FileWriter(new File("D:\\project2nd\\dataset\\training_pos.txt"));
		BufferedWriter bw_pos = new BufferedWriter(fw_pos);
		
		
		
		FileWriter fw_negN = new FileWriter(new File("D:\\project2nd\\dataset\\training_negN.txt"));
		BufferedWriter bw_negN = new BufferedWriter(fw_negN);
		
		FileWriter fw_posN = new FileWriter(new File("D:\\project2nd\\dataset\\training_posN.txt"));
		BufferedWriter bw_posN = new BufferedWriter(fw_posN);
		
		FileWriter fw_negA = new FileWriter(new File("D:\\project2nd\\dataset\\training_negA.txt"));
		BufferedWriter bw_negA = new BufferedWriter(fw_negA);
		
		FileWriter fw_posA = new FileWriter(new File("D:\\project2nd\\dataset\\training_posA.txt"));
		BufferedWriter bw_posA = new BufferedWriter(fw_posA);
		
		//FileWriter fw_tag = new FileWriter(new File("D:\\project2nd\\dataset\\training_tag.txt"));
		//BufferedWriter bw_tag = new BufferedWriter(fw_tag);
		
		String line = "";
		int ct = 0;
		while(true){
			if(ct % 10000 == 0) System.out.println("progress : " + ct);
			
			line = br.readLine();
			if(line == null) break;

			String linetmp = line.substring(1, line.length()-1);
			String[] splitStr = linetmp.split("\",\"");
			//StringTokenizer st = new StringTokenizer(linetmp,"\",\"");
			if(splitStr.length >= 6){
				String tweetSentiment= splitStr[0];
				String tweetID= splitStr[1];
				String date= splitStr[2];
				String query= splitStr[3];
				String userID= splitStr[4];
				String tweet= splitStr[5];
				//System.out.println(tweetSentiment + ":" + tweet);
				//System.out.println(linetmp);
				
				List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());
				
				
				String wordtagStr = "";
				int wtct = 0;
				boolean isNeg = false;
				boolean isNegator = false;

				for (TaggedToken token : taggedTokens) {
					
					String word = token.token;
					String tag = token.tag;
					
					for(String negword : negation_cues){
						if(negword.equals(word.toLowerCase()) || word.toLowerCase().endsWith("n't") ){
							isNeg = true;
							isNegator = true;
						}
					}
					
					if(isNegator){
						isNegator = false;
						continue;
					}
					
					if(tag.equals(",")){
						isNeg = false;
					}
					
					
					
					
					if(wtct!=0) wordtagStr += "\t";
					wordtagStr += word + "\t" + tag;
					
					if( !wordList.contains(word) ){
						wordList.add(word);
					}
					
					if(tweetSentiment.equals("0")){
						if(negWordCount.containsKey(word)){
							negWordCount.put(word, negWordCount.get(word)+1);
						}else{
							negWordCount.put(word, 1);
						}
						
						if(!tag.equals(",")){
							if(isNeg){
								if(negWordCountN.containsKey(word)){
									negWordCountN.put(word, negWordCountN.get(word)+1);
								}else{
									negWordCountN.put(word, 1);
								}
							}else{
								if(negWordCountA.containsKey(word)){
									negWordCountA.put(word, negWordCountA.get(word)+1);
								}else{
									negWordCountA.put(word, 1);
								}	
							}
						}
				
						
					}else if(tweetSentiment.equals("4")){
						if(posWordCount.containsKey(word)){
							posWordCount.put(word, posWordCount.get(word)+1);
						}else{
							posWordCount.put(word, 1);
						}
						
						if(!tag.equals(",")){
							if(isNeg){
								if(posWordCountN.containsKey(word)){
									posWordCountN.put(word, posWordCountN.get(word)+1);
								}else{
									posWordCountN.put(word, 1);
								}
							}else{
								if(posWordCountA.containsKey(word)){
									posWordCountA.put(word, posWordCountA.get(word)+1);
								}else{
									posWordCountA.put(word, 1);
								}	
							}
						}	
					}
					
					
					
					
					wtct++;
				}
				
				//bw_tag.write(tweetID + "\t" + wordtagStr );
				//bw_tag.newLine();
				
				//tagWordCount.put(tweetID, wordtagStr);
				
			}
			
			ct++;
		}
		//bw_tag.close();
		//fw_tag.close();
		
		br.close();
		fr.close();
		
		
		
		IntValueComparator bvc = new IntValueComparator(negWordCountN);
	    TreeMap<String, Integer> tMap = new TreeMap<String, Integer>(bvc);
	    tMap.putAll(negWordCountN);

	    for(Map.Entry<String, Integer> entry : tMap.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	bw_negN.write(key + "\t" + value);
	    	bw_negN.newLine();
	    	
	    }
	    bw_negN.close();
	    fw_negN.close();
		
		IntValueComparator bvc2 = new IntValueComparator(posWordCountN);
	    TreeMap<String, Integer> tMap2 = new TreeMap<String, Integer>(bvc2);
	    tMap2.putAll(posWordCountN);

	    for(Map.Entry<String, Integer> entry : tMap2.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	bw_posN.write(key + "\t" + value);
	    	bw_posN.newLine();
	    	
	    }
	    
	    bw_posN.close();
	    fw_posN.close();
		
		
		IntValueComparator bvc3 = new IntValueComparator(negWordCountA);
	    TreeMap<String, Integer> tMap3 = new TreeMap<String, Integer>(bvc3);
	    tMap3.putAll(negWordCountA);

	    for(Map.Entry<String, Integer> entry : tMap3.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	bw_negA.write(key + "\t" + value);
	    	bw_negA.newLine();
	    	
	    }
	    bw_negA.close();
	    fw_negA.close();
		
		IntValueComparator bvc4 = new IntValueComparator(posWordCountA);
	    TreeMap<String, Integer> tMap4 = new TreeMap<String, Integer>(bvc4);
	    tMap4.putAll(posWordCountA);

	    for(Map.Entry<String, Integer> entry : tMap4.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	bw_posA.write(key + "\t" + value);
	    	bw_posA.newLine();
	    	
	    }
	    
	    bw_posA.close();
	    fw_posA.close();
	    
	    
	    
		/*
		IntValueComparator bvc = new IntValueComparator(negWordCount);
	    TreeMap<String, Integer> tMap = new TreeMap<String, Integer>(bvc);
	    tMap.putAll(negWordCount);
	    

	    for(Map.Entry<String, Integer> entry : negWordCount.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	System.out.println(key + ":" + value);
	    	
	    	bw_neg.write(key + "\t" + value);
	    	bw_neg.newLine();
	    	
	    	
	    }
	    
	    bw_neg.close();
	    fw_neg.close();
	    
    	System.out.println();

	    IntValueComparator bvc2 = new IntValueComparator(posWordCount);
	    TreeMap<String, Integer> tMap2 = new TreeMap<String, Integer>(bvc2);
	    tMap2.putAll(posWordCount);
			   
	    for(Map.Entry<String, Integer> entry : posWordCount.entrySet() ){
	    	String key = entry.getKey();
	    	int value = entry.getValue();
	    	
	    	System.out.println(key + ":" + value);
	    	
	    	bw_pos.write(key + "\t" + value);
	    	bw_pos.newLine();
	    	
	    	
	    }
	    
	    bw_pos.close();
	    fw_pos.close();
	    */
	    
		System.out.println("complete : "+ct);

	}
	
	public static boolean isNegation(String tweet){
		
		boolean isNeg = false;
		
		for(String negcue : negation_cues){
			if(tweet.contains(negcue)){
				isNeg = true;
				break;
			}
		}
		
		if(tweet.contains("n't")){
			isNeg = true;
		}
		
		return isNeg;
	}

	
	

}
