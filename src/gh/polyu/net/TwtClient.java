package gh.polyu.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TwtClient {

	private Socket sock = null;
	
	private void connect() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		connect("127.0.0.1", 10406);
	}

	public void connect (String sServerName, int iPortNumber) throws UnknownHostException, IOException
	{
		sock = new Socket(sServerName, iPortNumber);
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String hostIP = "127.0.0.1";
		int iPortNumber = 10406;
		
		TwtClient client = new TwtClient();
		try {
			client.connect(hostIP, iPortNumber);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Unknown host " + hostIP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Couldn't get I/O for the connection to " + hostIP);
		}
	}



	
}
