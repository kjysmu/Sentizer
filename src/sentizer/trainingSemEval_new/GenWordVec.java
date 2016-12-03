package sentizer.trainingSemEval_new;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import sentizer.training.FileFunction;
import sentizer.util.Tagger;
import sentizer.util.Word2vecReader;
import sentizer.util.Tagger.TaggedToken;

public class GenWordVec {


	public static void main(String args[]) throws Exception{

		Word2vecReader w2v;
		w2v = new Word2vecReader();
		

		String pathTraining = "F:\\Semeval2014\\training\\finalTrainingInput_ext.txt";		
		String pathTrainingOriginal = "F:\\Semeval2014\\training\\trainingDatasetComplete.txt";
		
		String pathW2V_pos = "F:\\w2v\\pos.txt";
		String pathW2V_neg = "F:\\w2v\\neg.txt";
		String pathW2V_neu = "F:\\w2v\\neu.txt";

		String modelFilename = "D:\\project2nd\\dataset_sentizer\\model.20120919";

		Tagger tagger;		
		tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		FileReader fr2 = new FileReader(new File(pathTrainingOriginal));
		BufferedReader br2 = new BufferedReader(fr2);
		Map<String, String> mapTweetID = new HashMap<String, String>();
		String line = "";
		int ct = 0;
		
		double[] wvecTweet = new double[200];

		
		while(true){			
			line = br2.readLine();
			if(line == null) break;
			String[] splitStr = line.split("\t");
			if(splitStr.length == 4){
				String tweetID= splitStr[0];
				String tweetID2= splitStr[1];
				String sentiment= splitStr[2];
				String tweet= splitStr[3];
				mapTweetID.put(tweetID, tweet);
			}
		}
		
		br2.close();
		fr2.close();

		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);


		FileWriter fw_pos = new FileWriter(new File(pathW2V_pos));
		BufferedWriter bw_pos = new BufferedWriter(fw_pos);

		FileWriter fw_neg = new FileWriter(new File(pathW2V_neg));
		BufferedWriter bw_neg = new BufferedWriter(fw_neg);
		
		FileWriter fw_neu = new FileWriter(new File(pathW2V_neu));
		BufferedWriter bw_neu = new BufferedWriter(fw_neu);
		
		int numPos = 0;
		int numNeg = 0;
		int numNeu = 0;
		
		while(true){
			if(ct % 1000 == 0) System.out.println("progress (train) : " + ct);

			line = br.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length >= 4){
				
				String fstr = "";
				
				String tweetID= splitStr[0];
				String tweet= splitStr[1];
				String tweetPOS= splitStr[2];
				String tweetSentiment = splitStr[3];
				
				
				String[] tweetTermList = tweet.split(" ");
				String[] tweetTermPOSList = tweetPOS.split(" ");
				
				int wvec_ct = 0;

				for (int i=0; i<tweetTermList.length; i++) {

					String tweetTerm = tweetTermList[i];
					String tweetTermPOS = tweetTermPOSList[i];

					tweetTerm = tweetTerm.toLowerCase();

					if(w2v.containWords(tweetTerm)){
						double[] wvec = w2v.getWordRepresentation(tweetTerm);
						for(int j=0;j<200;j++){
							wvecTweet[j] += wvec[j];
						}
						wvec_ct++;
					}
				}
				
				String w2vStr = "";
				for(int i=0;i<200;i++){
					w2vStr += String.format("%.8f", wvecTweet[i])+ " ";
				}
				w2vStr = w2vStr.trim();
				
				if(tweetSentiment.equals("positive")){
					if(wvec_ct!=0){
						bw_pos.write(tweetID + "\t" + w2vStr);
						bw_pos.newLine();
					}
					
					numPos++;
				}else if(tweetSentiment.equals("negative")){
					if(wvec_ct!=0){
						bw_neg.write(tweetID + "\t" + w2vStr);
						bw_neg.newLine();
					}
					
					numNeg++;
				}else if(tweetSentiment.equals("neutral")){
					if(wvec_ct!=0){
						bw_neu.write(tweetID + "\t" + w2vStr);
						bw_neu.newLine();

					}
					
					numNeu++;
				}
				
			}

			ct++;
		}

		br.close();
		fr.close();
		
		bw_pos.close();
		bw_neg.close();
		bw_neu.close();
		
		fw_pos.close();
		fw_neg.close();
		fw_neu.close();

		System.out.println("Pos : " + numPos);
		System.out.println("Neg : " + numNeg);
		System.out.println("Neu : " + numNeu);

		System.out.println("complete");

	}

	public double distance(double[] v1, double[] v2){
		
		double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;
 
        for (int i = 0; i < v1.length; i++) //v1 and v2 must be of same length
        {
            dotProduct += v1[i] * v2[i];  //a.b
            magnitude1 += Math.pow(v1[i], 2);  //(a^2)
            magnitude2 += Math.pow(v2[i], 2); //(b^2)
        }
 
        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)
 
        if (magnitude1 != 0.0 | magnitude2 != 0.0){
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } 
        else{
            return 0.0;
        }
        return cosineSimilarity;
		
	}
	
	

}
