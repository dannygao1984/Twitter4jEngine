package gh.polyu.webcrawler;

import gh.polyu.log.LogHandle;

import java.net.*;
import java.io.*;


public class WebPageReader {
	
	private URLConnection connect;
	private String strURL;
	private String strExdendURL;

	public boolean connect( String urlString ) {
		try {
			URL url = new URL(urlString);
			connect = url.openConnection();
			this.strURL = urlString;
			//System.out.println(connect.getClass());
			return true;
		} catch (MalformedURLException e){
			//e.printStackTrace();
			System.err.println("Connecting Error#" + urlString);
			return false;
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("Connecting Error#" + urlString);
			return false;
		}
	}
	
	public String getHost()
	{
		return connect.getURL().getHost().toString();
	}
	
	public String getExpandURL()
	{
		read1stLine();
		return connect.getURL().toString();
	}
	
	private String read1stLine() 
	{
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			
			String strText = in.readLine();
			
			if (in != null) in.close();
			System.out.println(strText);
			return strText;
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
	}

	public String readContents() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			
			
			String strText = "";
			char[] chr = new char[600000];
			while ((in.read(chr, 0, chr.length)) != -1) 
			{
				strText = strText + new String(chr);
			}
			chr = null;
			System.gc();
			
			if (in != null) in.close();
			
			this.setStrExdendURL(connect.getURL().toString());
			
			return strText;
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("Reading URL Error #" + this.strURL);
			return null;
		} catch (OutOfMemoryError e)
		{
			System.err.println("OutOfMemoryError #" + this.strURL);
			return null;
		}
	}

	public static void main(String[] args) 
	{
//		if (args.length != 1) {
//		System.err.println("usage: java WebPageReader "+ "<url>");
//		System.exit(0);
//		}
		System.err.println("Web Crawler");
		WebPageReader webRd = new WebPageReader();
		webRd.connect("http://twitter.com/#!/appinn/followers");
		String strText = webRd.readContents();
		
		LogHandle.LongFileInitial();
		LogHandle.LogWriteline(strText);
	}

	public void setStrExdendURL(String strExdendURL) {
		this.strExdendURL = strExdendURL;
	}

	public String getStrExdendURL() {
		return strExdendURL;
	}
}