package gh.polyu.twitter4jEngine;

import gh.polyu.database.TwitterDBHandle;
import gh.polyu.hyperlinkDecoding.threadDecoder;
import gh.polyu.thread.ThreadOfTweets;
import gh.polyu.twittercore._HyperlinkInfo;
import gh.polyu.webcrawler.ThreadOfWebReader;

import java.util.ArrayList;
import java.util.Hashtable;

public class GlobalParameterOfTweetsDownloadAndHyperlinkDecoder {

	// Url -> Code(-1: Can't open, 0: do not retrieval, 1: decoded)
	Hashtable<String, _HyperlinkInfo> hasUrls = new Hashtable<String, _HyperlinkInfo>();
	
	ArrayList<ThreadOfWebReader> listWebReader = new ArrayList<ThreadOfWebReader>();
	
	Thread threadTweet   = null;
	
	public TwitterDBHandle twitterDbHandle = new TwitterDBHandle();
	
	public String table = "";
	
}
