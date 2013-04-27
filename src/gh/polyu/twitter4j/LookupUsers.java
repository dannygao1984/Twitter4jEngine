package gh.polyu.twitter4j;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public final class LookupUsers {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	 {
	       
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            
            //Authorize the Twitter
    		twitterOAuth.ReadProperties("properties/twitter4j.properties");
    		twitterOAuth.Authority(twitter, twitterOAuth.prop.getProperty("consumerKey"), 
    				twitterOAuth.prop.getProperty("consumerSecret"));
    		
            String[] usrlist = new String[]{"dannygao"};
            ResponseList<User> users = twitter.lookupUsers(usrlist);
            for (User user : users) {
                if (user.getStatus() != null) {
                    System.out.println("@" + user.getScreenName() + " - " + user.getStatus().getText());
                } else {
                    // the user is protected
                    System.out.println("@" + user.getScreenName());
                }
            }
           
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to lookup users: " + te.getMessage());
            System.exit(-1);
        }
	  }

}
