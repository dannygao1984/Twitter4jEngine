package gh.polyu.webcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import com.ning.http.client.oauth.ConsumerKey;
import com.ning.http.client.oauth.OAuthSignatureCalculator;

import gh.polyu.log.LogHandle;
import gh.polyu.twitter4j.twitterOAuth;

public class AsyncWebReader {

	private AsyncHttpClient client = new AsyncHttpClient();
	private String url = null;
	
	public static void main(String[] args) 
	{
		System.err.println("Async Web Crawler");
		//http://twitter.com/login?redirect_after_login=%2Fdannygao%2Ffollowers  
		String strUrl = "http://twitter.com/!#/dannygao/followers";
		AsyncWebReader webRd = new AsyncWebReader();
		System.out.println("Connecting url:" + strUrl);
		webRd.connect(strUrl);
		String strText = null;
		try {
			strText = webRd.readContents();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogHandle.LongFileInitial();
		LogHandle.LogWriteline(strText);
		System.out.println("Async Web Crawler finished");
		
//		System.out.println("Authority web crawler");
//		String strUrl = "http://twitter.com/!#/dannygao/followers";
//		System.out.println("Connecting url:" + strUrl);
//		AsyncWebReader webRd = new AsyncWebReader();
//		webRd.connectAndOAuth(strUrl);
//		String strText = null;
//		strText = webRd.OauthreadContents();
//		
//		LogHandle.LongFileInitial();
//		LogHandle.LogWriteline(strText);
//		System.out.println("Async Web Crawler finished");
	}
	
	private String readContents() throws InterruptedException, ExecutionException, IOException {
		// TODO Auto-generated method stub
		Response response = client.prepareGet(url).execute().get();
		if (response.getStatusCode() >= 500) {
	        // Retry by submitting another request.
	        System.out.println("error...");		       
	     }
		if (response.getStatusCode() == 302) {
			System.out.print("Redirecting to... ");
	        String redirect = response.getHeader("Location");
	        this.url = redirect;
	        System.out.println(this.url);
	        response = client.prepareGet(redirect).execute().get();
		}
		return response.getResponseBody("UTF-8");
	}
	

	private void connect(String url) {
		// TODO Auto-generated method stub
		this.client = new AsyncHttpClient();
		this.url = url;
	}
	
}
