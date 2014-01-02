package network;

public class NetworkCommand
{
	
	public int ID;
	public int command;
	public String message; 
	
	
	public void setMessage(String m)
	{
		this.message = m;
	}

	public void setCommand(int com)
	{
		this.command = com;
	}

	public int getID() {
		return this.ID;
	}
}
