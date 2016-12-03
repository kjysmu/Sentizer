package sentizer.training;

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
import sentizer.util.DoubleValueComparator;
import sentizer.util.IntValueComparator;
import sentizer.util.Tagger;

public class TweetLoaderEL_WC {
	
	
	
	static String[] negation_cues= { "aint","cannot","cant","darent","didnt",
			"doesnt","dont","hadnt","hardly","hasnt",
			"havent","havnt","isnt","lack","lacking",
			"lacks","neither","never","no","nobody",
			"none","nor","not","nothing","nowhere",
			"mightnt","mustnt","neednt","oughtnt","shant",
			"shouldnt","wasnt","without","wouldnt" };
	
	
	public static void main(String args[]) throws Exception{
		
		String indexRoute = "luceneIndex2";// lucene index folder

		String pathTraining = "D:\\project2nd\\dataset\\sentiment-tweet\\training.csv";
		
		String modelFilename = "D:\\project2nd\\library\\model.20120919";
		Tagger tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		Map<String, String> tweetCategryMap = new HashMap<String, String>();
		
		Map<String, Map<String, Double>> NaverWikiCFICF = new HashMap<String, Map<String, Double>>();

		List<String> wordList = new ArrayList<String>();
		
	
		List<File> fileListWikiCFICF = FileFunction.getListOfFiles("D:\\project2nd\\dataset_sentizer\\naver_category");
		
		for (File file : fileListWikiCFICF) {
			Map<String, Double> WikiCFICF = new HashMap<String, Double>();				
			WikiCFICF = FileFunction.readMapStrDou(file);	
			NaverWikiCFICF.put( file.getName().replaceAll(".txt","").trim()  , WikiCFICF); 
		
		}
		
		FileReader fr_cmap = new FileReader(new File("D:\\project2nd\\dataset\\tweetCategoryMap.txt"));
		BufferedReader br_cmap = new BufferedReader(fr_cmap);
		
		String line = "";
		
		while(true){
			
			line = br_cmap.readLine();
			if(line == null) break;
			
			
			StringTokenizer st = new StringTokenizer(line, "\t");
			
			if(st.countTokens() == 2){
				String key = st.nextToken();
				String value = st.nextToken();
				
				tweetCategryMap.put(key, value);
			}
			
			
		}
		
		br_cmap.close();
		fr_cmap.close();


		FileReader fr = new FileReader(new File(pathTraining));
		BufferedReader br = new BufferedReader(fr);
		
		
		line = "";
		Map<String, Map<String, Integer>> posCat = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> negCat = new HashMap<String, Map<String, Integer>>();
		
		int ct = 0;
		
		while(true){
			if(ct % 10000 == 0) System.out.println("progress : " + ct);
			
			line = br.readLine();
			if(line == null) break;

			
			String linetmp = line.substring(1, line.length()-1);
			String[] splitStr = linetmp.split("\",\"");
			
			if(splitStr.length >= 6){
				String tweetSentiment= splitStr[0];
				String tweetID= splitStr[1];
				String date= splitStr[2];
				String query= splitStr[3];
				String userID= splitStr[4];
				String tweet= splitStr[5];
				
				String cat = tweetCategryMap.get(tweetID);
				
				List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(tweet.trim());
			
				Map<String, Integer> wcount = new HashMap<String, Integer>();

				if(tweetSentiment.equals("0")){
					
					if(negCat.containsKey(cat)){
						wcount = negCat.get(cat);
					}else{
						
					}
					
				}else{
					
					if(posCat.containsKey(cat)){
						wcount = posCat.get(cat);
					}else{
						
					}
					
				}					
				
				for (TaggedToken token : taggedTokens) {
					
					String word = token.token;
					String tag = token.tag;
					
					if(wcount.containsKey(word)){
						wcount.put(word, wcount.get(word) + 1);	
					}else{
						wcount.put(word, 1);
					}
				}
		
				if(tweetSentiment.equals("0")){
					negCat.put(cat, wcount);
				}else if(tweetSentiment.equals("4")){
					posCat.put(cat, wcount);
				}					
				
			}
			
			ct++;
		}
		
		br.close();
		fr.close();
		
		
		for(Map.Entry<String, Map<String,Integer>> entry : posCat.entrySet()){
			String key = entry.getKey();
			Map<String, Integer> valueMap = entry.getValue();

			FileWriter fwcat= new FileWriter(new File("D:\\project2nd\\dataset\\posCat\\" + key + ".txt"));
			BufferedWriter bwcat = new BufferedWriter(fwcat);
			
		    IntValueComparator bvc = new IntValueComparator(valueMap);
		    TreeMap<String, Integer> tMap = new TreeMap<String, Integer>(bvc);
		    tMap.putAll(valueMap);
			
			for(Map.Entry<String, Integer> entry2 : tMap.entrySet()){
			
				String key2 = entry2.getKey();
				int value = entry2.getValue();
				
				bwcat.write(key2 + "\t" + value);
				bwcat.newLine();
				
			}
			
			bwcat.close();
			fwcat.close();
			
		}
		
		
		for(Map.Entry<String, Map<String,Integer>> entry : negCat.entrySet()){
			String key = entry.getKey();
			Map<String, Integer> valueMap = entry.getValue();

			FileWriter fwcat= new FileWriter(new File("D:\\project2nd\\dataset\\negCat\\" + key + ".txt"));
			BufferedWriter bwcat = new BufferedWriter(fwcat);
			
		    IntValueComparator bvc = new IntValueComparator(valueMap);
		    TreeMap<String, Integer> tMap = new TreeMap<String, Integer>(bvc);
		    tMap.putAll(valueMap);
			
			for(Map.Entry<String, Integer> entry2 : tMap.entrySet()){
			
				String key2 = entry2.getKey();
				int value = entry2.getValue();
				
				bwcat.write(key2 + "\t" + value);
				bwcat.newLine();
				
			}
			
			bwcat.close();
			fwcat.close();
			
		}
		
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
