package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.lbl.kb_bachelor_app.network.NetworkCommand;


public class TCPServer {

	public final static String TAG = "TCPServer: ";

	// DESIGNATE A PORT
	public static final int SERVERPORT = 8080;

	protected ServerSocket serverSocket;

	protected Thread acceptThread;
	protected Thread serverOutputThread;
	protected List<Thread> clientThreads;

	public TCPServer() {
		acceptThread = new Thread(new AcceptThread());
		serverOutputThread = new Thread()
	}

	public void InitServer() {
		acceptThread.start();
		serverOutputThread.start();
	}
	
	private void addNewConnection(Socket client) 
	{

	}
	
	private void pushCommandTo(int id, NetworkCommand nc)
	{
		
	}

	public class AcceptThread implements Runnable {

		public void run() {
			try {
				serverSocket = new ServerSocket(SERVERPORT);
				System.out.println(TAG + "started server");
				while (true) {
					// LISTEN FOR INCOMING CLIENTS

					System.out.println(TAG + "wait for incoming connections");
					Socket client = serverSocket.accept();
					addNewConnection(client);
					System.out.println(TAG + "handle shit");

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public class ServerOutputThread implements Runnable
	{

		@Override
		public void run() {
			while(true)
			{
				
			}
			
		}
		
	}
	

	public class ClientThread implements Runnable {

		public int ID;
		public Socket client;
		
		public PrintWriter out;
		public BufferedReader in;
		
		public Queue<NetworkCommand> commands = new LinkedList<NetworkCommand>();

		public ClientThread(int id, Socket client) {
			this.client = client;
			ID = id;
		}

		public void run() {
			try {
				System.out.println(TAG + "started connection with ID: " + ID);
				in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(client.getOutputStream())), true);
				while (true) {
					try {

						readInput(in);
						
						System.out.println(TAG + ID + " Sending command.");
						if (!commands.isEmpty()) {
							writeOutput(out);
							out.flush();
						} else
							System.out.println(TAG + ID + " no commands");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void readInput(BufferedReader in) throws IOException {
			System.out.println(TAG + ID + " read income");
			String line = in.readLine();
			System.out.println(TAG + ID + " " + line);
		}

		private void writeOutput(PrintWriter out) {
			
			System.out.println(TAG + ID + "write output");
			out.println(TAG + "Hey Server! was geht sascha");
		}
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
