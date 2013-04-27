package gh.polyu.HtmlCrawl;

import gh.polyu.twittercore.FollowersOfUser;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListMap;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import gh.polyu.twitter4j.*;

import com.google.common.base.Preconditions;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class TwitterHtmlCrawler {

	private final ConcurrentSkipListMap<Long, Integer> retries = new ConcurrentSkipListMap<Long, Integer>();
	private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
	
	public void Fetch(String name, long ld) throws IOException
	{
		String strUrl = "http://twitter.com/appinn/followers";
		System.out.println("Do fetching");
		Preconditions.checkNotNull(strUrl);
		asyncHttpClient.prepareGet(strUrl).execute(
					new FetcherHandler(name, strUrl, ld));
		while(!bCompleted)
		{
			try {
			      Thread.sleep(10000);
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		}
		
	    System.out.println("Fetching over");
	    asyncHttpClient.close();
	}
	
	private boolean bCompleted = false;
	private class FetcherHandler extends AsyncCompletionHandler<Response> 
	{
		private String name;
		private long   ld;
		private String url;  
		public FetcherHandler(String strName,
				String url, long ld)
		{
			this.name = strName;
			this.ld   = ld;
			this.url  = url;
		}

		@Override
	    public void onThrowable(Throwable t)
		{
			try{
				retry();
			}catch(Exception e)
			{
				
			}
		}
		
		
		private synchronized void retry() throws Exception
		{
			// Wait before retrying.
		   Thread.sleep(1000);

		   if (!retries.containsKey(ld)) {
			   System.out.println("Retrying: " + url + " attempt 1");
		        retries.put(ld, 1);
		        asyncHttpClient.prepareGet(url).execute(
		        		new FetcherHandler(name, url, ld));
		        return;
		   }

	      int attempts = retries.get(ld);
	      if (attempts > 3) {
	        System.err.println("Abandoning: " + url + " after max retry attempts");
	        return;
	      }

		  attempts++;
		  System.err.println("Retrying: " + url + " attempt " + attempts);
		  asyncHttpClient.prepareGet(url).execute(
				  new FetcherHandler(name, url, ld));
		  retries.put(ld, attempts);

		}
		
		@Override
		public Response onCompleted(Response response) throws Exception 
		{
			// TODO Auto-generated method stub
			if (response.getStatusCode() >= 500) 
			{
				System.err.println("Error status " + response.getStatusCode() + ": " + url);
		        return response;
		    }
			// TODO 
		    if (response.getStatusCode() == 302) {
		        String redirect = response.getHeader("Location");
		        asyncHttpClient.prepareGet(redirect).execute(
		        		new FetcherHandler(name, url, ld));
		        return response;
		    }
		    
		    System.out.println(response.getResponseBody("UTF-8"));
		    new FollowersOfUser(response.getStatusCode(), System.currentTimeMillis(),
		    							response.getResponseBody("UTF-8"));
		    bCompleted = true; 
		    return response;
		}

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {			
			Twitter twitter = new TwitterFactory().getInstance();
			twitterOAuth twtOauth = new twitterOAuth();	
			twitterOAuth.ReadProperties();
			twtOauth.Authority(twitter,  twitterOAuth.prop.getProperty("consumerKey"), 
					twitterOAuth.prop.getProperty("consumerSecret"));
			
			new TwitterHtmlCrawler().Fetch("dannygao", 1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
