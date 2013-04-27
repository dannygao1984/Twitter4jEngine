package gh.polyu.toolets;

/**
 * function: Keep tracking the public timeline and get all the tweets 
 * Input   : Null
 * output  : Every Hour output one file, named with the Year-Month-Day Hour.
 * 			 The file stores all the tweets posted in this hour.
 * 			 Seen in directory ./out/publictimeline/
 * 
 * */

import gh.polyu.common.CommonFunction;
import gh.polyu.common.CommonFunctionDateFormat;
import gh.polyu.twitter4j.TwitterTimeline;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class PublicTimelineDetecting {

	public void doDetecting() 
	{
		Twitter twitter = new TwitterFactory().getInstance();
		TwitterTimeline timeline = new TwitterTimeline();
		ArrayList<Status> listStatus = new ArrayList<Status>();
		Hashtable<Long, Boolean> hasExists = new Hashtable<Long, Boolean>();
		
		String format = "yyyy-MM-dd HH:mm:ss";
		
		java.util.Date date_start = new java.util.Date();
		String format_hour = "yyyy-MM-dd HH";
		String fileName = CommonFunctionDateFormat.Date2String(date_start, format_hour);

		
		while(true)
		{			
			List<Status> listSts = timeline.getPublicTimeline(twitter);
			
			for (Status status : listSts) {
				String date = CommonFunctionDateFormat.Date2String(status.getCreatedAt(), format);
	            System.out.println(date + "\t@" + status.getUser().getScreenName() + "\t\t" + status.getText());
	           
	            // update
	            if(!hasExists.containsKey(status.getId()))
	            {
	            	listStatus.add(status);
	            	hasExists.put(status.getId(), true);
	            }	            
	        }  
	        
			Status status = listSts.get(0);
//			String format_hour = "yyyy-MM-dd HH";
			String date = CommonFunctionDateFormat.Date2String(status.getCreatedAt(), format_hour);
			if (date.compareTo(fileName) != 0 && !listStatus.isEmpty())
            {
            	System.out.println("File Name:" + date + "h.txt");             	
            	OutputStatus(listStatus, "./output/publictimeline/"+fileName+"h.txt");
            	fileName = date;
            	listStatus.clear();
            	hasExists.clear();
            }
	        
	        
	        try {
				Thread.sleep(50*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println("Sleep error");
			}
		}
		
	}
	
	private void OutputStatus(ArrayList<Status> listStatus, String string) {
		// TODO Auto-generated method stub
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(string, true), true);
			String format = "yyyy-MM-dd HH:mm:ss";
			for (Status status : listStatus) {
				String date = CommonFunctionDateFormat.Date2String(status.getCreatedAt(), format);
				pw.append(date + "\t@" + status.getUser().getScreenName() 
	        		   + "\t\t" + status.getText() + "\n");
	        }  
			
			if(pw != null) pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new PublicTimelineDetecting().doDetecting();
	}

}
