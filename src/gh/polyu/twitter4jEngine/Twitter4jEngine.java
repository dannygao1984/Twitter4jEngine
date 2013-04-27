/**
 * This program is twitter engine with twitter4j.
 * The expected functions includes tweet search, user search, 
 * relation search, etc on.
 * 
 * Author: Danny Gao at polyu
 * email: gaodehong_polyu@163.com
 * 
 * 
 * */
package gh.polyu.twitter4jEngine;

import java.util.Hashtable;

import twitter4j.Twitter;
import gh.polyu.thread.ThreadOfTweets;
import gh.polyu.thread.ThreadOfTwitterPool;

public class Twitter4jEngine {
	
	private ThreadOfTwitterPool relationshipThread = null;
	private ThreadOfTweets tweetsThread = null;
	private Twitter[] authrizedTwitter = null;
	private Hashtable<Long, Integer> hasTweetID = new Hashtable<Long, Integer>();
	
	private void MainLoop() {
		// TODO Auto-generated method stub
		Thread threadTweet   = new Thread(tweetsThread);
		threadTweet.start();
		
		Thread threadTwitter = new Thread(relationshipThread);		
		threadTwitter.start();
		
		while(true)
		{
			System.out.println("Main thread running...");
			try {
				Thread.sleep(3000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	

	protected void finalize()
	{		
		if(!tweetsThread.isbExit())
		{
			tweetsThread.setbExist(true);
			System.out.println("Waiting for tweet thrend shutting down....");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("tweet thrend  shuted down");
		}
		
		if(!relationshipThread.isbExit())
		{
			relationshipThread.setbExit(true);
			System.out.println("Waiting for twitter pool thread shutting down....");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("twitter pool thread shuted down");
		}
		
	}


	private void InitialAll() {
		// TODO Auto-generated method stub
		// twitter key secret pool thread initialize
//		relationshipThread = 
//			new ThreadOfTwitterPool("Relationship", 1);
		
		// tweet search thread initialize
		tweetsThread = new ThreadOfTweets(relationshipThread, this.hasTweetID);
		
	}
	/**
	 * @param: 1st File contains N pairs of Key and Secret Twitter 
	 * 		   2nd Updating time of new trending topics
	 * */
	static public void main(String[] args)
	{
		GlobalParameters.TREND_EXISTING_HOURS = 168;
		
		Twitter4jEngine engine = new Twitter4jEngine();
		engine.InitialAll();
		
		engine.MainLoop();
	}

}
