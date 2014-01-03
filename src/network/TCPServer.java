package network;

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

import Control.ControlAction;
import Control.Controller;

public class TCPServer implements Server
{

	public static void main(String... arg)
	{
		Controller c = new Controller();
		c.server.startServer();

	}

	public final static String					TAG	= "TCPServer: ";
	public static int								idCount;

	public Controller								ctrl;
	// DESIGNATE A PORT
	public int										serverport;

	private ServerSocket							serverSocket;

	private Thread									acceptThread;
	private Thread									serverOutputThread;
	// private Thread serverInputThread;
	private HashMap<Integer, ClientData>	connectedClients;

	private State									state;

	private Queue<NetworkCommand>				outputCommands;


	public TCPServer()
	{
		serverport = 8080;
		state = State.STOPPED;
	}


	@Override
	public void addController(Controller controller)
	{
		this.ctrl = controller;
	}

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
		acceptThread = new Thread(new AcceptThread());
		serverOutputThread = new Thread(new ServerOutputThread());
		// serverInputThread = new Thread(new ServerInputThread());

		outputCommands = new LinkedList<NetworkCommand>();
		connectedClients = new HashMap<>();

		acceptThread.start();
		serverOutputThread.start();
		// serverInputThread.start();

		state = State.STARTED;
	}


	@Override
	public void stopServer()
	{
		state = State.STOPPED;
	}

	public class AcceptThread implements Runnable
	{
		public void run()
		{
			try
			{
				serverSocket = new ServerSocket(serverport);
				System.out.println(TAG + "started server");

				while (state.equals(State.STARTED))
				{
					System.out.println(TAG + "waiting for incoming connections");
					Socket client = serverSocket.accept();
					addNewConnection(client);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	private void addNewConnection(Socket client)
	{
		connectedClients.put(idCount, new ClientData(client));
	}

	public class ServerOutputThread implements Runnable
	{
		@Override
		public void run()
		{
			System.out.println(TAG + "waiting for output");
			while (state.equals(State.STARTED))
			{
				while (!outputCommands.isEmpty())
				{
					NetworkCommand nc = getNextOutputCommand();
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
			}
		}
	}


	public synchronized void addCommandToOutput(NetworkCommand nc)
	{
		// TODO hier ist dann die schnittstelle von draußen
		outputCommands.add(nc);
	}


	private synchronized NetworkCommand getNextOutputCommand()
	{
		return outputCommands.poll();
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

	public class ClientData
	{
		public int							ID;
		public Socket						client;

		public PrintWriter				out;
		public BufferedReader			in;

		public Queue<NetworkCommand>	clientCommands;

		public Thread						clientThread;


		public ClientData(Socket client)
		{
			this.client = client;

			clientCommands = new LinkedList<NetworkCommand>();
			clientThread = new Thread(getNewClientRun());
			clientThread.start();

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
			ControlAction ca = ctrl.getNewAction();
			ca.setAction(Integer.valueOf(in.readLine()));
			switch (ca.action)
			{
				case 0:// Get ID

					break;
				case 1: // send Message

					break;
				case 5: // get message

					break;
				default:
					break;
			}
			
			tryAction(ca);
			// System.out.println(TAG + ID + " " + line);
		}


		private synchronized void writeOutput(NetworkCommand networkCommand)
		{
			// TODO output schreiben
			System.out.println(TAG + ID + "write output");
			out.println(TAG + "Hey Server! was geht sascha");
		}


		public synchronized void addCommandToClient(NetworkCommand nc)
		{
			// TODO hier ist dann die schnittstelle für den Server
			clientCommands.add(nc);
		}


		private NetworkCommand getNextClientCommand()
		{
			return clientCommands.poll();
		}


		private Runnable getNewClientRun()
		{
			return new Runnable(){
				public void run()
				{
					try
					{
						System.out.println(TAG + "started connection with ID: " + ID);
						in = new BufferedReader(new InputStreamReader(client.getInputStream()));
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
						while (state.equals(State.STARTED))
						{
							readInput();

							if (!clientCommands.isEmpty())
							{
								System.out.println(TAG + ID + " Sending command.");
								writeOutput(getNextClientCommand());
								out.flush();
							}
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
