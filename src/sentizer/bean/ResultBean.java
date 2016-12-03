package sentizer.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResultBean {

	InputBean inputBean;
	Map<String, TweetBean> resultMap;
	
	int posTweetNum;
	int negTweetNum;
	int neuTweetNum;

	public ResultBean(){
		inputBean = new InputBean();
		resultMap = new HashMap<String, TweetBean>();
		
		posTweetNum = 0;
		negTweetNum = 0;
		neuTweetNum = 0;
		
		
	}
	
	public void setInputBean(InputBean inputBean){
		this.inputBean = inputBean;
	}
	public InputBean getInputBean(){
		return inputBean;
	}
	
	public void addTweetResult(String id, TweetBean tbean){
		
		resultMap.put(id, tbean);
		
		String sentiment = tbean.getSentiment();
		
		if(sentiment.equals("Positive")){
			posTweetNum++;
		}else if(sentiment.equals("Negative")){
			negTweetNum++;
		}else{
			neuTweetNum++;
		}
	}
	
	
	public Map<String, TweetBean> getResultMap(){
		return resultMap;	
	}
	public int getPosTweetNum(){
		return posTweetNum;
	}
	public int getNegTweetNum(){
		return negTweetNum;
	}
	public int getNeuTweetNum(){
		return neuTweetNum;
	}
	
	
}
