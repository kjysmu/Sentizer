package sentizer.trainingSemEval;

import libsvm.*;
import java.io.*;
import java.util.*;

public class SVMPredict {
	private static svm_print_interface svm_print_null = new svm_print_interface()
	{
		public void print(String s) {}
	};

	private static svm_print_interface svm_print_stdout = new svm_print_interface()
	{
		public void print(String s)
		{
			System.out.print(s);
		}
	};

	private static svm_print_interface svm_print_string = svm_print_stdout;

	static void info(String s) 
	{
		svm_print_string.print(s);
	}

	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	private static void predict(BufferedReader input, DataOutputStream output, svm_model model, int predict_probability) throws IOException
	{
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1)
		{
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR)
			{
				SVMPredict.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
			}
			else
			{
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model,labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(" "+labels[j]);
				output.writeBytes("\n");
			}
		}
		while(true)
		{
			String line = input.readLine();
			if(line == null) break;

			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

			double target = atof(st.nextToken());
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			double v;
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
			{
				v = svm.svm_predict_probability(model,x,prob_estimates);
				output.writeBytes(v+" ");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(prob_estimates[j]+" ");
				output.writeBytes("\n");
			}
			else
			{
				v = svm.svm_predict(model,x);
				output.writeBytes(v+"\n");
			}

			if(v == target)
				++correct;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			SVMPredict.info("Mean squared error = "+error/total+" (regression)\n");
			SVMPredict.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
			SVMPredict.info("Accuracy = "+(double)correct/total*100+
				 "% ("+correct+"/"+total+") (classification)\n");
	}

	private static void exit_with_help()
	{
		System.err.print("usage: svm_predict [options] test_file model_file output_file\n"
		+"options:\n"
		+"-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
		+"-q : quiet mode (no outputs)\n");
		System.exit(1);
	}

	public static void main(String argv[]) throws IOException
	{
		String resName = "candidate-SVM-v1.0";
		
		String resNamePara = "s0t0";
		
		String[] param = new String[5];
		param[0] = "-b";
		param[1] = "0";
		
		param[2] = "F:/svm/semeval14.test"; //test file
		//param[3] = "F:/svm/semeval14.model"; //model
		
		param[3] = "F:/svm/semeval14."+ resNamePara + ".model"; //model
		param[4] = "F:/svm/semeval14."+ resNamePara + ".output"; //output
		
		int i, predict_probability=0;
        	svm_print_string = svm_print_stdout;

		// parse options
		for(i=0;i<param.length;i++)
		{
			if(param[i].charAt(0) != '-') break;
			++i;
			switch(param[i-1].charAt(1))
			{
				case 'b':
					predict_probability = atoi(param[i]);
					break;
				case 'q':
					svm_print_string = svm_print_null;
					i--;
					break;
				default:
					System.err.print("Unknown option: " + param[i-1] + "\n");
					exit_with_help();
			}
		}
		if(i>=param.length-2)
			exit_with_help();
		try 
		{
			BufferedReader input = new BufferedReader(new FileReader(param[i]));
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(param[i+2])));
			svm_model model = svm.svm_load_model(param[i+1]);
			if (model == null)
			{
				System.err.print("can't open model file "+param[i+1]+"\n");
				System.exit(1);
			}
			if(predict_probability == 1)
			{
				if(svm.svm_check_probability_model(model)==0)
				{
					System.err.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			}
			else
			{
				if(svm.svm_check_probability_model(model)!=0)
				{
					SVMPredict.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			predict(input,output,model,predict_probability);
			input.close();
			output.close();
		} 
		catch(FileNotFoundException e) 
		{
			exit_with_help();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			exit_with_help();
		}
		
		FileReader fr_res = new FileReader(new File("F:\\svm\\semeval14."+resNamePara+".output"));
		BufferedReader br_res = new BufferedReader(fr_res);

		FileWriter fw_res = new FileWriter(new File("F:\\Semeval2014\\evaluation\\"+ resName + "." +resNamePara+ ".txt"));
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
