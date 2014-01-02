package Control;


public class ControlAction
{
	
	public int action;

	public String serverIp;
	public int serverPort;

	public String message;

	public void setMessage(String message)
	{
		this.message = message;
	}

	public void setAction(int action)
	{
		this.action = action;
	}

	public void setPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	public void setIpAddress(String serverIpAddress)
	{
		this.serverIp = serverIpAddress;
	}
}
