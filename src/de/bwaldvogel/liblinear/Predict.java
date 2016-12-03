package de.bwaldvogel.liblinear;

import static de.bwaldvogel.liblinear.Linear.atof;
import static de.bwaldvogel.liblinear.Linear.atoi;
import static de.bwaldvogel.liblinear.Linear.closeQuietly;
import static de.bwaldvogel.liblinear.Linear.printf;
import static de.bwaldvogel.liblinear.Linear.info;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class Predict {

    private static boolean       flag_predict_probability = false;

    private static final Pattern COLON                    = Pattern.compile(":");

    /**
     * <p><b>Note: The streams are NOT closed</b></p>
     */
    static void doPredict(BufferedReader reader, Writer writer, Model model) throws IOException {
        int correct = 0;
        int total = 0;
        double error = 0;
        double sump = 0, sumt = 0, sumpp = 0, sumtt = 0, sumpt = 0;

        int nr_class = model.getNrClass();
        double[] prob_estimates = null;
        int n;
        int nr_feature = model.getNrFeature();
        if (model.bias >= 0)
            n = nr_feature + 1;
        else
            n = nr_feature;

        if (flag_predict_probability && !model.isProbabilityModel()) {
            throw new IllegalArgumentException("probability output is only supported for logistic regression");
        }

        Formatter out = new Formatter(writer);

        if (flag_predict_probability) {
            int[] labels = model.getLabels();
            prob_estimates = new double[nr_class];

            printf(out, "labels");
            for (int j = 0; j < nr_class; j++)
                printf(out, " %d", labels[j]);
            printf(out, "\n");
        }


        String line = null;
        while ((line = reader.readLine()) != null) {
            List<Feature> x = new ArrayList<Feature>();
            StringTokenizer st = new StringTokenizer(line, " \t\n");
            double target_label;
            try {
                String label = st.nextToken();
                target_label = atof(label);
            } catch (NoSuchElementException e) {
                throw new RuntimeException("Wrong input format at line " + (total + 1), e);
            }

            while (st.hasMoreTokens()) {
                String[] split = COLON.split(st.nextToken(), 2);
                if (split == null || split.length < 2) {
                    throw new RuntimeException("Wrong input format at line " + (total + 1));
                }

                try {
                    int idx = atoi(split[0]);
                    double val = atof(split[1]);

                    // feature indices larger than those in training are not used
                    if (idx <= nr_feature) {
                        Feature node = new FeatureNode(idx, val);
                        x.add(node);
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Wrong input format at line " + (total + 1), e);
                }
            }

            if (model.bias >= 0) {
                Feature node = new FeatureNode(n, model.bias);
                x.add(node);
            }

            Feature[] nodes = new Feature[x.size()];
            nodes = x.toArray(nodes);

            double predict_label;

            if (flag_predict_probability) {
                assert prob_estimates != null;
                predict_label = Linear.predictProbability(model, nodes, prob_estimates);
                printf(out, "%g", predict_label);
                for (int j = 0; j < model.nr_class; j++)
                    printf(out, " %g", prob_estimates[j]);
                printf(out, "\n");
            } else {
                predict_label = Linear.predict(model, nodes);
                printf(out, "%g\n", predict_label);
            }

            if (predict_label == target_label) {
                ++correct;
            }

            error += (predict_label - target_label) * (predict_label - target_label);
            sump += predict_label;
            sumt += target_label;
            sumpp += predict_label * predict_label;
            sumtt += target_label * target_label;
            sumpt += predict_label * target_label;
            ++total;
        }

        if (model.solverType.isSupportVectorRegression()) //
        {
            info("Mean squared error = %g (regression)%n", error / total);
            info("Squared correlation coefficient = %g (regression)%n", //
                ((total * sumpt - sump * sumt) * (total * sumpt - sump * sumt)) / ((total * sumpp - sump * sump) * (total * sumtt - sumt * sumt)));
        } else {
            info("Accuracy = %g%% (%d/%d)%n", (double)correct / total * 100, correct, total);
        }
    }

    private static void exit_with_help() {
        System.out.printf("Usage: predict [options] test_file model_file output_file%n" //
            + "options:%n" //
            + "-b probability_estimates: whether to output probability estimates, 0 or 1 (default 0); currently for logistic regression only%n" //
            + "-q quiet mode (no outputs)%n");
        System.exit(1);
    }

    public static void main(String[] argv) throws IOException {
        int i;
        
		String[] param = new String[5];
		
		String type = "-s1";
		String ver = "-v1.57";
		
		param[0] = "F:/svm/semeval14.test";
		param[1] = "F:/svm/semeval14.linear"+type+ver+".model";
		param[2] = "F:/svm/semeval14.linear"+type+ver+".output";
    	
		param[3] = "-b";
		param[4] = "1";
    	
		
        // parse options
        for (i = 0; i < param.length; i++) {
            if (param[i].charAt(0) != '-') break;
            ++i;
            switch (param[i - 1].charAt(1)) {
                case 'b':
                    try {
                        flag_predict_probability = (atoi(param[i]) != 0);
                    } catch (NumberFormatException e) {
                        exit_with_help();
                    }
                    break;

                case 'q':
                    i--;
                    Linear.disableDebugOutput();
                    break;

                default:
                    System.err.printf("unknown option: -%d%n", param[i - 1].charAt(1));
                    exit_with_help();
                    break;
            }
        }
        if (i >= param.length || param.length <= i + 2) {
            exit_with_help();
        }

        BufferedReader reader = null;
        Writer writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(param[i]), Linear.FILE_CHARSET));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(param[i + 2]), Linear.FILE_CHARSET));

            Model model = Linear.loadModel(new File(param[i + 1]));
            doPredict(reader, writer, model);
        }
        finally {
            closeQuietly(reader);
            closeQuietly(writer);
        }

		FileReader fr_res = new FileReader(new File("F:\\svm\\semeval14.linear"+type+ver+".output"));
		BufferedReader br_res = new BufferedReader(fr_res);

		FileWriter fw_res = new FileWriter(new File("F:\\Semeval2014\\evaluation\\candidate-LSVM"+type+ver+".txt"));
		BufferedWriter bw_res = new BufferedWriter(fw_res);
	    
		String line = "";
		int res_ct = 1;
		while(true){

			line = br_res.readLine();
			if(line == null) break;

			String label = "";
			
			if(line.startsWith("l")) continue;
			
			if(line.startsWith("1")){
				label = "positive";
			}else if(line.startsWith("2")){
				label = "negative";
			}else if(line.startsWith("3")){
				label = "neutral";
			}
			
			bw_res.write("NA" + "\t" + res_ct + "\t" + label);			
			bw_res.newLine();
			
			res_ct++;
			
		}

		bw_res.close();
		fw_res.close();
		
		br_res.close();
		fr_res.close();

    }
}
