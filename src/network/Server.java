package network;

import Control.Controller;

public interface Server
{

	public enum State
	{
		STARTED, STOPPED
	}

	public void addCommandToOutput(NetworkCommand nc);


	public void startServer();


	public void stopServer();
	
	public State getState();
	
	public void addController(Controller controller);
}
