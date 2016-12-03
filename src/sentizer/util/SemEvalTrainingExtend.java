package sentizer.util;

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

public class SemEvalTrainingExtend {
	
	public static void main(String args[]) throws Exception{
		
		String pathTraining = "D:\\project2nd\\dataset\\sentiment-tweet\\training.csv";
		String pathTrainingExt = "D:\\project2nd\\dataset\\sentiment-tweet\\training_ext.csv";

		//String pathTesting = "D:\\project2nd\\dataset\\sentiment-tweet\\testing.csv";
		
		String modelFilename = "D:\\project2nd\\library\\model.20120919";
		Tagger tagger = new Tagger();
		tagger.loadModel(modelFilename);
	
		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fw = new FileWriter(new File(pathTrainingExt));
		BufferedWriter bw = new BufferedWriter(fw);
		
		
		
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
				
				String sentimentStr = "neutral";

				if(tweetSentiment.equals("0")){
					sentimentStr = "negative";
				}else if(tweetSentiment.equals("4")){
					sentimentStr = "positive";
				}
				
				String wordStr = "";
				String tagStr = "";
				

				for (TaggedToken token : taggedTokens) {
					
					String word = token.token;
					String tag = token.tag;
					
					wordStr += word + " ";
					tagStr += tag + " ";
					
					wtct++;
				}
				
				//tagWordCount.put(tweetID, wordtagStr);
				bw.write(tweetID + "\t" + wordStr + "\t" + tagStr + "\t" + sentimentStr );
				
			}
			
			ct++;
		}
		
		br.close();
		fr.close();
		
		bw.close();
		fw.close();
	    
		System.out.println("complete : "+ct);

	}

}
