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

public class TweetLoaderBM25 {
	
	public static void main(String args[]) throws Exception{
		
		String pathTraining = "D:\\project2nd\\dataset\\sentiment-tweet\\training.csv";
		//String pathTesting = "D:\\project2nd\\dataset\\sentiment-tweet\\testing.csv";
		
		String modelFilename = "D:\\project2nd\\library\\model.20120919";
		Tagger tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		Map<String, Integer> posWordCount = new HashMap<String, Integer>();
		Map<String, Integer> negWordCount = new HashMap<String, Integer>();
		
		
		List<String> wordList = new ArrayList<String>();
		
		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);
		
		
		
		FileWriter fw_neg = new FileWriter(new File("D:\\project2nd\\dataset\\training_neg.txt"));
		BufferedWriter bw_neg = new BufferedWriter(fw_neg);
		
		FileWriter fw_pos = new FileWriter(new File("D:\\project2nd\\dataset\\training_pos.txt"));
		BufferedWriter bw_pos = new BufferedWriter(fw_pos);
		
		FileWriter fw_tag = new FileWriter(new File("D:\\project2nd\\dataset\\training_tag.txt"));
		BufferedWriter bw_tag = new BufferedWriter(fw_tag);
		
		
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

				for (TaggedToken token : taggedTokens) {
					
					String word = token.token;
					String tag = token.tag;
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
					}else if(tweetSentiment.equals("4")){
						if(posWordCount.containsKey(word)){
							posWordCount.put(word, posWordCount.get(word)+1);
						}else{
							posWordCount.put(word, 1);
						}
					}
					
					wtct++;
				}
				
				bw_tag.write(tweetID + "\t" + wordtagStr );
				bw_tag.newLine();
				
				//tagWordCount.put(tweetID, wordtagStr);
				
			}
			
			ct++;
		}
		bw_tag.close();
		fw_tag.close();
		
		br.close();
		fr.close();
		
		
		
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
	    
		System.out.println("complete : "+ct);

	}

}
