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
import sentizer.util.Word2vecReader;
import sentizer.util.IntValueComparator;
import sentizer.util.Tagger;

public class TweetLoaderW2V2 {
	
	public static void main(String args[]) throws Exception{
		
		String pathTraining = "D:\\project2nd\\dataset\\sentiment-tweet\\training.csv";
		//String pathTesting = "D:\\project2nd\\dataset\\sentiment-tweet\\testing.csv";

		
		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fw = new FileWriter(new File("D:\\project2nd\\dataset_sentizer\\trainingSNT.txt"));
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
				
				bw.write(tweet);
				bw.newLine();
				
			}
			
			ct++;
		}
		
		br.close();
		fr.close();
		

		System.out.println("complete : "+ct);
		
		bw.close();
		fw.close();
		

	}

}
