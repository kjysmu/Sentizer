package sentizer.trainingSemEval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import sentizer.training.FileFunction;
import sentizer.util.DoubleValueComparator;
import sentizer.util.TermFunction;

public class TweetIDF {
	
	public static void main(String args[]) throws Exception{
		
		String pathNeg = "F:\\Semeval2014\\training\\sentiment140\\training_neg.txt";
		String pathPos = "F:\\Semeval2014\\training\\sentiment140\\training_pos.txt";
		String pathNeu = "F:\\Semeval2014\\training\\sentiment140\\training_neu.txt";
	
		Map<String, Integer> mapNeg = FileFunction.readMapStrInt(new File(pathNeg));
		Map<String, Integer> mapPos = FileFunction.readMapStrInt(new File(pathPos));
		Map<String, Integer> mapNeu = FileFunction.readMapStrInt(new File(pathNeu));
		
		mapNeg = TermFunction.mapThresholdInt(mapNeg, 0.0001);
		mapPos = TermFunction.mapThresholdInt(mapPos, 0.0001);
		mapNeu = TermFunction.mapThresholdInt(mapNeu, 0.0001);
		
		Map<String, Integer> mapTotal = new HashMap<String,Integer>();        
		
		Map<String, Double> idf = new HashMap<String, Double>();

		mapTotal = TermFunction.CombineCountsInt(mapTotal, mapNeg);
		mapTotal = TermFunction.CombineCountsInt(mapTotal, mapPos);
		mapTotal = TermFunction.CombineCountsInt(mapTotal, mapNeu);
		
		for(Map.Entry<String, Integer> entry : mapTotal.entrySet()){
			String key = entry.getKey();
			int value = entry.getValue();
			int count = 0 ;
			
			if(mapNeg.containsKey(key)){
				count++;
			}
			if(mapPos.containsKey(key)){
				count++;
			}
			if(mapNeu.containsKey(key)){
				count++;
			}
								
			
			idf.put( key, Math.log( 3.0 / count ) + 0.1 );
			
			
		}
		
		FileFunction.writeMapStrDou(idf, "F:\\Semeval2014\\training\\sentiment140\\idf.txt", 8);
		
		
		System.out.println("complete!");
		
		
	}

}
