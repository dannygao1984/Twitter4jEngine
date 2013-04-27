package gh.polyu.hyperlinkDecoding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

public class hyperlinkDecoder {

	// hasLinks<uncoded_links, decoded_links> 
	// decoded_links: 0-未解析; -1-无法解析; xxx-正常解析
	Hashtable<String, String> hasLinks = new Hashtable<String, String>();
	String hardDecodedFile = "./output/hyperlinkDecode/hardDecoded.txt";
	String undecodedFile = "./output/hyperlinkDecode/uncodedhyperlink.txt";
	String decodedFile = "./output/hyperlinkDecode/decodedLinks.txt";
	
	final int MAX_THREAD_NUMBER = 1000;
	final int SLEEP_TIME = 100; //microsecond
	final int TIME_OUT = 3;
	
	public void Decoder(String strUndecodedFile)
	{
		this.undecodedFile = strUndecodedFile;
		ReadHyperlinks(hasLinks, this.undecodedFile);
		System.out.println(hasLinks.size());
		RemoveDecodedHyperlinks(hasLinks, this.decodedFile);
		System.out.println(hasLinks.size());
		RemoveHardDecodedHyperlinks(hasLinks, this.hardDecodedFile);
		System.out.println(hasLinks.size());
		
		ArrayList<threadDecoder> listThreads = new ArrayList<threadDecoder>();
		
		while(true)
		{
			// 更新解析线程
			if(listThreads.size() < MAX_THREAD_NUMBER)
			{
				int tmpNum = listThreads.size();
				for (String uncoded_link : hasLinks.keySet())
				{
					String tmp = hasLinks.get(uncoded_link);
					if(tmp.equals("0"))
					{
						threadDecoder decoder = new threadDecoder(uncoded_link, 
														hasLinks, this.decodedFile);
						decoder.start();
						listThreads.add(decoder);
					}
					if(listThreads.size() >= MAX_THREAD_NUMBER)
						break;
				}
				System.out.println("##[Updated] #" + (listThreads.size() - tmpNum) + "Thread");
			}			
			// sleep
			
			try {
				Thread.sleep(1000*SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 效验解析线程
			for(int i = 0; i < listThreads.size(); i++)
			{
				System.err.println("State of Thread#" + i + " " + 
						listThreads.get(i).isAlive() + " " +
						listThreads.get(i).getDecoded_link() + " " + 
						listThreads.get(i).getTimeStamp());
				// 线程退出了
				if(!listThreads.get(i).isAlive() && !listThreads.get(i).getDecoded_link().equals("-1"))
				{
					System.out.println("Decoded:\t" + listThreads.get(i).getUncoded_link() + " -> " +
							listThreads.get(i).getDecoded_link());
					
					OutputDecodedHyperlinks(listThreads.get(i).getUncoded_link(),
							listThreads.get(i).getDecoded_link(),
							this.decodedFile);
					
					if (hasLinks.containsKey(listThreads.get(i).getUncoded_link()))
						hasLinks.remove(listThreads.get(i).getUncoded_link());
					
					listThreads.get(i).stop();
					listThreads.remove(i);
					continue;
				}
				
				// 解析不出Link
				if(listThreads.get(i).getDecoded_link().equals("-1"))
				{
					System.out.println("Hard Decoded Link:\t" + listThreads.get(i).getUncoded_link());
					OutputHardDecodedList(listThreads.get(i).getUncoded_link(), 
								listThreads.get(i).getDecoded_link(),
								this.hardDecodedFile);
					
					if (hasLinks.containsKey(listThreads.get(i).getUncoded_link()))
						hasLinks.remove(listThreads.get(i).getUncoded_link());
					
					listThreads.get(i).stop();
					listThreads.remove(i);					
					continue;
				}		
				
				
				// 线程超时了
				long span = System.currentTimeMillis() - listThreads.get(i).getBegTime();
				System.err.println("Thread #" + i + " time span" + (int)(span/1000/60));
				if( TIME_OUT < (int)(span/1000/60))
				{
					System.err.println("###[TimeOut]Thread #" + i + " start at:" + listThreads.get(i).getBegTime() + " now is:" + System.currentTimeMillis());
					OutputHardDecodedList(listThreads.get(i).getUncoded_link(), 
								listThreads.get(i).getDecoded_link(),
								this.hardDecodedFile);
					
					if (hasLinks.containsKey(listThreads.get(i).getUncoded_link()))
						hasLinks.remove(listThreads.get(i).getUncoded_link());
					
					listThreads.get(i).stop();
					listThreads.remove(i);					
					continue;
				}
				
			}
			
			// 检查退出条件
			if(listThreads.isEmpty())
			{
				System.out.println("checking quiting");
				boolean bOver = true;
				for (String str : hasLinks.keySet())
				{
					String tmp = hasLinks.get(str);
					if(tmp.equals("0"))
					{
						bOver = false;
						break;
					}
				}				
				if(bOver)
					break;
			}
			
			
		}
	}
	
	
	private void OutputDecodedHyperlinks(String uncodedLink, String decodeLink, 
			String decodedFile) {
		// TODO Auto-generated method stub
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(decodedFile, true),true);
			
			pw.append(uncodedLink.trim() + "\t" + decodeLink + "\n");
			
			if(pw != null) pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void OutputHardDecodedList(String uncodedLink,
			String hardDecodedCode, String hardDecodedFile) {
		// TODO Auto-generated method stub
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(hardDecodedFile, true),true);
			
			pw.append(uncodedLink.trim() + "\t" + hardDecodedCode+ "\n");
			
			if(pw != null) pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void RemoveHardDecodedHyperlinks(
			Hashtable<String, String> hasLinks, String string) {
		// TODO Auto-generated method stub
		RemoveDecodedHyperlinks(hasLinks, string);
	}


	private void RemoveDecodedHyperlinks(Hashtable<String, String> hasLinks,
			String string) {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new FileReader(string));
			
			String strLine = "";
			while((strLine = br.readLine()) != null)
			{
				String[] tmp = strLine.split("\\t");
				if(hasLinks.containsKey(tmp[0].trim()))
				{
					hasLinks.remove(tmp[0]);
				}
			}
			
			if (br != null) br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	private void ReadHyperlinks(Hashtable<String, String> hasLinks,
			String string) {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new FileReader(string));
			
			String strLine = "";
			while((strLine = br.readLine()) != null)
			{
				hasLinks.put(strLine.trim(), "0");
			}
			
			if (br != null) br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new hyperlinkDecoder().Decoder(args[0]);
	}

}
