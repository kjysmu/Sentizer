package sentizer.toy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class GenTestData {
	
	public static void main(String args[]) throws Exception{
		
		//String pathTesting = "D:\\project2nd\\dataset\\sentiment-tweet\\testing.csv";

		String pathSem13 = "F:\\Semeval2014\\SemEval2013-test.txt";
		String pathSem14 = "F:\\Semeval2014\\SemEval2014-test.txt";
		
		FileReader frSem13 = new FileReader(new File(pathSem13));
		BufferedReader brSem13 = new BufferedReader(frSem13);
		
		Map<String, String> mapSem13 = new HashMap<String, String>();
		Map<String, String> mapSem14 = new HashMap<String, String>();
		
		String line = "";
		
		while(true){
			line = brSem13.readLine();
			if(line == null) break;
			String[] splitStr = line.split("\t");
			if(splitStr.length == 4){
				String tweetID= splitStr[0];
				String testID= "T13"+splitStr[1]; // only SemEval2013
				String tweetSentiment= splitStr[2];
				String tweet= splitStr[3];
				
				mapSem13.put(testID, tweet);
				
			}
			
		}
		
		brSem13.close();
		frSem13.close();
		
		FileReader frSem14 = new FileReader(new File(pathSem14));
		BufferedReader brSem14 = new BufferedReader(frSem14);
		
		while(true){
			line = brSem14.readLine();
			if(line == null) break;
			String[] splitStr = line.split("\t");
			if(splitStr.length == 4){
				String tweetID= splitStr[0];
				String testID= splitStr[1];
				String tweetSentiment= splitStr[2];
				String tweet= splitStr[3];
				
				mapSem14.put(testID, tweet);
				
			}
			
		}
		
		brSem14.close();
		frSem14.close();
		
		String pathGold = "F:\\Semeval2014\\SemEval2014-task9-test-B-gold-full.txt";

		FileReader frGold = new FileReader(new File(pathGold));
		BufferedReader brGold = new BufferedReader(frGold);
		
		FileWriter fwGold = new FileWriter(new File("F:\\Semeval2014\\SemEval2014-task9-test-B-gold-full-added.txt"));
		BufferedWriter bwGold = new BufferedWriter(fwGold);
		
		FileWriter fwGold2 = new FileWriter(new File("F:\\Semeval2014\\SemEval2014-task9-test-B-gold-modifed.txt"));
		BufferedWriter bwGold2 = new BufferedWriter(fwGold2);
		
		
		
		
		int ct=0;
		
		while(true){
			line = brGold.readLine();
			if(line == null) break;
			String[] splitStr = line.split("\t");
			if(splitStr.length == 3){
				String tweetID= splitStr[0];
				String testID= splitStr[1];
				String tweetSentiment= splitStr[2];
				
				if(testID.startsWith("T13")){
					if(mapSem13.containsKey(testID)){
						String tweet = mapSem13.get(testID);
						//bwGold.write(tweetID + "\t" + testID + "\t" + tweetSentiment + "\t" + tweet);
						//bwGold.newLine();
					}else{
						//System.out.println(testID);
					}
					
				}else if(testID.startsWith("T14")){
					if(mapSem14.containsKey(testID)){
						String tweet = mapSem14.get(testID);
						bwGold.write(tweetID + "\t" + testID + "\t" + tweetSentiment + "\t" + tweet);
						bwGold.newLine();
						
						bwGold2.write("NA"+"\t"+"Twiter2014"+"\t"+tweetSentiment);
						bwGold2.newLine();
						
					}else{
						System.out.println(testID);
					}
				}
				
			}else if(splitStr.length == 4){
				
				String id= splitStr[0];
				String testID= splitStr[1];
				String tweetSentiment= splitStr[2];
				String tweet= splitStr[3];
				
				if(testID.startsWith("LJ")){
					bwGold2.write("NA"+"\t"+"LiveJournal2014"+"\t"+tweetSentiment);
					bwGold2.newLine();
					
				}else if(testID.startsWith("SM")){
					bwGold2.write("NA"+"\t"+"SMS2013"+"\t"+tweetSentiment);
					bwGold2.newLine();
					
				}else if(testID.startsWith("TS")){
					bwGold2.write("NA"+"\t"+"Twitter2014Sarcasm"+"\t"+tweetSentiment);
					bwGold2.newLine();					
				}
				
				
				
				bwGold.write(line);
				bwGold.newLine();
				
			}
			
		}
		
		brGold.close();
		frGold.close();
		
		bwGold.close();
		fwGold.close();
		
		bwGold2.close();
		fwGold2.close();
		
		
		
		System.out.println("Complete");
		
	}

}
