package gh.polyu.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TwtServer {

	ServerSocket serverSocket = null;
	int iPortNumber = 10406;
	static boolean TWT_Listenning = true;
	
	
	TwtServer()
	{
		this(10406);
	}
	
	TwtServer(int iPortNumber)
	{
		this.iPortNumber = iPortNumber;
		try {
			System.out.println("Server binding port:" + this.iPortNumber);
			this.serverSocket = new ServerSocket(iPortNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error when creating the server socket");
		}
	}
	
	public int Listen() throws IOException
	{
		System.out.println("Server Listenning port:" + this.iPortNumber);
		while(TwtServer.TWT_Listenning)
		{
			if(TwtServer.TWT_Listenning)
			{
				Socket sock = this.serverSocket.accept();
				System.out.print("Get a connection:");
				System.out.println(sock.getInetAddress() + "::" +sock.getPort());
			}
		}
		return 0;
	}
	
	public int DataBaseTest()
	{
		return 0;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TwtServer ser = new TwtServer(10406);
		try {
			ser.Listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
