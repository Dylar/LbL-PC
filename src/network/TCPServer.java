package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import Control.ControlAction;
import Control.ControlHandler;

public class TCPServer implements Server
{

	public final static String					TAG	= "TCPServer: ";
	public static int								idCount;

	// DESIGNATE A PORT
	public int										serverport;
	public ControlHandler						ctrl;
	private ServerSocket							serverSocket;

	private Thread									acceptThread;
	private HashMap<Integer, ClientData>	connectedClients;

	private State									state;


	public TCPServer()
	{
		serverport = 8080;
		state = State.STOPPED;
	}


	@Override
	public void addController(ControlHandler ch)
	{
		this.ctrl = ch;
	}


	@Override
	public void tryAction(ControlAction ca)
	{
		ctrl.scheduleAction(ca);
	}


	@Override
	public State getState()
	{
		return state;
	}


	@Override
	public void startServer()
	{
		connectedClients = new HashMap<>();
		
		acceptThread = new Thread(new AcceptThread());
		acceptThread.start();
	}


	@Override
	public void stopServer()
	{
		state = State.STOPPED;
		try
		{
			serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public ControlAction getNewAction()
	{
		return ctrl.getNewAction();
	}

	private void appendServerHistory(String s)
	{
		ControlAction ca = getNewAction();
		ca.setAction(ControlHandler.APPEND_SERVER_HISTORY);
		ca.setMessage(s);
		tryAction(ca);
	}

	private class AcceptThread implements Runnable
	{
		public void run()
		{
			try
			{
				serverSocket = new ServerSocket(serverport);
				System.out.println(TAG + "started server");

				state = State.STARTED;
				
				ControlAction ca = getNewAction();
				ca.setAction(ControlHandler.SET_SERVER_HEALTH);
				ca.setServerHealth(true);
				tryAction(ca);
				
				appendServerHistory("Server started");
				
				while (state.equals(State.STARTED))
				{
					System.out.println(TAG + "waiting for incoming connections");
					Socket client = serverSocket.accept();
					addNewConnection(client);
				}
			}
			catch (SocketException e)
			{
				ControlAction ca = getNewAction();
				ca.setAction(ControlHandler.SET_SERVER_HEALTH);
				ca.setServerHealth(false);
				tryAction(ca);
				
				appendServerHistory("Server stopped");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}


	private void addNewConnection(Socket client)
	{
		connectedClients.put(idCount, new ClientData(client));
	}

	@Override
	public synchronized void addCommandToOutput(NetworkCommand nc)
	{
		// TODO hier ist dann die schnittstelle von draußen
		int id = nc.getID();
		switch (id)
		{
			case -1:
				pushCommandToAll(nc);
				break;
			default:
				pushCommandToClient(id, nc);
				break;
		}
	}

	private void pushCommandToClient(int id, NetworkCommand nc)
	{
		ClientData ct = connectedClients.get(id);
		ct.addCommandToClient(nc);
	}


	private void pushCommandToAll(NetworkCommand nc)
	{
		Iterator<Entry<Integer, ClientData>> iter = connectedClients.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry<Integer, ClientData> pairs = (Map.Entry<Integer, ClientData>) iter.next();
			Integer id = pairs.getKey();
			pushCommandToClient(id, nc);
			iter.remove(); // avoids a ConcurrentModificationException
		}
	}

	private class ClientData
	{
		public int							ID;
		public Socket						client;

		public PrintWriter				out;
		public BufferedReader			in;

		public Thread						clientInput;


		private ClientData(Socket client)
		{
			this.client = client;
			setID();

			clientInput = new Thread(getNewClientInputRun());
			clientInput.start();
		}


		private void setID()
		{
			this.ID = idCount++;
			System.out.println(TAG + ID + " SetID");
		}


		private synchronized void readInput() throws IOException
		{
			// TODO
			System.out.println(TAG + ID + " read income");

			ControlAction ca = getNewAction();
			ca.setAction(Integer.valueOf(in.readLine()));

			switch (ca.action)
			{
				case ControlHandler.SEND_MESSAGE: // send Message
					ca.setAction(ControlHandler.GET_MESSAGE);
					ca.setMessage(in.readLine());
					break;
			}
			tryAction(ca);
		}


		private synchronized void writeOutput(NetworkCommand nc)
		{
			// TODO output schreiben
			System.out.println(TAG + ID + "write output");
			switch (nc.getCommand())
			{
				case ControlHandler.SEND_ID:
					out.println(ControlHandler.SEND_ID);
					out.println(nc.ID);
					break;
				case ControlHandler.SEND_MESSAGE:
					out.println(ControlHandler.SEND_MESSAGE);
					out.println(nc.getMessage());
				case ControlHandler.SEND_GESTURE:
					out.println(ControlHandler.SEND_GESTURE);
					//TODO hier dann gucken was übertragen werden muss
					break;
			}
			out.flush();
		}


		private synchronized void addCommandToClient(NetworkCommand nc)
		{
			// TODO hier ist dann die schnittstelle für den Server
			writeOutput(nc);
		}

		private Runnable getNewClientInputRun()
		{
			return new Runnable(){
				public void run()
				{
					try
					{
						System.out.println(TAG + "started connection with ID: " + ID);
						in = new BufferedReader(new InputStreamReader(client.getInputStream()));
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
						
						ControlAction ca = getNewAction();
						ca.setAction(ControlHandler.SEND_ID);
						ca.setID(ID);
						tryAction(ca);
						
						appendServerHistory("Client " + ID + ": Connected");
						
						while (state.equals(State.STARTED))
						{
							if (in.ready())
								readInput();
							Thread.sleep(500);
						}

					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			};
		}

	}

}
