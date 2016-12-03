package sentizer.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: tpeng
 * Date: 6/22/12
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataSetSent {

    public static List<Instance> readDataSet(String file) throws FileNotFoundException {
        List<Instance> dataset = new ArrayList<Instance>();
        Scanner scanner = new Scanner(new File(file));
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("#")) {
                continue;
            }
            String[] columns = line.split(" ");
            
            String[] sub_col = columns[0].split("_");
            int sentence_no = Integer.parseInt(sub_col[1]);
            

            // skip first column and last column is the label
            int i = 1;
            double[] data = new double[columns.length-1];
            for (i=1; i<columns.length; i++) {
                data[i-1] = Double.parseDouble(columns[i]);
            }
            
            int label=0;
            
            if(sentence_no >=0 && sentence_no <=799999){
            	label=0;
            }else if(sentence_no >=800000 && sentence_no <=1599999){
            	label=1;
            }
            
            Instance instance = new Instance(label, data);
            dataset.add(instance);
        }
        return dataset;
    }
}
