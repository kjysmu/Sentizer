package sentizer.trainingSemEval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sentizer.training.FileFunction;
import sentizer.util.IntValueComparator;
import sentizer.util.Tagger;
import sentizer.util.Tagger.TaggedToken;

public class MaxEntTraining {

	
	public static void main(String args[]) throws Exception{
		
		String pathTraining = "F:\\Semeval2014\\training\\finalTrainingInput.txt";
	
		String pathTrainingOriginal = "F:\\Semeval2014\\training\\trainingDatasetComplete.txt";
		
		String mStr = "lex_v1.17";

		FileReader fr2 = new FileReader(new File(pathTrainingOriginal));
		BufferedReader br2 = new BufferedReader(fr2);

		Map<String, String> mapTweetID = new HashMap<String, String>();
		String line = "";
		int ct = 0;
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
		
		FeatureGenerator featureGenerator = new FeatureGenerator();
		
		//String modelFilename = "D:\\project2nd\\library\\model.20120919";
		//Tagger tagger = new Tagger();
		//tagger.loadModel(modelFilename);
		
		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);
		
		//Just for new input format
		FileWriter fw_ent = new FileWriter(new File("F:\\examples\\semeval_"+ mStr +".train"));
		BufferedWriter bw_ent = new BufferedWriter(fw_ent);
		
		
		while(true){
			if(ct % 1000 == 0) System.out.println("progress : " + ct);
			
			line = br.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");
			
			//StringTokenizer st = new StringTokenizer(linetmp,"\",\"");
			if(splitStr.length >= 4){
				String tweetID= splitStr[0];
				String tweet= splitStr[1];
				String tweetPOS= splitStr[2];
				String tweetSentiment = splitStr[3];
				
				String elements[] = featureGenerator.getFeatures(tweet, tweetPOS, tweetSentiment);
				
				if(mapTweetID.containsKey(tweetID)){
					elements[1] = mapTweetID.get(tweetID);
				}
				
				String elementsStr = "";
				for(int k=0; k<elements.length; k++ ){
					elementsStr += elements[k];
					if(k != elements.length-1){
						elementsStr += "\t";						
					}
				}
				bw_ent.write(elementsStr);			
				bw_ent.newLine();
			}
			
			ct++;
		}
		
		
		bw_ent.close();
		fw_ent.close();
		
		br.close();
		fr.close();
		
		
		
		
	    
		System.out.println("complete : "+ct);

	}
	



}
