package server;

public interface Server{
	
	
	public enum State
	{
		STARTED, STOPPED
	}
	
	public void addCommandToOutput(NetworkCommand nc);
	public void startServer();
	public void stopServer();
}
