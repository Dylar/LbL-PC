package Control;

import java.util.LinkedList;
import java.util.Queue;

import network.NetworkCommand;
import network.Server;
import network.TCPServer;
import network.gui.MainGui;

public class Controller implements ControlHandler
{

	public final static String	TAG				= "Controller: ";


	public Server					server;
	public MainGui					gui;

	Queue<ControlAction>			schedule;
	Thread							scheThread;


	public Controller()
	{
		this.server = new TCPServer();
		this.server.addController(this);
		
		this.gui = new MainGui();
		this.gui.addController(this);
	}

	@Override
	public synchronized void scheduleAction(ControlAction ca)
	{
		tryAction(ca);
		addToPool(ca);
	}

	private synchronized void tryAction(ControlAction ca)
	{
		switch (ca.action)
		{
			case SEND_ID:
				sendID(ca.getID());
				break;
			case SEND_MESSAGE:
				sendChatMessage(ca.message);
				break;
			case START_SERVER:
				startServer();
				break;
			case STOP_SERVER:
				stopServer();
				break;
			case SET_SERVER_HEALTH:
				gui.setServerHealth(ca.serverHealth);
				break;
			case APPEND_SERVER_HISTORY:
				gui.appendServerHistory(ca.message);
				break;
		}
	}
	
	private void startServer()
	{
		System.out.println(TAG + "try server start ");
		if (server.getState().equals(Server.State.STOPPED))
		{
			server.startServer();
		}
		else
		{
			String m = "allready started";
			System.out.println(TAG + m);
		}
	}


	private void stopServer()
	{
		System.out.println(TAG + "stop server");
		server.stopServer();
	}


	private void sendChatMessage(String m)
	{
		NetworkCommand nc = getNewNetworkCommand();
		nc.setCommand(SEND_MESSAGE);
		nc.setMessage(m);
		server.addCommandToOutput(nc);
		
		gui.appendChatMessage(m);
	}


	private void sendID(int id)
	{
		NetworkCommand nc = getNewNetworkCommand();
		nc.setCommand(SEND_ID);
		nc.setID(id);
		server.addCommandToOutput(nc);
	}
	
	@Override
	public ControlAction getNewAction()
	{
		// TODO: Hier ne Fabrik + pool bauen damit wir die objecte immer wieder
		// verwenden
		return new ControlAction();
	}


	private void addToPool(ControlAction ca)
	{
		// TODO H
	}


	private NetworkCommand getNewNetworkCommand()
	{
		// TODO: auch hier ne fabrik
		return new NetworkCommand();
	}

}
