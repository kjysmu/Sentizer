package sentizer.trainingSemEval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;

import org.apache.lucene.queryparser.classic.QueryParser;


import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import sentizer.util.Tagger.TaggedToken;
import sentizer.training.FileFunction;
import sentizer.util.DoubleValueComparator;
import sentizer.util.IntValueComparator;
import sentizer.util.Tagger;

public class TweetLoaderEL_M {
	
	
	
	static String[] negation_cues= { "aint","cannot","cant","darent","didnt",
			"doesnt","dont","hadnt","hardly","hasnt",
			"havent","havnt","isnt","lack","lacking",
			"lacks","neither","never","no","nobody",
			"none","nor","not","nothing","nowhere",
			"mightnt","mustnt","neednt","oughtnt","shant",
			"shouldnt","wasnt","without","wouldnt" };
	
	
	public static void main(String args[]) throws Exception{
		
		String indexRoute = "luceneIndex2";// lucene index folder

		String pathTraining = "F:\\Semeval2014\\training\\finalTrainingInput.txt";
		
		// 3 classes
		
		Map<String, Integer> posWordCount = new HashMap<String, Integer>();
		Map<String, Integer> negWordCount = new HashMap<String, Integer>();
		Map<String, Integer> neuWordCount = new HashMap<String, Integer>();
		
		Map<String, Integer> posWordCountN = new HashMap<String, Integer>();
		Map<String, Integer> negWordCountN = new HashMap<String, Integer>();
		Map<String, Integer> neuWordCountN = new HashMap<String, Integer>();
		
		Map<String, Integer> posWordCountA = new HashMap<String, Integer>();
		Map<String, Integer> negWordCountA = new HashMap<String, Integer>();
		Map<String, Integer> neuWordCountA = new HashMap<String, Integer>();
		
		
		Map<String, String> tweetCategryMap = new HashMap<String, String>();
		
		Map<String, Map<String, Double>> NaverWikiCFICF = new HashMap<String, Map<String, Double>>();

		List<String> wordList = new ArrayList<String>();
		
		Map<String, Double> WikiICF = new HashMap<String, Double>();
		Map<String, String> NaverEnMap = new HashMap<String, String>();
		
		NaverEnMap = FileFunction.readMapStrStr("D:\\project2nd\\dataset_sentizer\\NaverCategoryEnMap.txt");
		WikiICF = FileFunction.readMapStrDou("D:\\project2nd\\dataset_sentizer\\wiki_icf\\WikiICF.txt");	
		
		List<File> fileListWikiCFICF = FileFunction.getListOfFiles("D:\\project2nd\\dataset_sentizer\\naver_categoryMain");
		
		for (File file : fileListWikiCFICF) {
			Map<String, Double> WikiCFICF = new HashMap<String, Double>();				
			WikiCFICF = FileFunction.readMapStrDou(file);	
			//NaverWikiCFICF.put( NaverEnMap.get(file.getName().replaceAll(".txt","").trim())  , WikiCFICF); 
			NaverWikiCFICF.put( file.getName().replaceAll(".txt","").trim()  , WikiCFICF); 
		
		}
				
		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);
		
		
		
		// Categorization results of training set		
		FileWriter fw_cmap = new FileWriter(new File("F:\\Semeval2014\\training\\tweetCategoryMainMap.txt"));
		BufferedWriter bw_cmap = new BufferedWriter(fw_cmap);
		
		Map<String, Map<String, Integer>> posCat = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> negCat = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> neuCat = new HashMap<String, Map<String, Integer>>();
		
		
		String line = "";
		int ct = 0;
		
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

				String[] tweetTermList = tweet.split(" ");
				String[] tweetTermPOSList = tweetPOS.split(" ");

				Map<String, Double> topsimilarities = new HashMap<String, Double>();

				
				
				
				Map<String, Double> similarities = new HashMap<String, Double>();		
				Map<String, Double> wikiCategory = new HashMap<String,Double>();

				for (int i=0; i<tweetTermList.length; i++) {
					String tweetTerm = tweetTermList[i];
					String tweetTermPOS = tweetTermPOSList[i];
				
					if(tweetTermPOS.equals("^") || tweetTermPOS.equals("N") ){
						
						ArrayList<String> results = searchIndex(tweetTerm.toLowerCase(), indexRoute);

						for (String r : results) {
							String[] titleAndcategories = r.split("\t");
							String title = titleAndcategories[0];
							String categories = titleAndcategories[1];
							
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
					tweetCategryMap.put(tweetID, "NONE");
				}else{
					tweetCategryMap.put(tweetID, tweet_category);
				}
				
				//System.out.println("\t >>" + tweetID + "\t" + tweet_category);
				//System.out.println("-----------------------------------------------");
	
			}
			
			ct++;
		}
		
		br.close();
		fr.close();

	    for(Map.Entry<String, String> entry : tweetCategryMap.entrySet() ){
	    	String key = entry.getKey();
	    	String value = entry.getValue();
	    	bw_cmap.write(key + "\t" + value);
	    	bw_cmap.newLine();
	    }
	    
	    bw_cmap.close();
	    fw_cmap.close();
		System.out.println("complete");
		
		
	}
	
	public static boolean isNegation(String tweet){
		
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
	public static boolean isNaN(double x) {return x != x;}

	
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
   

}
