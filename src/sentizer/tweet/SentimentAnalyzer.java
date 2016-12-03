package sentizer.tweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.lucene.LucenePackage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;

import org.apache.lucene.queryparser.classic.QueryParser;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import data.Pair;
import sentizer.bean.TweetBean;


import sentizer.parameter.Exp;
import sentizer.training.FileFunction;
import sentizer.util.DoubleValueComparator;
import sentizer.util.SentiWordNet;
import sentizer.util.Tagger;
import sentizer.util.Tagger.TaggedToken;
import sentizer.util.Word2vecReader;

public class SentimentAnalyzer {
	
	String query;
	double sentimentRatio;
	String modelFilename;
	
	int totalpct = 0;
	int totalnct = 0;
	
	int totalpctN = 0;
	int totalnctN = 0;
	int totalpctA = 0;
	int totalnctA = 0;
	
	
	int totalptct = 0;
	int totalntct = 0;
	
	Map<String, Integer> posWordCount;
	Map<String, Integer> negWordCount;
	
	Map<String, Integer> posWordCountN;
	Map<String, Integer> negWordCountN;
	Map<String, Integer> posWordCountA;
	Map<String, Integer> negWordCountA;
	
	Map<String, Integer> posTagWordCount;
	Map<String, Integer> negTagWordCount;
	
	Map<String, String> labelResults;
	Map<String, String> predictResults;

	Set<String> wordSet;
	Set<String> wordTagSet;
	
	Set<String> wordSetN;
	Set<String> wordSetA;
	
	
	Map<String, Map<String, Double>> NaverWikiCFICF;

	Map<String, Double> WikiICF;

	Map<String, Map<String, Integer>> posCat;
	Map<String, Map<String, Integer>> negCat;
	
	Map<String, Map<String, Integer>> posCatMain;
	Map<String, Map<String, Integer>> negCatMain;
	
	
	Map<String, Integer> posTmap;
	Map<String, Integer> negTmap;

	
	Map<String, Integer> posTmapMain;
	Map<String, Integer> negTmapMain;

	
	
	
	Map<String, Integer> catSize;
	
	Map<String, Integer> catSizeMain;
	
	
	
	
	Tagger tagger;
	
	FileReader fr_neg;
	BufferedReader br_neg;
	FileReader fr_pos;
	BufferedReader br_pos;
	
	FileReader fr_negN;
	BufferedReader br_negN;
	FileReader fr_posN;
	BufferedReader br_posN;
	FileReader fr_negA;
	BufferedReader br_negA;
	FileReader fr_posA;
	BufferedReader br_posA;
	
	FileReader fr_negTag;
	BufferedReader br_negTag;
		
	FileReader fr_posTag;
	BufferedReader br_posTag;
	
	FileReader fr_w2v_lg;
	BufferedReader br_w2v_lg;

	FileReader fr_d2v_lg;
	BufferedReader br_d2v_lg;
	
	SentiWordNet sentiWordNet;
	
	FileReader fr_senVec;
	BufferedReader br_senVec;
	
	FileReader fr_senVec2;
	BufferedReader br_senVec2;
	
	String[] negation_cues= { "aint","cannot","cant","darent","didnt",
			"doesnt","dont","hadnt","hardly","hasnt",
			"havent","havnt","isnt","lack","lacking",
			"lacks","neither","never","no","nobody",
			"none","nor","not","nothing","nowhere",
			"mightnt","mustnt","neednt","oughtnt","shant",
			"shouldnt","wasnt","without","wouldnt" };
	
	
	double[] posVec;
	double[] negVec;
	
	double[] posVec2;
	double[] negVec2;
	
	
	int wordSize;
	int wordTagSize;
	
	int wordSizeN;
	int wordSizeA;
	
	
	
    double[] lg_weights_w2v;
    double[] lg_weights_d2v;
    

	
	Word2vecReader word2vec;
	
	public SentimentAnalyzer() throws Exception{
		init();
	}
	public void init() throws Exception{
		
		String line = "";
		
		lg_weights_w2v = new double[200];
		lg_weights_d2v = new double[200];


		word2vec = new Word2vecReader();
		sentiWordNet = new SentiWordNet("D:\\project2nd\\dataset_sentizer\\SentiWordNet_3.0.0.txt");
		
		posWordCount = new HashMap<String, Integer>();
		negWordCount = new HashMap<String, Integer>();
		
		posWordCountN = new HashMap<String, Integer>();
		negWordCountN = new HashMap<String, Integer>();
		posWordCountA = new HashMap<String, Integer>();
		negWordCountA = new HashMap<String, Integer>();
		
		posTagWordCount = new HashMap<String, Integer>();
		negTagWordCount = new HashMap<String, Integer>();
		
		
		posTmap = new HashMap<String, Integer>();
		negTmap = new HashMap<String, Integer>();;

		posTmapMain = new HashMap<String, Integer>();
		negTmapMain = new HashMap<String, Integer>();;
		
		labelResults = new HashMap<String, String>();
		predictResults = new HashMap<String, String>();
		
		modelFilename = "D:\\project2nd\\dataset_sentizer\\model.20120919";
		
		wordSet = new HashSet<String>();
		wordTagSet = new HashSet<String>();
		
		wordSetN = new HashSet<String>();
		wordSetA = new HashSet<String>();
		
		catSize = new HashMap<String, Integer >() ;
		
		catSizeMain = new HashMap<String, Integer >() ;
		
		
		
		tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		fr_neg = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_neg.txt"));
		br_neg = new BufferedReader(fr_neg);
		
		while(true){
			line = br_neg.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totalnct+=value;
				negWordCount.put(key, value);
				wordSet.add(key);		
			}
		}
		
		br_neg.close();
		fr_neg.close();
		
		fr_negN = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_negN.txt"));
		br_negN = new BufferedReader(fr_negN);
		
		while(true){
			line = br_negN.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totalnctN+=value;
				negWordCountN.put(key, value);
				wordSetN.add(key);		
			}
		}
		
		br_negN.close();
		fr_negN.close();

		fr_negA = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_negA.txt"));
		br_negA = new BufferedReader(fr_negA);
		
		while(true){
			line = br_negA.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totalnctA+=value;
				negWordCountA.put(key, value);
				wordSetA.add(key);	
			}
		}
		
		br_negA.close();
		fr_negA.close();
		
		fr_negTag = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_negTag.txt"));
		br_negTag = new BufferedReader(fr_negTag);
		
		while(true){
			line = br_negTag.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totalntct+=value;
				negTagWordCount.put(key, value);
				wordTagSet.add(key);		
			}
		}
		br_negTag.close();
		fr_negTag.close();
		
		
		
		fr_w2v_lg = new FileReader(new File("D:\\project2nd\\workspace\\doc2vec\\file\\trainingSNT_logistic_model_w2v.txt"));
		br_w2v_lg = new BufferedReader(fr_w2v_lg);
		
		line = "";
		int wct = 0;
		while(true){
			line = br_w2v_lg.readLine();
			if(line == null) break;
			double weight = Double.parseDouble(line.trim());
			lg_weights_w2v[wct] = weight;
			
			wct++;

		}
		br_w2v_lg.close();
		fr_w2v_lg.close();
		
		
		fr_d2v_lg = new FileReader(new File("D:\\project2nd\\workspace\\doc2vec\\file\\trainingSNT_logistic_model_d2v.txt"));
		br_d2v_lg = new BufferedReader(fr_d2v_lg);
		
		line = "";
		wct = 0;
		while(true){
			line = br_d2v_lg.readLine();
			if(line == null) break;
			
			double weight = Double.parseDouble(line.trim());
			lg_weights_d2v[wct] = weight;
			
			wct++;
			
		}
		br_d2v_lg.close();
		fr_d2v_lg.close();
		
		
		posVec = new double[200];
		negVec = new double[200];
		
		posVec2 = new double[200];
		negVec2 = new double[200];
		
		

		fr_pos = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_pos.txt"));
		br_pos = new BufferedReader(fr_pos);
		
		line = "";
		while(true){
			line = br_pos.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totalpct+=value;
				posWordCount.put(key, value);
				wordSet.add(key);
			}
		}
		
		br_pos.close();
		fr_pos.close();
		
		
		fr_posN = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_posN.txt"));
		br_posN = new BufferedReader(fr_posN);
		
		while(true){
			line = br_posN.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totalpctN+=value;
				posWordCountN.put(key, value);
				wordSetN.add(key);		
			}
		}
		
		br_posN.close();
		fr_posN.close();

		fr_posA = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_posA.txt"));
		br_posA = new BufferedReader(fr_posA);
		
		while(true){
			line = br_posA.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totalpctA+=value;
				posWordCountA.put(key, value);
				wordSetA.add(key);	
			}
		}
		
		br_posA.close();
		fr_posA.close();
		
		fr_posTag = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_posTag.txt"));
		br_posTag = new BufferedReader(fr_posTag);

		while(true){
			line = br_posTag.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totalptct+=value;
				posTagWordCount.put(key, value);
				wordTagSet.add(key);
			}
		}
		
		br_posTag.close();
		fr_posTag.close();
		
		fr_senVec = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\training_w2v_new.txt"));
		br_senVec = new BufferedReader(fr_senVec);
		  
		while(true){
			line = br_senVec.readLine();
			if(line == null) break;
			String[] token = line.split(" ");
			
			if(token[0].equals("Positive")){
				for(int i =0; i<200; i++){
					posVec[i] = Double.parseDouble(token[i+1]);
				}
			}else if(token[0].equals("Negative")){
				for(int i =0; i<200; i++){
					negVec[i] = Double.parseDouble(token[i+1]);
				}
			}
		}
		
		br_senVec.close();
		fr_senVec.close();
		
		
		fr_senVec2 = new FileReader(new File("D:\\project2nd\\dataset_sentizer\\doc2vec_avg.txt"));
		br_senVec2 = new BufferedReader(fr_senVec2);
		while(true){
			line = br_senVec2.readLine();
			if(line == null) break;
			String[] token = line.split(" ");
			
			if(token[0].equals("Positive")){
				for(int i =0; i<200; i++){
					posVec2[i] = Double.parseDouble(token[i+1]);
				}
			}else if(token[0].equals("Negative")){
				for(int i =0; i<200; i++){
					negVec2[i] = Double.parseDouble(token[i+1]);
				}
			}
		}
		
		br_senVec2.close();
		fr_senVec2.close();
		
		
		wordSize= wordSet.size();
		
		wordSizeN= wordSetN.size();
		wordSizeA= wordSetA.size();
		
		
		wordTagSize= wordTagSet.size();
		
		
		NaverWikiCFICF = new HashMap<String, Map<String, Double>>();

		WikiICF = new HashMap<String, Double>();

		
		WikiICF = FileFunction.readMapStrDou("D:\\project2nd\\dataset_sentizer\\wiki_icf\\WikiICF.txt");	
		List<File> fileListWikiCFICF = FileFunction.getListOfFiles("D:\\project2nd\\dataset_sentizer\\naver_category");
		for (File file : fileListWikiCFICF) {
			Map<String, Double> WikiCFICF = new HashMap<String, Double>();				
			WikiCFICF = FileFunction.readMapStrDou(file);	
			//NaverWikiCFICF.put( NaverEnMap.get(file.getName().replaceAll(".txt","").trim())  , WikiCFICF); 
			NaverWikiCFICF.put( file.getName().replaceAll(".txt","").trim()  , WikiCFICF); 
		
		}
		
		
		posCat = new HashMap<String, Map<String, Integer>>();
		negCat = new HashMap<String, Map<String, Integer>>();
		

		posCatMain = new HashMap<String, Map<String, Integer>>();
		negCatMain = new HashMap<String, Map<String, Integer>>();
		
		
		
		Map<String, Map<String, Double>> NaverWikiCFICF = new HashMap<String, Map<String, Double>>();

		List<String> wordList = new ArrayList<String>();
		
	
		List<File> fileListPosCat = FileFunction.getListOfFiles("D:\\project2nd\\dataset\\posCat");
		List<File> fileListNegCat = FileFunction.getListOfFiles("D:\\project2nd\\dataset\\negCat");
		
		List<File> fileListPosCatMain = FileFunction.getListOfFiles("D:\\project2nd\\dataset\\posCatMain");
		List<File> fileListNegCatMain = FileFunction.getListOfFiles("D:\\project2nd\\dataset\\negCatMain");
		
		
		
		
		for (File file : fileListPosCat) {
			String categoryName = file.getName().replaceAll(".txt","").trim();
			
			Map<String, Integer> wordCount = new HashMap<String, Integer>();	
			wordCount = FileFunction.readMapStrInt(file);
			
			for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
				
				
				if(posTmap.containsKey(categoryName)){
					posTmap.put(categoryName, posTmap.get(categoryName) + entry.getValue() );
				}else{
					posTmap.put(categoryName, entry.getValue());
				}
			}
			
			posCat.put( categoryName  , wordCount); 
		
		}

		for (File file : fileListNegCat) {
			String categoryName = file.getName().replaceAll(".txt","").trim();

			Map<String, Integer> wordCount = new HashMap<String, Integer>();	
			wordCount = FileFunction.readMapStrInt(file);
			
			for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
				if(negTmap.containsKey(categoryName)){
					negTmap.put(categoryName, negTmap.get(categoryName) + entry.getValue() );
				}else{
					negTmap.put(categoryName, entry.getValue());
				}
			}
			
			negCat.put( categoryName  , wordCount); 
			
			if(posCat.containsKey(categoryName)){
				Map<String, Integer> map =	CombineCounts(posCat.get(categoryName), wordCount);
				catSize.put(categoryName, map.size());
			}else{
				catSize.put(categoryName, wordCount.size());
			}

		}
		
		
		for (File file : fileListPosCatMain) {
			String categoryName = file.getName().replaceAll(".txt","").trim();
			
			Map<String, Integer> wordCount = new HashMap<String, Integer>();	
			wordCount = FileFunction.readMapStrInt(file);
			
			for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
				
				
				if(posTmapMain.containsKey(categoryName)){
					posTmapMain.put(categoryName, posTmapMain.get(categoryName) + entry.getValue() );
				}else{
					posTmapMain.put(categoryName, entry.getValue());
				}
			}
			
			posCatMain.put( categoryName  , wordCount); 
		
		}

		for (File file : fileListNegCatMain) {
			String categoryName = file.getName().replaceAll(".txt","").trim();

			Map<String, Integer> wordCount = new HashMap<String, Integer>();	
			wordCount = FileFunction.readMapStrInt(file);
			
			for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
				if(negTmapMain.containsKey(categoryName)){
					negTmapMain.put(categoryName, negTmapMain.get(categoryName) + entry.getValue() );
				}else{
					negTmapMain.put(categoryName, entry.getValue());
				}
			}
			
			negCatMain.put( categoryName  , wordCount); 
			
			if(posCatMain.containsKey(categoryName)){
				Map<String, Integer> map =	CombineCounts(posCatMain.get(categoryName), wordCount);
				catSizeMain.put(categoryName, map.size());
			}else{
				catSizeMain.put(categoryName, wordCount.size());
			}

		}
		
	}
	
	public String getSentiment(String tweet) throws Exception{
		return getSentiment(tweet, null);
	}
	
	public String getSentiment(String tweet,String query) throws Exception{
		
		if(Exp.approach.contains("NB")){
			
			if(Exp.approach.contains("NB-AN")){
				return getSentimentNB2(tweet, null);
			}else if(Exp.approach.contains("NB-WC")){
				
				return getSentimentNB_WC3(tweet, null);
				
			}else{
				return getSentimentNB(tweet, null);

			}
			

		}else if(Exp.approach.contains("W2V")){
			
			if(Exp.approach.contains("LG")){
				return getSentimentW2V_LG(tweet, null);
			}else{
				return getSentimentW2V(tweet, null);
			}
			

		}else if(Exp.approach.contains("SWN")){
			return getSentimentSWN(tweet, null);

		}else{
			//default
			return getSentimentNB(tweet, null);

		}
		
	}
	
	public String getSentimentD2V(double[] msgVec){
		
		double posScoreD2V = 1.0;
		double negScoreD2V = 1.0;
		
		String result="";
		
		if(Exp.approach.contains("LG")){
			double lgscore = classify_d2v(msgVec);
			
			posScoreD2V = lgscore;
			negScoreD2V = 1.0 - lgscore;
		}else{
			posScoreD2V = calculateDistance(msgVec, posVec2);
			negScoreD2V = calculateDistance(msgVec, negVec2);	
			
		}
		
	
		if(posScoreD2V > negScoreD2V){
			result= "Negative";
		}else if(posScoreD2V < negScoreD2V){
			result= "Positive";
		}else {
			result= "Neutral";
		}
		
		return result;
		
	}

	
	
	
	
	// Naive Bayes Approach



	
	public String getSentimentNB(String tweet, String query){

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());		
		double posScoreNB = 1.0;
		double negScoreNB = 1.0;
		
		String result="";
		boolean isNeg = false;
		
		for (TaggedToken token : taggedTokens) {
			
			String word = token.token;
			String tag = token.tag;
			
			if(negation_cues.equals(word.toLowerCase()) || word.toLowerCase().endsWith("n't") ){
				isNeg = true;
				continue;
			}
			if(tag.equals(",")){
				isNeg = false;
				continue;
			}
			
			double posScore = 1.0;
			double negScore = 1.0;
			
			
			if(tag.equals("@") || tag.equals("U") ||  tag.equals(",")) continue;
			
			word = word.replaceAll("([a-z])\\1+", "$1$1");
	
			if( posWordCount.containsKey(word) && negWordCount.containsKey(word)){
				
				if(Exp.approach.contains("NB-SN")){
					
					if(isNeg){
						negScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
						posScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					}else{
						posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
						negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					}
					
				}else{
					posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
						
				}
				
			}else if(posWordCount.containsKey(word)){
				
				
				if(Exp.approach.contains("Smooth")){
					if(Exp.approach.contains("NB-SN")){
						
						if(isNeg){
							negScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
							posScore = (double)(1.0)/(double)(totalnct+wordSize);
						}else{
							posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
							negScore = (double)(1.0)/(double)(totalnct+wordSize);
						}
						
					}else{
						posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
						negScore = (double)(1.0)/(double)(totalnct+wordSize);
							
					}
				}
				
			}else if(negWordCount.containsKey(word)){
				
				
				if(Exp.approach.contains("Smooth")){
					if(Exp.approach.contains("NB-SN")){
						if(isNeg){
							negScore = (double)(1.0)/(double)(totalpct+wordSize);
							posScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
							
						}else{
							posScore = (double)(1.0)/(double)(totalpct+wordSize);
							negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
							
						}
					}else{
						posScore = (double)(1.0)/(double)(totalpct+wordSize);
						negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
							
					}
				}

			}	 
			
			posScoreNB *= posScore;
			negScoreNB *= negScore;
		
		}	
		if(posScoreNB > negScoreNB){
			result= "Positive";
		}else if(posScoreNB < negScoreNB){
			result= "Negative";
		}else{
			result= "Neutral";
		}
		
		
		return result;
	
	}
	
	public String getSentimentNB2(String tweet, String query){

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());		
		double posScoreNB = 1.0;
		double negScoreNB = 1.0;
		
		String result="";
		boolean isNeg = false;
		
		for (TaggedToken token : taggedTokens) {
			
			String word = token.token;
			String tag = token.tag;
			
			for(String negword : negation_cues){
				if(negword.equals(word.toLowerCase()) || word.toLowerCase().endsWith("n't") ){
					isNeg = true;
				}	
			}
			
			if(tag.equals(",")){
				isNeg = false;
			}
			
			double posScore = 1.0;
			double negScore = 1.0;
			
			if(tag.equals("@") || tag.equals("U") ||  tag.equals(",")) continue;
			
			word = word.replaceAll("([a-z])\\1+", "$1$1");
			
			if(isNeg){
				if( posWordCountN.containsKey(word) && negWordCountN.containsKey(word)){

					posScore = (double)(posWordCountN.get(word)+1)/(double)(totalpctN+wordSizeN);
					negScore = (double)(negWordCountN.get(word)+1)/(double)(totalnctN+wordSizeN);
					
				}else if(posWordCountN.containsKey(word)){
					
					if(Exp.approach.contains("Smooth")){
						//Smoothing
						posScore = (double)(posWordCountN.get(word)+1)/(double)(totalpctN+wordSizeN);
						negScore = (double)(1.0)/(double)(totalnctN+wordSizeN);
					}


					
				}else if(negWordCountN.containsKey(word)){
					if(Exp.approach.contains("Smooth")){
						//Smoothing
						posScore = (double)(1.0)/(double)(totalpctN+wordSizeN);
						negScore = (double)(negWordCountN.get(word)+1)/(double)(totalnctN+wordSizeN);
					}

					
				}
			}else{
				if( posWordCountA.containsKey(word) && negWordCountA.containsKey(word)){
					
					posScore = (double)(posWordCountA.get(word)+1)/(double)(totalpctA+wordSizeA);
					negScore = (double)(negWordCountA.get(word)+1)/(double)(totalnctA+wordSizeA);
					
				}else if(posWordCountA.containsKey(word)){
					
					if(Exp.approach.contains("Smooth")){
						//Smoothing
						posScore = (double)(posWordCountA.get(word)+1)/(double)(totalpctA+wordSizeA);
						negScore = (double)(1.0)/(double)(totalnctA+wordSizeA);
					}

	
					
				}else if(negWordCountA.containsKey(word)){
					
					if(Exp.approach.contains("Smooth")){
						//Smoothing
						posScore = (double)(1.0)/(double)(totalpctA+wordSizeA);
						negScore = (double)(negWordCountA.get(word)+1)/(double)(totalnctA+wordSizeA);
						
					}

				}
			}
	
			posScoreNB *= posScore;
			negScoreNB *= negScore;
		
		}	
		if(posScoreNB > negScoreNB){
			result= "Positive";
		}else if(posScoreNB < negScoreNB){
			result= "Negative";
		}else{
			result= "Neutral";
		}
		
		
		return result;
	
	}
	
	public String getSentimentNB_WC(String tweet, String query) throws Exception{

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());		
		double posScoreNB = 1.0;
		double negScoreNB = 1.0;
		
		String result="";
		boolean isNeg = false;
		
		String indexRoute = "luceneIndex2";// lucene index folder
		
		Map<String, Double> topsimilarities = new HashMap<String, Double>();
		Map<String, Double> similarities = new HashMap<String, Double>();		
		Map<String, Double> wikiCategory = new HashMap<String,Double>();

		for (TaggedToken token : taggedTokens) {
			
			String word = token.token;
			String tag = token.tag;
			
			word = word.replaceAll("([a-z])\\1+", "$1$1");
			
			if(tag.equals("^") || tag.equals("N") ){
				
				//System.out.println( "\t" + word + "\t" + tag);
				
				ArrayList<String> results = searchIndex(word.toLowerCase(), indexRoute);

				for (String r : results) {
					String[] titleAndcategories = r.split("\t");
					String title = titleAndcategories[0];
					String categories = titleAndcategories[1];
					
					//System.out.println("\t" + "\t" + title + "\t" + categories);
					
					String[] categorylist = categories.split(" ");
					
					for(String category : categorylist){
						category = category.replaceAll("_", " ");
						if(wikiCategory.containsKey(category)){
							wikiCategory.put(category, wikiCategory.get(category) + 1.0);
						}else{
							wikiCategory.put(category, 1.0);
						}								
					}
				}
			}
			
			if(isNeg){}else{}
	
			//posScoreNB *= posScore;
			//negScoreNB *= negScore;
		
		}
		

		Map<String, Double> wikiCategoryMap = new HashMap<String, Double>();
		
		wikiCategoryMap = getNorm(wikiCategory);
		wikiCategoryMap = getTFIDF(wikiCategoryMap, WikiICF);
		
		for(Map.Entry<String, Map<String,Double>> NaverWIKI_entry : NaverWikiCFICF.entrySet() ){
			
			String key = NaverWIKI_entry.getKey();
			Map<String,Double> map = NaverWIKI_entry.getValue();
			for(Map.Entry<String,Double> map_entry : map.entrySet() ){
				map_entry.getKey();
				map_entry.getValue();
			}

			
			similarities.put(NaverWIKI_entry.getKey(), ComputeCosineSimilarity( wikiCategoryMap , NaverWIKI_entry.getValue()) );
		}
		
		DoubleValueComparator bvc = new DoubleValueComparator(similarities);
		TreeMap<String, Double> tMap = new TreeMap<String, Double>(bvc);
		tMap.putAll(similarities);
		
		double maxsim = 0.0;
		double totalsim = 0.0;

		
		for(Map.Entry<String, Double> similarity : tMap.entrySet() ){
			if( !(similarity.getValue() == 0 || similarity.getValue() == null || isNaN(similarity.getValue())) ){
				totalsim += similarity.getValue();
			}
			if( maxsim < similarity.getValue()) maxsim = similarity.getValue();
		}
		
		if(totalsim == 0 || isNaN(totalsim)){
			topsimilarities.clear();
		}
		
		Iterator<Map.Entry<String,Double>> iter = tMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String,Double> entry = iter.next();
			if(entry.getValue() == 0 || entry.getValue() == null || isNaN(entry.getValue()) ) iter.remove();
			else{
				double score = entry.getValue();
				double score2 = score / totalsim;
				if( score < 0.0 ){
					iter.remove();
				}else if( score2 < 0.3 ){
					iter.remove();
				}
			}
		}
		
		
		String tweet_category = "";
		
		if(similarities.isEmpty()){
			topsimilarities.clear();
		}else{
			int count = 0;
			
			
			for(Map.Entry<String, Double> similarity : tMap.entrySet() ){
				//topsimilarities.put(similarity.getKey(), similarity.getValue());
				tweet_category = similarity.getKey();
				count ++;
				if(count >= 1) break;
			}
		}
		
		if(tweet_category.equals("")){
			tweet_category = "NONE";
		}else{
		}
		
		int totalpct = 0;
		int totalnct = 0;
		
		
		Map<String, Integer> posWordCount = new HashMap<String, Integer>();
		if(posCat.containsKey(tweet_category)){
			posWordCount =  posCat.get(tweet_category);
			totalpct = posTmap.get(tweet_category);
		}

		Map<String, Integer> negWordCount = new HashMap<String, Integer>();
		if(negCat.containsKey(tweet_category)){
			negWordCount =  negCat.get(tweet_category);
			totalnct = negTmap.get(tweet_category);

		}
		int wordSize = 0;
		if(catSize.containsKey(tweet_category)){
			wordSize =  catSize.get(tweet_category);
		}
		
		for (TaggedToken token : taggedTokens) {
			String word = token.token;
			String tag = token.tag;
			
			double posScore = 1.0;
			double negScore = 1.0;
			
			if(tag.equals("R")){
				
				
			}
			
			if( posWordCount.containsKey(word) && negWordCount.containsKey(word)){

				posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
				negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
				
			}else if(posWordCount.containsKey(word)){
				
				posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
				negScore = (double)(1.0)/(double)(totalnct+wordSize);
				
				
			}else if(negWordCount.containsKey(word)){
				
				posScore = (double)(1.0)/(double)(totalpct+wordSize);
				negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
				
			}
			
			
			posScoreNB *= posScore;
			negScoreNB *= negScore;
			
			
		}

		if(posScoreNB > negScoreNB){
			result= "Positive";
		}else if(posScoreNB < negScoreNB){
			result= "Negative";
		}else{
			result= "Neutral";
		}
		
		
		return result;
	
	}
	
	public String getSentimentNB_WC2(String tweet, String query) throws Exception{

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());		
		double posScoreNB = 1.0;
		double negScoreNB = 1.0;
		
		String result="";
		boolean isNeg = false;
		
		String indexRoute = "luceneIndex3";// lucene index folder
		
		Map<String, Double> topsimilarities = new HashMap<String, Double>();
		Map<String, Double> similarities = new HashMap<String, Double>();		
		Map<String, Double> wikiCategory = new HashMap<String,Double>();

		for (TaggedToken token : taggedTokens) {
			
			String word = token.token;
			String tag = token.tag;
			
			word = word.replaceAll("([a-z])\\1+", "$1$1");
			
			if(tag.equals("^") || tag.equals("N") ){
				
				//System.out.println( "\t" + word + "\t" + tag);
				
				ArrayList<String> results = searchIndex(word.toLowerCase(), indexRoute);

				for (String r : results) {
					String[] titleAndcategories = r.split("\t");
					String title = titleAndcategories[0];
					String categories = titleAndcategories[1];
					
					//System.out.println("\t" + "\t" + title + "\t" + categories);
					
					String[] categorylist = categories.split(" ");
					
					for(String category : categorylist){
						category = category.replaceAll("_", " ");
						if(wikiCategory.containsKey(category)){
							wikiCategory.put(category, wikiCategory.get(category) + 1.0);
						}else{
							wikiCategory.put(category, 1.0);
						}								
					}
				}
			}
			
			if(isNeg){}else{}
	
			//posScoreNB *= posScore;
			//negScoreNB *= negScore;
		
		}
		

		Map<String, Double> wikiCategoryMap = new HashMap<String, Double>();
		
		wikiCategoryMap = getNorm(wikiCategory);
		wikiCategoryMap = getTFIDF(wikiCategoryMap, WikiICF);
		
		for(Map.Entry<String, Map<String,Double>> NaverWIKI_entry : NaverWikiCFICF.entrySet() ){
			
			String key = NaverWIKI_entry.getKey();
			Map<String,Double> map = NaverWIKI_entry.getValue();
			for(Map.Entry<String,Double> map_entry : map.entrySet() ){
				map_entry.getKey();
				map_entry.getValue();
			}

			
			similarities.put(NaverWIKI_entry.getKey(), ComputeCosineSimilarity( wikiCategoryMap , NaverWIKI_entry.getValue()) );
		}
		
		DoubleValueComparator bvc = new DoubleValueComparator(similarities);
		TreeMap<String, Double> tMap = new TreeMap<String, Double>(bvc);
		tMap.putAll(similarities);
		
		double maxsim = 0.0;
		double totalsim = 0.0;

		
		for(Map.Entry<String, Double> similarity : tMap.entrySet() ){
			if( !(similarity.getValue() == 0 || similarity.getValue() == null || isNaN(similarity.getValue())) ){
				totalsim += similarity.getValue();
			}
			if( maxsim < similarity.getValue()) maxsim = similarity.getValue();
		}
		
		if(totalsim == 0 || isNaN(totalsim)){
			topsimilarities.clear();
		}
		
		Iterator<Map.Entry<String,Double>> iter = tMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String,Double> entry = iter.next();
			if(entry.getValue() == 0 || entry.getValue() == null || isNaN(entry.getValue()) ) iter.remove();
			else{
				double score = entry.getValue();
				double score2 = score / totalsim;
				if( score < 0.0 ){
					iter.remove();
				}else if( score2 < 0.3 ){
					iter.remove();
				}
			}
		}
		
		
		String tweet_category = "";
		
		if(similarities.isEmpty()){
			topsimilarities.clear();
		}else{
			int count = 0;
			
			
			for(Map.Entry<String, Double> similarity : tMap.entrySet() ){
				//topsimilarities.put(similarity.getKey(), similarity.getValue());
				tweet_category = similarity.getKey();
				count ++;
				if(count >= 1) break;
			}
		}
		
		if(tweet_category.equals("")){
			tweet_category = "NONE";
		}else{
		}
		
		int totalpctNew = 0;
		int totalnctNew = 0;
		
		
		Map<String, Integer> posWordCountNew = new HashMap<String, Integer>();
		if(posCat.containsKey(tweet_category)){
			posWordCountNew =  posCat.get(tweet_category);
			totalpctNew = posTmap.get(tweet_category);
		}

		Map<String, Integer> negWordCountNew = new HashMap<String, Integer>();
		if(negCat.containsKey(tweet_category)){
			negWordCountNew =  negCat.get(tweet_category);
			totalnctNew = negTmap.get(tweet_category);

		}
		int wordSizeNew = 0;
		if(catSize.containsKey(tweet_category)){
			wordSizeNew =  catSize.get(tweet_category);
		}
		
		for (TaggedToken token : taggedTokens) {
			String word = token.token;
			String tag = token.tag;
			
			double posScore = 1.0;
			double negScore = 1.0;
			
			if(tag.equals("R")){
				if( posWordCountNew.containsKey(word) && negWordCountNew.containsKey(word)){

					posScore = (double)(posWordCountNew.get(word)+1)/(double)(totalpctNew+wordSizeNew);
					negScore = (double)(negWordCountNew.get(word)+1)/(double)(totalnctNew+wordSizeNew);
					
				}else if(posWordCountNew.containsKey(word)){
					
					posScore = (double)(posWordCountNew.get(word)+1)/(double)(totalpctNew+wordSizeNew);
					negScore = (double)(1.0)/(double)(totalnctNew+wordSizeNew);
					
					
				}else if(negWordCountNew.containsKey(word)){
					
					posScore = (double)(1.0)/(double)(totalpctNew+wordSizeNew);
					negScore = (double)(negWordCountNew.get(word)+1)/(double)(totalnctNew+wordSizeNew);
					
				}
			}else{
				if( posWordCount.containsKey(word) && negWordCount.containsKey(word)){

					posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					
				}else if(posWordCount.containsKey(word)){
					
					posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					negScore = (double)(1.0)/(double)(totalnct+wordSize);
					
					
				}else if(negWordCount.containsKey(word)){
					
					posScore = (double)(1.0)/(double)(totalpct+wordSize);
					negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					
				}
				
			}
			
		
			
			posScoreNB *= posScore;
			negScoreNB *= negScore;
			
			
		}

		if(posScoreNB > negScoreNB){
			result= "Positive";
		}else if(posScoreNB < negScoreNB){
			result= "Negative";
		}else{
			result= "Neutral";
		}
		
		
		return result;
	
	}
	
	public String getSentimentNB_WC3(String tweet, String query) throws Exception{

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());		
		double posScoreNB = 1.0;
		double negScoreNB = 1.0;
		
		String result="";
		boolean isNeg = false;
		
		String indexRoute = "luceneIndex2";// lucene index folder
		
		Map<String, Double> topsimilarities = new HashMap<String, Double>();
		Map<String, Double> similarities = new HashMap<String, Double>();		
		Map<String, Double> wikiCategory = new HashMap<String,Double>();

		for (TaggedToken token : taggedTokens) {
			
			String word = token.token;
			String tag = token.tag;
			
			word = word.replaceAll("([a-z])\\1+", "$1$1");
			
			if(tag.equals("^") || tag.equals("N") ){
				
				//System.out.println( "\t" + word + "\t" + tag);
				
				ArrayList<String> results = searchIndex(word.toLowerCase(), indexRoute);

				for (String r : results) {
					String[] titleAndcategories = r.split("\t");
					String title = titleAndcategories[0];
					String categories = titleAndcategories[1];
					
					//System.out.println("\t" + "\t" + title + "\t" + categories);
					
					String[] categorylist = categories.split(" ");
					
					for(String category : categorylist){
						category = category.replaceAll("_", " ");
						if(wikiCategory.containsKey(category)){
							wikiCategory.put(category, wikiCategory.get(category) + 1.0);
						}else{
							wikiCategory.put(category, 1.0);
						}								
					}
				}
			}
			
			if(isNeg){}else{}
	
			//posScoreNB *= posScore;
			//negScoreNB *= negScore;
		
		}
		

		Map<String, Double> wikiCategoryMap = new HashMap<String, Double>();
		
		wikiCategoryMap = getNorm(wikiCategory);
		wikiCategoryMap = getTFIDF(wikiCategoryMap, WikiICF);
		
		for(Map.Entry<String, Map<String,Double>> NaverWIKI_entry : NaverWikiCFICF.entrySet() ){
			
			String key = NaverWIKI_entry.getKey();
			Map<String,Double> map = NaverWIKI_entry.getValue();
			for(Map.Entry<String,Double> map_entry : map.entrySet() ){
				map_entry.getKey();
				map_entry.getValue();
			}

			
			similarities.put(NaverWIKI_entry.getKey(), ComputeCosineSimilarity( wikiCategoryMap , NaverWIKI_entry.getValue()) );
		}
		
		DoubleValueComparator bvc = new DoubleValueComparator(similarities);
		TreeMap<String, Double> tMap = new TreeMap<String, Double>(bvc);
		tMap.putAll(similarities);
		
		double maxsim = 0.0;
		double totalsim = 0.0;

		
		for(Map.Entry<String, Double> similarity : tMap.entrySet() ){
			if( !(similarity.getValue() == 0 || similarity.getValue() == null || isNaN(similarity.getValue())) ){
				totalsim += similarity.getValue();
			}
			if( maxsim < similarity.getValue()) maxsim = similarity.getValue();
		}
		
		if(totalsim == 0 || isNaN(totalsim)){
			topsimilarities.clear();
		}
		
		Iterator<Map.Entry<String,Double>> iter = tMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String,Double> entry = iter.next();
			if(entry.getValue() == 0 || entry.getValue() == null || isNaN(entry.getValue()) ) iter.remove();
			else{
				double score = entry.getValue();
				double score2 = score / totalsim;
				if( score < 0.0 ){
					iter.remove();
				}else if( score2 < 0.3 ){
					iter.remove();
				}
			}
		}
		
		
		String tweet_category = "";
		
		if(similarities.isEmpty()){
			topsimilarities.clear();
		}else{
			int count = 0;
			
			
			for(Map.Entry<String, Double> similarity : tMap.entrySet() ){
				//topsimilarities.put(similarity.getKey(), similarity.getValue());
				tweet_category = similarity.getKey();
				count ++;
				if(count >= 1) break;
			}
		}
		
		if(tweet_category.equals("")){
			tweet_category = "NONE";
		}else{
		}
		
		tweet_category = tweet_category.split(" ")[0];
		
		int totalpctNew = 0;
		int totalnctNew = 0;
		
		
		Map<String, Integer> posWordCountNew = new HashMap<String, Integer>();
		if(posCatMain.containsKey(tweet_category)){
			posWordCountNew =  posCatMain.get(tweet_category);
			totalpctNew = posTmapMain.get(tweet_category);
		}

		Map<String, Integer> negWordCountNew = new HashMap<String, Integer>();
		if(negCatMain.containsKey(tweet_category)){
			negWordCountNew =  negCatMain.get(tweet_category);
			totalnctNew = negTmapMain.get(tweet_category);

		}
		int wordSizeNew = 0;
		if(catSizeMain.containsKey(tweet_category)){
			wordSizeNew =  catSizeMain.get(tweet_category);
		}
		
		for (TaggedToken token : taggedTokens) {
			String word = token.token;
			String tag = token.tag;
			
			double posScore = 1.0;
			double negScore = 1.0;
			
			if(tag.equals("R")){
				if( posWordCountNew.containsKey(word) && negWordCountNew.containsKey(word)){

					posScore = (double)(posWordCountNew.get(word)+1)/(double)(totalpctNew+wordSizeNew);
					negScore = (double)(negWordCountNew.get(word)+1)/(double)(totalnctNew+wordSizeNew);
					
				}else if(posWordCountNew.containsKey(word)){
					
					posScore = (double)(posWordCountNew.get(word)+1)/(double)(totalpctNew+wordSizeNew);
					negScore = (double)(1.0)/(double)(totalnctNew+wordSizeNew);
					
					
				}else if(negWordCountNew.containsKey(word)){
					
					posScore = (double)(1.0)/(double)(totalpctNew+wordSizeNew);
					negScore = (double)(negWordCountNew.get(word)+1)/(double)(totalnctNew+wordSizeNew);
					
				}
			}else{
				if( posWordCount.containsKey(word) && negWordCount.containsKey(word)){

					posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					
				}else if(posWordCount.containsKey(word)){
					
					posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					negScore = (double)(1.0)/(double)(totalnct+wordSize);
					
					
				}else if(negWordCount.containsKey(word)){
					
					posScore = (double)(1.0)/(double)(totalpct+wordSize);
					negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					
				}
				
			}
			
			posScoreNB *= posScore;
			negScoreNB *= negScore;
			
		}

		if(posScoreNB > negScoreNB * 1.5){
			result= "Positive";
		}else if(posScoreNB * 1.5 < negScoreNB){
			result= "Negative";
		}else{
			result= "Neutral";
		}
		
		
		return result;
	
	}
	
	
	
	
	// Word2Vec Approach
	
	public String getSentimentW2V(String tweet, String query){

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());
		
		double posScoreW2V = 1.0;
		double negScoreW2V = 1.0;
		
		String result="";
		double[] msgVec = new double[200];
		boolean isVec = false;
		int msgct = 0;
		
		for (TaggedToken token : taggedTokens) {
			
			String word = token.token;
			String tag = token.tag;
			
			if(tag.equals("@") || tag.equals("U") || tag.equals(",") ) continue;
			word = word.replaceAll("([a-z])\\1+", "$1$1");
			
			double[] vec =word2vec.getWordRepresentation(word);
			if(vec != null){
				isVec = true;
				for(int i=0; i<200; i++){
					msgVec[i] += vec[i];
				}
				msgct++;
			}
	
		}
		
		if(isVec){
			if(msgct >= 1){
				for(int i=0; i<200; i++){
					msgVec[i] /= msgct;
				}
			}
			posScoreW2V = calculateDistance(msgVec, posVec);
			negScoreW2V = calculateDistance(msgVec, negVec);	
		}
	
		if(posScoreW2V > negScoreW2V){
			result= "Negative";
		}else if(posScoreW2V < negScoreW2V){
			result= "Positive";
		}else {
			result= "Neutral";
		}
		
		return result;
	
	}
	
	
	// SentiWordNet Approach
	
	public String getSentimentSWN(String tweet, String query){

		
		//boolean isNeg = isNegation(tweet);
		
		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());
		
		String result="";
		double swn_score = 0.0;
		
		int swn_ct = 0;
		
		boolean isNeg = false;
		
		for (TaggedToken token : taggedTokens) {
			
			String word = token.token;
			String tag = token.tag;
			

			if(tag.equals(",")){
				isNeg = false;
				continue;
			}
			
			
			for(String negword : negation_cues){
				if(negword.equals(word.toLowerCase()) || word.toLowerCase().endsWith("n't") ){
					isNeg = true;
					break;
				}	
			}
			
			
			if(tag.equals("@") || tag.equals("U") ) continue;
			word = word.replaceAll("([a-z])\\1+", "$1$1");
			
			if(tag.equals("N") || tag.equals("A") || tag.equals("V") || tag.equals("R")){
				if( sentiWordNet.isDictionary(word.toLowerCase(), tag.toLowerCase()) ){
					
					swn_ct++;
					
					if(tag.equals("A") || tag.equals("V") || tag.equals("R")){
						swn_score += sentiWordNet.extract(word.toLowerCase(), tag.toLowerCase());
						swn_score = swn_score * 1;
						
						if(isNeg) swn_score *= -1;
						
					}else{
						swn_score += sentiWordNet.extract(word.toLowerCase(), tag.toLowerCase());
						
						if(isNeg) swn_score *= -1;

					}
					
				}
			}
			
		}
		
		swn_score = swn_score/swn_ct;

		if(swn_score > 0.0){
			
			result= "Positive";
			
		}else if(swn_score < 0.0){
			
			result= "Negative";
			
		}else {
			result= "Neutral";
		}
		
		return result;
	
	}
	
	
	// Word2Vec-Logistic Approach
	
	public String getSentimentW2V_LG(String tweet, String query){

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());
		
		double posScoreW2V = 1.0;
		double negScoreW2V = 1.0;
		
		
		String result="";
		double[] msgVec = new double[200];
		boolean isVec = false;
		int msgct = 0;
		
		for (TaggedToken token : taggedTokens) {
			
			String word = token.token;
			String tag = token.tag;
			
			if(tag.equals("@") || tag.equals("U") || tag.equals(",") ) continue;
			word = word.replaceAll("([a-z])\\1+", "$1$1");
			
			double[] vec =word2vec.getWordRepresentation(word);
			if(vec != null){
				isVec = true;
				for(int i=0; i<200; i++){
					msgVec[i] += vec[i];
				}
				msgct++;
			}
	
		}
		
		if(isVec){
			if(msgct >= 1){
				for(int i=0; i<200; i++){
					msgVec[i] /= msgct;
				}
			}
			
			
			double lgscore = classify_w2v(msgVec);
			
			System.out.println(lgscore);
			
			posScoreW2V = lgscore;
			negScoreW2V = 1.0 - lgscore;
			
		}
	
		if(posScoreW2V > negScoreW2V){
			result= "Negative";
		}else if(posScoreW2V < negScoreW2V){
			result= "Positive";
		}else {
			result= "Neutral";
		}
		
		return result;
	
	}
	
	
	public boolean isNegation(String tweet){
		
		boolean isNeg = false;
		
		for(String negcue : negation_cues){
			if(tweet.contains(negcue)){
				isNeg = true;
				break;
			}
		}
		
		if(tweet.contains("n't")){
			isNeg = true;
		}
		
		return isNeg;
	}

	
	
	
	public double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }
	
    public double classify_w2v(double[] x) {
        double logit = .0;
        for (int i=0; i<lg_weights_w2v.length;i++){
            logit += lg_weights_w2v[i] * x[i];
        }
        return sigmoid(logit);
    }

    public double classify_d2v(double[] x) {
        double logit = .0;
        for (int i=0; i<lg_weights_d2v.length;i++){
            logit += lg_weights_d2v[i] * x[i];
        }
        return sigmoid(logit);
    }
	
	
	public double calculateDistance(double[] array1, double[] array2)
    {
        double Sum = 0.0;
        for(int i=0;i<array1.length;i++) {
           Sum = Sum + Math.pow((array1[i]-array2[i]),2.0);
        }
        return Math.sqrt(Sum);
    }
	
	
	public static ArrayList<String> searchIndex(String queryString,
			String indexRoute) throws Exception {
		
		//System.out.println(LucenePackage.get().getImplementationVersion());
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexRoute)));
	    IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("title", analyzer);
		ArrayList<String> titleAndcategories = new ArrayList<String>();
		Query query;

		try{
			query = parser.parse(queryString);
		}catch(Exception e){
			
			return titleAndcategories;

		}
		
		TopDocs results = searcher.search(query, 10);


		//System.out.println("total hits: " + results.totalHits + "\n");

		ScoreDoc[] hits = results.scoreDocs;
		for (ScoreDoc hit : hits) {
			Document doc = searcher.doc(hit.doc);
			String title = doc.get("title");
			String categories = doc.get("category");
			String resultForThisHit = title + "\t" + categories;
			titleAndcategories.add(resultForThisHit);// use \t as delimiter
														// between
														// title and categories
			// System.out.println("title: " + title);
			// System.out.println("categories: " + categories + "\n");
		}
		return titleAndcategories;
	}
	
	
	public static Map<String, Double> getNorm (Map<String, Double> map){
		Map<String, Double> termFrequencies = new HashMap<String, Double>();
		double count = 0;
		for (Map.Entry<String, Double> termcount : map.entrySet()){
			count += termcount.getValue();
		}
		for (Map.Entry<String, Double> termCount : map.entrySet()){
			termFrequencies.put(termCount.getKey(), termCount.getValue() / (double)count);
		}
		return termFrequencies;		
	}
	
    
    public static Map<String, Double> getTFIDF(Map<String, Double> TFs, Map<String, Double> IDFs){
    	
        Map<String, Double> tfidf = new HashMap<String, Double>();
        for(Map.Entry<String, Double> term : TFs.entrySet()){
        	if(IDFs.containsKey(term.getKey())){
        		tfidf.put(term.getKey(), term.getValue() * IDFs.get(term.getKey()) );
        	}else{
        		//System.out.println(term.getKey());
        	}
        }
        
        return tfidf;

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
	
	
	public static boolean isNaN(double x) {return x != x;}
	
	public static Map<String, Integer> CombineCounts(Map<String, Integer> source, Map<String, Integer> target) {
		Map<String, Integer> combinedCounts = new HashMap<String, Integer>();
		
		combinedCounts.putAll(source);
		for (Map.Entry<String, Integer> entry : target.entrySet()) {
			String key = entry.getKey();
			
			if (source.containsKey(key)) {
				combinedCounts.put(key, source.get(key) + entry.getValue());
			}
			else {
				combinedCounts.put(key, entry.getValue());
			}
		}
		
		return combinedCounts;
	}
	

	

}
