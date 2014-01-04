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
import Control.ControlHandler;
import Control.Controller;

public class TCPServer implements Server
{

	public final static String					TAG	= "TCPServer: ";
	public static int								idCount;

	
	// DESIGNATE A PORT
	public int										serverport;
	public ControlHandler						ctrl;
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

	private class AcceptThread implements Runnable
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

	private class ServerOutputThread implements Runnable
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

	@Override
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

	private class ClientData
	{
		public int							ID;
		public Socket						client;

		public PrintWriter				out;
		public BufferedReader			in;

		public Queue<NetworkCommand>	clientCommands;

		public Thread						clientThread;


		private ClientData(Socket client)
		{
			this.client = client;
			setID();

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
				case ControlHandler.SEND_ID:// Get ID
//					setID();
//					ca.setID(ID);
					System.out.println(TAG + ID + " GET ID");
					break;
				case ControlHandler.SEND_MESSAGE: // send Message

					System.out.println(TAG + ID + " SEND MESSAGE");
					break;
				case ControlHandler.GET_MESSAGE: // get message

					System.out.println(TAG + ID + " GET MESSAGE");
					break;
				default:
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
				case Controller.SEND_ID:
					out.println(Controller.SEND_ID);
					out.println(nc.ID);
					break;
				default:
					out.println(TAG + "Hey Server! was geht");
					break;
			}
			out.flush();
		}


		private synchronized void addCommandToClient(NetworkCommand nc)
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
						
						ControlAction ca = ctrl.getNewAction();
						ca.setAction(ControlHandler.SEND_ID);
						ca.setID(ID);
						tryAction(ca);
						
						while (state.equals(State.STARTED))
						{
							readInput();

							if (!clientCommands.isEmpty())
							{
								System.out.println(TAG + ID + " Sending command.");
								writeOutput(getNextClientCommand());
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
