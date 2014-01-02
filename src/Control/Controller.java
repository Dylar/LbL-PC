package Control;
import java.util.LinkedList;
import java.util.Queue;

import network.NetworkCommand;
import network.Server;
import network.TCPServer;
import network.gui.MainGui;


public class Controller{

	public final static String TAG = "Controller: ";
	
	public static final int GETID = 0;
	public static final int SENDMESSAGE = 1;
	public static final int STARTSERVER = 2;
	public static final int STOPSERVER = 3;
	public static final int GETMESSAGE = 4;
	
	public Server server;
	public MainGui gui;
	
	Queue<ControlAction> schedule;
	Thread scheThread;

	public Controller(MainGui gui)
	{
		this.gui = gui;
		server = new TCPServer(this);
		schedule = new LinkedList<ControlAction>();
		scheThread = newScheduleThread();
		scheThread.start();
	}

	public Thread newScheduleThread()
	{
		return new Thread(new Runnable(){
				@Override
				public void run()
				{
					while (true)
					{
						while (!schedule.isEmpty())
						{
							System.out.println(TAG +"Try next scheduled action");
							ControlAction ca = schedule.poll();
							switch (ca.action)
							{
								case 0: //get ID
									
									break;
								case 1: //SEND MESSAGE
									String m = ca.message;
									sendChatMessage(m);
									break;
								case 2://start server
									startServer();
									//TODO gui.setMeldung b lubb was auch immer
									break;
								case 3://stop server
									stopServer();
									break;
								case 5:break;
							}
							addToPool(ca);
						}
					}
				}
			});
	}

	public void scheduleAction(ControlAction ac)
	{
		schedule.add(ac);
	}

	private void startServer()
	{
		System.out.println(TAG +"try server start ");
		if (server.getState().equals(Server.State.STOPPED))
		{ 
				server.startServer();
				//TODO gui.setStatus etc
		}
		else
		{
			String m = "allready started";	
			System.out.println(TAG +m);
		}
	}

	private void stopServer()
	{
		System.out.println(TAG +"stop server");
		server.stopServer();
	}

	private void sendChatMessage(String m)
	{
		NetworkCommand nc = getNewNetworkCommand();
		nc.setCommand(SENDMESSAGE);
		nc.setMessage(m);
		server.addCommandToOutput(nc);
	}

//	public void writeQuickInfo(String m)
//	{
//		final String me = m;
//		cAct.handler.post(new Runnable(){
//			@Override
//			public void run(){
//				cAct.quickInfoTV.setText(me);
//			}
//		});
//	}

	public ControlAction getNewAction()
	{
		// TODO: Hier ne Fabrik + pool bauen damit wir die objecte immer wieder verwenden 
		return new ControlAction();
	}

	public void addToPool(ControlAction ca)
	{
		//TODO H
	}

	private NetworkCommand getNewNetworkCommand()
	{
		// TODO: auch hier ne fabrik
		return new NetworkCommand();
	}

	public void tryAction(ControlAction ac)
	{
		// TODO: Implement this method
	}
	
}
