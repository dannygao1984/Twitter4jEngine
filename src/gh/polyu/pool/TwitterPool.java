package gh.polyu.pool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import twitter4j.Twitter;

public class TwitterPool implements pool_core<TwitterKeyInstance> {

	private int poolSize = 0;
	private ArrayList listInstances = new ArrayList<TwitterKeyInstance>();
	
	private boolean poolBusy = false;
	private int 	minResetTime = 0;
	private int		avgResetTime = 0;
	
	@Override
	public int fresh_pool() {
		// TODO Auto-generated method stub
		int iAvgResetTime = 0;
		int iMinResetTime = 0;
		int iCnt = 0;
		for(int i = 0 ; i < listInstances.size(); i++)
		{
			if (((TwitterKeyInstance) listInstances.get(i)).Try2RefreshTwitter())
			{
				iCnt ++;
			}else
			{
				int iResetTime = ((TwitterKeyInstance) listInstances.get(i)).getResetTimeInSecond();
				iAvgResetTime = iAvgResetTime + iResetTime;
				iMinResetTime = iMinResetTime > iResetTime ? iResetTime : iMinResetTime;				
			}
			
		}
		if(iCnt > 0)
		{
			this.minResetTime = 0;
			this.avgResetTime = 0;
			this.poolBusy = false;
		}else 
		{
			this.minResetTime = iMinResetTime;
			this.avgResetTime = iAvgResetTime;
			this.poolBusy = true;
		}
		return iCnt;
	}
	
	public ArrayList<KeySecretPair> ReadKeySecret()
	{
		ArrayList<KeySecretPair> pairs = new 
							ArrayList<KeySecretPair>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"./properties/TwitterKeySecretPair.txt"));
			
			String strLine = "";
			while((strLine = br.readLine()) != null)
			{
				if(strLine.charAt(0) == '#')
					continue;
				String[] strPair = strLine.split("\t");
				KeySecretPair pair = new KeySecretPair(
						strPair[1], strPair[2]);
				pairs.add(pair);
			}
			
			if(br != null) br.close();
			return pairs;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public int initial_pool(Twitter[] twitters2Authoriz) {
		// TODO Auto-generated method stub
		ArrayList<KeySecretPair> pairs = ReadKeySecret();
		this.poolSize = pairs.size();
		
		int iCnt = 0;
		for(int i = 0 ; i < this.poolSize; i++)
		{
			TwitterKeyInstance ins = new TwitterKeyInstance(twitters2Authoriz[i]);
			if(1 == ins.InitialAndOauthKey(pairs.get(i).getKey(),
					pairs.get(i).getSecret()))
			{
				iCnt++;
				listInstances.add(ins);
			}			
		}
		if(iCnt > 0)
		{
			this.minResetTime = 0;
			this.avgResetTime = 0;
			this.poolBusy = false;
		}
		System.out.println(iCnt + " key-secret pairs were intialized!");
		return iCnt;
	}

	@Override
	public int initial_pool() {
		// TODO Auto-generated method stub
		ArrayList<KeySecretPair> pairs = ReadKeySecret();
		this.poolSize = pairs.size();
		
		int iCnt = 0;
		for(int i = 0 ; i < this.poolSize; i++)
		{
			TwitterKeyInstance ins = new TwitterKeyInstance();
			if(1 == ins.InitialAndOauthKey(pairs.get(i).getKey(),
					pairs.get(i).getSecret()))
			{
				iCnt++;
				listInstances.add(ins);
			}			
		}
		if(iCnt > 0)
		{
			this.minResetTime = 0;
			this.avgResetTime = 0;
			this.poolBusy = false;
		}
		System.out.println(iCnt + " key-secret pairs were intialized!");
		return iCnt;
	}
	
	@Override
	public int initial_pool_withAuthorizedTwitters(Twitter[] authorizedTwitters) {
		// TODO Auto-generated method stub
		
		this.poolSize = authorizedTwitters.length;
		
		int iCnt = 0;
		for(int i = 0 ; i < this.poolSize; i++)
		{
			TwitterKeyInstance ins = new TwitterKeyInstance(authorizedTwitters[i]);		
		}
		if(iCnt > 0)
		{
			this.minResetTime = 0;
			this.avgResetTime = 0;
			this.poolBusy = false;
		}
		System.out.println(iCnt + " key-secret pairs were intialized!");
		return iCnt;
	}

	@Override
	public boolean release(TwitterKeyInstance instance) {
		// TODO Auto-generated method stub
		boolean bFresh = instance.Try2RefreshTwitter();
		if(bFresh)
		{
			this.minResetTime = 0;
			this.avgResetTime = 0;
			this.poolBusy = false;
		}
		return bFresh;
	}

	@Override
	public TwitterKeyInstance request_instance() {
		// TODO Auto-generated method stub
		int iMinResetTime = 0;
		int iAvgResetTime = 0;
		for (int i = 0; i < listInstances.size(); i++)
		{
			if(!((TwitterKeyInstance) listInstances.get(i)).isbBand())
			{
				this.minResetTime = 0;
				this.avgResetTime = 0;
				this.poolBusy = false;
				return (TwitterKeyInstance)listInstances.get(i);
			}else
			{
				int iResetTime = ((TwitterKeyInstance) listInstances.get(i)).getResetTimeInSecond();
				iAvgResetTime = iAvgResetTime + iResetTime;
				iMinResetTime = iMinResetTime > iResetTime ? iResetTime : iMinResetTime;
				
			}
		}
		this.poolBusy = true;
		this.minResetTime = iMinResetTime;
		this.avgResetTime = iAvgResetTime/this.poolSize;
		return null;
	}

	@Override
	public boolean statusOfInstance(TwitterKeyInstance instance) {
		// TODO Auto-generated method stub
		return instance.isbBand();
	}
	
	public boolean isPoolBusy() {
		return poolBusy;
	}

	public int getMinResetTime() {
		return minResetTime;
	}

	public int getAvgResetTime() {
		return avgResetTime;
	}
	
	public int getPoolsize()
	{
		return this.poolSize;
	}

	
}
