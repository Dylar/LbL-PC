package server;

public class NetworkCommand
{
	public enum Commands{
		SENDMESSAGE,
	}
	
	public Commands command;
	public String message; 
	
	
	public void setMessage(String m)
	{
		this.message = m;
	}

	public void setCommand(NetworkCommand.Commands com)
	{
		this.command = com;
	}
	
	
	
}
