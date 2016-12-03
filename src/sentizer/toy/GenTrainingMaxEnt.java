package sentizer.toy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;

import edu.stanford.nlp.objectbank.ObjectBank;
import sentizer.training.FileFunction;

public class GenTrainingMaxEnt {
	public static void main(String args[]) throws Exception{
		
		
		FileReader frlex = new FileReader(new File("F:/examples/semeval_lex2.train"));
		BufferedReader brlex = new BufferedReader(frlex);
		
		Map<String,String> mapCat = FileFunction.readMapStrStr("F:/Semeval2014/training/tweetCategoryMainMap.txt");
		
		Map<String,String> mapNaverEn = FileFunction.readMapStrStr("F:/NaverCategoryEnMainMap.txt");
				
		FileWriter fw = new FileWriter(new File("F:/examples/semeval_lex2_cat.train"));
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (String line : ObjectBank.getLineIterator("F:/examples/finalTrainingInput.txt")) {
			String orgStr = brlex.readLine();
			String lineArr[] = line.split("\t");
			if(mapCat.containsKey(lineArr[0])){
				
				System.out.println(lineArr[0]);
				System.out.println(mapCat.get(lineArr[0]));
				System.out.println(mapNaverEn.get(mapCat.get(lineArr[0])));
				
				bw.write(orgStr + "\t" + mapNaverEn.get(mapCat.get(lineArr[0])));
				bw.newLine();
			}
			
		}
		bw.close();
		fw.close();
		
		brlex.close();
		frlex.close();
		
		System.out.println("complete");

	}

}
