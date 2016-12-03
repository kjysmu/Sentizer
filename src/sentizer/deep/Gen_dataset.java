package sentizer.deep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import sentizer.util.Tagger;
import sentizer.util.Tagger.TaggedToken;

public class Gen_dataset {
	
	public static void main(String args[]) throws Exception {
		
		String path = "F:\\svm\\dataset\\finalTrainingInput.txt";
		
		String pathPos = "F:\\dcnn_project\\CNN_sentence-master\\CNN_sentence-master\\SemEvalPos.train";
		String pathNeg = "F:\\dcnn_project\\CNN_sentence-master\\CNN_sentence-master\\SemEvalNeg.train";
		String pathNeu = "F:\\dcnn_project\\CNN_sentence-master\\CNN_sentence-master\\SemEvalNeu.train";
		
		FileReader fr = new FileReader(new File(path));
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fwPos = new FileWriter(new File(pathPos));
		BufferedWriter bwPos = new BufferedWriter(fwPos);
		
		FileWriter fwNeg = new FileWriter(new File(pathNeg));
		BufferedWriter bwNeg = new BufferedWriter(fwNeg);
		
		FileWriter fwNeu = new FileWriter(new File(pathNeu));
		BufferedWriter bwNeu = new BufferedWriter(fwNeu);
		
		String line = "";
		
		while(true){
	
			line = br.readLine();
			if(line == null) break;
			
			String[] splitStr = line.split("\t");
			
			if(splitStr.length == 4){
				
				String tweetID= splitStr[0];
				String tweet = splitStr[1];
				String POStag = splitStr[2];
				String sentiment= splitStr[3];
				
				if(sentiment.equals("positive")){
					
					bwPos.write(tweet + " ");
					bwPos.newLine();
					
				}else if(sentiment.equals("negative")){
					
					bwNeg.write(tweet + " ");
					bwNeg.newLine();
					
				}else if(sentiment.equals("neutral")){
				
					bwNeu.write(tweet + " ");
					bwNeu.newLine();
					
				}
				
			}
			
			
		}
		
		br.close();
		fr.close();
		
		bwPos.close();
		fwPos.close();
		
		bwNeg.close();
		fwNeg.close();
		
		bwNeu.close();
		fwNeu.close();
		
		
		System.out.println("Complete1");
		
		
		FileReader fr_test = new FileReader(new File("F:\\svm\\dataset\\semeval.test"));
		BufferedReader br_test = new BufferedReader(fr_test);

		FileWriter fw_test = new FileWriter(new File("F:\\dcnn_project\\CNN_sentence-master\\CNN_sentence-master\\SemEval.test"));
		BufferedWriter bw_test = new BufferedWriter(fw_test);
	    
		int ct = 0;
		
		String modelFilename = "D:\\project2nd\\dataset_sentizer\\model.20120919";

		Tagger tagger;		
		tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		while(true){

			line = br_test.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length == 2){
				
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
				
				}

				bw_test.write(tweet);
				bw_test.newLine();

			}

		}
		
		bw_test.close();
		fw_test.close();
		
		br_test.close();
		fr_test.close();
		
		System.out.println("Complete2");
		
	}
	

}
