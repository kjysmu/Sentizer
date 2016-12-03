package sentizer.trainingSemEval_new;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import sentizer.training.FileFunction;
import sentizer.util.Tagger;
import sentizer.util.Word2vecReader;
import sentizer.util.Tagger.TaggedToken;

public class GenWordVecSimilarity {


	public static void main(String args[]) throws Exception{

		Word2vecReader w2v;
		w2v = new Word2vecReader();
		
		
		String pathW2V_pos = "F:\\w2v\\pos.txt";
		String pathW2V_neg = "F:\\w2v\\neg.txt";
		String pathW2V_neu = "F:\\w2v\\neu.txt";

		String pathW2V_pos_sim = "F:\\w2v\\pos_sim.txt";
		String pathW2V_neg_sim = "F:\\w2v\\neg_sim.txt";
		String pathW2V_neu_sim = "F:\\w2v\\neu_sim.txt";

		Map<String, String> mapTweetID = new HashMap<String, String>();
		String line = "";
		int ct = 0;
		
		double[] wvecTweet = new double[200];

		FileReader fr_pos = new FileReader(new File(pathW2V_pos));
		BufferedReader br_pos = new BufferedReader(fr_pos);

		FileReader fr_neg = new FileReader(new File(pathW2V_neg));
		BufferedReader br_neg = new BufferedReader(fr_neg);
		
		FileReader fr_neu = new FileReader(new File(pathW2V_neu));
		BufferedReader br_neu = new BufferedReader(fr_neu);

		
		
		FileWriter fw_pos = new FileWriter(new File(pathW2V_pos_sim));
		BufferedWriter bw_pos = new BufferedWriter(fw_pos);

		FileWriter fw_neg = new FileWriter(new File(pathW2V_neg_sim));
		BufferedWriter bw_neg = new BufferedWriter(fw_neg);
		
		FileWriter fw_neu = new FileWriter(new File(pathW2V_neu_sim));
		BufferedWriter bw_neu = new BufferedWriter(fw_neu);
		
		int numPos = 0;
		int numNeg = 0;
		int numNeu = 0;
		
		Map<String, double[]> mapW2V_pos = new HashMap<String, double[]>();
		Map<String, double[]> mapW2V_neg = new HashMap<String, double[]>();
		Map<String, double[]> mapW2V_neu = new HashMap<String, double[]>();
				
		
		while(true){
			line = br_pos.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length >= 2){
	
				String tweetID= splitStr[0];
				
				String w2vstr = splitStr[1];
				String w2vstr_arr[] = w2vstr.split(" ");
				
				double w2v_arr[] = new double[200];
				for(int i=0; i<200; i++){
					w2v_arr[i] = Double.parseDouble(w2vstr_arr[i]);
				}
				
				mapW2V_pos.put(tweetID, w2v_arr);
				
			}
		}

		br_pos.close();
		fr_pos.close();
		
		while(true){
			line = br_neg.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length >= 2){
	
				String tweetID= splitStr[0];
				
				String w2vstr = splitStr[1];
				String w2vstr_arr[] = w2vstr.split(" ");
				
				double w2v_arr[] = new double[200];
				for(int i=0; i<200; i++){
					w2v_arr[i] = Double.parseDouble(w2vstr_arr[i]);
				}
				
				mapW2V_neg.put(tweetID, w2v_arr);
				
			}
		}

		br_neg.close();
		fr_neg.close();

		while(true){
			line = br_neu.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length >= 2){
	
				String tweetID= splitStr[0];
				
				String w2vstr = splitStr[1];
				String w2vstr_arr[] = w2vstr.split(" ");
				
				double w2v_arr[] = new double[200];
				for(int i=0; i<200; i++){
					w2v_arr[i] = Double.parseDouble(w2vstr_arr[i]);
				}
				
				mapW2V_neu.put(tweetID, w2v_arr);
				
			}
		}

		br_neu.close();
		fr_neu.close();
		
		Set<String> set_pos = new HashSet<String>();
		Set<String> set_neg = new HashSet<String>();
		Set<String> set_neu = new HashSet<String>();
		
		Map<String, Double> scoreMap_pos = new HashMap<String, Double>();
		Map<String, Double> scoreMap_neg = new HashMap<String, Double>();
		Map<String, Double> scoreMap_neu = new HashMap<String, Double>();

		System.out.println("calculating pos sim...");
		
		double maxScore_pos = 0.0;
		double minScore_pos = 0.0;
		boolean isFst = true;		
		for(Map.Entry<String, double[]> entry1 : mapW2V_pos.entrySet() ){
			String key1 = entry1.getKey();
			double value1[] = entry1.getValue();
		
			for(Map.Entry<String, double[]> entry2 : mapW2V_pos.entrySet() ){
		
				String key2 = entry2.getKey();
				double value2[] = entry2.getValue();
				
				if(!key1.equals(key2)){
					if(!set_pos.contains(key1+" "+key2)){
						double score = distance(value1, value2);
						
						if(isFst){
							maxScore_pos = score;
							minScore_pos = score;
							isFst = false;
						}else{
							
							if(maxScore_pos < score){
								maxScore_pos = score;
							}
							
							if(minScore_pos > score){
								minScore_pos = score;
							}
							
						}
						
						scoreMap_pos.put(key1+"\t"+key2, score);
						
					}
				}
				
				set_pos.add(key1 + " " + key2);
				set_pos.add(key2 + " " + key1);
			
			}
			
		}
		
		
		for(Map.Entry<String, Double> entry : scoreMap_pos.entrySet() ){
			String key = entry.getKey();
			double value = entry.getValue();
			

			if( value > 0.999 ){
				double score_norm = (value - 0.999) / (maxScore_pos - 0.999) ;
				bw_pos.write(key + " > " + String.format("%.8f", score_norm));
				bw_pos.newLine();
								
			}
			
			
			
		}
			
		
		
		
		System.out.println("calculating neg sim...");

		

		double maxScore_neg = 0.0;
		double minScore_neg = 0.0;
		isFst = true;		
		for(Map.Entry<String, double[]> entry1 : mapW2V_neg.entrySet() ){
			String key1 = entry1.getKey();
			double value1[] = entry1.getValue();
		
			for(Map.Entry<String, double[]> entry2 : mapW2V_neg.entrySet() ){
		
				String key2 = entry2.getKey();
				double value2[] = entry2.getValue();
				
				if(!key1.equals(key2)){
					if(!set_neg.contains(key1+" "+key2)){
						double score = distance(value1, value2);
						
						if(isFst){
							maxScore_neg = score;
							minScore_neg = score;
							isFst = false;
						}else{
							
							if(maxScore_neg < score){
								maxScore_neg = score;
							}
							
							if(minScore_neg > score){
								minScore_neg = score;
							}
							
						}
						
						scoreMap_neg.put(key1+"\t"+key2, score);
						
					}
				}
				
				set_neg.add(key1 + " " + key2);
				set_neg.add(key2 + " " + key1);
			
			}
			
		}
		
		System.out.println("min neg "+minScore_neg);
		System.out.println("max neg "+maxScore_neg);
		
		
		for(Map.Entry<String, Double> entry : scoreMap_neg.entrySet() ){
			String key = entry.getKey();
			double value = entry.getValue();
			
			if( value > 0.999 ){
				double score_norm = (value - 0.999) / (maxScore_neg - 0.999) ;
				bw_neg.write(key + " > " + String.format("%.8f", score_norm));
				bw_neg.newLine();
								
			}
			

		}

		
		
		System.out.println("calculating neu sim...");
		
		double maxScore_neu = 0.0;
		double minScore_neu = 0.0;
		isFst = true;		
		for(Map.Entry<String, double[]> entry1 : mapW2V_neu.entrySet() ){
			String key1 = entry1.getKey();
			double value1[] = entry1.getValue();
		
			for(Map.Entry<String, double[]> entry2 : mapW2V_neu.entrySet() ){
		
				String key2 = entry2.getKey();
				double value2[] = entry2.getValue();
				
				if(!key1.equals(key2)){
					if(!set_neu.contains(key1+" "+key2)){
						double score = distance(value1, value2);
						
						if(isFst){
							maxScore_neu = score;
							minScore_neu = score;
							isFst = false;
						}else{
							
							if(maxScore_neu < score){
								maxScore_neu = score;
							}
							
							if(minScore_neu > score){
								minScore_neu = score;
							}
							
						}
						
						scoreMap_neu.put(key1+"\t"+key2, score);
						
					}
				}
				
				set_neu.add(key1 + " " + key2);
				set_neu.add(key2 + " " + key1);
			
			}
			
		}
		
		
		for(Map.Entry<String, Double> entry : scoreMap_neu.entrySet() ){
			String key = entry.getKey();
			double value = entry.getValue();

			if( value > 0.999 ){
				double score_norm = (value - 0.999) / (maxScore_neu - 0.999) ;
				bw_neu.write(key + " > " + String.format("%.8f", score_norm));
				bw_neu.newLine();
								
			}
			
			
		}

		
		
		
		
		bw_pos.close();
		fw_pos.close();

		bw_neg.close();
		fw_neg.close();

		bw_neu.close();
		fw_neu.close();

		System.out.println("complete");

	}

	public static double distance(double[] v1, double[] v2){
		
		double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;
 
        for (int i = 0; i < v1.length; i++) //v1 and v2 must be of same length
        {
            dotProduct += v1[i] * v2[i];  //a.b
            magnitude1 += Math.pow(v1[i], 2);  //(a^2)
            magnitude2 += Math.pow(v2[i], 2); //(b^2)
        }
 
        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)
 
        if (magnitude1 != 0.0 | magnitude2 != 0.0){
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } 
        else{
            return 0.0;
        }
        return cosineSimilarity;
		
	}
	
	

}
