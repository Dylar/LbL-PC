package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public abstract class AbsServer implements Server{

	
	// DESIGNATE A PORT
	public static final int SERVERPORT = 8080;

	protected ServerSocket serverSocket;
	
	protected Thread st;
	protected List<Thread> ct;
	
	public AbsServer()
	{
		st = new Thread(new ServerThread());
	}
	
	public void InitServer()
	{
		st.start();
	}
	
	public class ServerThread implements Runnable {

		public void run() {
			try {
				serverSocket = new ServerSocket(SERVERPORT);
				System.out.println("started");
				while (true) {
					// LISTEN FOR INCOMING CLIENTS

					System.out.println("wait shit");
					Socket client = serverSocket.accept();

					System.out.println("handle shit");
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public class ConnectThread implements Runnable {
		
		public int ID;
		public Socket client;
		
		public ConnectThread(int id, Socket client)
		{
			this.client = client;
			ID = id;
		}
		
		public void run() {
			try {
				serverSocket = new ServerSocket(SERVERPORT);
				System.out.println("started");
				while (true) {
					// LISTEN FOR INCOMING CLIENTS

					System.out.println("wait shit");
					Socket client = serverSocket.accept();

					System.out.println("handle shit");
					try {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(client.getInputStream()));
						String line = null;
						line = in.readLine();
//						while ((line = in.readLine()) != null) {
							System.out.println(line);
							// DO WHATEVER YOU WANT TO THE FRONT END
							// THIS IS WHERE YOU CAN BE CREATIVE
							
//						}
						System.out.println("in rdy");
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
						//test++;
						Thread.sleep(10000);

						System.out.println("write shit");
						out.write("muh");
						
						out.newLine();

						System.out.println("flush shit");
						out.flush();

						System.out.println("flush done");
						//break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
