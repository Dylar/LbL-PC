package network;

import Control.ControlListener;


public interface Server extends ControlListener
{

	public enum State
	{
		STARTED, STOPPED
	}

	public void addCommandToOutput(NetworkCommand nc);


	public void startServer();


	public void stopServer();
	
	public State getState();
	
}
