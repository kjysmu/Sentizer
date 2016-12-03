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
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.io.InputStreamReader;

import sentizer.training.FileFunction;
import sentizer.util.DoubleValueComparator;
import sentizer.util.IntValueComparator;
import sentizer.util.Tagger;

// Function related to FILE

public class FeatureGenerator {
	
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
	
	
	
	static String[] negation_cues= { "aint","cannot","cant","darent","didnt",
			"doesnt","dont","hadnt","hardly","hasnt",
			"havent","havnt","isnt","lack","lacking",
			"lacks","neither","never","no","nobody",
			"none","nor","not","nothing","nowhere",
			"mightnt","mustnt","neednt","oughtnt","shant",
			"shouldnt","wasnt","without","wouldnt" };
	
	public FeatureGenerator(){
		
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
		
		
		try 
		{
			//FileReader fr = new FileReader(file);
			//BufferedReader reader = new BufferedReader(fr);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(new File ( path_brownCluster )), "UTF8"));

			String line = "";
			
			String tmpToken1 = "";
			String tmpToken2 = "";
			
			Set<String> cSet = new HashSet<String>();
			
			while ((line = reader.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(line, "\t");
				if(token.countTokens() >= 2){
					String token1= token.nextToken();
					String token2= token.nextToken();
					
					map_bCluster.put(token2, token1);
					
					/*
					if( tmpToken1 == token1 || tmpToken1.equals("") ){
						cSet.add(token2);
					}else{
						map_Cluster.put(tmpToken1, cSet);
						cSet = new HashSet<String>();
					}
					
					tmpToken1=token1;
					tmpToken2=token2;
					 */	
				}
			}
			
			/*
			if(!cSet.isEmpty()){
				map_Cluster.put(tmpToken1, cSet);
			}
			*/
			
			reader.close();
			//fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		//FileFunction.readMapStr_StrDou(path)
		
		
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
	
	public String[] getFeatures(String tweet, String tweetPOS, String sentiment)
	{

		String elements[] = new String[31];
		// 0 : class
		// 1 : tweet
		// 2~17 : lexicons 
		// 18 : allcaps
		// 19~34 : lexicons_bi
		
		// 35 : Total neg
		
		// 35~50 : lexicon_pair
		
		String[] tweetTermList = tweet.split(" ");
		String[] tweetTermPOSList = tweetPOS.split(" ");
		
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
		int totalPOS_V = 0;
		int totalPOS_O = 0;
		int totalPOS_A = 0;
		int totalPOS_R = 0;
		int totalPOS_hash = 0;
		int totalPOS_E = 0;
		int totalPOS_PN = 0;
		
		int totalPosEmo = 0;
		int totalNegEmo = 0;
		
		int lastEmo = 0;
		
		String clusterList = "";

		for (int i=0; i<tweetTermList.length; i++) {
			
			String tweetTerm = tweetTermList[i];
			String tweetTermPOS = tweetTermPOSList[i];
			
			if( map_bCluster.containsKey(tweetTerm) ){
				clusterList += map_bCluster.get(tweetTerm);
				
				clusterList += " ";
			}
			
			if( map_emoticon.containsKey(tweetTerm) ){
				String eStr = map_emoticon.get(tweetTerm);
				if(eStr.equals("1")){
					totalPosEmo++;
					lastEmo = 1;
				}else if(eStr.equals("-1")){
					totalNegEmo++;
					lastEmo = -1;
				}else{
					lastEmo = 0;
				}
			}
			
			if(tweetTermPOS.equals("N")){
				totalPOS_N++;
			}else if(tweetTermPOS.equals("V")){
				totalPOS_V++;
			}else if(tweetTermPOS.equals("O")){
				totalPOS_O++;
			}else if(tweetTermPOS.equals("A")){
				totalPOS_A++;
			}else if(tweetTermPOS.equals("R")){
				totalPOS_R++;
			}else if(tweetTermPOS.equals("#")){
				totalPOS_hash++;
			}else if(tweetTermPOS.equals("E")){
				totalPOS_E++;
			}else if(tweetTermPOS.equals("^")){
				totalPOS_PN++;
			}
			
			if(tweetMap.containsKey(tweetTerm)){
				tweetMap.put(tweetTerm, tweetMap.get(tweetTerm) + 1.0);
			}else{
				tweetMap.put(tweetTerm, 1.0);
			}
			
			
			for(String negword : negation_cues){
				if(negword.equals(tweetTerm.toLowerCase()) || tweetTerm.toLowerCase().endsWith("n't") ){
					totalNeg++;
				}
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

		
		
		
		elements[0] = sentiment;
		elements[1] = tweet;
		
		elements[2] = Integer.toString(totalCountPos);
		elements[3] = String.format("%.3f", sumScorePos);
		elements[4] = String.format("%.3f", maxScorePos);
		
		//elements[5] = String.format("%.3f", lastScorePos);
		
		elements[5] = Integer.toString(totalCountNeg);
		elements[6] = String.format("%.3f", sumScoreNeg);
		elements[7] = String.format("%.3f", maxScoreNeg);
		
		//elements[9] = String.format("%.3f", lastScoreNeg);
		
		elements[8] = Integer.toString(totalCountPos2);
		elements[9] = String.format("%.3f", sumScorePos2);
		elements[10] = String.format("%.3f", maxScorePos2);
		
		//elements[13] = String.format("%.3f", lastScorePos2);
		
		elements[11] = Integer.toString(totalCountNeg2);
		elements[12] = String.format("%.3f", sumScoreNeg2);
		elements[13] = String.format("%.3f", maxScoreNeg2);
		
		//elements[17] = String.format("%.3f", lastScoreNeg2);
		
		//elements[18] = Integer.toString(totalAllCaps);
		
		elements[14] = Integer.toString(totalCountPos_bi);
		elements[15] = String.format("%.3f", sumScorePos_bi);
		elements[16] = String.format("%.3f", maxScorePos_bi);
		//elements[21] = String.format("%.3f", lastScorePos_bi);
		
		elements[17] = Integer.toString(totalCountNeg_bi);
		elements[18] = String.format("%.3f", sumScoreNeg_bi);
		elements[19] = String.format("%.3f", maxScoreNeg_bi);
		//elements[25] = String.format("%.3f", lastScoreNeg_bi);
		
		elements[20] = Integer.toString(totalCountPos2_bi);
		elements[21] = String.format("%.3f", sumScorePos2_bi);
		elements[22] = String.format("%.3f", maxScorePos2_bi);
		//elements[29] = String.format("%.3f", lastScorePos2_bi);
		
		elements[23] = Integer.toString(totalCountNeg2_bi);
		elements[24] = String.format("%.3f", sumScoreNeg2_bi);
		elements[25] = String.format("%.3f", maxScoreNeg2_bi);

		elements[26] = String.format("%.8f", ComputeCosineSimilarity(tweetMap,map_negTFIDF));
		elements[27] = String.format("%.8f", ComputeCosineSimilarity(tweetMap,map_posTFIDF));

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
		
		
		
		return elements;
	
	}
	
    public static double ComputeNorm(Map<String, Double> termFrequencies){
    	
    	double norm = 0.0;
    	
    	for(Map.Entry<String, Double> entry : termFrequencies.entrySet()){
    		norm += entry.getValue() * entry.getValue();
    	}
    	return Math.sqrt(norm);
    	
    }
    
    public static double ComputeCosineSimilarity(Map<String, Double> termFrequencies1, Map<String, Double> termFrequencies2){
    	
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
	
	
	
	


}
