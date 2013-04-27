package gh.polyu.thread;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import gh.polyu.common.CommonFunction;
import gh.polyu.database.TwitterDBHandle;
import gh.polyu.twitter4jEngine.GlobalParameterOfTweetsDownloadAndHyperlinkDecoder;
import gh.polyu.twitter4jEngine.GlobalParameters;
import gh.polyu.twittercore.*;
import gh.polyu.twitter4j.*;

public class ThreadOfTweets extends SearchTweets implements Runnable {

	Hashtable<String, _TrendInfo> hasTrends = new Hashtable<String, _TrendInfo>();
	Twitter twitter = new TwitterFactory().getInstance();
	private boolean bExit = false;
	private Hashtable<String, _HyperlinkInfo> hasUrls = null;
	private Hashtable<Long, Integer> hasTweetID = null;
	private TwitterDBHandle twitterDbHandle = new TwitterDBHandle();
	private String table = "";
	private GlobalParameterOfTweetsDownloadAndHyperlinkDecoder glb = null;
	
	public ThreadOfTweets(Hashtable<String, _HyperlinkInfo> has,
			GlobalParameterOfTweetsDownloadAndHyperlinkDecoder glb)
	{
		if (UpdateCurrentTrends() > 0)
		{
			this.hasUrls = has;
			this.glb = glb;
			System.out.println("Tweets and trends thread starting....");
		}else
		{
			System.out.println("updating trends error");
			System.exit(-1);
		}
		
		System.out.println("Tweets Thread Database connecting .....");
		twitterDbHandle.intialTwitterDBhandle();
		if (twitterDbHandle.database_connection() != 1)
		{
			System.out.println("Twitter database connect error");
			System.exit(-1);
			
		}else
		{
			System.out.println("Twitter database connect OK!");
		}
	}
	
	public ThreadOfTweets(ThreadOfTwitterPool pool, Hashtable<Long, Integer> hasTweetID)
	{
		this.hasTweetID = hasTweetID;
		if (UpdateCurrentTrends() > 0)
		{
			System.out.println("Tweets and trends thread starting....");
		}else
		{
			System.out.println("updating trends error");
			System.exit(-1);
		}
		
		System.out.println("Tweets Thread Database connecting .....");
		twitterDbHandle.intialTwitterDBhandle();
		if (twitterDbHandle.database_connection() != 1)
		{
			System.out.println("Twitter database connect error");
			System.exit(-1);
			
		}else
		{
			System.out.println("Twitter database connect OK!");
		}
	}
	
	
	private int UpdateCurrentTrends() {
		// TODO Auto-generated method stub
		try {
			ResponseList<Trends> trends1 = twitter.getDailyTrends();
			int iAddTrends = 0;
			Trends trends = trends1.get(0);
			for (Trend trend : trends.getTrends()) 
			{
				if(!hasTrends.containsKey(trend.getName()))
				{
					_TrendInfo trendInfo = new _TrendInfo();
	                trendInfo.setStartTime(System.currentTimeMillis());
	                trendInfo.setTrendName(trend.getName());
	                hasTrends.put(trend.getName(), trendInfo);
	                iAddTrends ++;
				}                
            }
			
			for(String trend : hasTrends.keySet())
			{
				if(hasTrends.get(trend).getExistingHour() >= 
							GlobalParameters.TREND_EXISTING_HOURS)
				{
					hasTrends.remove(trend);
					iAddTrends --;
				}
			}
			
			System.out.println("Tatally #" + iAddTrends + " trends updated");
			
			return hasTrends.size();

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return 0;
		} 
	}
	
	public ArrayList<_TweetInfo> SearchAllTrendingTweets()
	{
		ArrayList<_TweetInfo> listTweets = new ArrayList<_TweetInfo>();
		for(String trendName : hasTrends.keySet())
		{
			List<Tweet> list = searchTrendingTweets(twitter, trendName);
			if(null == list)
			{
				continue;
			}
			for(Tweet twt : list)
			{			
				listTweets.add(new _TweetInfo(twt, trendName));
			}
			
		}
		return listTweets;
	}	

	public ArrayList<Long> getAllUsersOfTweets(ArrayList<_TweetInfo> listTweets)
	{
		ArrayList<Long> userslist = new ArrayList<Long>();
		for(_TweetInfo twt : listTweets)
		{
			userslist.add(twt.getlFromUsr());
			if(twt.getlToUsr() != -1)
				userslist.add(twt.getlToUsr());
		}
		
		return userslist;
	}
	
	public String getDatabaseTableNameFromTweetInfo(_TweetInfo newTwt)
	{
		Timestamp timestamp = newTwt.getTweetTimestamp();
		String strMonth = Integer.toString(timestamp.getMonth() + 1);
		if(strMonth.length() == 1)
			strMonth = "0" + strMonth;
		String strYear = Integer.toString(timestamp.getYear() + 1900);
		
		return strYear+strMonth;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.err.println("Thread Tweet begin");
		
		long lastUpdateTrendingTime = System.currentTimeMillis();
		
		while(!isbExit())
		{	
			// Get all tweets
			//ArrayList<_TweetInfo> listTweets = SearchAllTrendingTweets();
			ArrayList<_TweetInfo> listTweets = SearchAllTrendingTweets();
			//System.out.println("In tweet thread: #" + listTweets.size() + " tweets found");
			
			// Insert to Database
			int iCnt = 0;
			for(_TweetInfo twtInfo : listTweets)
			{
				if(!hasTweetID.containsKey(twtInfo.getlId()))
				{
					twitterDbHandle.insertTweet2DB(twtInfo);
					hasTweetID.put(twtInfo.getlId(), 1);
					iCnt++;
				}				
				//System.out.println(twtInfo.toString());				
			}
			System.out.println("In tweet thread: #" + iCnt + " tweets are insert to DB");
			
			// Extract all the urls and write to hashtable
//			ArrayList<String> listUrls = new ArrayList<String>();
//			for(_TweetInfo twtInfo : listTweets)
//			{
//				listUrls.addAll(CommonFunction.GetLinksFromTweet(twtInfo.getStrText()));
//			}	
//			System.err.println("#" + listUrls.size() + " Links extracted from tweets");
//			int iUpdatedCnt = Write2HashTable(this.hasUrls, listUrls);
//			System.err.println("#" + iUpdatedCnt + " hyperlinks are updated into hashtable");
			
//			listUrls.clear(); listUrls = null;
//			listTweets.clear(); listTweets = null;
			
			// update trending topics
			if(((System.currentTimeMillis() - lastUpdateTrendingTime)/1000)
					> 1800)
			{
				lastUpdateTrendingTime = System.currentTimeMillis();
				UpdateCurrentTrends();
				System.gc();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	synchronized private int Write2HashTable(Hashtable<String, _HyperlinkInfo> hasUrls,
			ArrayList<String> listUrls) {
		// TODO Auto-generated method stub
		int iCnt = 0;
		for(String url : listUrls)
		{
			if(! hasUrls.containsKey(url) 
					&& !twitterDbHandle.IsContainHyperlink(url, 
							CommonFunction.GetYear() + CommonFunction.GetMonth() + "Hyperlink"))
			{
				hasUrls.put(url, new _HyperlinkInfo(0, url,
						"0", ""));
				System.err.println("found hyperlink#" + url);
				iCnt ++;
			}
		}
		return iCnt;
		
	}

	public boolean isbExit() {
		return bExit;
	}

	public void setbExist(boolean bExit) {
		this.bExit = bExit;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTable() {
		return table;
	}


}
