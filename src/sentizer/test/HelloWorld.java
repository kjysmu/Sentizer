package sentizer.test;

import sentizer.util.Word2vecReader;

public class HelloWorld {
	public static void main(String args[]){
		
		Word2vecReader w2v = new Word2vecReader();
		
		double[] vec = w2v.getWordRepresentation("happy");
		
		int ct = 1;
		for(double d : vec){
			System.out.println( ct + " : " + String.format("%.8f", d) );
			ct++;
		}
		
		
	}

}
