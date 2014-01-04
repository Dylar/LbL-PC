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

		schedule = new LinkedList<ControlAction>();
		scheThread = newScheduleThread();
		scheThread.start();
	}


	private Thread newScheduleThread()
	{
		return new Thread(new Runnable(){
			@Override
			public void run()
			{
				while (true)
				{
					while (!schedule.isEmpty())
					{
						System.out.println(TAG + "Try next scheduled action");
						ControlAction ca = schedule.poll();
						tryAction(ca);
						addToPool(ca);
					}
				}
			}
		});
	}


	@Override
	public void scheduleAction(ControlAction ac)
	{
		schedule.add(ac);
	}


	private void startServer()
	{
		System.out.println(TAG + "try server start ");
		if (server.getState().equals(Server.State.STOPPED))
		{
			server.startServer();
			// TODO gui.setStatus etc
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
	}


	private void sendID(int id)
	{
		NetworkCommand nc = getNewNetworkCommand();
		nc.setCommand(SEND_ID);
		nc.setID(id);
		server.addCommandToOutput(nc);
	}


	// public void writeQuickInfo(String m)
	// {
	// final String me = m;
	// cAct.handler.post(new Runnable(){
	// @Override
	// public void run(){
	// cAct.quickInfoTV.setText(me);
	// }
	// });
	// }

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


	private void tryAction(ControlAction ca)
	{
		switch (ca.action)
		{
			case SEND_ID: // send ID
				sendID(ca.getID());
				break;
			case SEND_MESSAGE: // SEND MESSAGE
				sendChatMessage(ca.message);
				break;
			case START_SERVER:// start server
				startServer();
				// TODO gui.setMeldung blubb was auch immer
				break;
			case STOP_SERVER:// stop server
				stopServer();
				break;
			case 5:
				break;
		}
	}

}
