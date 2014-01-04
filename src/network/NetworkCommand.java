package network;

public class NetworkCommand
{
	
	public int ID = -1;
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

	public void setID(int id)
	{
		this.ID = id;
	}
	public int getID() {
		return this.ID;
	}

	public int getCommand()
	{
		return command;
	}
}
