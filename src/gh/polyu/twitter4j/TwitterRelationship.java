package gh.polyu.twitter4j;

import java.util.ArrayList;

import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.Twitter;

public class TwitterRelationship {

	public ArrayList<Long> getFriends(Twitter twitter,
			String strName) throws TwitterException
	{
		User usr = twitter.showUser(strName);
		return getFriends(twitter, usr.getId());
	}
	
	public ArrayList<Long> getFriends(Twitter twitter,
			long ld) throws TwitterException
	{
		ArrayList<Long> listUserlds = new ArrayList<Long>();
		long cursor = -1;
        IDs ids = null;
        //System.out.println("Listing following ids.");
        do {
            ids = twitter.getFriendsIDs(ld, cursor);
            
            for(long l : ids.getIDs())
            {
            	listUserlds.add(l);
            }   
            
        } while ((cursor = ids.getNextCursor()) != 0);
        		
		return listUserlds;
	}
	
	public ArrayList<Long> getFollowers(Twitter twitter,
			long ld) throws TwitterException
	{
		ArrayList<Long> listUserlds = new ArrayList<Long>();
		long cursor = -1;
        IDs ids = null;
        //System.out.println("Listing following ids.");
        do {
            ids = twitter.getFollowersIDs(ld, cursor);
            
            for(long l : ids.getIDs())
            {
            	listUserlds.add(l);
            }              
        } while ((cursor = ids.getNextCursor()) != 0);
        
		return listUserlds;
	}
	
	public ArrayList<Long> getFollowers(Twitter twitter,
			String strName) throws TwitterException
	{
		User usr = twitter.showUser(strName);
		return getFollowers(twitter, usr.getId());
	}	
	
	public void IsFollower(Twitter twitter, String strUser1, String strUser2)
	{
        try {
			boolean isAFollowingB = twitter.existsFriendship(strUser1, strUser2);
			System.out.println("@" + strUser1 + (isAFollowingB ? " is" : " isn't") 
					+ " following @" + strUser2);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Twitter twitter = new TwitterFactory().getInstance();
		twitterOAuth.ReadProperties("properties/twitter4j.properties");
		twitterOAuth.Authority(twitter, twitterOAuth.prop.getProperty("consumerKey"), 
				twitterOAuth.prop.getProperty("consumerSecret"));
		
		TwitterRelationship twtRel = new TwitterRelationship();
		try {
			
			//114704299:dannygao
			long ld = 114704299;
			
			ArrayList<Long> usersFr = twtRel.getFriends(twitter, ld);
			System.out.println("Friends size:" + usersFr.size());
			
			for(long usr : usersFr)
			{
				System.out.println(usr);
			}
			
			ArrayList<Long> usersFl = twtRel.getFollowers(twitter, ld);
			System.out.println("Followers size:" + usersFl.size());
			for(long usr : usersFl)
			{
				System.out.println(usr);
			}
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
}
