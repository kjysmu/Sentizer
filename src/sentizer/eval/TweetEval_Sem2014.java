package sentizer.eval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import sentizer.tweet.SentimentAnalyzer;
import sentizer.tweet.SentimentAnalyzer_Sem2014;
import sentizer.util.Tagger.TaggedToken;
import sentizer.parameter.*;


public class TweetEval_Sem2014 {

	public static void main(String args[]) throws Exception{

		Map<String, String> labelResults = new HashMap<String, String>();

		String line = "";
		int ct = 0;

		SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
		SentimentAnalyzer_Sem2014 sentimentAnalyzer2 = new SentimentAnalyzer_Sem2014();

		FileReader fr = new FileReader(new File("F:\\Semeval2014\\evaluation\\SemEval2014-task9-test-B-gold-full.txt"));
		BufferedReader br = new BufferedReader(fr);	

		FileWriter fw = new FileWriter(new File("F:\\Semeval2014\\evaluation\\candidate-NB-3C-IDF-Sent140.txt"));
		BufferedWriter bw = new BufferedWriter(fw);	

		FileWriter fw_ent = new FileWriter(new File("F:\\examples\\semeval.test"));
		BufferedWriter bw_ent = new BufferedWriter(fw_ent);	
		
		while(true){

			if(ct % 100 == 0) System.out.println("progress : " + ct);

			line = br.readLine();
			if(line == null) break;


			String[] splitStr = line.split("\t");

			if(splitStr.length == 4){
				
				ct++;

				String tweetID= splitStr[0];
				String testID= splitStr[1];
				String tweetSentiment= splitStr[2];
				String tweet= splitStr[3];
				
				bw_ent.write(tweetSentiment + "\t" + tweet);
				bw_ent.newLine();
				

				labelResults.put(tweetID, tweetSentiment);

				//String sentiment = sentimentAnalyzer.getSentiment(tweet);
				String sentiment = sentimentAnalyzer2.getSentiment(tweet);

				if(sentiment.equals("Positive")){
					
					bw.write("NA" + "\t" + ct + "\t" + "positive");
					bw.newLine();
					
				}else if(sentiment.equals("Negative")){
					
					bw.write("NA" + "\t" + ct + "\t" + "negative");
					bw.newLine();
										
				}else{
					
					bw.write("NA" + "\t" + ct + "\t" + "neutral");
					bw.newLine();
					
				}

			}

		}

		bw_ent.close();
		fw_ent.close();
				
		br.close();
		fr.close();

		bw.close();
		fw.close();
		
		System.out.println("complete");

	}

}
