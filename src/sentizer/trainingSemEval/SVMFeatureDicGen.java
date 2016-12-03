package sentizer.trainingSemEval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SVMFeatureDicGen {


	public static void main(String args[]) throws Exception{

		String pathTraining = "F:\\Semeval2014\\training\\finalTrainingInput.txt";
		String pathTrainingOriginal = "F:\\Semeval2014\\training\\trainingDatasetComplete.txt";

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

		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);

		//Just for new input format
		//Set<String> unigramSet = new HashSet<String>();
		
	    SortedSet<String> unigramSet = new TreeSet<String>();
	    SortedSet<String> bigramSet = new TreeSet<String>();
	    SortedSet<String> trigramSet = new TreeSet<String>();
	    SortedSet<String> fourgramSet = new TreeSet<String>();

	    SortedSet<String> charSet_n3 = new TreeSet<String>();
	    SortedSet<String> charSet_n4 = new TreeSet<String>();
	    SortedSet<String> charSet_n5 = new TreeSet<String>();

	    
	    
		while(true){
			if(ct % 1000 == 0) System.out.println("progress : " + ct);

			line = br.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length >= 4){
				String tweetID= splitStr[0];
				String tweet= splitStr[1];
				String tweetPOS= splitStr[2];
				String tweetSentiment = splitStr[3];

				/*
				if(mapTweetID.containsKey(tweetID)){
					tweet = mapTweetID.get(tweetID);
				}
				*/

				String[] tweetTermList = tweet.split(" ");
				String[] tweetTermPOSList = tweetPOS.split(" ");
				
				String uniStr = "";

				for (int i=0; i<tweetTermList.length; i++) {
					
					if(i>=1){
						bigramSet.add(tweetTermList[i-1] + " " +tweetTermList[i]);
					}
					
					if(i>=2){
						trigramSet.add(tweetTermList[i-2] + " " +tweetTermList[i-1] + " " +tweetTermList[i]);
					}
					
					if(i>=3){
						fourgramSet.add(tweetTermList[i-3] + " " +tweetTermList[i-2] + " " +tweetTermList[i-1] + " " +tweetTermList[i]);
					}

					if(!tweetTermList[i].trim().isEmpty()){
						if(tweetTermList[i].length() >= 3 ){
							charSet_n3.add(tweetTermList[i].substring(0, 3));
							if(tweetTermList[i].length() >= 4 ){
								charSet_n4.add(tweetTermList[i].substring(0, 4));
								if(tweetTermList[i].length() >= 5 ){
									charSet_n5.add(tweetTermList[i].substring(0, 5));
								}
							}
						}
						
						
						unigramSet.add(tweetTermList[i]);
						uniStr += tweetTermList[i];
						uniStr += " ";
						
						if(tweetTermPOSList[i].equals("U") || tweetTermPOSList[i].equals("@")){
						}else{
							//unigramSet.add(tweetTermList[i]);
							//uniStr += tweetTermList[i];
							
						}
					}

				}
				
				
				

			}

			ct++;
		}


		int dic_ct = 1;
		
		FileWriter fw_uni = new FileWriter(new File("F:\\svm\\dic\\unigram3.dic"));
		BufferedWriter bw_uni = new BufferedWriter(fw_uni);

		Iterator<String> iterator = unigramSet.iterator(); 

		while (iterator.hasNext()){
			bw_uni.write(dic_ct + "\t" + iterator.next());
			bw_uni.newLine();
			dic_ct++;
		}

		bw_uni.close();
		fw_uni.close();
		

		FileWriter fw_bi = new FileWriter(new File("F:\\svm\\dic\\bigram3.dic"));
		BufferedWriter bw_bi = new BufferedWriter(fw_bi);
		
		Iterator<String> iterator2 = bigramSet.iterator(); 

		while (iterator2.hasNext()){
			bw_bi.write(dic_ct + "\t" + iterator2.next());
			bw_bi.newLine();
			dic_ct++;
		}

		bw_bi.close();
		fw_bi.close();

		FileWriter fw_tri = new FileWriter(new File("F:\\svm\\dic\\trigram3.dic"));
		BufferedWriter bw_tri = new BufferedWriter(fw_tri);
		
		Iterator<String> iterator3 = trigramSet.iterator(); 

		while (iterator3.hasNext()){
			bw_tri.write(dic_ct + "\t" + iterator3.next());
			bw_tri.newLine();
			dic_ct++;
		}

		bw_tri.close();
		fw_tri.close();

		FileWriter fw_four = new FileWriter(new File("F:\\svm\\dic\\fourgram3.dic"));
		BufferedWriter bw_four = new BufferedWriter(fw_four);
		
		Iterator<String> iterator4 = fourgramSet.iterator(); 

		while (iterator4.hasNext()){
			bw_four.write(dic_ct + "\t" + iterator4.next());
			bw_four.newLine();
			dic_ct++;
		}

		bw_four.close();
		fw_four.close();
		
		FileWriter fw_c3 = new FileWriter(new File("F:\\svm\\dic\\chargram3.dic"));
		BufferedWriter bw_c3 = new BufferedWriter(fw_c3);
		
		Iterator<String> iterator_c3 = charSet_n3.iterator(); 

		while (iterator_c3.hasNext()){
			bw_c3.write(dic_ct + "\t" + iterator_c3.next());
			bw_c3.newLine();
			dic_ct++;
		}

		bw_c3.close();
		fw_c3.close();
		
		
		FileWriter fw_c4 = new FileWriter(new File("F:\\svm\\dic\\chargram4.dic"));
		BufferedWriter bw_c4 = new BufferedWriter(fw_c4);
		
		Iterator<String> iterator_c4 = charSet_n4.iterator(); 

		while (iterator_c4.hasNext()){
			bw_c4.write(dic_ct + "\t" + iterator_c4.next());
			bw_c4.newLine();
			dic_ct++;
		}

		bw_c4.close();
		fw_c4.close();
		
		
		FileWriter fw_c5 = new FileWriter(new File("F:\\svm\\dic\\chargram5.dic"));
		BufferedWriter bw_c5 = new BufferedWriter(fw_c5);
		
		Iterator<String> iterator_c5 = charSet_n5.iterator(); 

		while (iterator_c5.hasNext()){
			bw_c5.write(dic_ct + "\t" + iterator_c5.next());
			bw_c5.newLine();
			dic_ct++;
		}

		bw_c5.close();
		fw_c5.close();
		
		
		FileWriter fw_uni_neg = new FileWriter(new File("F:\\svm\\dic\\unigram3_neg.dic"));
		BufferedWriter bw_uni_neg = new BufferedWriter(fw_uni_neg);

		Iterator<String> iterator_neg = unigramSet.iterator(); 

		while (iterator_neg.hasNext()){
			bw_uni_neg.write(dic_ct + "\t" + iterator_neg.next()+"_NEG");
			bw_uni_neg.newLine();
			dic_ct++;
		}

		bw_uni_neg.close();
		fw_uni_neg.close();

		
		
		
		

		br.close();
		fr.close();

		System.out.println("complete : "+ct);

	}


}
