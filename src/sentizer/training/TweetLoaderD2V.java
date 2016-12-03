package sentizer.training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import sentizer.util.Tagger.TaggedToken;
import sentizer.util.Word2vecReader;
import sentizer.util.IntValueComparator;
import sentizer.util.Tagger;

public class TweetLoaderD2V {
	
	public static void main(String args[]) throws Exception{
		

		FileReader fr_snt_vec = new FileReader(new File("D:\\project2nd\\workspace\\doc2vec\\file\\trainingSNT_200_java.vec"));
		BufferedReader br_snt_vec = new BufferedReader(fr_snt_vec);
		
		double[] pos_vec = new double[200];
		double[] neg_vec = new double[200];
		
		int pos_vec_ct = 0;
		int neg_vec_ct = 0;
		
		String line ="";
		
		while(true){
			
			line = br_snt_vec.readLine();
			if(line == null || line.isEmpty()) break;
			
			StringTokenizer st = new StringTokenizer(line, " ");
			String sentname = st.nextToken();
			String[] sentname_sub = sentname.split("_");
			
			int sentno = Integer.parseInt( sentname_sub[1] );
			
			double vec[] = new double[200];

			int vct = 0;
			while(st.hasMoreTokens()){
				vec[vct] = Double.parseDouble(st.nextToken().trim());

				vct++;
				if(vct >= 200) break;
				
			}
			
			if(sentno < 800000){
				
				for(int i=0; i<200; i++){
					neg_vec[i] += vec[i];
				}
				neg_vec_ct++;				
			}else{
				for(int i=0; i<200; i++){
					pos_vec[i] += vec[i];
				}
				pos_vec_ct++;
				
			}

		}
		
		FileWriter fw_doc = new FileWriter(new File("D:\\project2nd\\workspace\\doc2vec\\file\\doc2vec_avg.txt"));
		BufferedWriter bw_doc = new BufferedWriter(fw_doc);
		

		
		for(int i=0; i<200; i++){
			pos_vec[i] /= pos_vec_ct;
			neg_vec[i] /= neg_vec_ct;
			
		}
		
		String posStr = "Positive ";
		String negStr = "Negative ";
		
		for(int i=0; i<200; i++){
			neg_vec[i] /= (double)neg_vec_ct;
			negStr += neg_vec[i]+" ";
				
			pos_vec[i] /= (double)pos_vec_ct;
			posStr += pos_vec[i] + " ";

		}
		
		bw_doc.write(posStr.trim());
		bw_doc.newLine();
		
		bw_doc.write(negStr.trim());
		
		bw_doc.close();
		fw_doc.close();
		
		
		br_snt_vec.close();
		fr_snt_vec.close();
	

	}

}
