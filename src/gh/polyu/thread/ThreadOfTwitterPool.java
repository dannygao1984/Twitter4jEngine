package gh.polyu.thread;

import gh.polyu.database.TwitterDBHandle;
import gh.polyu.pool.TwitterKeyInstance;
import gh.polyu.pool.TwitterPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;

import twitter4j.Twitter;

public class ThreadOfTwitterPool implements Runnable {

	public Semaphore semap = new Semaphore(0);
	private boolean   bExit = false;
	private Hashtable<Long, Integer> hasUsers = new Hashtable<Long, Integer>();
	
	private TwitterPool twitterKeyPool = new TwitterPool();
	private TwitterDBHandle twitterDbHandle = new TwitterDBHandle();

	public ThreadOfTwitterPool(String threadName,
			Twitter[] authorizedTwitters)
	{
		
	}
	
	public void InitialTwitter(Twitter[] authorizedTwitters)
	{
		System.out.println("Twitter pool start...");
		if (twitterKeyPool.initial_pool(authorizedTwitters) == 0)
		{
			System.out.println("Twitter pool initial error");
			System.exit(-1);
		}else
		{
			System.out.println(twitterKeyPool.getPoolsize() +
					 " start in twitter pool .....");
		}
	}
	
	public void InitialTwitter()
	{
		System.out.println("Twitter pool start...");
		if (twitterKeyPool.initial_pool() == 0)
		{
			System.out.println("Twitter pool initial error");
			System.exit(-1);
		}else
		{
			System.out.println(twitterKeyPool.getPoolsize() +
					 " start in twitter pool .....");
		}
	}
	
	public void InitialDatatbase()
	{
		System.out.println("Twitter Pool Database connecting .....");
		twitterDbHandle.intialTwitterDBhandle();
		if (twitterDbHandle.database_connection() != 1)
		{
			System.out.println("Twitter database connect error");
			System.exit(-1);
			
		}else
		{
			System.out.println("Twitter database connect OK!");
		}
		
		System.out.println("Thread started!");
	}
	
	public ThreadOfTwitterPool(String threadName,
			int threadPam)
	{
		InitialTwitter();
		InitialDatatbase();
		
	}
	
	synchronized public void addUsers2List(ArrayList<Long> newList)
	{
		for(int i = 0 ; i < newList.size(); i++)
		{
			if(!hasUsers.containsKey(newList.get(i)))
			{
				hasUsers.put(newList.get(i), 1);
			}
		}
		newList.clear();
		newList = null;
	}
	
	synchronized public long getOneUserFromList()
	{
		if(hasUsers.isEmpty())
		{
			return 0;
		}else
		{
			for(long key : hasUsers.keySet())
			{
				return key;
			}
		}
		return 0;
		
	}
	
	synchronized public void RemoveOneUserFromList(long id)
	{
		hasUsers.remove(id);
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.err.println("Twitter Thread Pool begin ");
		while(!isbExit())
		{
//			long id = getOneUserFromList();
//			if(id == 0)
//			{
//				try {
//					Thread.sleep(30,000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					//e.printStackTrace();
//				}
//				continue;
//			}else
//			{
//				if(twitterDbHandle.IsExistInFollowersTable(id))
//				{
//					RemoveOneUserFromList(id);
//					continue;
//				}
//			}
			
//			TwitterKeyInstance twitter4followers = twitterKeyPool.request_instance();
//			if(null == twitter4followers)
//			{
//				System.err.println("Twitter Pool Busy, and please wait " 
//						+ twitterKeyPool.getAvgResetTime() + " sec. [in Followers section]");
//				try {	
//					
//					Thread.sleep(twitterKeyPool.getAvgResetTime() * 10);					
//					twitterKeyPool.fresh_pool();
//					
//					System.gc();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					//e.printStackTrace();
//				}
//				continue;
//			}
//			
//			TwitterKeyInstance twitter4friends = twitterKeyPool.request_instance();
//			if(null == twitter4friends)
//			{
//				System.err.println("Twitter Pool Busy, and please wait " 
//						+ twitterKeyPool.getAvgResetTime() + " sec. [in Friends section]");
//				try {					
//					Thread.sleep(twitterKeyPool.getAvgResetTime() * 10);					
//					twitterKeyPool.fresh_pool();
//					
//					System.gc();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				continue;
//			}
//			
//			ArrayList<Long> followersList = twitter4followers.getFollowers(id);
//			ArrayList<Long> friendsList = twitter4friends.getFriends(id);
//			
//			twitterDbHandle.insertFollowers2DB(id, followersList);
//			twitterDbHandle.insertFriends2DB(id, friendsList);
//			
//			if(null != followersList)
//			{
//				System.err.println("In twitter realtion thread: " + 
//						followersList.size() + " followers of user#" + id + " found");
//				followersList.clear(); followersList = null;
//			}
//				
//			if(null != friendsList)
//			{
//				System.err.println("In twitter realtion thread: " + 
//						friendsList.size() + " friends of user#" + id + " found");
//				friendsList.clear(); friendsList = null;
//			}
//			
//			RemoveOneUserFromList(id);
			
//			if(twitterKeyPool.fresh_pool() == 0) 
//			{
//				System.err.println("Twitter Pool Busy, and please wait " 
//						+ twitterKeyPool.getAvgResetTime() + " sec. [in main section]");
//				try {
//					Thread.sleep(twitterKeyPool.getAvgResetTime() * 10);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				twitterKeyPool.fresh_pool();
//			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// twitterKeyPool.fresh_pool();
		}
	}


	public boolean isbExit() {
		return bExit;
	}


	public void setbExit(boolean bExit) {
		this.bExit = bExit;
	}

	
}
