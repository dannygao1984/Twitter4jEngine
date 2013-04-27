package gh.polyu.pool;

import java.util.ArrayList;

import gh.polyu.twitter4j.TwitterRelationship;
import gh.polyu.twitter4j.twitterOAuth;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class TwitterKeyInstance extends TwitterRelationship {

	Twitter twitter = null;
	long lBandTime  = 0;
	int  iResetTimeInSecond = 0;
	boolean bBand   = false;
	
	public TwitterKeyInstance(Twitter twitter)
	{
		this.twitter = twitter;
	}
	
	public TwitterKeyInstance()
	{
		this.twitter = new TwitterFactory().getInstance();
	}
	
	public ArrayList<Long> getFriends(String strName) 
	{
		try {
			return getFriends(this.twitter, strName);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			setResetTimeInSecond(e);
			setbBand(true);
			return null;
		}
	}
	
	public ArrayList<Long> getFriends(long ld)
	{
		try {
			return getFriends(this.twitter, ld);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			setResetTimeInSecond(e);
			setbBand(true);
			return null;
		}
	}
	
	public ArrayList<Long> getFollowers(long ld)
	{
		try {
			return getFollowers(this.twitter, ld);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			setResetTimeInSecond(e);
			setbBand(true);
			return null;
		}
	}
	
	public ArrayList<Long> getFollowers(String strName)
	{
		try {
			return getFollowers(this.twitter, strName);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			setResetTimeInSecond(e);
			setbBand(true);
			return null;
		}
	}
	
	public int InitialAndOauthKey(String strKey, String strSecret)
	{
		if(this.twitter == null)
		{
			this.twitter = new TwitterFactory().getInstance();
		}		
		return OAuthKey(strKey, strSecret);
	}
	
	private int OAuthKey(String strKey, String strSecret)
	{
		return twitterOAuth.Authority(twitter,
				 strKey, strSecret);		
	}
	
	public boolean isbBand() {
		return bBand;
	}

	public void setbBand(boolean bBand) {
		this.bBand = bBand;
	}

	public boolean Try2RefreshTwitter()
	{
		if(bBand)
		{
			long lTimeSpan = System.currentTimeMillis() - lBandTime;
			if ((int)(lTimeSpan/100) > this.iResetTimeInSecond)
			{
				bBand = false;
				lBandTime = 0;
				return true;
			}else
			{
				return false;
			}
		}else 
		{
			lBandTime = 0;
			
			return true;
		}
		 
	}
	
	public void setResetTimeInSecond(TwitterException e)
	{
		this.lBandTime = System.currentTimeMillis();
		RateLimitStatus rate = e.getRateLimitStatus();
		setResetTimeInSecond(rate.getSecondsUntilReset());
	}
	
	public void setResetTimeInSecond(int iTime)
	{
		this.iResetTimeInSecond = iTime;
	}
	
	public int getResetTimeInSecond()
	{
		return this.iResetTimeInSecond;
	}
	
}
