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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;

import org.apache.lucene.queryparser.classic.QueryParser;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import data.Pair;
import sentizer.bean.TweetBean;

import sentizer.parameter.Exp;
import sentizer.training.FileFunction;
import sentizer.util.DoubleValueComparator;
import sentizer.util.SentiWordNet;
import sentizer.util.Tagger;
import sentizer.util.Tagger.TaggedToken;
import sentizer.util.Word2vecReader;

public class SentimentAnalyzer_Sem2014 {
	
	String query;
	double sentimentRatio;
	String modelFilename;
	
	int totalpct = 0;
	int totalnct = 0;
	int totaloct = 0; // neutral or objective
	
	int totalpctN = 0;
	int totalnctN = 0;
	
	int totalpctA = 0;
	int totalnctA = 0;
	
	
	int totalptct = 0;
	int totalntct = 0;
	int totalotct = 0;
	
	
	
	Map<String, Integer> posWordCount;
	Map<String, Integer> negWordCount;
	Map<String, Integer> neuWordCount;
	
	Map<String, Integer> posWordCountN;
	Map<String, Integer> negWordCountN;
	Map<String, Integer> posWordCountA;
	Map<String, Integer> negWordCountA;
	
	
	Map<String, Integer> posTagWordCount;
	Map<String, Integer> negTagWordCount;
	Map<String, Integer> neuTagWordCount;
	
	
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
	Map<String, Map<String, Integer>> neuCat;

	Map<String, Map<String, Integer>> posCatMain;
	Map<String, Map<String, Integer>> negCatMain;
	Map<String, Map<String, Integer>> neuCatMain;
	
	Map<String, Integer> posTmap;
	Map<String, Integer> negTmap;
	Map<String, Integer> neuTmap;
	
	Map<String, Integer> posTmapMain;
	Map<String, Integer> negTmapMain;
	Map<String, Integer> neuTmapMain;

	Map<String, Integer> catSize;
	Map<String, Integer> catSizeMain;
	
	Map<String, Double> tweetIDF;
	Map<String, Double> pos_TFIDF;
	Map<String, Double> neg_TFIDF;
	Map<String, Double> neu_TFIDF;
	
	Tagger tagger;
	
	FileReader fr_neg;
	BufferedReader br_neg;
	FileReader fr_pos;
	BufferedReader br_pos;
	FileReader fr_neu;
	BufferedReader br_neu;
	
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
	
	FileReader fr_neuTag;
	BufferedReader br_neuTag;
	
	
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
	
	public SentimentAnalyzer_Sem2014() throws Exception{
		init();
	}
	public void init() throws Exception{
		
		if(Exp.useSent140){
			tweetIDF = FileFunction.readMapStrDou("F:\\Semeval2014\\training\\sentiment140\\idf.txt");
			neg_TFIDF = FileFunction.readMapStrDou("F:\\Semeval2014\\training\\sentiment140\\neg_tfidf.txt");
			pos_TFIDF = FileFunction.readMapStrDou("F:\\Semeval2014\\training\\sentiment140\\pos_tfidf.txt");
			neu_TFIDF = FileFunction.readMapStrDou("F:\\Semeval2014\\training\\sentiment140\\neu_tfidf.txt");
			
		}else{
			tweetIDF = FileFunction.readMapStrDou("F:\\Semeval2014\\training\\idf.txt");
			neg_TFIDF = FileFunction.readMapStrDou("F:\\Semeval2014\\training\\neg_tfidf.txt");
			pos_TFIDF = FileFunction.readMapStrDou("F:\\Semeval2014\\training\\pos_tfidf.txt");
			neu_TFIDF = FileFunction.readMapStrDou("F:\\Semeval2014\\training\\neu_tfidf.txt");
			
		}
		
		String line = "";
		
		lg_weights_w2v = new double[200];
		lg_weights_d2v = new double[200];

		word2vec = new Word2vecReader();
		sentiWordNet = new SentiWordNet("D:\\project2nd\\dataset_sentizer\\SentiWordNet_3.0.0.txt");
		
		posWordCount = new HashMap<String, Integer>();
		negWordCount = new HashMap<String, Integer>();
		neuWordCount = new HashMap<String, Integer>();
		
		posWordCountN = new HashMap<String, Integer>();
		negWordCountN = new HashMap<String, Integer>();
		
		posWordCountA = new HashMap<String, Integer>();
		negWordCountA = new HashMap<String, Integer>();
		
		posTagWordCount = new HashMap<String, Integer>();
		negTagWordCount = new HashMap<String, Integer>();
		neuTagWordCount = new HashMap<String, Integer>();
		
		
		posTmap = new HashMap<String, Integer>();
		negTmap = new HashMap<String, Integer>();;
		neuTmap = new HashMap<String, Integer>();;

		
		posTmapMain = new HashMap<String, Integer>();
		negTmapMain = new HashMap<String, Integer>();;
		neuTmapMain = new HashMap<String, Integer>();;
		
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
		
		if(Exp.useSent140){
			fr_neg = new FileReader(new File("F:\\Semeval2014\\training\\sentiment140\\training_neg.txt"));
		}else{
			fr_neg = new FileReader(new File("F:\\Semeval2014\\training\\training_neg.txt"));
		}
		
		
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
		
		if(Exp.useSent140){
			fr_neu = new FileReader(new File("F:\\Semeval2014\\training\\sentiment140\\training_neu.txt"));
		}else{
			fr_neu = new FileReader(new File("F:\\Semeval2014\\training\\training_neu.txt"));
		}
		
		
		br_neu = new BufferedReader(fr_neu);
		
		while(true){
			line = br_neu.readLine();
			if(line == null) break;
			
			StringTokenizer st = new StringTokenizer(line,"\t");
			if(st.countTokens() == 2){
				String key = st.nextToken();
				int value = Integer.parseInt( st.nextToken() );
				totaloct+=value;
				neuWordCount.put(key, value);
				wordSet.add(key);		
			}
		}
		
		br_neu.close();
		fr_neu.close();
		
		
		if(Exp.useSent140){
			fr_pos = new FileReader(new File("F:\\Semeval2014\\training\\sentiment140\\training_pos.txt"));
		}else{
			fr_pos = new FileReader(new File("F:\\Semeval2014\\training\\training_pos.txt"));
		}
		
		
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
		
		wordSize= wordSet.size();

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
		neuCat = new HashMap<String, Map<String, Integer>>();

		posCatMain = new HashMap<String, Map<String, Integer>>();
		negCatMain = new HashMap<String, Map<String, Integer>>();
		neuCatMain = new HashMap<String, Map<String, Integer>>();
		
		
		Map<String, Map<String, Double>> NaverWikiCFICF = new HashMap<String, Map<String, Double>>();

		List<String> wordList = new ArrayList<String>();
		
	
		//List<File> fileListPosCat = FileFunction.getListOfFiles("F:\\Semeval2014\\training\\posCat");
		//List<File> fileListNegCat = FileFunction.getListOfFiles("F:\\Semeval2014\\training\\negCat");
		//List<File> fileListNeuCat = FileFunction.getListOfFiles("F:\\Semeval2014\\training\\negCat");
		
		List<File> fileListPosCatMain = FileFunction.getListOfFiles("F:\\Semeval2014\\training\\posCatMain");
		List<File> fileListNegCatMain = FileFunction.getListOfFiles("F:\\Semeval2014\\training\\negCatMain");
		List<File> fileListNeuCatMain = FileFunction.getListOfFiles("F:\\Semeval2014\\training\\neuCatMain");
		
		
		/*
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
		*/
		
		
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

			/*
			if(posCatMain.containsKey(categoryName)){
				Map<String, Integer> map =	CombineCounts(posCatMain.get(categoryName), wordCount);
				catSizeMain.put(categoryName, map.size());
			}else{
				catSizeMain.put(categoryName, wordCount.size());
			}
			*/

		}
		
		for (File file : fileListNeuCatMain) {
			String categoryName = file.getName().replaceAll(".txt","").trim();

			Map<String, Integer> wordCount = new HashMap<String, Integer>();	
			wordCount = FileFunction.readMapStrInt(file);
			
			for(Map.Entry<String, Integer> entry : wordCount.entrySet()){
				if(neuTmapMain.containsKey(categoryName)){
					neuTmapMain.put(categoryName, neuTmapMain.get(categoryName) + entry.getValue() );
				}else{
					neuTmapMain.put(categoryName, entry.getValue());
				}
			}
			
			neuCatMain.put( categoryName  , wordCount); 
			
			if(posCatMain.containsKey(categoryName)){
				Map<String, Integer> map =	CombineCounts(posCatMain.get(categoryName), wordCount);
				
				if(negCatMain.containsKey(categoryName)){
					map = CombineCounts(negCatMain.get(categoryName), map);
				}
				
				catSizeMain.put(categoryName, map.size());
			}else{
				
				if(negCatMain.containsKey(categoryName)){
					Map<String, Integer> map = CombineCounts(negCatMain.get(categoryName), wordCount);
					catSizeMain.put(categoryName, map.size());

				}else{
					catSizeMain.put(categoryName, wordCount.size());

				}
				
			}

		}

	}
	
	public String getSentiment(String tweet) throws Exception{
		return getSentiment(tweet, null);
	}
	
	public String getSentiment(String tweet,String query) throws Exception{
		
		if(Exp.approach.contains("NB")){
			
			if(Exp.approach.contains("NB-WC")){
				
				return getSentimentNB_WC3(tweet, null);
				
			}else{
				
				return getSentimentNB(tweet, null);

			}
			

		}else{
			//default
			return getSentimentNB(tweet, null);

		}
		
	}
	
	// Naive Bayes Approach
	
	public String getSentimentNB(String tweet, String query){

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());
		
		double posScoreNB = 1.0;
		double negScoreNB = 1.0;
		double neuScoreNB = 1.0;
		
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
			double neuScore = 1.0;
			
			if(tag.equals("@") || tag.equals("U") ||  tag.equals(",")) continue;
			
			word = word.replaceAll("([a-z])\\1+", "$1$1");
			
			if(!Exp.approach.contains("IDF")){
				tweetIDF.clear();
			}
			
			if(tweetIDF.containsKey(word)){
				
				if( pos_TFIDF.containsKey(word) && neg_TFIDF.containsKey(word) && neu_TFIDF.containsKey(word)){
					
					posScore = (double)(pos_TFIDF.get(word)+1)/(double)(totalpct+wordSize);
					negScore = (double)(neg_TFIDF.get(word)+1)/(double)(totalnct+wordSize);
					neuScore = (double)(neu_TFIDF.get(word)+1)/(double)(totaloct+wordSize);
					
				}else{
					
					if(pos_TFIDF.containsKey(word)){
						posScore = (double)(pos_TFIDF.get(word)+1)/(double)(totalpct+wordSize);
					}else{
						posScore = (double)(1.0)/(double)(totalpct+wordSize);
					}
					
					if(neg_TFIDF.containsKey(word)){
						negScore = (double)(neg_TFIDF.get(word)+1)/(double)(totalnct+wordSize);
					}else{
						negScore = (double)(1.0)/(double)(totalnct+wordSize);
					}
					
					if(neu_TFIDF.containsKey(word)){
						neuScore = (double)(neu_TFIDF.get(word)+1)/(double)(totaloct+wordSize);
					}else{
						neuScore = (double)(1.0)/(double)(totaloct+wordSize);
					}
									
				} 

			}else{
				

				if( posWordCount.containsKey(word) && negWordCount.containsKey(word) && neuWordCount.containsKey(word)){
					
					posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					neuScore = (double)(neuWordCount.get(word)+1)/(double)(totaloct+wordSize);
					
					
				}else{
					
					if(posWordCount.containsKey(word)){
						posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					}else{
						posScore = (double)(1.0)/(double)(totalpct+wordSize);
					}
					
					if(negWordCount.containsKey(word)){
						negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					}else{
						negScore = (double)(1.0)/(double)(totalnct+wordSize);
					}
					
					if(neuWordCount.containsKey(word)){
						neuScore = (double)(neuWordCount.get(word)+1)/(double)(totaloct+wordSize);
					}else{
						neuScore = (double)(1.0)/(double)(totaloct+wordSize);
					}
									
				} 
			
				
			}
				
			posScoreNB *= posScore;
			negScoreNB *= negScore;
			neuScoreNB *= neuScore;
		
		}
		
		if(Exp.approach.contains("3C")){
			result= "Neutral";
			
			if(posScoreNB > negScoreNB && posScoreNB > neuScoreNB){
				result= "Positive";
			}
			if(negScoreNB > posScoreNB && negScoreNB > neuScoreNB){
				result= "Negative";
			}
			if(neuScoreNB > posScoreNB && neuScoreNB > negScoreNB){
				result= "Neutral";
			}
			
			return result;

			
		}else{
			
			if(posScoreNB > negScoreNB * 1.5){
				result= "Positive";
			}else if(posScoreNB * 1.5 < negScoreNB){
				result= "Negative";
			}else{
				result= "Neutral";
			}
			
			return result;

		}
		
	
	}
		
	public String getSentimentNB_WC3(String tweet, String query) throws Exception{

		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());		
		double posScoreNB = 1.0;
		double negScoreNB = 1.0;
		double neuScoreNB = 1.0;
		
		String result="";
		boolean isNeg = false;
		
		String indexRoute = "D:/index/luceneIndex3";// lucene index folder
		
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
		int totaloctNew = 0;
		
		
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
		
		Map<String, Integer> neuWordCountNew = new HashMap<String, Integer>();
		if(neuCatMain.containsKey(tweet_category)){
			neuWordCountNew =  neuCatMain.get(tweet_category);
			totaloctNew = neuTmapMain.get(tweet_category);

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
			double neuScore = 1.0;
			
			if(tag.equals("R")){
				if( posWordCountNew.containsKey(word) && negWordCountNew.containsKey(word) && neuWordCountNew.containsKey(word)){

					posScore = (double)(posWordCountNew.get(word)+1)/(double)(totalpctNew+wordSizeNew);
					negScore = (double)(negWordCountNew.get(word)+1)/(double)(totalnctNew+wordSizeNew);
					neuScore = (double)(neuWordCountNew.get(word)+1)/(double)(totaloctNew+wordSizeNew);
					
				}else{
					
					if(posWordCountNew.containsKey(word)){
						posScore = (double)(posWordCountNew.get(word)+1)/(double)(totalpctNew+wordSizeNew);
					}else{
						posScore = (double)(1.0)/(double)(totalpctNew+wordSizeNew);

					}
					
					if(negWordCountNew.containsKey(word)){
						negScore = (double)(negWordCountNew.get(word)+1)/(double)(totalnctNew+wordSizeNew);
					}else{
						negScore = (double)(1.0)/(double)(totalnctNew+wordSizeNew);

					}
					
					if(neuWordCountNew.containsKey(word)){
						neuScore = (double)(neuWordCountNew.get(word)+1)/(double)(totaloctNew+wordSizeNew);
					}else{
						neuScore = (double)(1.0)/(double)(totaloctNew+wordSizeNew);

					}
					
					
				}
				
				
			}else{
				if( posWordCount.containsKey(word) && negWordCount.containsKey(word) && neuWordCount.containsKey(word)){

					posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					neuScore = (double)(neuWordCount.get(word)+1)/(double)(totaloct+wordSize);
					
				}else{
					if(posWordCount.containsKey(word)){
						posScore = (double)(posWordCount.get(word)+1)/(double)(totalpct+wordSize);
					}else{
						posScore = (double)(1.0)/(double)(totalpct+wordSize);
					}
					
					if(negWordCount.containsKey(word)){
						negScore = (double)(negWordCount.get(word)+1)/(double)(totalnct+wordSize);
					}else{
						negScore = (double)(1.0)/(double)(totalnct+wordSize);
					}
					
					if(neuWordCount.containsKey(word)){
						neuScore = (double)(neuWordCount.get(word)+1)/(double)(totaloct+wordSize);
					}else{
						neuScore = (double)(1.0)/(double)(totaloct+wordSize);
					}
					
				}
				
			}
			
			posScoreNB *= posScore;
			negScoreNB *= negScore;
			neuScoreNB *= neuScore;
			
		}
		
		if(Exp.approach.contains("3C")){
			result= "Neutral";
			
			if(posScoreNB > negScoreNB && posScoreNB > neuScoreNB){
				result= "Positive";
			}
			if(negScoreNB > posScoreNB && negScoreNB > neuScoreNB){
				result= "Negative";
			}
			if(neuScoreNB > posScoreNB && neuScoreNB > negScoreNB){
				result= "Neutral";
			}
			
			return result;

			
		}else{
			
			if(posScoreNB > negScoreNB * 1.5){
				result= "Positive";
			}else if(posScoreNB * 1.5 < negScoreNB){
				result= "Negative";
			}else{
				result= "Neutral";
			}
			
			return result;

		}
		
	
	
	
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
