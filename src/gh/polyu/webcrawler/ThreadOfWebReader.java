package gh.polyu.webcrawler;

import gh.polyu.database.TwitterDBHandle;
import gh.polyu.twittercore._HyperlinkInfo;

import java.util.Hashtable;

public class ThreadOfWebReader extends Thread {

	private Hashtable<String, _HyperlinkInfo> hasUrls = null;
	private String url = "";
	private long   begTime = 0;
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ThreadOfWebReader(String url,
			Hashtable<String, _HyperlinkInfo> hasUrls)
	{
		this.hasUrls = hasUrls;
		this.url     = url;
		this.begTime = System.currentTimeMillis();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Crawling url #" + this.url);
		
		WebPageReader webRd = new WebPageReader();
		if (webRd.connect(url))
		{
			String strText = webRd.readContents();
			String longUrl = webRd.getStrExdendURL();
			this.hasUrls.put(url, new _HyperlinkInfo(2, url,
					longUrl , strText));
			System.err.println("Url #" + url + " retrieved OK [Thread of web reader]");
		}else
		{
			this.hasUrls.put(url, new _HyperlinkInfo(-1, url, "-1" , ""));
			System.err.println("Url #" + url + " error link [Thread of web reader]");
		}
		
	}

	public long getBegTime() {
		return begTime;
	}

}
