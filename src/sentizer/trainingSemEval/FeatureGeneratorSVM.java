package sentizer.trainingSemEval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.io.InputStreamReader;

import sentizer.training.FileFunction;
import sentizer.util.DoubleValueComparator;
import sentizer.util.IntValueComparator;
import sentizer.util.Tagger;
import sentizer.util.Word2vecReader;

// Function related to FILE

public class FeatureGeneratorSVM {

	String pathLex_NRC_hash_uni = "F:\\lexicon\\NRC_hashtag\\unigrams.txt";
	String pathLex_NRC_hash_bi = "F:\\lexicon\\NRC_hashtag\\bigrams.txt";
	String pathLex_NRC_hash_pairs = "F:\\lexicon\\NRC_hashtag\\pairs.txt";

	String pathLex_SEN_uni = "F:\\lexicon\\Sentiment140\\unigrams.txt";
	String pathLex_SEN_bi = "F:\\lexicon\\Sentiment140\\bigrams.txt";
	String pathLex_SEN_pairs = "F:\\lexicon\\Sentiment140\\pairs.txt";

	String path_brownCluster = "F:\\browncluster\\50mpaths2.txt";
	String path_emoticon = "F:\\lexicon\\Emoticon\\EmoticonSentimentLexicon.txt";

	String path_negTFIDF = "F:\\sentiment140\\neg_tfidf.txt";
	String path_posTFIDF = "F:\\sentiment140\\pos_tfidf.txt";
	String path_neuTFIDF = "F:\\sentiment140\\neu_tfidf.txt";

	String path_negTF = "F:\\sentiment140\\training_neg.txt";
	String path_posTF = "F:\\sentiment140\\training_pos.txt";
	String path_neuTF = "F:\\sentiment140\\training_neu.txt";

	String pathDic = "F:\\svm\\dic\\unigram3.dic";
	String pathDic2 = "F:\\svm\\dic\\bigram3.dic";
	String pathDic3 = "F:\\svm\\dic\\trigram3.dic";
	String pathDic4 = "F:\\svm\\dic\\fourgram3.dic";

	String pathDic5 = "F:\\svm\\dic\\chargram3.dic";
	String pathDic6 = "F:\\svm\\dic\\chargram4.dic";
	String pathDic7 = "F:\\svm\\dic\\chargram5.dic";

	String pathDic8 = "F:\\svm\\dic\\unigram3_neg.dic";

	// w2v for pos and neg in Sent140 dataset
	String pathDicW2V = "F:\\w2v\\training_w2v_new.txt";

	Map<String, Double> map_NRC_hash_uni;
	Map<String, Double> map_NRC_hash_bi;
	Map<String, Double> map_NRC_hash_pairs;

	Map<String, Double> map_SEN_uni;
	Map<String, Double> map_SEN_bi;
	Map<String, Double> map_SEN_pairs;

	Map<String, Double> map_negTFIDF;
	Map<String, Double> map_posTFIDF;
	Map<String, Double> map_neuTFIDF;

	Map<String, Double> map_negTF;
	Map<String, Double> map_posTF;
	Map<String, Double> map_neuTF;

	Map<String, String> map_bCluster;
	Map<String, Set<String>> map_Cluster;

	Map<String, String> map_emoticon;

	Map<String,String> map_uni;
	Map<String,String> map_bi;
	Map<String,String> map_tri;
	Map<String,String> map_four;

	Map<String,String> map_char3;
	Map<String,String> map_char4;
	Map<String,String> map_char5;

	Map<String,String> map_uni_neg;

	double[] w2vPos;
	double[] w2vNeg;

	Word2vecReader w2v;

	static String[] negation_cues = 
		{ "aint","cannot","cant","darent","didnt",
				"doesnt","dont","hadnt","hardly","hasnt",
				"havent","havnt","isnt","lack","lacking",
				"lacks","neither","never","no","nobody",
				"none","nor","not","nothing","nowhere",
				"mightnt","mustnt","neednt","oughtnt","shant",
				"shouldnt","wasnt","without","wouldnt" };

	public FeatureGeneratorSVM() throws Exception{

		w2v = new Word2vecReader();

		map_uni = FileFunction.readMapStrStr(pathDic,true);
		map_bi = FileFunction.readMapStrStr(pathDic2,true);
		map_tri = FileFunction.readMapStrStr(pathDic3,true);
		map_four = FileFunction.readMapStrStr(pathDic4,true);

		map_char3 = FileFunction.readMapStrStr(pathDic5,true);
		map_char4 = FileFunction.readMapStrStr(pathDic6,true);
		map_char5 = FileFunction.readMapStrStr(pathDic7,true);

		map_uni_neg = FileFunction.readMapStrStr(pathDic8,true);

		map_NRC_hash_uni= FileFunction.readMapStrDou(pathLex_NRC_hash_uni);
		map_NRC_hash_bi= FileFunction.readMapStrDou(pathLex_NRC_hash_bi);
		map_NRC_hash_pairs= FileFunction.readMapStrDou(pathLex_NRC_hash_pairs);

		map_SEN_uni= FileFunction.readMapStrDou(pathLex_SEN_uni);
		map_SEN_bi= FileFunction.readMapStrDou(pathLex_SEN_bi);
		map_SEN_pairs= FileFunction.readMapStrDou(pathLex_SEN_pairs);

		map_negTFIDF = FileFunction.readMapStrDou(path_negTFIDF);
		map_posTFIDF = FileFunction.readMapStrDou(path_posTFIDF);
		map_neuTFIDF = FileFunction.readMapStrDou(path_neuTFIDF);

		map_negTF = FileFunction.readMapStrDou(path_negTF);
		map_posTF = FileFunction.readMapStrDou(path_posTF);
		map_neuTF = FileFunction.readMapStrDou(path_neuTF);

		//map_bCluster = FileFunction.readMapStrStr(path_brownCluster);

		map_Cluster = new HashMap<String, Set<String>>();
		map_bCluster = new HashMap<String, String>();

		map_emoticon = FileFunction.readMapStrStr(path_emoticon);

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(new File ( path_brownCluster )), "UTF8"));
		String line = "";
		
		while ((line = reader.readLine()) != null) {
			StringTokenizer token = new StringTokenizer(line, "\t");
			if(token.countTokens() >= 2){
				String token1= token.nextToken();
				String token2= token.nextToken();
				map_bCluster.put(token2, token1);
			}
		}
		reader.close();
		
		w2vPos = new double[200];
		w2vNeg = new double[200];


		FileReader fr_w2v = new FileReader(pathDicW2V);
		BufferedReader br_w2v = new BufferedReader(fr_w2v);
		
		line = "";
		while ((line = br_w2v.readLine()) != null) {
			String[] vStr = line.split(" ");
			if( vStr[0].equals("Positive") ){
				for(int i=0; i<200; i++){
					w2vPos[i] = Double.parseDouble( vStr[i+1] );
				}
			}else{
				for(int i=0; i<200; i++){
					w2vNeg[i] = Double.parseDouble( vStr[i+1] );
				}
			}
			
		}
		br_w2v.close();
		fr_w2v.close();

	}

	public boolean isUpperCase(String s)
	{
		for (int i=0; i<s.length(); i++)
		{
			if (!Character.isUpperCase(s.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}

	public String getFeatures(String tweet, String tweetPOS, String sentiment)
	{

		String fstr = "";
		if(sentiment.equals("positive")){
			fstr += "1";
		}else if(sentiment.equals("negative")){
			fstr += "2";
		}else if(sentiment.equals("neutral")){
			fstr += "3";
		}
		fstr += " ";
		String[] tweetTermList = tweet.split(" ");
		String[] tweetTermPOSList = tweetPOS.split(" ");
		

		/*
		for (int i=0; i<tweetTermList.length; i++) {
			tweetTermList[i] = tweetTermList[i].toLowerCase();
		}*/

		Map<Integer, Integer> fmap = new HashMap<Integer, Integer>();

		Boolean isNegContext = false;
		Boolean isNegator = false;


		for (int i=0; i<tweetTermList.length; i++) {
			String term = tweetTermList[i];
			String POS = tweetTermPOSList[i];

			isNegator = false;
			for(String negword : negation_cues){
				if(negword.equals(term.toLowerCase()) || term.toLowerCase().endsWith("n't") ){
					isNegContext = true;
					isNegator = true;
					break;
				}
			}

			if(POS.equals(",")){
				isNegContext = false;
			}
			if(i>=1){
				String biterm = tweetTermList[i-1] + " " +tweetTermList[i];

				if(map_bi.containsKey(biterm)){
					String findex = map_bi.get(biterm);
					if(fmap.containsKey(findex)){
						fmap.put(Integer.parseInt(findex), 1);
					}else{
						fmap.put(Integer.parseInt(findex), 1);
					}
				}
			}

			if(i>=2){
				String triterm = tweetTermList[i-2] + " " +tweetTermList[i-1] + " " +tweetTermList[i];
				if(map_tri.containsKey(triterm)){
					String findex = map_tri.get(triterm);
					if(fmap.containsKey(findex)){
						fmap.put(Integer.parseInt(findex), 1);
					}else{
						fmap.put(Integer.parseInt(findex), 1);
					}
				}

			}

			if(i>=3){
				String fourterm = tweetTermList[i-3]+" "+tweetTermList[i-2] + " " +tweetTermList[i-1] + " " +tweetTermList[i];
				if(map_four.containsKey(fourterm)){
					String findex = map_four.get(fourterm);
					if(fmap.containsKey(findex)){
						fmap.put(Integer.parseInt(findex), 1);
					}else{
						fmap.put(Integer.parseInt(findex), 1);
					}
				}
			}

			if(!term.trim().isEmpty()){

				if(term.length() >= 3 ){
					String term3 = term.substring(0, 3);

					if(map_char3.containsKey(term3)){
						String findex = map_char3.get(term3);
						if(fmap.containsKey(findex)){
							fmap.put(Integer.parseInt(findex), 1);
						}else{
							fmap.put(Integer.parseInt(findex), 1);
						}
					}

					if(term.length() >= 4 ){
						String term4 = term.substring(0, 4);

						if(map_char4.containsKey(term4)){
							String findex = map_char4.get(term4);
							if(fmap.containsKey(findex)){
								fmap.put(Integer.parseInt(findex), 1);
							}else{
								fmap.put(Integer.parseInt(findex), 1);
							}
						}

						if(term.length() >= 5 ){
							String term5 = term.substring(0, 5);

							if(map_char5.containsKey(term5)){
								String findex = map_char5.get(term5);
								if(fmap.containsKey(findex)){
									fmap.put(Integer.parseInt(findex), 1);
								}else{
									fmap.put(Integer.parseInt(findex), 1);
								}
							}

						}


					}
				}

				if(map_uni.containsKey(term)){
					String findex = map_uni.get(term);
					if(fmap.containsKey(findex)){
						fmap.put(Integer.parseInt(findex), 1);
					}else{
						fmap.put(Integer.parseInt(findex), 1);
					}
				}


				if(map_uni.containsKey(term)){
					String findex = map_uni.get(term);
					if(fmap.containsKey(findex)){
						fmap.put(Integer.parseInt(findex), 1);
					}else{
						fmap.put(Integer.parseInt(findex), 1);
					}
				}

				/*
				if(isNegContext && !isNegator && ( POS.equals("V") || POS.equals("A") || POS.equals("R") ) ){
					String termNeg = term + "_NEG";
					if(map_uni_neg.containsKey(termNeg)){
						String findex = map_uni_neg.get(termNeg);
						if(fmap.containsKey(findex)){
							fmap.put(Integer.parseInt(findex), 1);
						}else{
							fmap.put(Integer.parseInt(findex), 1);
						}
					}
				}else{

				}
				 */

			}
		}

		SortedSet<Integer> keys = new TreeSet<Integer>(fmap.keySet());
		for (int key : keys) { 
			int value = fmap.get(key);
			fstr += key + ":" + value;
			fstr += " ";
		}

		Map<String,Double> tweetMap = new HashMap<String, Double>();
		List<String> tweetTermList_bi = new ArrayList<String>();

		for (int i=0; i<tweetTermList.length; i++) {
			if(i!=0){
				tweetTermList_bi.add(tweetTermList[i-1]+" "+tweetTermList[i]);		
			}
		}

		Set<String> tweetTermList_pairs = new HashSet<String>();

		for (int i=0; i<tweetTermList.length; i++) {
			for (int j=i+1; j<tweetTermList.length; j++) {
				tweetTermList_pairs.add(tweetTermList[i]+"---"+tweetTermList[j]);		
				tweetTermList_pairs.add(tweetTermList[j]+"---"+tweetTermList[i]);	
			}
		}

		for (String biTerm : tweetTermList_bi) {
			for (String biTerm2 : tweetTermList_bi) {
				if(!biTerm.equals(biTerm2)){
					tweetTermList_pairs.add(biTerm+"---"+biTerm2);	
					tweetTermList_pairs.add(biTerm2+"---"+biTerm);	
				}
			}	
		}

		for (int i=0; i<tweetTermList.length; i++) {
			for (String biTerm : tweetTermList_bi) {
				tweetTermList_pairs.add(biTerm+"---"+tweetTermList[i]);	
				tweetTermList_pairs.add(tweetTermList[i]+"---"+biTerm);	
			}
		}

		for(Map.Entry<String, Set<String>> entry : map_Cluster.entrySet()){
			String clusterID = entry.getKey();
			Set<String> clusterElement = entry.getValue();
			for (int i=0; i<tweetTermList.length; i++) {
				if(clusterElement.contains(tweetTermList[i])){
				}
			}
		}

		//LexiconFeature
		int totalCountPos = 0;
		int totalCountNeg = 0;
		double sumScorePos = 0;
		double sumScoreNeg = 0;
		double maxScorePos = 0;
		double maxScoreNeg = 0;
		double lastScorePos = 0;
		double lastScoreNeg = 0;

		int totalCountPos_bi = 0;
		int totalCountNeg_bi = 0;
		double sumScorePos_bi = 0;
		double sumScoreNeg_bi = 0;
		double maxScorePos_bi = 0;
		double maxScoreNeg_bi = 0;
		double lastScorePos_bi = 0;
		double lastScoreNeg_bi = 0;

		int totalCountPos_pr = 0;
		int totalCountNeg_pr = 0;
		double sumScorePos_pr = 0;
		double sumScoreNeg_pr = 0;
		double maxScorePos_pr = 0;
		double maxScoreNeg_pr = 0;
		double lastScorePos_pr = 0;
		double lastScoreNeg_pr = 0;


		//LexiconFeature2
		int totalCountPos2 = 0;
		int totalCountNeg2 = 0;
		double sumScorePos2 = 0;
		double sumScoreNeg2 = 0;
		double maxScorePos2 = 0;
		double maxScoreNeg2 = 0;
		double lastScorePos2 = 0;
		double lastScoreNeg2 = 0;

		int totalCountPos2_bi = 0;
		int totalCountNeg2_bi = 0;
		double sumScorePos2_bi = 0;
		double sumScoreNeg2_bi = 0;
		double maxScorePos2_bi = 0;
		double maxScoreNeg2_bi = 0;
		double lastScorePos2_bi = 0;
		double lastScoreNeg2_bi = 0;

		int totalCountPos2_pr = 0;
		int totalCountNeg2_pr = 0;
		double sumScorePos2_pr = 0;
		double sumScoreNeg2_pr = 0;
		double maxScorePos2_pr = 0;
		double maxScoreNeg2_pr = 0;
		double lastScorePos2_pr = 0;
		double lastScoreNeg2_pr = 0;

		// AllCaps
		int totalAllCaps = 0;

		// # of Negation
		int totalNeg = 0;

		// POS features

		int totalPOS_N = 0;
		int totalPOS_O = 0;
		int totalPOS_PN = 0;
		int totalPOS_S = 0;
		int totalPOS_Z = 0;

		
		int totalPOS_V = 0;
		int totalPOS_A = 0;
		int totalPOS_R = 0;
		int totalPOS_IJ = 0;
		
		int totalPOS_D = 0;
		int totalPOS_P = 0;
		int totalPOS_CC = 0;
		int totalPOS_T = 0;
		int totalPOS_X = 0;
		
		
		int totalPOS_hash = 0;
		int totalPOS_mention = 0;
		int totalPOS_DM = 0;
		int totalPOS_U = 0;
		int totalPOS_E = 0;

		int totalPOS_NU = 0;
		int totalPOS_PU = 0;
		int totalPOS_G = 0;

		int totalPOS_L = 0;
		int totalPOS_M = 0;
		int totalPOS_Y = 0;
		
		int totalPosEmo = 0;
		int totalNegEmo = 0;

		int lastEmo = 0;

		String clusterList = "";

		double[] wvecTweet = new double[200];

		double[] wvecTweet_min = new double[200];
		double[] wvecTweet_max = new double[200];


		int wvec_ct = 0;

		for (int i=0; i<tweetTermList.length; i++) {

			String tweetTerm = tweetTermList[i];
			String tweetTermPOS = tweetTermPOSList[i];

			tweetTerm = tweetTerm.toLowerCase();

			if(w2v.containWords(tweetTerm)){
				double[] wvec = w2v.getWordRepresentation(tweetTerm);
				for(int j=0;j<200;j++){
					wvecTweet[j] += wvec[j];
					if(wvec_ct == 0){
						wvecTweet_min[j] = wvec[j];
						wvecTweet_max[j] = wvec[j];
					}else{
						if(wvecTweet_min[j] > wvec[j]) wvecTweet_min[j] = wvec[j];
						if(wvecTweet_min[j] < wvec[j]) wvecTweet_max[j] = wvec[j];
					}



				}
				wvec_ct++;
			}

			if( map_bCluster.containsKey(tweetTerm) ){
				clusterList += map_bCluster.get(tweetTerm);
				clusterList += " ";
			}

			if( map_emoticon.containsKey(tweetTerm) ){
				String eStr = map_emoticon.get(tweetTerm);
				if(eStr.equals("1")){
					//totalPosEmo++;
					totalPosEmo = 1;
					lastEmo = 1;
				}else if(eStr.equals("-1")){
					//totalNegEmo++;
					totalNegEmo = 1;
					lastEmo = -1;
				}else{
					lastEmo = 0;
				}
			}

			if(tweetTermPOS.equals("N")){
				totalPOS_N++;
			}else if(tweetTermPOS.equals("O")){
				totalPOS_O++;
			}else if(tweetTermPOS.equals("^")){
				totalPOS_PN++;
			}else if(tweetTermPOS.equals("S")){
				totalPOS_S++;
			}else if(tweetTermPOS.equals("Z")){
				totalPOS_Z++;
			}else if(tweetTermPOS.equals("V")){
				totalPOS_V++;
			}else if(tweetTermPOS.equals("A")){
				totalPOS_A++;
			}else if(tweetTermPOS.equals("R")){
				totalPOS_R++;
			}else if(tweetTermPOS.equals("!")){
				totalPOS_IJ++;
			}else if(tweetTermPOS.equals("D")){
				totalPOS_D++;
			}else if(tweetTermPOS.equals("P")){
				totalPOS_P++;
			}else if(tweetTermPOS.equals("&")){
				totalPOS_CC++;
			}else if(tweetTermPOS.equals("T")){
				totalPOS_T++;
			}else if(tweetTermPOS.equals("X")){
				totalPOS_X++;
			}else if(tweetTermPOS.equals("#")){
				totalPOS_hash++;
			}else if(tweetTermPOS.equals("@")){
				totalPOS_mention++;
			}else if(tweetTermPOS.equals("~")){
				totalPOS_DM++;
			}else if(tweetTermPOS.equals("U")){
				totalPOS_U++;
			}else if(tweetTermPOS.equals("E")){
				totalPOS_E++;
			}else if(tweetTermPOS.equals("$")){
				totalPOS_NU++;
			}else if(tweetTermPOS.equals(",")){
				totalPOS_PU++;
			}else if(tweetTermPOS.equals("G")){
				totalPOS_G++;
			}else if(tweetTermPOS.equals("L")){
				totalPOS_L++;
			}else if(tweetTermPOS.equals("M")){
				totalPOS_M++;
			}else if(tweetTermPOS.equals("Y")){
				totalPOS_Y++;
			}
			

			if(tweetMap.containsKey(tweetTerm)){
				tweetMap.put(tweetTerm, tweetMap.get(tweetTerm) + 1.0);
			}else{
				tweetMap.put(tweetTerm, 1.0);
			}

			if( isUpperCase(tweetTerm) ){
				totalAllCaps++;
			}

			if(map_NRC_hash_uni.containsKey(tweetTerm)){
				double score = map_NRC_hash_uni.get(tweetTerm);
				if(score > 0.0){
					totalCountPos++;
					sumScorePos += score;
					if(score > maxScorePos ){
						maxScorePos = score;
					}
					lastScorePos = score;
				}else{
					totalCountNeg++;
					sumScoreNeg += Math.abs(score);
					if(Math.abs(score) > maxScoreNeg ){
						maxScoreNeg = Math.abs(score);
					}
					lastScoreNeg = Math.abs(score);
				}

			}

			if(map_SEN_uni.containsKey(tweetTerm)){

				double score = map_SEN_uni.get(tweetTerm);
				if(score > 0.0){
					totalCountPos2++;
					sumScorePos2 += score;
					if(score > maxScorePos2 ){
						maxScorePos2 = score;
					}
					lastScorePos2 = score;
				}else{
					totalCountNeg2++;
					sumScoreNeg2 += Math.abs(score);
					if(Math.abs(score) > maxScoreNeg2 ){
						maxScoreNeg2 = Math.abs(score);
					}
					lastScoreNeg2 = Math.abs(score);
				}
			}
		}

		if( wvec_ct >= 1){
			for(int i=0;i<200;i++){
				wvecTweet[i] /= wvec_ct;
			}
		}

		for (String biTerm : tweetTermList_bi) {
			if(map_NRC_hash_bi.containsKey(biTerm)){
				double score = map_NRC_hash_bi.get(biTerm);
				if(score > 0.0){
					totalCountPos_bi++;
					sumScorePos_bi += score;
					if(score > maxScorePos_bi ){
						maxScorePos_bi = score;
					}
					lastScorePos_bi = score;
				}else{
					totalCountNeg_bi++;
					sumScoreNeg_bi += Math.abs(score);
					if(Math.abs(score) > maxScoreNeg_bi ){
						maxScoreNeg_bi = Math.abs(score);
					}
					lastScoreNeg_bi = Math.abs(score);
				}
			}

			if(map_SEN_bi.containsKey(biTerm)){

				double score = map_SEN_bi.get(biTerm);
				if(score > 0.0){
					totalCountPos2_bi++;
					sumScorePos2_bi += score;
					if(score > maxScorePos2_bi ){
						maxScorePos2_bi = score;
					}
					lastScorePos2_bi = score;
				}else{
					totalCountNeg2_bi++;
					sumScoreNeg2_bi += Math.abs(score);
					if(Math.abs(score) > maxScoreNeg2_bi ){
						maxScoreNeg2_bi = Math.abs(score);
					}
					lastScoreNeg2_bi = Math.abs(score);
				}
			}
		}

		for (String prTerm : tweetTermList_pairs) {
			if(map_NRC_hash_pairs.containsKey(prTerm)){
				double score = map_NRC_hash_pairs.get(prTerm);
				if(score > 0.0){
					totalCountPos_pr++;
					sumScorePos_pr += score;
					if(score > maxScorePos_pr ){
						maxScorePos_pr = score;
					}
					lastScorePos_pr = score;
				}else{
					totalCountNeg_pr++;
					sumScoreNeg_pr += Math.abs(score);
					if(Math.abs(score) > maxScoreNeg_pr ){
						maxScoreNeg_pr = Math.abs(score);
					}
					lastScoreNeg_pr = Math.abs(score);
				}
			}

			if(map_SEN_pairs.containsKey(prTerm)){
				double score = map_SEN_pairs.get(prTerm);
				if(score > 0.0){
					totalCountPos2_pr++;
					sumScorePos2_pr += score;
					if(score > maxScorePos2_pr ){
						maxScorePos2_pr = score;
					}
					lastScorePos2_pr = score;
				}else{
					totalCountNeg2_pr++;
					sumScoreNeg2_pr += Math.abs(score);
					if(Math.abs(score) > maxScoreNeg2_pr ){
						maxScoreNeg2_pr = Math.abs(score);
					}
					lastScoreNeg2_pr = Math.abs(score);
				}
			}
		}

		//int findex = 533752;
		
		
		
		int findex = 564635;

		// AutoLex (NRC, SEN) Unigram
		findex++;
		fstr += findex+":" + Integer.toString(totalCountPos) + " ";findex++;
		fstr += findex+":"+ String.format("%.3f", sumScorePos) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScorePos) + " ";findex++;
		
		fstr += findex+":" + Integer.toString(totalCountNeg) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScoreNeg) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScoreNeg) + " ";findex++;
		
		fstr += findex+":" + Integer.toString(totalCountPos2) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScorePos2) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScorePos2) + " ";findex++;
		
		fstr += findex+":" + Integer.toString(totalCountNeg2) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScoreNeg2) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScoreNeg2) + " ";findex++;
		

		// AutoLex (NRC, SEN) Bigram

		fstr += findex+":" + Integer.toString(totalCountPos_bi) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScorePos_bi) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScorePos_bi) + " ";findex++;
		
		fstr += findex+":" + Integer.toString(totalCountNeg_bi) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScoreNeg_bi) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScoreNeg_bi) + " ";findex++;

		fstr += findex+":" + Integer.toString(totalCountPos2_bi) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScorePos2_bi) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScorePos2_bi) + " ";findex++;

		fstr += findex+":" + Integer.toString(totalCountNeg2_bi) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScoreNeg2_bi) + " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScoreNeg2_bi) + " ";findex++;

		
		// AutoLex (NRC, SEN) Pair

		fstr += findex+":" + Integer.toString(totalCountPos_pr)+ " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScorePos_pr)+ " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScorePos_pr)+ " ";findex++;

		fstr += findex+":" + Integer.toString(totalCountNeg_pr)+ " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScoreNeg_pr)+ " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScoreNeg_pr)+ " ";findex++;

		fstr += findex+":" + Integer.toString(totalCountPos2_pr)+ " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScorePos2_pr)+ " ";findex++;
     	fstr += findex+":" + String.format("%.3f", maxScorePos2_pr)+ " ";findex++;
		
		fstr += findex+":" + Integer.toString(totalCountNeg2_pr)+ " ";findex++;
		fstr += findex+":" + String.format("%.3f", sumScoreNeg2_pr)+ " ";findex++;
		fstr += findex+":" + String.format("%.3f", maxScoreNeg2_pr)+ " ";findex++;

		// POS
		fstr += findex+":" + Integer.toString(totalPOS_N)+ " ";findex++;
		fstr += findex+":" + Integer.toString(totalPOS_O)+ " ";findex++;
		fstr += findex+":" + Integer.toString(totalPOS_V)+ " ";findex++;
		fstr += findex+":" + Integer.toString(totalPOS_A)+ " ";findex++;
		fstr += findex+":" + Integer.toString(totalPOS_R)+ " ";findex++;
		fstr += findex+":" + Integer.toString(totalPOS_hash)+ " ";findex++;
		fstr += findex+":" + Integer.toString(totalPOS_E)+ " ";findex++;
		fstr += findex+":" + Integer.toString(totalPOS_PN)+ " ";findex++;

		// EMO
		fstr += findex+":" + Integer.toString(totalPosEmo)+ " ";findex++;
		fstr += findex+":" + Integer.toString(totalNegEmo)+ " ";findex++;
		fstr += findex+":" + Integer.toString(lastEmo)+ " ";findex++;

		for(int i=0;i<200;i++){
			fstr += findex+":"+String.format("%.8f", wvecTweet[i])+ " ";findex++;
		}

		return fstr.trim();




	}

	public double ComputeNorm(Map<String, Double> termFrequencies){

		double norm = 0.0;

		for(Map.Entry<String, Double> entry : termFrequencies.entrySet()){
			norm += entry.getValue() * entry.getValue();
		}
		return Math.sqrt(norm);

	}

	public double ComputeCosineSimilarity(Map<String, Double> termFrequencies1, Map<String, Double> termFrequencies2){

		if(termFrequencies1.isEmpty() || termFrequencies2.isEmpty()) return 0.0;

		double set1Norm = ComputeNorm(termFrequencies1);
		double set2Norm = ComputeNorm(termFrequencies2);

		double dotProduct = 0.0;
		for(Map.Entry<String, Double> termFrequency1 : termFrequencies1.entrySet()){
			if(termFrequencies2.containsKey(termFrequency1.getKey())){
				dotProduct += termFrequency1.getValue() * termFrequencies2.get(termFrequency1.getKey());
			}
		}
		return dotProduct / (set1Norm * set2Norm);

	}
	
    public double cosineSimilarity(double[] docVector1, double[] docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
        {
            dotProduct += docVector1[i] * docVector2[i];  //a.b
            magnitude1 += Math.pow(docVector1[i], 2);  //(a^2)
            magnitude2 += Math.pow(docVector2[i], 2); //(b^2)
        }

        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)

        if (magnitude1 != 0.0 && magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }
        
        
        
        return cosineSimilarity;
    }
	

}



// POS
/*
fstr += findex+":" + Integer.toString(totalPOS_N)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_O)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_PN)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_S)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_Z)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_V)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_A)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_R)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_IJ)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_D)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_P)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_CC)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_T)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_X)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_hash)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_mention)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_DM)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_U)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_E)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_NU)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_PU)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_G)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_L)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_M)+ " ";findex++;
fstr += findex+":" + Integer.toString(totalPOS_Y)+ " ";findex++;
*/

/*
double w2vPosCos = cosineSimilarity(wvecTweet, w2vPos);
double w2vNegCos = cosineSimilarity(wvecTweet, w2vNeg);

fstr += findex+":" + String.format("%.3f", w2vPosCos)+ " ";findex++;
fstr += findex+":" + String.format("%.3f", w2vNegCos)+ " ";findex++;
 */

/*
if(totalNeg != 0){
	fstr += findex+":"+"1"+ " ";findex++;
}else{
	fstr += findex+":"+"0"+ " ";findex++;
}*/



/*
for(int i=0;i<200;i++){
	fstr += findex+":"+String.format("%.8f", wvecTweet_min[i])+ " ";findex++;
}

for(int i=0;i<200;i++){
	fstr += findex+":"+String.format("%.8f", wvecTweet_max[i])+ " ";findex++;
}
 */

//495050

//elements[26] = String.format("%.8f", ComputeCosineSimilarity(tweetMap,map_negTFIDF));
//elements[27] = String.format("%.8f", ComputeCosineSimilarity(tweetMap,map_posTFIDF));


/*
elements[28] = Integer.toString(totalPOS_N);
elements[29] = Integer.toString(totalPOS_V);
elements[30] = Integer.toString(totalPOS_O);
elements[31] = Integer.toString(totalPOS_A);
elements[32] = Integer.toString(totalPOS_R);
elements[33] = Integer.toString(totalPOS_hash);
elements[34] = Integer.toString(totalPOS_E);
elements[35] = Integer.toString(totalPOS_PN);
 */

//elements[36] = clusterList.trim();
/*
elements[28] = Integer.toString(totalPosEmo);
elements[29] = Integer.toString(totalNegEmo);


if(totalPosEmo >= 1){
	elements[28] = "YES";
}else{
	elements[28] = "NO";
}

if(totalNegEmo >= 1){
	elements[29] = "YES";
}else{
	elements[29] = "NO";
}

 */
/*
if(lastEmo == 1){
	elements[30] = "POS";
}else if(lastEmo == -1){
	elements[30] = "NEG";
}else{
	elements[30] = "NONE";
}
 */

//elements[33] = String.format("%.3f", lastScoreNeg2_bi);

/*
elements[35] = Integer.toString(totalCountPos_pr);
elements[36] = String.format("%.3f", sumScorePos_pr);
elements[37] = String.format("%.3f", maxScorePos_pr);
elements[38] = String.format("%.3f", lastScorePos_pr);

elements[39] = Integer.toString(totalCountNeg_pr);
elements[40] = String.format("%.3f", sumScoreNeg_pr);
elements[41] = String.format("%.3f", maxScoreNeg_pr);
elements[42] = String.format("%.3f", lastScoreNeg_pr);

elements[43] = Integer.toString(totalCountPos2_pr);
elements[44] = String.format("%.3f", sumScorePos2_pr);
elements[45] = String.format("%.3f", maxScorePos2_pr);
elements[46] = String.format("%.3f", lastScorePos2_pr);

elements[47] = Integer.toString(totalCountNeg2_pr);
elements[48] = String.format("%.3f", sumScoreNeg2_pr);
elements[49] = String.format("%.3f", maxScoreNeg2_pr);
elements[50] = String.format("%.3f", lastScoreNeg2_pr);
 */



