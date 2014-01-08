package Control;

public interface ControlHandler
{

	public static final int	SEND_ID						= 0;
	public static final int	SEND_MESSAGE				= 1;
	public static final int	GET_MESSAGE					= 4;
	public static final int	START_SERVER				= 2;
	public static final int	STOP_SERVER					= 3;
	public static final int	SEND_GESTURE				= 5;
	public static final int	SET_SERVER_HEALTH			= 6;
	public static final int	APPEND_SERVER_HISTORY	= 7;


	public void scheduleAction(ControlAction ca);


	public ControlAction getNewAction();

}
