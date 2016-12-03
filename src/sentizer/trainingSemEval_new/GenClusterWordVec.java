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
import java.util.TreeMap;
import java.util.TreeSet;

import sentizer.training.FileFunction;
import sentizer.util.Tagger;
import sentizer.util.Word2vecReader;
import sentizer.util.Tagger.TaggedToken;

public class GenClusterWordVec {


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
		
		
		
		String pathW2V_cluster_neg = "F:\\w2v\\cluster\\results\\neg_sim.nodes.txt";
		String pathW2V_cluster_pos = "F:\\w2v\\cluster\\results\\pos_sim.nodes.txt";
		String pathW2V_cluster_neu = "F:\\w2v\\cluster\\results\\neu_sim.nodes.txt";

		String pathW2V_cluster_neg_res = "F:\\w2v\\cluster\\results\\neg_sim.centroid.txt";
		String pathW2V_cluster_pos_res = "F:\\w2v\\cluster\\results\\pos_sim.centroid.txt";
		String pathW2V_cluster_neu_res = "F:\\w2v\\cluster\\results\\neu_sim.centroid.txt";

		
		
		FileReader fr_pos_c = new FileReader(new File(pathW2V_cluster_pos));
		BufferedReader br_pos_c = new BufferedReader(fr_pos_c);

		FileReader fr_neg_c = new FileReader(new File(pathW2V_cluster_neg));
		BufferedReader br_neg_c = new BufferedReader(fr_neg_c);
		
		FileReader fr_neu_c = new FileReader(new File(pathW2V_cluster_neu));
		BufferedReader br_neu_c = new BufferedReader(fr_neu_c);

		
		FileWriter fw_pos_c = new FileWriter(new File(pathW2V_cluster_pos_res));
		BufferedWriter bw_pos_c = new BufferedWriter(fw_pos_c);

		FileWriter fw_neg_c = new FileWriter(new File(pathW2V_cluster_neg_res));
		BufferedWriter bw_neg_c = new BufferedWriter(fw_neg_c);
		
		FileWriter fw_neu_c = new FileWriter(new File(pathW2V_cluster_neu_res));
		BufferedWriter bw_neu_c = new BufferedWriter(fw_neu_c);
		
		
		Map<Integer, double[]> clusterVecMap_pos = new HashMap<Integer, double[]>();
		Map<Integer, Integer> clusterVecSizeMap_pos = new HashMap<Integer, Integer>();
		
		while(true){
			line = br_pos_c.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length >= 2){
	
				String tweetID= splitStr[0];
				String clusterID = splitStr[1];
				
				double w2v_arr[] = mapW2V_pos.get(tweetID);
				
				if(clusterVecMap_pos.containsKey(clusterID)){
					double w2v_arr2[] = clusterVecMap_pos.get(clusterID);
					for(int i=0; i<200; i++){
						w2v_arr2[i] = w2v_arr2[i] + w2v_arr[i];
					}
					
					clusterVecMap_pos.put(Integer.parseInt(clusterID), w2v_arr2);
					clusterVecSizeMap_pos.put(Integer.parseInt(clusterID), clusterVecSizeMap_pos.get(clusterID)+1 );

				}else{
					clusterVecMap_pos.put(Integer.parseInt(clusterID), w2v_arr);
					clusterVecSizeMap_pos.put(Integer.parseInt(clusterID), 1);
				}
				
			}
		}
		
	    Map<Integer, double[]> clusterVecMap_pos_sort = new TreeMap<Integer, double[]>(clusterVecMap_pos);

		for(Map.Entry<Integer, double[]> entry : clusterVecMap_pos_sort.entrySet()){
			int key = entry.getKey();
			int size = clusterVecSizeMap_pos.get(key);
			
			double value[] = entry.getValue();
			
			String valueStr = "";
			for(int i=0; i<200; i++){
				value[i] = value[i] / (double) size;
				
				valueStr += String.format("%.8f", value[i])+ " ";
			}
			valueStr = valueStr.trim();
			
			bw_pos_c.write(key + "\t" + valueStr);
			bw_pos_c.newLine();
			
		}

		Map<Integer, double[]> clusterVecMap_neg = new HashMap<Integer, double[]>();
		Map<Integer, Integer> clusterVecSizeMap_neg = new HashMap<Integer, Integer>();
		
		while(true){
			line = br_neg_c.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length >= 2){
	
				String tweetID= splitStr[0];
				String clusterID = splitStr[1];
				
				double w2v_arr[] = mapW2V_neg.get(tweetID);
				
				if(clusterVecMap_neg.containsKey(clusterID)){
					double w2v_arr2[] = clusterVecMap_neg.get(clusterID);
					for(int i=0; i<200; i++){
						w2v_arr2[i] = w2v_arr2[i] + w2v_arr[i];
					}
					
					clusterVecMap_neg.put(Integer.parseInt(clusterID), w2v_arr2);
					clusterVecSizeMap_neg.put(Integer.parseInt(clusterID), clusterVecSizeMap_neg.get(clusterID)+1 );

				}else{
					clusterVecMap_neg.put(Integer.parseInt(clusterID), w2v_arr);
					clusterVecSizeMap_neg.put(Integer.parseInt(clusterID), 1);
				}
				
			}
		}

	    Map<Integer, double[]> clusterVecMap_neg_sort = new TreeMap<Integer, double[]>(clusterVecMap_neg);
		
		for(Map.Entry<Integer, double[]> entry : clusterVecMap_neg_sort.entrySet()){
			int key = entry.getKey();
			int size = clusterVecSizeMap_neg.get(key);
			
			double value[] = entry.getValue();
			
			String valueStr = "";
			for(int i=0; i<200; i++){
				value[i] = value[i] / (double) size;
				
				valueStr += String.format("%.8f", value[i])+ " ";
			}
			valueStr = valueStr.trim();
			
			bw_neg_c.write(key + "\t" + valueStr);
			bw_neg_c.newLine();
			
		}

		Map<Integer, double[]> clusterVecMap_neu = new HashMap<Integer, double[]>();
		Map<Integer, Integer> clusterVecSizeMap_neu = new HashMap<Integer, Integer>();
		
		while(true){
			line = br_neu_c.readLine();
			if(line == null) break;

			String[] splitStr = line.split("\t");

			if(splitStr.length >= 2){
	
				String tweetID= splitStr[0];
				String clusterID = splitStr[1];
				
				double w2v_arr[] = mapW2V_neu.get(tweetID);
				
				if(clusterVecMap_neu.containsKey(clusterID)){
					double w2v_arr2[] = clusterVecMap_neu.get(clusterID);
					for(int i=0; i<200; i++){
						w2v_arr2[i] = w2v_arr2[i] + w2v_arr[i];
					}
					
					clusterVecMap_neu.put(Integer.parseInt(clusterID), w2v_arr2);
					clusterVecSizeMap_neu.put(Integer.parseInt(clusterID), clusterVecSizeMap_neu.get(clusterID)+1 );

				}else{
					clusterVecMap_neu.put(Integer.parseInt(clusterID), w2v_arr);
					clusterVecSizeMap_neu.put(Integer.parseInt(clusterID), 1);
				}
				
			}
		}
		
	    Map<Integer, double[]> clusterVecMap_neu_sort = new TreeMap<Integer, double[]>(clusterVecMap_neu);

		for(Map.Entry<Integer, double[]> entry : clusterVecMap_neu_sort.entrySet()){
			int key = entry.getKey();
			int size = clusterVecSizeMap_neu.get(key);
			
			double value[] = entry.getValue();
			
			String valueStr = "";
			for(int i=0; i<200; i++){
				value[i] = value[i] / (double) size;
				
				valueStr += String.format("%.8f", value[i])+ " ";
			}
			valueStr = valueStr.trim();
			
			bw_neu_c.write(key + "\t" + valueStr);
			bw_neu_c.newLine();
			
		}
		
		bw_pos_c.close();
		fw_pos_c.close();

		bw_neg_c.close();
		fw_neg_c.close();

		bw_neu_c.close();
		fw_neu_c.close();
		
		br_pos_c.close();
		fr_pos_c.close();

		br_neg_c.close();
		fr_neg_c.close();

		br_neu_c.close();
		fr_neu_c.close();

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
