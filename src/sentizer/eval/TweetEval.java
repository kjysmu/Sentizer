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
import sentizer.util.Tagger.TaggedToken;
import sentizer.parameter.*;


public class TweetEval {
	
	public static void main(String args[]) throws Exception{
		
		Map<String, String> labelResults = new HashMap<String, String>();
		Map<String, String> predictResults = new HashMap<String, String>();
		
		String line = "";
		int ct = 0;
		
		SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
		
		FileReader fr;
		if(Exp.testset.equals("sts")){
			fr = new FileReader(new File(Path.SGS_TEST_FILEPATH));
		}else if(Exp.testset.equals("snt")){
			fr = new FileReader(new File(Path.SNT_TEST_FILEPATH));
		}else if(Exp.testset.equals("sem13")){
			fr = new FileReader(new File(Path.SEM13_TEST_FILEPATH));
		}else if(Exp.testset.equals("sem14")){
			fr = new FileReader(new File(Path.SEM14_TEST_FILEPATH));
		}else{
			//Default
			fr = new FileReader(new File(Path.SGS_TEST_FILEPATH));
		}
		
		BufferedReader br = new BufferedReader(fr);	
		Map<String, double[]> sntMap = new HashMap<String, double[]>();

		
		if(Exp.approach.contains("D2V")){
			FileReader fr_snt_vec = new FileReader(new File("D:\\project2nd\\workspace\\doc2vec\\file\\testingSNT_200_java.vec"));
			BufferedReader br_snt_vec = new BufferedReader(fr_snt_vec);
			
	
			while(true){
				
				line = br_snt_vec.readLine();
				if(line == null || line.isEmpty()) break;
				
				StringTokenizer st = new StringTokenizer(line, " ");
				String sentname = st.nextToken();
				
				double vec[] = new double[200];

				int vct = 0;
				while(st.hasMoreTokens()){
					vec[vct] = Double.parseDouble(st.nextToken().trim());

					vct++;
					if(vct >= 200) break;
					
				}
				
				sntMap.put(sentname, vec);

			}
			
			br_snt_vec.close();
			fr_snt_vec.close();
		}
	
		int test_ct = 0;
		

		while(true){
			if(ct % 100 == 0) System.out.println("progress : " + ct);
			
			line = br.readLine();
			if(line == null) break;
			
			if(Exp.testset.equals("sts")){
				String[] splitStr = line.split("\t");
				
				if(splitStr.length == 3){
					
					String tweetID= splitStr[0];
					String tweetSentiment= splitStr[1];
					String tweet= splitStr[2];
					
					labelResults.put(tweetID, tweetSentiment);
					
			    	String sentiment = sentimentAnalyzer.getSentiment(tweet);

					if(sentiment.equals("Positive")){
						predictResults.put(tweetID, "4");
					}else if(sentiment.equals("Negative")){
						predictResults.put(tweetID, "0");
					}else{
						predictResults.put(tweetID, "2");
					}
					
				}
			}else if(Exp.testset.equals("snt")){
				//snt : 1.6 million set of training tweets using emo
				
				
				String[] splitStr = line.split("\t");
				
				if(splitStr.length == 6){
					String tweetSentiment= splitStr[0];
					String tweetID= splitStr[1];
					String date= splitStr[2];
					String query= splitStr[3];
					String userID= splitStr[4];
					String tweet= splitStr[5];
					
					String sname = "sent_"+test_ct;
					
					
					labelResults.put(tweetID, tweetSentiment);
					String sentiment ="";
					
					if(Exp.approach.contains("D2V")){
						
						//System.out.println(sntMap.size());
						sentiment = sentimentAnalyzer.getSentimentD2V( sntMap.get(sname) );
					}else{
						sentiment = sentimentAnalyzer.getSentiment(tweet, query);
					}
					

					if(sentiment.equals("Positive")){
						predictResults.put(tweetID, "4");
					}else if(sentiment.equals("Negative")){
						predictResults.put(tweetID, "0");
					}else{
						predictResults.put(tweetID, "2");
					}
					
					test_ct++;
					
				}
			}else if(Exp.testset.equals("sem13")){

				//sem13
				
				String[] splitStr = line.split("\t");
				
				if(splitStr.length == 4){
					String tweetID= splitStr[0];
					String testID= splitStr[1];
					
					String tweetSentiment= splitStr[2];
					if(tweetSentiment.equals("positive")){
						tweetSentiment = "4";
					}else if(tweetSentiment.equals("negative")){
						tweetSentiment = "0";
					}else if(tweetSentiment.equals("neutral")){
						tweetSentiment = "2";
					}
					
					String tweet= splitStr[3];
					
					String sname = "sent_"+test_ct;
					
					labelResults.put(tweetID, tweetSentiment);
					
					
					String sentiment ="";
					
					if(Exp.approach.contains("D2V")){
						
						//System.out.println(sntMap.size());
						//sentiment = sentimentAnalyzer.getSentimentD2V( sntMap.get(sname) );
						
					}else{
						
						sentiment = sentimentAnalyzer.getSentiment(tweet);
						
						
						
					}
					

					if(sentiment.equals("Positive")){
						predictResults.put(tweetID, "4");
					}else if(sentiment.equals("Negative")){
						predictResults.put(tweetID, "0");
					}else{
						predictResults.put(tweetID, "2");
					}
					
					test_ct++;
					
				}
			
				
			}else if(Exp.testset.equals("sem14")){


				//sem14 
				
				String[] splitStr = line.split("\t");
				
				if(splitStr.length == 4){
					String tweetID= splitStr[0];
					String testID= splitStr[1];
					
					String tweetSentiment= splitStr[2];
					if(tweetSentiment.equals("positive")){
						tweetSentiment = "4";
					}else if(tweetSentiment.equals("negative")){
						tweetSentiment = "0";
					}else if(tweetSentiment.equals("neutral")){
						tweetSentiment = "2";
					}
					
					String tweet= splitStr[3];
					
					String sname = "sent_"+test_ct;
					
					labelResults.put(tweetID, tweetSentiment);
					
					
					String sentiment ="";
					
					if(Exp.approach.contains("D2V")){
						
						//System.out.println(sntMap.size());
						//sentiment = sentimentAnalyzer.getSentimentD2V( sntMap.get(sname) );
						
					}else{
						
						sentiment = sentimentAnalyzer.getSentiment(tweet);
						
						
						
					}
					

					if(sentiment.equals("Positive")){
						predictResults.put(tweetID, "4");
					}else if(sentiment.equals("Negative")){
						predictResults.put(tweetID, "0");
					}else{
						predictResults.put(tweetID, "2");
					}
					
					test_ct++;
					
				}
			
				
			
			}
			
			ct++;
		}
		
		br.close();
		fr.close();
		
		int correctNumber = 0;
		int totalNumber = 0;
		
		int totalPredictPos = 0;
		int totalPredictNeg = 0;
		
		int totalLabelPos = 0;
		int totalLabelNeg = 0;
		
		int correctNumberPos = 0;
		int correctNumberNeg = 0;
		
		
		for(Map.Entry<String, String> entry : predictResults.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			if(value.equals("0")){
				totalPredictNeg++;
			}else if(value.equals("4")){
				totalPredictPos++;
			}
		}

		for(Map.Entry<String, String> entry : labelResults.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			
			if(value.equals("0")){
				totalLabelNeg++;
			}else if(value.equals("4")){
				totalLabelPos++;
			}
			
			if(value.equals("2")) continue;
			
			if(predictResults.get(key).equals(value)){
				correctNumber++;

				if(value.equals("0")){
					correctNumberNeg++;
				}else if(value.equals("4")){
					correctNumberPos++;
				}
			}
			totalNumber++;
		}
		
		double precisionPos = (double)correctNumberPos/totalPredictPos;
		double recallPos = (double)correctNumberPos/totalLabelPos;
		double fscorePos = (2.0 * precisionPos * recallPos) / (precisionPos+recallPos);
		
		double precisionNeg = (double)correctNumberNeg/totalPredictNeg;
		double recallNeg = (double)correctNumberNeg/totalLabelNeg;
		double fscoreNeg = (2.0 * precisionNeg * recallNeg) / (precisionNeg+recallNeg);
		
		double precisionAvr = (precisionPos+precisionNeg)/2.0;
		double recallAvr = (recallPos+recallNeg)/2.0;
		double fscoreAvr = (fscorePos+fscoreNeg)/2.0;
		
		
		double accuracy = (double)correctNumber/totalNumber;
		
		NumberFormat formatter = new DecimalFormat("#0.0000");     
		
		System.out.println("precisionPos : " + formatter.format(precisionPos));
		System.out.println("recallPos : " + formatter.format(recallPos));
		System.out.println("fscorePos : " + formatter.format(fscorePos));
		
		System.out.println("precisionNeg : " + formatter.format(precisionNeg));
		System.out.println("recallNeg : " + formatter.format(recallNeg));
		System.out.println("fscoreNeg : " + formatter.format(fscoreNeg));
		
		System.out.println("precisionAvr : " + formatter.format(precisionAvr));
		System.out.println("recallAvr : " + formatter.format(recallAvr));
		System.out.println("fscoreAvr : " + formatter.format(fscoreAvr));
		
		System.out.println("# of correctly classified tweets : " + correctNumber);
		System.out.println("# of total tweets : " + totalNumber);
		
		
		System.out.println("Classifiation accuracy : " + formatter.format(accuracy));
		
	}

}
