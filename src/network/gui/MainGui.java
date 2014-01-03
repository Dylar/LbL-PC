package network.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import network.Server;
import Control.ControlAction;
import Control.Controller;

/**
 * @date 16.12.2013
 * @time 22:23
 */
public class MainGui extends JFrame implements ActionListener {

	// JFrame mainFrame;
	private Controller ctrl;
	
	final private JTextArea serverHistory = new JTextArea(5, 16);
	final private JTextArea chatHistory = new JTextArea(5, 5);
	final private JTextField userInputField = new JTextField(50);
	private JLabel serverHealth;
	private JPanel innerCenterUpperPanel;
	private int deviceIterator = 0;
	
	public MainGui() {
		mainFrame();
		menuBar();
		initMainFrame();
	}

	private void mainFrame() {
		// Fenster initialisieren
		setTitle("LbL Server - Best Servers in the World!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(600, 450));
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);

		// TODO ServerHistory Font options
		Font f = serverHistory.getFont();
		Font serverFont = new Font(f.getFontName(), f.getStyle(),
				f.getSize() - 1);
		serverHistory.setFont(serverFont);
		serverHistory.setForeground(Color.RED);
	}

	
	private void initMainFrame() {
		// mainPane initialisieren
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		setContentPane(mainPane);

		// TODO Konstanten
		GridLayout rightUpperButtonSize = new GridLayout(3, 1);

		// TODO initialising Panels to mainFrame
		JPanel rightPanel = new JPanel();
		JPanel rightUpperButtonPanel = new JPanel();
		JPanel rightMiddlePanel = new JPanel();
		JPanel rightLowerPanel = new JPanel();
		
		JPanel leftPanel = new JPanel();

		// Center Panel
		JPanel centerPanel = new JPanel();
		innerCenterUpperPanel = new JPanel();
		JPanel innerCenterLowerPanel = new JPanel();
		JPanel userInputPanel = new JPanel();
		JPanel chatBoxPanel = new JPanel();

		// TODO Give Panels Layout
		rightPanel.setLayout(new BorderLayout());
		rightUpperButtonPanel.setLayout(rightUpperButtonSize);
		rightMiddlePanel.setLayout(new BorderLayout());
		rightLowerPanel.setLayout(new BorderLayout());
		leftPanel.setLayout(new BorderLayout());
		centerPanel.setLayout(new BorderLayout());
		innerCenterUpperPanel.setLayout(new GridLayout(5, 2, 3, 3));
		innerCenterLowerPanel.setLayout(new BorderLayout());
		userInputPanel.setLayout(new BorderLayout());
		chatBoxPanel.setLayout(new BorderLayout());

		// TODO creating Buttons to rightUpperPanel
		JLabel serverInfoLabel = new JLabel("Server information:");
		JLabel serverConnection = new JLabel("Mit Server verbunden?");
		
		
		JButton connectButton = new JButton("Connect");// das ist "Start"
		JButton disconnectButton = new JButton("Disconnect");// das ist "Stop"
		JButton restartButton = new JButton("Restart");
		JButton sendButton = new JButton("Send Message");

		// TODO Assign Panels to the "Frames"
		mainPane.add(rightPanel, BorderLayout.EAST);
		mainPane.add(leftPanel, BorderLayout.CENTER);

		leftPanel.add(centerPanel, BorderLayout.CENTER);

		// Adding right panels
		rightPanel.add(rightUpperButtonPanel, BorderLayout.NORTH);
		rightPanel.add(rightMiddlePanel, BorderLayout.CENTER);
		rightPanel.add(rightLowerPanel, BorderLayout.SOUTH);

		centerPanel.add(innerCenterUpperPanel, BorderLayout.CENTER);
		centerPanel.add(innerCenterLowerPanel, BorderLayout.SOUTH);

		innerCenterLowerPanel.add(userInputPanel, BorderLayout.SOUTH);
		userInputPanel.add(userInputField, BorderLayout.CENTER);

		
		
		// Scrollpanes for the Server message panel and chatbox
		JScrollPane chatScroll = new JScrollPane(chatHistory);
		chatHistory.setLineWrap(true);
		chatHistory.setWrapStyleWord(true);
		chatHistory.setEditable(false);
		chatScroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		innerCenterLowerPanel.add(chatBoxPanel, BorderLayout.NORTH);
		chatBoxPanel.add(chatScroll, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(serverHistory);
		serverHistory.setLineWrap(true);
		serverHistory.setWrapStyleWord(true);
		serverHistory.setEditable(false);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		rightMiddlePanel.add(scrollPane, SwingConstants.CENTER);

		// TODO invisible Borders
		// TOP, LEFT, BOT, RIGHT
		rightMiddlePanel
				.setBorder(BorderFactory.createEmptyBorder(10, 1, 4, 1));
		innerCenterLowerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5,
				0, 5));
		rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		rightLowerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		userInputPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 4, 0));
		chatBoxPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));
		innerCenterUpperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		// TODO Adding buttons to Panels
		serverHealth  = new JLabel(" ", JLabel.CENTER);
		serverHealth.setOpaque(true);
		serverHealth.setBackground(Color.RED);
		
		rightUpperButtonPanel.add(connectButton);
		rightUpperButtonPanel.add(disconnectButton);
		rightUpperButtonPanel.add(restartButton);
		userInputPanel.add(sendButton, BorderLayout.EAST);
		rightMiddlePanel.add(serverInfoLabel, BorderLayout.NORTH);
		rightLowerPanel.add(serverConnection, BorderLayout.NORTH);
		rightLowerPanel.add(serverHealth, BorderLayout.CENTER);
		

		// TODO Separators
		leftPanel.add(new JSeparator(SwingConstants.VERTICAL),
				BorderLayout.EAST);
		innerCenterLowerPanel.add(new JSeparator(SwingConstants.HORIZONTAL),
				BorderLayout.CENTER);

		// TODO Actions
		sendButton.addActionListener(this);
		sendButton.setActionCommand("ACTION_COMMAND_SEND");
		userInputField.addActionListener(this);
		userInputField.setActionCommand("ACTION_COMMAND_SEND");
		connectButton.addActionListener(this);
		connectButton.setActionCommand("ACTION_COMMAND_CONNECT");
		disconnectButton.addActionListener(this);
		disconnectButton.setActionCommand("ACTION_COMMAND_DISCONNECT");
		restartButton.addActionListener(this);
		restartButton.setActionCommand("ACTION_COMMAND_RESET");		
		
		validate();
	}

	//bei connect von client 
	private JPanel deviceInformation() {
		
		deviceIterator++;
		if(deviceIterator <= 10) {
		JPanel devicePanel = new JPanel();
		devicePanel.setLayout(new BoxLayout(devicePanel, BoxLayout.Y_AXIS));
		JLabel deviceName = new JLabel("Geraetename: ");
		JLabel deviceLatency = new JLabel("Latenz:");
		JLabel deviceVariable = new JLabel("Platzhalter");
		devicePanel.setSize(new Dimension(200,60));
		
		Border border = devicePanel.getBorder();
		Border margin = new LineBorder(Color.darkGray,4);
		devicePanel.setBorder(new CompoundBorder(border, margin));
		
		devicePanel.add(deviceName);
		devicePanel.add(deviceLatency);
		devicePanel.add(deviceVariable);
		innerCenterUpperPanel.add(devicePanel);
		devicePanel.revalidate();
		devicePanel.repaint();
		return devicePanel;
		} else {	
			serverHistory.append("Es sind zu viele Gerï¿½te angemeldet!"); 
			return null;
		}
	}
	
	public void actionPerformed(ActionEvent e) {// TODO Das ganze kann bzw soll in den controller... in der GUIkeine "logik" sondern nur infos fÃ¼r den controller(also das command enfch eeitercgebb ) guck dir dss beim clientact und controller in der app an
		String PLACEHOLDER = " _____________\n";
		
		

		
		if (e.getActionCommand().equals("ACTION_COMMAND_SEND")) {
			final String fromUser = userInputField.getText();
			if (!(fromUser.equals(""))) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				String formattedDate = sdf.format(date);

				chatHistory.append("[" + formattedDate + "]");
				chatHistory.append("\"User\": ");
				chatHistory.append(fromUser);
				chatHistory.append("\n");
				chatHistory.setCaretPosition(chatHistory.getDocument()
						.getLength());
				chatHistory.setCaretPosition(chatHistory.getDocument()
						.getLength());

				userInputField.setText("");
			}
		}// end if

		if (e.getActionCommand().equals("ACTION_COMMAND_CONNECT")) {
			ControlAction ca = ctrl.getNewAction();
			ca.setAction(Controller.STARTSERVER);
			tryAction(ca);
			//TODO Server message an die GUI, in die ServerHistory!
			//"Server ist gestartet" oder was da reinkommt...
		}

		if (e.getActionCommand().equals("ACTION_COMMAND_DISCONNECT")) {
			// TODO WENN nicht mehr verbunden mit dem server -> ausgabe oder
			//wenn disconnect gedrückt würde
			ControlAction ca = ctrl.getNewAction();
			ca.setAction(Controller.STOPSERVER);
			tryAction(ca);
			//TODO genauso hier. möglichst wie unten
//			serverHistory.append("Verbindung zum Server unterbrochen!");
//			serverHistory.append(PLACEHOLDER);
//			serverHealth.setBackground(Color.RED);
		}// end if

		if (e.getActionCommand().equals("ACTION_COMMAND_RESET")) {
			Object[] options = { "Ja, bitte!", "Nein danke!" };
			int choice = JOptionPane.showOptionDialog(null,
					"Soll der Server neu gestartet werden?", "Server neustart",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.DEFAULT_OPTION, null, options, options[1]);

			if (choice == 0) {
				ControlAction ca = ctrl.getNewAction();
				ca.setAction(Controller.STOPSERVER);
				tryAction(ca);
				ca.setAction(Controller.STARTSERVER);
				tryAction(ca);
				//TODO same procedure as every method? YES as EVERY method
				//extra abfrage noch integrieren ?
//				serverHistory.append("Server wird neu gestartet!");
//				serverHistory.append(PLACEHOLDER);
				// TODO Was passiert, wenn der server neugestartet werden soll
			}
		}// end if

		if (e.getActionCommand().equals("ACTION_COMMAND_CLOSE")) {
			System.exit(EXIT_ON_CLOSE);
		}
	}
	
	public void tryAction(ControlAction ca)
	{
		ctrl.scheduleAction(ca);
	}
	
	public void setServerHistory(String s)
	{
		String PLACEHOLDER = " _____________\n";
		serverHistory.append(s);
		serverHistory.append(PLACEHOLDER);
		
	}

	public void setServerHealth(boolean b)
	{
		serverHealth.setBackground(new Color(0, 180, 0));
	}
	private void menuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// Hauptreiter
		JMenu menuDatei = new JMenu("Datei");
		menuBar.add(menuDatei);

		JMenu menuAbout = new JMenu("Mehr");
		menuBar.add(menuAbout);

		JMenuItem aboutEntry = new JMenuItem("About");
		menuAbout.add(aboutEntry);
		// Untermenuepunkte
		JMenuItem newEntry = new JMenuItem("Neu");
		menuDatei.add(newEntry);

		JMenuItem oeffnenEntry = new JMenuItem("ï¿½ffnen");
		menuDatei.add(oeffnenEntry);

		JMenuItem exitEntry = new JMenuItem("Beenden");
		menuDatei.add(exitEntry);
		exitEntry.addActionListener(this);
		exitEntry.setActionCommand("ACTION_COMMAND_CLOSE");
	}

	public static void systemLookandFeel() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("Unsupported Look and Feel Exception!");
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found Exception!");
		} catch (InstantiationException e) {
			System.err.println("Instantiation Exception!");
		} catch (IllegalAccessException e) {
			System.err.println("Illegal Acces Exception!");
		}
	}
}
