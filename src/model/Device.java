package model;

public class Device {
	
	public boolean connected;
	public float latenz;
	public Gesture gesture;
	
	public String name;
	public String IP;
	public int ID;
	
	
	public void initDevice(String name, String ip, int id)
	{
		this.name = name;
		this.IP = ip;
		this.ID= id;
	}
	
	public void setGesture(Gesture g){
		this.gesture = g;
	}
	
	

}
