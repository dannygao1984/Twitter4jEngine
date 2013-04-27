package gh.polyu.urlTransfer;

import java.net.*;
import java.io.*;


public class WebURLExpand {
	
	private URLConnection connect;

	public int connect( String ShortUrl ) {
		try {
			URL url = new URL(ShortUrl);			
			connect = url.openConnection();
			
			return 1;
		} catch (MalformedURLException e){
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public String getExpandURL()
	{
		read1stLine();
		return connect.getURL().toString();
	}
	
	@SuppressWarnings("finally")
	boolean URLExist(String strURL)
	{

		int iState = -1;
		HttpURLConnection con = null;
		try {
			HttpURLConnection.setFollowRedirects(true);
			
			con = (HttpURLConnection) new URL(strURL).openConnection();
			
			//con.setRequestMethod("HEAD");
			iState = con.getResponseCode();
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("Error link: " + strURL);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("Error link: " + strURL);
			
		} finally 
		{
			if (con != null)
				con.disconnect();
			if (iState == 200)
			{
				return true;
			}else 
			{
				return false;
			}
		}
	}
	
	public String getExpandURL(String strShortURL)
	{
		if (URLExist(strShortURL))
		{
			if( 1 == connect(strShortURL))
			{
				if (null == read1stLine())
				{
					return null;
				}else
				{
					return connect.getURL().toString();
				}				
				
			}else
				return null;
			
		}else 
		{
			return null;
		}
		
	}

	private String read1stLine() 
	{
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			
			String strText = in.readLine();
			
			if (in != null) in.close();
			//System.out.println(strText);
			return strText;
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) 
	{
		//http://goo.gl/tq4M6 http://t.co/nE9tSTs  http://tiny.cc/2rdlt
		WebURLExpand web = new WebURLExpand();		
		System.out.println(web.getExpandURL("http://bit.ly/fyHv2K"));
	}
	
}