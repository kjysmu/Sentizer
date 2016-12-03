package sentizer.trainingSemEval;

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
import sentizer.util.Tagger.TaggedToken;

public class SVMFeatureGen {


	public static void main(String args[]) throws Exception{

		String pathTraining = "F:\\Semeval2014\\training\\finalTrainingInput.txt";
		
		String pathTrainingOriginal = "F:\\Semeval2014\\training\\trainingDatasetComplete.txt";

		String modelFilename = "D:\\project2nd\\dataset_sentizer\\model.20120919";

		Tagger tagger;		
		tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		FeatureGeneratorSVM featureGen = new FeatureGeneratorSVM();
		
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

		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);

		//Just for new input format

		//Set<String> unigramSet = new HashSet<String>();
	    SortedSet<String> unigramSet = new TreeSet<String>();

		FileWriter fw_tr = new FileWriter(new File("F:\\svm\\semeval14.train"));
		BufferedWriter bw_tr = new BufferedWriter(fw_tr);
		
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
				
				if(tweetSentiment.equals("positive")){
					numPos++;
				}else if(tweetSentiment.equals("negative")){
					numNeg++;
				}else if(tweetSentiment.equals("neutral")){
					numNeu++;
				}
				
				
				fstr = featureGen.getFeatures(tweet, tweetPOS, tweetSentiment);
				
				bw_tr.write(fstr.trim());
				bw_tr.newLine();
			}

			ct++;
		}

		bw_tr.close();
		fw_tr.close();
		
		br.close();
		fr.close();

		
		System.out.println("Pos : " + numPos);
		System.out.println("Neg : " + numNeg);
		System.out.println("Neu : " + numNeu);
		
		
		FileReader fr_test = new FileReader(new File("F:\\svm\\dataset\\semeval.test"));
		BufferedReader br_test = new BufferedReader(fr_test);

		FileWriter fw_test = new FileWriter(new File("F:\\svm\\semeval14.test"));
		BufferedWriter bw_test = new BufferedWriter(fw_test);
	    
		ct = 0;
		
		while(true){
			if(ct % 1000 == 0) System.out.println("progress (test) : " + ct);

			line = br_test.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length == 2){
				
				String fstr = "";
				
				String tweetSentiment = splitStr[0];
				String tweetTest= splitStr[1];

				List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweetTest);

				String tweet = "";
				String tweetPOS = "";
				
				for (TaggedToken token : taggedTokens) {
					String word = token.token;
					String tag = token.tag;
					
					tweet += word;
					tweet += " ";
					
					tweetPOS += tag;
					tweetPOS += " ";
				
				}

				fstr = featureGen.getFeatures(tweet, tweetPOS, tweetSentiment);
				
				bw_test.write(fstr.trim());
				bw_test.newLine();

			}

			ct++;
		}
		
		bw_test.close();
		fw_test.close();
		
		br_test.close();
		fr_test.close();

		System.out.println("complete");

	}


}
