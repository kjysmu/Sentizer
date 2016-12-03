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

public class TweetLoaderW2V {
	
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
		
		Word2vecReader word2vec = new Word2vecReader();
		
		
		FileWriter fw = new FileWriter(new File("D:\\project2nd\\dataset_sentizer\\training_w2v_new.txt"));
		BufferedWriter bw = new BufferedWriter(fw);
		
		FileWriter fw_vec = new FileWriter(new File("D:\\project2nd\\dataset_sentizer\\training_w2v_vector.txt"));
		BufferedWriter bw_vec = new BufferedWriter(fw_vec);
		

		double[] posVec = new double[200];
		double[] negVec = new double[200];
		
		
		
		int posVecCt = 0;
		int negVecCt = 0;
		
		
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
				
				List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());
				String wordtagStr = "";
				
				double[] msgVec = new double[200];
				
				boolean isVec = false;
				
				int msgct= 0;

				for (TaggedToken token : taggedTokens) {
					
					String word = token.token;
					String tag = token.tag;
					
					if(tweetSentiment.equals("0")){
						
						double[] vec =word2vec.getWordRepresentation(word);
						if(vec != null){
							isVec = true;
							for(int i=0; i<200; i++){
								msgVec[i] += vec[i];
							}
							msgct++;
							
						}
						
					}else if(tweetSentiment.equals("4")){
						double[] vec =word2vec.getWordRepresentation(word);
						if(vec != null){
							isVec = true;
							for(int i=0; i<200; i++){
								msgVec[i] += vec[i];
							}
							msgct++;

						}
						
					}
					
				}
				if(msgct >= 1){
					for(int i=0; i<200; i++){
						msgVec[i] /= msgct;
					}	
				}
				
				
				if(isVec){
					
					if(tweetSentiment.equals("0")){
						
						bw_vec.write(tweetID);

						for(int i=0; i<200; i++){
							bw_vec.write(" ");
							bw_vec.write(String.format("%.6f", msgVec[i]) );
						}
						
						bw_vec.write(" "+"0");
						bw_vec.newLine();
						
						
						negVecCt++;
						for(int i=0; i<200; i++){
							negVec[i] += msgVec[i];
						}
					}else if(tweetSentiment.equals("4")){
						
						bw_vec.write(tweetID);

						for(int i=0; i<200; i++){
							bw_vec.write(" ");
							bw_vec.write(String.format("%.6f", msgVec[i]) );
						}
						
						bw_vec.write(" "+"4");
						bw_vec.newLine();
						
						posVecCt++;
						for(int i=0; i<200; i++){
							posVec[i] += msgVec[i];
						}
					}
					
				}
				
				
			}
			
			ct++;
		}
		
		br.close();
		fr.close();
		
		String posStr = "Positive ";
		String negStr = "Negative ";
		
		if(negVecCt != 0 && posVecCt !=0){
			for(int i=0; i<200; i++){
				negVec[i] /= negVecCt;
				negStr += String.format("%.6f", negVec[i]) + " ";
				
				posVec[i] /= posVecCt;
				posStr += String.format("%.6f", posVec[i]) + " ";

			}
		}
		bw_vec.close();
		fw_vec.close();
		
		bw.write(posStr.trim());
		bw.newLine();
		
		bw.write(negStr.trim());

		System.out.println("complete : "+ct);
		
		bw.close();
		fw.close();
		

	}

}
