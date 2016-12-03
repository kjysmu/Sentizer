package sentizer.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tpeng
 * Date: 6/22/12
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Logistic {

    /** the learning rate */
    private double rate;

    /** the weight to learn */
    private double[] weights;

    /** the number of iterations */
    private int ITERATIONS = 3000;

    public Logistic(int n) {
        this.rate = 0.0001;
        weights = new double[n];
    }

    private double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }

    public void train(List<Instance> instances) {
        for (int n=0; n<ITERATIONS; n++) {
            double lik = 0.0;
            for (int i=0; i<instances.size(); i++) {
                double[] x = instances.get(i).getX();
                double predicted = classify(x);
                int label = instances.get(i).getLabel();
                for (int j=0; j<weights.length; j++) {
                    weights[j] = weights[j] + rate * (label - predicted) * x[j];
                }
                // not necessary for learning
                lik += label * Math.log(classify(x)) + (1-label) * Math.log(1- classify(x));
            }
            System.out.println("iteration: " + n + " " + Arrays.toString(weights) + " mle: " + lik);
        }
    }

    private double classify(double[] x) {
        double logit = .0;
        for (int i=0; i<weights.length;i++){
            logit += weights[i] * x[i];
        }
        return sigmoid(logit);
    }

    private void saveModel(File f) throws Exception {
    	
    	FileWriter fw_vec = new FileWriter(f);
		BufferedWriter bw_vec = new BufferedWriter(fw_vec);
		
        for (int i=0; i<weights.length;i++){
        	
        	bw_vec.write(String.format("%.8f", weights[i]));
        	bw_vec.newLine();
           
        }
        bw_vec.close();
        fw_vec.close();
        
    }

    public static void main(String... args) throws Exception {
        List<Instance> instances = DataSetSent.readDataSet("D:\\project2nd\\workspace\\doc2vec\\file\\trainingSNT_200_java.vec");
        //List<Instance> instances = DataSetSent2.readDataSet("D:\\project2nd\\dataset_sentizer\\training_w2v_vector.txt");
        
        
        Logistic logistic = new Logistic(200);
        logistic.train(instances);
        
        //logistic.saveModel(new File("D:\\project2nd\\workspace\\doc2vec\\file\\trainingSNT_logistic_model_w2v.txt"));
        logistic.saveModel(new File("D:\\project2nd\\workspace\\doc2vec\\file\\trainingSNT_logistic_model_d2v.txt"));
        
        
        System.out.println("complete");
        /*
        double[] x = {2, 1, 1, 0, 1};
        System.out.println("prob(1|x) = " + logistic.classify(x));
        System.out.println("prob(0|x) = " + logistic.classify(x));

        double[] x2 = {1, 0, 1, 0, 0};
        System.out.println("prob(1|x2) = " + logistic.classify(x2));
        System.out.println("prob(0|x2) = " + logistic.classify(x2));
         */
        
        
    }
}
