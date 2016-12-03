package sentizer.test;

import java.util.ArrayList;

import data.Pair;
import sentizer.util.Word2vecReader;

public class Word2vecReaderTest {
	
	public static void main(String args[]){
		
		
		Word2vecReader word2vec = new Word2vecReader();
		
		ArrayList<Pair<String, Double>> alist = word2vec.getCloest("sick", 30);
		
		double w2v[] = word2vec.getWordRepresentation("happy");
		for(Pair<String, Double> pair : alist ){
			String key = pair.getFirst();
			Double value = pair.getSecond();
			
			System.out.println(key +":"+value);
			
		}
		
		
		for(double d : w2v ){
			//System.out.println(d);
			
		}
		
		
		
		
	}

}
