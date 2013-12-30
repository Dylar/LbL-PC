package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;



public class TCPServer {

	public final static String TAG = "TCPServer: ";

	// DESIGNATE A PORT
	public static final int SERVERPORT = 8080;

	protected ServerSocket serverSocket;

	protected Thread acceptThread;
	protected Thread serverOutputThread;
	protected HashMap<Integer, ClientThread> clientThreads;

	public TCPServer() {
		acceptThread = new Thread(new AcceptThread());
		serverOutputThread = new Thread(new ServerOutputThread());
	}

	public void InitServer() {
		acceptThread.start();
		serverOutputThread.start();
	}
	
	public void pushCommand(NetworkCommand nc)
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
		
		private void addNewConnection(Socket client) 
		{
			
		}

	}
	
	public class ServerOutputThread implements Runnable
	{

		public Queue<NetworkCommand> commands = new LinkedList<NetworkCommand>();
		
		@Override
		public void run() {
			while(true)
			{
				while(!commands.isEmpty())
				{
					NetworkCommand nc = commands.poll();
					int id = nc.getID();
					switch (id) {
					case -1:
						pushCommandToAll(nc);
						break;
					default:
						pushCommandTo(id, nc);
						break;
					}
				}
			}
		}
		
		private void pushCommandTo(int id, NetworkCommand nc)
		{
			ClientThread ct = clientThreads.get(id);
			ct.commands.add(nc);
		}
		
		private void pushCommandToAll(NetworkCommand nc)
		{
			Iterator<Entry<Integer, ClientThread>> iter = clientThreads.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer, ClientThread> pairs = (Map.Entry<Integer, ClientThread>)iter.next();
				Integer id = pairs.getKey();
				pushCommandTo(id, nc);
		        iter.remove(); // avoids a ConcurrentModificationException
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

						readInput();
						
						System.out.println(TAG + ID + " Sending command.");
						if (!commands.isEmpty()) {
							writeOutput(commands.poll());
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

		private void readInput() throws IOException {
			System.out.println(TAG + ID + " read income");
			String line = in.readLine();
			System.out.println(TAG + ID + " " + line);
		}

		private void writeOutput(NetworkCommand networkCommand) {
			
			System.out.println(TAG + ID + "write output");
			out.println(TAG + "Hey Server! was geht sascha");
		}
	}
}
