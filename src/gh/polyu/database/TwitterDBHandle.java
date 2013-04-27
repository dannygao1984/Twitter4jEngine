package gh.polyu.database;

import gh.polyu.common.CommonFunction;
import gh.polyu.log.LogHandle;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import gh.polyu.twittercore.*;

public class TwitterDBHandle extends databasehandle_core {

	/**
	 * @param: 1st File contains N pairs of Key and Secret Twitter 
	 * 		   2nd Updating time of new trending topics
	 * */
	static public void main(String[] args)
	{
		TwitterDBHandle handle = new TwitterDBHandle();
		handle.intialTwitterDBhandle();
		handle.database_connection();
		handle.ConnectTest();
	}
	
	public void ConnectTest()
	{
		String sql = "SELECT * FROM myTweet.CHARACTER_SETS LIMIT 0 , 30";
		if (conn == null)
		{
			this.database_connection();
		}
	
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs =  pstmt.executeQuery();
			
			while(rs.next())
			{
				System.out.println(rs.getString("CHARACTER_SET_NAME"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
		} finally
		{
			ClearPreparedStatement();
			ClearResultSet();			
		}
	}
	
	public void intialTwitterDBhandle()
	{
		Properties prop = CommonFunction.GetProperties(
				"./properties/database.properties");
		initial_databasehandle(prop);
	}
	
	public String getDatabaseTableNameFromTweetInfo(_TweetInfo newTwt)
	{
		Timestamp timestamp = newTwt.getTweetTimestamp();
		String strMonth = Integer.toString(timestamp.getMonth() + 1);
		if(strMonth.length() == 1)
			strMonth = "0" + strMonth;
		String strYear = Integer.toString(timestamp.getYear() + 1900);
		
		return strYear+strMonth;
	}
	
	public boolean isTweetExistInDB(_TweetInfo newTwt)
	{
		String sql = "SELECT * FROM twitterandhyperlink." + 
						CommonFunction.GetYear() + CommonFunction.GetMonth() +
						" WHERE TwtID='" + newTwt.getlId() + "'";
		
		//System.out.println("#1 Is Tweet Exist in DB #" + sql);
		
		if (conn == null)
		{
			this.database_connection();
		}
	
		boolean bFound = false;
		try {
			pstmt = conn.prepareStatement(sql);
			rs =  pstmt.executeQuery();
			
			if(rs.next())
			{
				bFound = true;
			}
			else
			{
				bFound = false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			bFound = false;
		} finally
		{
			ClearPreparedStatement();
			ClearResultSet();
			
			return bFound;
		}
		
	}
	
	public int insertTweet2DB(_TweetInfo newTwt)
	{
		if(isTweetExistInDB(newTwt))
		{
			return 0;
		}
		String table = CommonFunction.GetYear() + CommonFunction.GetMonth();
		String strInsert = "INSERT INTO  myTweet.tweet" + 
				table +
				" (TwtID, TwtTrend, TwtDate, TwtFromUsrID, TwtFromUsrName, " +
				"TwtText, TwtRemark, TwtToUsrID, TwtToUsrName)" +
				" VALUES " +
				"('"+ newTwt.getlId() + "',?,?" +
				",'" + newTwt.getlFromUsr() + "',?,?,'','" +
				newTwt.getlToUsr() + "',?)";
		
		int iCode = -1;
		try {
						
			 
			 PreparedStatement pstmt = conn.prepareStatement(strInsert);
			
			 pstmt.setString(1, newTwt.getStrTrend());
			 		
			 pstmt.setTimestamp(2, newTwt.getTweetTimestamp());
			
			 pstmt.setString(3, newTwt.getStrFromUsrName());
			 
			 pstmt.setString(4, newTwt.getStrText());
			
			 pstmt.setString(5, newTwt.getStrToUsr());
			
			 //pstmt.setString(6, newTwt.getStrJson());
			 
			 pstmt.executeUpdate();
			 
			 if(pstmt != null)
			 {
				 pstmt.close();
				 pstmt = null;
			 }
				
			 iCode = 1;
			 //System.out.println("Insert Tweet to Database #" + newTwt.getlId());
		} catch (SQLException e) {
			
			System.err.println("Error in fun insertTweet2DB of class TwitterDBHandle" );
//			e.printStackTrace();
//			
//			System.err.println("InsertDB error SQL  #" +  newTwt.getStrFromUsrName() + " " 
//					+ newTwt.getStrToUsr() + " " + newTwt.getStrText());	
		}  finally
		{			
			return iCode;
		}
	}
	
	public void ClearPreparedStatement()
	{
		try {
			if (pstmt != null)
			{
				pstmt.close();
				pstmt = null;
			}
		    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void ClearResultSet()
	{
		try {
			if (rs != null)
			{
				rs.close();
				rs = null;
			}
		    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertFriends2DB(long userId, ArrayList<Long> friendsId)
	{
		if(friendsId == null || IsExistInFollowersTable(userId))
			return;
		
		String INSERT_RECORD = "insert into twitter.friends"  + 
							"(userId, friendId) " +
							"values(?, ?)";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(INSERT_RECORD);
			
			for(Long l : friendsId)
			{
				pstmt.setLong(1, userId);
				pstmt.setLong(2, l);
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			ClearPreparedStatement();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public void insertFollowers2DB(long userId, ArrayList<Long> followersId)
	{
		if(followersId == null || IsExistInFollowersTable(userId))
			return;
		
		String INSERT_RECORD = "insert into twitter.followers"  + 
							"(userId, followerId) " +
							"values(?, ?)";

		try {
			PreparedStatement pstmt = conn.prepareStatement(INSERT_RECORD);
			
			for(Long l : followersId)
			{
				pstmt.setLong(1, userId);
				pstmt.setLong(2, l);
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			ClearPreparedStatement();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
	}
	
	public boolean IsExistInFriendsTable(long id)
	{
		String sql = "SELECT * FROM twitter.friends" +
				" WHERE usrld = '" + id + "'";
		
		if (conn == null)
		{
			this.database_connection();
		}
	
		boolean bFound = false;
		try {
			pstmt = conn.prepareStatement(sql);
			rs =  pstmt.executeQuery();
			
			if(rs.next())
			{
				bFound = true;
			}
			else
			{
				bFound = false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			bFound = false;
		} finally
		{
			ClearPreparedStatement();
			ClearResultSet();
			return bFound;
		}
		
	}	
	
	@SuppressWarnings("finally")
	public boolean IsExistInFollowersTable(long id)
	{
		String sql = "SELECT * FROM twitter.followers" +
		" WHERE usrld = '" + id + "'";

		if (conn == null)
		{
			this.database_connection();
		}
	
		boolean bFound = false;
		try {
			pstmt = conn.prepareStatement(sql);
			rs =  pstmt.executeQuery();
			
			if(rs.next())
			{
				bFound = true;
			}
			else
			{
				bFound = false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			bFound = false;
		} finally
		{
			ClearPreparedStatement();
			ClearResultSet();
			return bFound;
		}			
	}

	public int InsertBatch(ArrayList<_HyperlinkInfo> list, String table) {
		// TODO Auto-generated method stub
		int iRet = 0;
		for(int i = 0; i < list.size(); i++)
		{
			if(IsContainHyperlink(list.get(i), table))
			{
				list.remove(i);
			}
		}
		String INSERT_RECORD = "insert into twitterandhyperlink."  + 
						table + 
						"(codedHyperlink, longHyperlink, HyperlinkContent) " +
						"values(?, ?, ?)";
		//System.out.println("#2" + INSERT_RECORD);
		try 
		{
			pstmt = conn.prepareStatement(INSERT_RECORD);
			
			for (_HyperlinkInfo twt : list)
			{		
				pstmt.setString(1, twt.getUrl());
				pstmt.setString(2, twt.getLongUrl());
				pstmt.setString(3, twt.getContent());
				
				pstmt.addBatch();	
				iRet++;
			}
			
			pstmt.executeBatch();
			
			ClearPreparedStatement();
			return iRet;
		}catch (SQLException e)
		{
			System.err.println("mysql error in insert list of tweets");
			e.printStackTrace();
			return iRet;
		}
	}
	
	public boolean IsContainHyperlink(String url,
			String table) 
	{
		// TODO Auto-generated method stub
		String sql = "SELECT codedHyperlink From twitterandhyperlink." +
				table + " WHERE codedHyperlink = ?";
				
		if (conn == null)
		{
			this.database_connection();
		}
	
		boolean bFound = false;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, url);
			rs =  pstmt.executeQuery();
			
			if(rs.next())
			{
				bFound = true;
			}
			else
			{
				bFound = false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			bFound = false;
		} finally
		{
			ClearPreparedStatement();
			ClearResultSet();
			return bFound;
		}			
	}

	public boolean IsContainHyperlink(_HyperlinkInfo hyper,
			String table) 
	{
			return IsContainHyperlink(hyper.getUrl(), table);
		
	}
	
}
