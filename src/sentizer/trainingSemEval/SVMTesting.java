package sentizer.trainingSemEval;

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

public class SVMTesting {

	public static void main(String[] args) throws Exception {
		
		String mStr = "lex_v1.16";

		// in case only prop is different
		String pStr = mStr + "";

		FeatureGenerator featureGenerator = new FeatureGenerator();

		boolean isCat = false;

		ColumnDataClassifier cdc = new ColumnDataClassifier("F:/examples/semeval_"+pStr+".prop");
		
		FileWriter fw = new FileWriter(new File("F:\\Semeval2014\\evaluation\\candidate-MaxEnt-"+ pStr +".txt"));
		BufferedWriter bw = new BufferedWriter(fw);	

		Boolean isLex = true;

		Classifier<String,String> cl =
				cdc.makeClassifier(cdc.readTrainingExamples("F:/examples/semeval_"+mStr+".train"));

		String modelFilename = "D:\\project2nd\\dataset_sentizer\\model.20120919";

		Tagger tagger;		
		tagger = new Tagger();
		tagger.loadModel(modelFilename);
		
		int ct = 1;

		for (String line : ObjectBank.getLineIterator("F:/examples/semeval.test")) {

			if(isLex){
				
				String lineArr[] = line.split("\t");
				String classStr = lineArr[0];
			
				List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(lineArr[1]);
			
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
				tweet = tweet.trim();
				tweetPOS = tweetPOS.trim();
				
				String elements[] = featureGenerator.getFeatures(tweet, tweetPOS, classStr);
				
				//elements[1] = wordSeq;
				elements[1] = lineArr[1];
				
				Datum<String,String> d = cdc.makeDatumFromStrings(elements);
				bw.write("NA" + "\t" + ct + "\t" + cl.classOf(d));
				bw.newLine();
				ct++;
				
			}else{}

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
