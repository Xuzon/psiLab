package window;

import java.awt.Button;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import window.LogMessage.LogLevel;

public class GameFrame extends Frame {

	private static final long serialVersionUID = 1L;
	protected ArrayList<LogMessage> messageList = new ArrayList<LogMessage>();
	public static final Color backgroundColor = new Color(31, 105, 127);
	protected static final Color newGameButtonColor = new Color(255, 238, 26);
	protected static final Color stopGameButtonColor = new Color(178, 26, 72);
	protected static final Color resumeGameButtonColor = new Color(29, 164, 204);
	protected static final Color labelColor = new Color(255, 158, 62);
	protected int width = 512;
	protected int height = 512;
	protected String title = "GUI";
	protected GridBagLayout GBL = new GridBagLayout();
	protected GridBagConstraints GBC = new GridBagConstraints();
	protected GameActionListener gActionListener;
	protected GameItemListener gItemListener;
	protected JScrollPane scrollPane;
	protected JTextArea matrixArea;
	protected List logList;
	protected boolean logActivated = true;
	protected LogLevel activeLogLevel = LogLevel.INFO;
	protected List playersList;
	protected ArrayList<PlayerInfo> playersInfoList = new ArrayList<PlayerInfo>();
	protected Button newGameButton;
	protected Button stopGameButton;
	protected Button resumeGameButton;
	protected Label nRoundsLabel;
	protected int nRounds;
	protected Label nPlayersLabel;
	protected int nPlayers;
	protected Label nGamesLabel;
	protected int nGames;
	protected int matrixTurnsChange = 10;
	protected int delay = 5;

	protected GameMatrix gameMatrix;

	public GameFrame() {
	}

	public GameFrame(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) dim.getWidth() / 2 - width / 2;
		int y = (int) dim.getHeight() / 2 - height / 2;
		Rectangle rect = new Rectangle(x, y, width, height);
		gActionListener = new GameActionListener(this);
		gItemListener = new GameItemListener(this);
		this.setLayout(GBL);
		SetupWindow();
		ActivateWindow(title, rect);
	}

	private void ActivateWindow(String title, Rectangle rect) {
		this.setBounds(rect);
		this.setTitle(title);
		this.setVisible(true);
		this.addWindowListener(new GameWindowListener(this));
	}

	protected void SetupWindow() {
		SetupMenu();
		AreasSetup();
		LabelSetup();
		ButtonsSetup();
		ChangeComponentsBackgroundColor(backgroundColor);

	}

	private void AreasSetup() {
		matrixArea = new JTextArea();
		matrixArea.setEditable(false);
		gameMatrix = new GameMatrix(5);
		matrixArea.setText(gameMatrix.ToString());
		matrixArea.setForeground(labelColor);
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 2;
		GBC.gridy = 0;
		GBC.gridwidth = 3;
		GBC.gridheight = 2;
		GBC.weightx = 1;
		GBC.weighty = 1;
		GBC.insets = new Insets(10, 10, 0, 10);
		scrollPane = new JScrollPane(matrixArea);
		this.add(scrollPane, GBC);
		GBC = new GridBagConstraints();

		logList = new List();
		for (int i = 0; i < 10; i++) {
			LogLevel logLevel = (i % 2) == 0 ? LogLevel.INFO : LogLevel.DETAILED;
			messageList.add(new LogMessage("LOG ENTRY :" + i, logLevel));
		}
		RefreshLog(true, LogLevel.INFO);
		logList.setForeground(labelColor);
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 1;
		GBC.gridy = 3;
		GBC.gridwidth = 3;
		GBC.gridheight = 2;
		GBC.weightx = 1;
		GBC.weighty = 1;
		GBC.insets = new Insets(0, 0, 10, 0);
		this.add(logList, GBC);
		GBC = new GridBagConstraints();

		playersList = new List();
		playersList.setForeground(labelColor);
		for (int i = 0; i < 10; i++) {
			PlayerInfo pI = new PlayerInfo("Player", i);
			playersInfoList.add(pI);
			nPlayers++;
		}
		RefreshPlayersList();
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 0;
		GBC.gridy = 0;
		GBC.gridwidth = 2;
		GBC.gridheight = 2;
		GBC.weightx = 0.1;
		GBC.weighty = 5;
		// up,left,bottom,right
		GBC.insets = new Insets(10, 10, 0, 10);
		this.add(playersList, GBC);
		GBC = new GridBagConstraints();
	}

	private void ButtonsSetup() {
		newGameButton = new Button("New Game");
		newGameButton.setBackground(newGameButtonColor);
		newGameButton.addActionListener(gActionListener);
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBC.gridx = 1;
		GBC.gridy = 2;
		GBC.gridwidth = 1;
		GBC.gridheight = 1;
		GBC.weightx = 0.33;
		GBC.weighty = 1;
		this.add(newGameButton, GBC);
		GBC = new GridBagConstraints();

		stopGameButton = new Button("Stop");
		stopGameButton.addActionListener(gActionListener);
		stopGameButton.setBackground(stopGameButtonColor);
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBC.gridx = 2;
		GBC.gridy = 2;
		GBC.gridwidth = 1;
		GBC.gridheight = 1;
		GBC.weightx = 0.33;
		GBC.weighty = 1;
		this.add(stopGameButton, GBC);
		GBC = new GridBagConstraints();

		resumeGameButton = new Button("Resume");
		resumeGameButton.addActionListener(gActionListener);
		resumeGameButton.setBackground(resumeGameButtonColor);
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBC.gridx = 3;
		GBC.gridy = 2;
		GBC.gridwidth = 1;
		GBC.gridheight = 1;
		GBC.weightx = 0.33;
		GBC.weighty = 1;
		this.add(resumeGameButton, GBC);
		GBC = new GridBagConstraints();
	}

	private void LabelSetup() {
		nRoundsLabel = new Label("NRounds = 0");
		nRoundsLabel.setForeground(labelColor);
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBC.gridx = 0;
		GBC.gridy = 2;
		GBC.gridwidth = 1;
		GBC.gridheight = 1;
		GBC.weightx = 0.1;
		GBC.weighty = 1;
		this.add(nRoundsLabel, GBC);
		GBC = new GridBagConstraints();

		nGamesLabel = new Label("NGames = 0");
		nGamesLabel.setForeground(labelColor);
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBC.gridx = 0;
		GBC.gridy = 3;
		GBC.gridwidth = 1;
		GBC.gridheight = 1;
		GBC.weightx = 0.1;
		GBC.weighty = 1;
		this.add(nGamesLabel, GBC);
		GBC = new GridBagConstraints();

		nPlayersLabel = new Label("NPlayers = 0");
		nPlayersLabel.setText("NPlayers = " + nPlayers);
		nPlayersLabel.setForeground(labelColor);
		GBC.anchor = GridBagConstraints.CENTER;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBC.gridx = 0;
		GBC.gridy = 4;
		GBC.gridwidth = 1;
		GBC.gridheight = 1;
		GBC.weightx = 0.1;
		GBC.weighty = 1;
		this.add(nPlayersLabel, GBC);
		GBC = new GridBagConstraints();
	}

	protected void SetupMenu() {
		MenuBar oMB = new MenuBar();
		Menu oMenu = new Menu("Fichero");
		MenuItem oMI = new MenuItem("Nueva Partida");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMenu.add(new MenuItem("-"));
		oMI = new MenuItem("Salir", new MenuShortcut('X'));
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMB.add(oMenu);

		oMenu = new Menu("Edit");
		oMI = new MenuItem("Renombra Jugador");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMI = new MenuItem("Reset Jugs");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMI = new MenuItem("Borra Jugador");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMB.add(oMenu);

		oMenu = new Menu("Ejecuta");
		oMI = new MenuItem("New Game");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMI = new MenuItem("Stop");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMI = new MenuItem("Resume");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMI = new MenuItem("Num. Partidas");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMI = new MenuItem("Cambia Matriz cada...");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMI = new MenuItem("Cambia Retardo");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMI = new MenuItem("Num. Filas/Columnas");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMB.add(oMenu);

		oMenu = new Menu("Ventana");
		CheckboxMenuItem oCBMI = new CheckboxMenuItem("Log", true);
		oCBMI.addItemListener(gItemListener);
		oMenu.add(oCBMI);
		oCBMI = new CheckboxMenuItem("Log Detallado", false);
		oCBMI.addItemListener(gItemListener);
		oMenu.add(oCBMI);
		oMB.add(oMenu);

		oMenu = new Menu("Ayuda");
		oMI = new MenuItem("About");
		oMI.addActionListener(gActionListener);
		oMenu.add(oMI);
		oMB.add(oMenu);
		oMB.setHelpMenu(oMenu);

		setMenuBar(oMB);
	}

	public void ChangeComponentsBackgroundColor(Color color) {
		this.setBackground(color);
		logList.setBackground(color);
		playersList.setBackground(color);
		matrixArea.setBackground(color);
		nRoundsLabel.setBackground(color);
		nPlayersLabel.setBackground(color);
		nGamesLabel.setBackground(color);
	}

	public void SetRounds(int rounds) {
		nRounds = rounds;
		nRoundsLabel.setText("NRounds = " + rounds);
	}

	public void RefreshLog(boolean active, LogLevel logLevel) {
		logList.removeAll();
		logActivated = active;
		activeLogLevel = logLevel;
		if (!logActivated) {
			return;
		}
		for (LogMessage lm : messageList) {
			if (logLevel == LogLevel.DETAILED) {
				logList.add(lm.message);
			} else {
				if (logLevel == lm.myLogLevel) {
					logList.add(lm.message);
				}
			}
		}
	}

	public void ChangeMatrixDimension(int dimension) {
		gameMatrix = new GameMatrix(dimension);
		matrixArea.setText(gameMatrix.ToString());
	}

	public void DeletePlayer() {
		int index = GetSelectedPlayer();
		if (index < 0) {
			// error
			return;
		}
		String message = "Player: " + playersInfoList.get(index).name + " removed";
		AddMessageToLog(message);
		playersInfoList.remove(index);
		playersList.remove(index);
		nPlayers--;
		nPlayersLabel.setText("NPlayers = " + nPlayers);
		RefreshPlayersList();
	}

	private int GetSelectedPlayer() {
		int index = playersList.getSelectedIndex();
		if (index <= 0) {
			JOptionPane.showMessageDialog(this, "Seleccione primero un jugador", "ERROR", JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		index--;
		return index;
	}

	public void RefreshPlayersList() {
		playersList.removeAll();
		playersList.add("Nombre Id G P Parcial Total");
		for (PlayerInfo pI : playersInfoList) {
			playersList.add(pI.ToString());
		}
	}

	public void RenameSelectedPlayer(String name) {
		int index = GetSelectedPlayer();
		String message = playersInfoList.get(index).name + " name changed to: " + name;
		AddMessageToLog(message);
		playersInfoList.get(index).name = name;
		RefreshPlayersList();
	}

	public void AddMessageToLog(String message) {
		LogMessage lm = new LogMessage(message, LogLevel.INFO);
		messageList.add(lm);
		RefreshLog(logActivated, activeLogLevel);
	}

	public void ResetPlayers() {
		ArrayList<PlayerInfo> temp = new ArrayList<PlayerInfo>();
		for (PlayerInfo pi : playersInfoList) {
			PlayerInfo piTemp = new PlayerInfo(pi.name, pi.id);
			temp.add(piTemp);
		}
		playersInfoList = temp;
		AddMessageToLog("Players have been reset");
		RefreshPlayersList();
	}

	public void ChangeTurnsMatrixChange(int turns) {
		this.matrixTurnsChange = turns;
	}

	public void ChangeDelay(int delay) {
		this.delay = delay;
	}
}
