package sentizer.tweet;

import java.util.ArrayList;
import java.util.List;

import sentizer.bean.TweetBean;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


public class TweetManager {

    public static ArrayList<TweetBean> getTweets(String topic, int maxTweetNum) {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("9kpJC8YwIFuPEPkHVUaUgQfzX")
		.setOAuthConsumerSecret("snBzbdIf7ghmfS7TCKSLlSegxl2IYBkSh3otLJ1Yufe86mLEAc")
		.setOAuthAccessToken("81024740-jyt50K7EWQnLYw4Vuwjc0uNHKtaHuYSHFAraY3GoG")
		.setOAuthAccessTokenSecret("dBKCHDzxwVrXDN3yXNMVCKjjYEEv8KaQNg6Wa3kqYqRYt");
		
		TwitterFactory tf = new TwitterFactory(cb.build());
    	
        Twitter twitter = tf.getInstance();
        
        ArrayList<TweetBean> tweetList = new ArrayList<TweetBean>();
        TweetBean tbean = new TweetBean();

        try {
            Query query = new Query("lang:en AND #"+topic);
            QueryResult result;
            
            
            do{
	            result = twitter.search(query);
	            
	            List<Status> tweets = result.getTweets();
	            for (Status tweet : tweets) {
	            	tbean = new TweetBean();
	            	tbean.setTweet(tweet.getText());
	            	tbean.setId(tweet.getId());
	                tweetList.add(tbean);
	
	            }
	            
	            if( tweetList.size() >= maxTweetNum ) break;
	            
            }while((query = result.nextQuery()) != null);
            
            /*
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    tweetList.add(tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
            */
            
            
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        
        return tweetList;
    }
}