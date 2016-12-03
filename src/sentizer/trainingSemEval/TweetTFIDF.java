package sentizer.trainingSemEval;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import sentizer.training.FileFunction;
import sentizer.util.TermFunction;

public class TweetTFIDF {
	
	public static void main(String args[]) throws Exception{
		
		String pathNeg = "F:\\Semeval2014\\training\\sentiment140\\training_neg.txt";
		String pathPos = "F:\\Semeval2014\\training\\sentiment140\\training_pos.txt";
		String pathNeu = "F:\\Semeval2014\\training\\sentiment140\\training_neu.txt";
		
		String pathIDF = "F:\\Semeval2014\\training\\sentiment140\\idf.txt";

		Map<String, Integer> mapNeg = FileFunction.readMapStrInt(new File(pathNeg));
		Map<String, Integer> mapPos = FileFunction.readMapStrInt(new File(pathPos));
		Map<String, Integer> mapNeu = FileFunction.readMapStrInt(new File(pathNeu));
		
		mapNeg = TermFunction.mapThresholdInt(mapNeg, 0.0001);
		mapPos = TermFunction.mapThresholdInt(mapPos, 0.0001);
		mapNeu = TermFunction.mapThresholdInt(mapNeu, 0.0001);
		
		Map<String, Double> mapIDF = FileFunction.readMapStrDou(new File(pathIDF));
		
		Map<String, Double> mapNegTFIDF = new HashMap<String, Double>();
		Map<String, Double> mapPosTFIDF = new HashMap<String, Double>();
		Map<String, Double> mapNeuTFIDF = new HashMap<String, Double>();
		
		
		for(Map.Entry<String, Integer> entry : mapNeg.entrySet()){
			String key = entry.getKey();
			int value = entry.getValue();
			if(mapIDF.containsKey(key)){
				mapNegTFIDF.put(key, value * mapIDF.get(key));	
			}
		}
		for(Map.Entry<String, Integer> entry : mapPos.entrySet()){
			String key = entry.getKey();
			int value = entry.getValue();
			if(mapIDF.containsKey(key)){
				mapPosTFIDF.put(key, value * mapIDF.get(key));	
			}
		}
		for(Map.Entry<String, Integer> entry : mapNeu.entrySet()){
			String key = entry.getKey();
			int value = entry.getValue();
			if(mapIDF.containsKey(key)){
				mapNeuTFIDF.put(key, value * mapIDF.get(key));	
			}
		}
		
		
		FileFunction.writeMapStrDou(mapNegTFIDF, "F:\\Semeval2014\\training\\sentiment140\\neg_tfidf.txt", 8);
		FileFunction.writeMapStrDou(mapPosTFIDF, "F:\\Semeval2014\\training\\sentiment140\\pos_tfidf.txt", 8);
		FileFunction.writeMapStrDou(mapNeuTFIDF, "F:\\Semeval2014\\training\\sentiment140\\neu_tfidf.txt", 8);

		
		
		System.out.println("complete!");
		
	}
	
	


}
