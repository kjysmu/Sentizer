package sentizer.toy;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import java.io.ObjectInputStream;
import java.io.IOException;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.util.ErasureUtils;
import sentizer.training.FileFunction;
import sentizer.util.DoubleValueComparator;
import sentizer.util.Tagger;
import sentizer.util.Tagger.TaggedToken;

class MaxEntTestingBackup {

	public static void main(String[] args) throws Exception {
		
		String mStr = "lex_v1";
		
		boolean isCat = false;

		ColumnDataClassifier cdc = new ColumnDataClassifier("F:/examples/semeval_"+mStr+".prop");

		Map<String,String> mapNaverEn = FileFunction.readMapStrStr("F:/NaverCategoryEnMainMap.txt");

		
		FileWriter fw = new FileWriter(new File("F:\\Semeval2014\\evaluation\\candidate-MaxEnt-"+ mStr +".txt"));
		BufferedWriter bw = new BufferedWriter(fw);	

		Boolean isLex = true;

		Classifier<String,String> cl =
				cdc.makeClassifier(cdc.readTrainingExamples("F:/examples/semeval_"+mStr+".train"));


		String pathLex_NRC_hash_uni = "F:\\lexicon\\NRC_hashtag\\unigrams.txt";
		String pathLex_NRC_hash_bi = "F:\\lexicon\\NRC_hashtag\\bigrams.txt";

		String pathLex_SEN_uni = "F:\\lexicon\\Sentiment140\\unigrams.txt";
		String pathLex_SEN_bi = "F:\\lexicon\\Sentiment140\\bigrams.txt";

		String modelFilename = "D:\\project2nd\\dataset_sentizer\\model.20120919";

		
		Map<String, Double> map_NRC_hash_uni= FileFunction.readMapStrDou(pathLex_NRC_hash_uni);
		Map<String, Double> map_NRC_hash_bi= FileFunction.readMapStrDou(pathLex_NRC_hash_bi);
		Map<String, Double> map_SEN_uni= FileFunction.readMapStrDou(pathLex_SEN_uni);
		Map<String, Double> map_SEN_bi= FileFunction.readMapStrDou(pathLex_SEN_bi);
		
		Map<String, Double> WikiICF = new HashMap<String, Double>();
		WikiICF = FileFunction.readMapStrDou("D:\\project2nd\\dataset_sentizer\\wiki_icf\\WikiICF.txt");	
		
		Map<String, Map<String, Double>> NaverWikiCFICF;
		NaverWikiCFICF = new HashMap<String, Map<String, Double>>();
		
		List<File> fileListWikiCFICF = FileFunction.getListOfFiles("D:\\project2nd\\dataset_sentizer\\naver_category");
		for (File file : fileListWikiCFICF) {
			Map<String, Double> WikiCFICF = new HashMap<String, Double>();				
			WikiCFICF = FileFunction.readMapStrDou(file);	
			NaverWikiCFICF.put( file.getName().replaceAll(".txt","").trim()  , WikiCFICF); 
		
		}
		

		Tagger tagger;
		
		tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		int ct = 1;

		for (String line : ObjectBank.getLineIterator("F:/examples/semeval.test")) {
			// instead of the method in the line below, if you have the individual elements
			// already you can use cdc.makeDatumFromStrings(String[])
			
			if(isLex){
				
				String elements[] = new String[18];
				
				String lineArr[] = line.split("\t");
				String tweet_category = "";
				//System.out.println(line);
				
				String classStr = lineArr[0];
				elements[0] = classStr;
				
				List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(lineArr[1]);
				
				//LexiconFeature
				int totalCountPos = 0;
				int totalCountNeg = 0;
				double sumScorePos = 0;
				double sumScoreNeg = 0;
				double maxScorePos = 0;
				double maxScoreNeg = 0;
				double lastScorePos = 0;
				double lastScoreNeg = 0;
				
				//LexiconFeature2
				int totalCountPos2 = 0;
				int totalCountNeg2 = 0;
				double sumScorePos2 = 0;
				double sumScoreNeg2 = 0;
				double maxScorePos2 = 0;
				double maxScoreNeg2 = 0;
				double lastScorePos2 = 0;
				double lastScoreNeg2 = 0;
				
				String wordSeq = "";

				String indexRoute = "D:/index/luceneIndex3";// lucene index folder
				
				Map<String, Double> topsimilarities = new HashMap<String, Double>();
				Map<String, Double> similarities = new HashMap<String, Double>();		
				Map<String, Double> wikiCategory = new HashMap<String,Double>();

				for (TaggedToken token : taggedTokens) {
					String word = token.token;
					String tag = token.tag;
					
					
					if(isCat){
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
						
					}
				
					wordSeq += word;
					wordSeq += " ";
					
					if(map_NRC_hash_uni.containsKey(word)){
						double score = map_NRC_hash_uni.get(word);
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
					

					if(map_SEN_uni.containsKey(word)){
						double score = map_SEN_uni.get(word);
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
				
				if(isCat){
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
					
					
					tweet_category = "";
					
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
						tweet_category = "null";
					}else{
					}
					
					tweet_category = tweet_category.split(" ")[0];
					
				}
				
				
				
				
				
				wordSeq = wordSeq.trim();
				//elements[1] = wordSeq;
				elements[1] = lineArr[1];
				
				elements[2] = Integer.toString(totalCountPos);
				elements[3] = String.format("%.3f", sumScorePos);
				elements[4] = String.format("%.3f", maxScorePos);
				elements[5] = String.format("%.3f", lastScorePos);
				
				elements[6] = Integer.toString(totalCountNeg);
				elements[7] = String.format("%.3f", sumScoreNeg);
				elements[8] = String.format("%.3f", maxScoreNeg);
				elements[9] = String.format("%.3f", lastScoreNeg);
				
				elements[10] = Integer.toString(totalCountPos2);
				elements[11] = String.format("%.3f", sumScorePos2);
				elements[12] = String.format("%.3f", maxScorePos2);
				elements[13] = String.format("%.3f", lastScorePos2);
				
				elements[14] = Integer.toString(totalCountNeg2);
				elements[15] = String.format("%.3f", sumScoreNeg2);
				elements[16] = String.format("%.3f", maxScoreNeg2);
				elements[17] = String.format("%.3f", lastScoreNeg2);
				
				if(isCat){
					/*
					if(mapNaverEn.containsKey(tweet_category)){
						elements[14] = mapNaverEn.get(tweet_category);	
					}else{
						elements[14] = "null";
					}
					*/
				}
				
				
				Datum<String,String> d = cdc.makeDatumFromStrings(elements);
				bw.write("NA" + "\t" + ct + "\t" + cl.classOf(d));
				bw.newLine();
				ct++;
				
			}else{

				String elements[] = new String[2];
				String lineArr[] = line.split("\t");
				//System.out.println(line);
				
				String classStr = lineArr[0];
				elements[0] = classStr;
				
				List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(lineArr[1]);
				
				String wordSeq = "";
				
				for (TaggedToken token : taggedTokens) {
					String word = token.token;
					String tag = token.tag;
					
					wordSeq += word;
					wordSeq += " ";
					
				}
				wordSeq = wordSeq.trim();
				elements[1] = wordSeq;
				//elements[1] = lineArr[1];
				//Datum<String,String> d = cdc.makeDatumFromLine(line);
				Datum<String,String> d = cdc.makeDatumFromStrings(elements);
						
				//System.out.println(line + "  ==>  " + cl.classOf(d));
				bw.write("NA" + "\t" + ct + "\t" + cl.classOf(d));
				bw.newLine();
				ct++;
				
				
				if(ct % 500 == 0 ) System.out.println(ct);
				
			}

			
			

		}

		bw.close();
		fw.close();

		System.out.println("Complete");



		//demonstrateSerialization();
	}


	public static void demonstrateSerialization()
			throws IOException, ClassNotFoundException {
		System.out.println("Demonstrating working with a serialized classifier");
		ColumnDataClassifier cdc = new ColumnDataClassifier("F:/examples/semeval.prop");
		Classifier<String,String> cl =
				cdc.makeClassifier(cdc.readTrainingExamples("F:/examples/semeval.train"));

		// Exhibit serialization and deserialization working. Serialized to bytes in memory for simplicity
		System.out.println(); System.out.println();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(cl);
		oos.close();
		byte[] object = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(object);
		ObjectInputStream ois = new ObjectInputStream(bais);
		LinearClassifier<String,String> lc = ErasureUtils.uncheckedCast(ois.readObject());
		ois.close();
		ColumnDataClassifier cdc2 = new ColumnDataClassifier("F:/examples/semeval.prop");

		// We compare the output of the deserialized classifier lc versus the original one cl
		// For both we use a ColumnDataClassifier to convert text lines to examples
		for (String line : ObjectBank.getLineIterator("F:/examples/semeval.test")) {
			Datum<String,String> d = cdc.makeDatumFromLine(line);
			Datum<String,String> d2 = cdc2.makeDatumFromLine(line);
			System.out.println(line + "  =origi=>  " + cl.classOf(d));
			System.out.println(line + "  =deser=>  " + lc.classOf(d2));
		}
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
    public static double ComputeNorm(Map<String, Double> termFrequencies){
    	
    	double norm = 0.0;
    	
    	for(Map.Entry<String, Double> entry : termFrequencies.entrySet()){
    		norm += entry.getValue() * entry.getValue();
    	}
    	return Math.sqrt(norm);
    	
    }
	public static boolean isNaN(double x) {return x != x;}

    
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

}
