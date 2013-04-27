package gh.polyu.twitter4jEngine;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import net.sourceforge.htmlunit.corejs.javascript.ObjToIntMap.Iterator;

import gh.polyu.common.CommonFunction;
import gh.polyu.thread.ThreadOfTweets;
import gh.polyu.twittercore._HyperlinkInfo;
import gh.polyu.webcrawler.ThreadOfWebReader;

public class TweetsDownloadAndHyperlinkDecoder {

	final int TIME_OUT = 10;
	final int MAX_WEBCRAWLER_THREAD = 1000;
	private GlobalParameterOfTweetsDownloadAndHyperlinkDecoder gPar = 
			new GlobalParameterOfTweetsDownloadAndHyperlinkDecoder();
	
	public void DownloadDecoderApp()
	{
		InitialDatabase();
		// start tweet download thread
		this.gPar.threadTweet = new Thread(new ThreadOfTweets(this.gPar.hasUrls, this.gPar));
		this.gPar.threadTweet.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true)
		{
			//Generate hyperlink 
			//System.out.println("Check web crawling threads");
			int iCrawlThread = GenerateCrawlingUrl(this.gPar.hasUrls);
			//System.out.println("#" + iCrawlThread + " webcrawling threads updated");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Check error hyperlink
			System.out.println("#" + gPar.listWebReader.size() + " threads are running![Main Thread]");
			for(int i = 0; i < gPar.listWebReader.size(); i++)
			{
				if(!gPar.listWebReader.get(i).isAlive())
				{
					gPar.listWebReader.remove(i);
					continue;
				}
				
				long span = System.currentTimeMillis() - gPar.listWebReader.get(i).getBegTime();
				String url = gPar.listWebReader.get(i).getUrl();
				if(TIME_OUT < (int)(span/1000/60))
				{
					_HyperlinkInfo hyper = gPar.hasUrls.get(url);
					hyper.setCode(-1);
					hyper.setLongUrl("-1");
					hyper.setContent("");
					
					gPar.listWebReader.remove(i);
				}
				
			}
			
			//Get all the error and ready hyperlinks
			ArrayList<_HyperlinkInfo> list = ErrorAndReadyHyperlink(this.gPar.hasUrls);
			
			//insert the error and ready hyperlinks
			int iRet = InsertHyperlink(list, 
					CommonFunction.GetYear() + CommonFunction.GetMonth() +  "Hyperlink");
			System.err.println("#" + iRet + " hyperlink insert into database [Main Thread]");
		}
	}
	
	private void InitialDatabase() {
		// TODO Auto-generated method stub
		System.out.println("Main Thread Database connecting .....");
		this.gPar.twitterDbHandle.intialTwitterDBhandle();
		if (this.gPar.twitterDbHandle.database_connection() != 1)
		{
			System.out.println("Main thread database connect error");
			System.exit(-1);
			
		}else
		{
			System.out.println("Main thread connect OK!");
		}
	}

	private int InsertHyperlink(ArrayList<_HyperlinkInfo> list, String table) {
		// TODO Auto-generated method stub
		return this.gPar.twitterDbHandle.InsertBatch(list, table);
	}

	private int GenerateCrawlingUrl(Hashtable<String, _HyperlinkInfo> hasUrls) {
		// TODO Auto-generated method stub
		int iCnt = 0;
		for (String url : hasUrls.keySet())
		{
			_HyperlinkInfo hyper = hasUrls.get(url);
			if(hyper.getCode() == 0)
			{
				if(this.gPar.listWebReader.size() < MAX_WEBCRAWLER_THREAD)
				{
					hyper.setCode(1);
					ThreadOfWebReader web = new ThreadOfWebReader(url, hasUrls);
					web.start();
					this.gPar.listWebReader.add(web);
					iCnt ++;
				}else
				{
					break;
				}			
			}
		}
		return iCnt;
	}

	private ArrayList<_HyperlinkInfo> ErrorAndReadyHyperlink(
			Hashtable<String, _HyperlinkInfo> hasUrls) {
		// TODO Auto-generated method stub
		ArrayList<_HyperlinkInfo> list = new ArrayList<_HyperlinkInfo> ();
		
		Enumeration e = hasUrls.keys();
		while(e.hasMoreElements())
		{
			String url = (String)e.nextElement();
			_HyperlinkInfo hyper = (_HyperlinkInfo)hasUrls.get(url);
			
			if(hyper.getCode() == 2 || hyper.getCode() == -1)
			{
				list.add(hyper);
				hasUrls.remove(url);
			}
			
		}
		return list;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TweetsDownloadAndHyperlinkDecoder().DownloadDecoderApp();
	}

}
