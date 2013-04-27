package gh.polyu.hyperlinkDecoding;

import gh.polyu.common.CommonFunction;
import gh.polyu.urlTransfer.WebURLExpand;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

public class threadDecoder extends Thread{

	private String uncoded_link = "";
	private String decoded_link = "";
	private Hashtable<String, String> hasLinks = null;
	private long   begTime = 0;
	private String decodedFile = "";
	private String timeStamp = "";
	
	public String getTimeStamp()
	{
		return this.timeStamp;
	}
	public String getDecoded_link() {
		return decoded_link;
	}

	public void setHasLinks(Hashtable<String, String> hasLinks) {
		this.hasLinks = hasLinks;
	}

	public threadDecoder(String uncoded_link, 
			Hashtable<String, String> hasLinks, String  decodedFile)
	{
		this.uncoded_link = uncoded_link;
		this.hasLinks     = hasLinks;
		this.begTime  = System.currentTimeMillis();
		this.decodedFile = decodedFile;
		this.decoded_link = "0";
		this.timeStamp = CommonFunction.GetCurrentTime();
	}
	
	public String getUncoded_link() {
		return uncoded_link;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		WebURLExpand web = new WebURLExpand();	
		String str = web.getExpandURL(this.uncoded_link);
		if(str == null || str.length() == 0)
		{
			SetHashTable(this.uncoded_link, "-1");
		}else
		{
			SetHashTable(this.uncoded_link, str);
			System.err.println("Decoding ...\t" + this.uncoded_link + "\t->"  
					+ str + "\t" + 
					new CommonFunction().GetCurrentTime() + "[Decoding thread]");
		}
		
	}


	synchronized private void SetHashTable(String uncodedLink, String str) {
		// TODO Auto-generated method stub
		this.hasLinks.put(uncodedLink, str);
	}

	public void setBegTime(long begTime) {
		this.begTime = begTime;
	}
	
	public long getBegTime() {
		return this.begTime;
	}
	
}
