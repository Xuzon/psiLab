import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class psi27_Referee extends Agent {
	private static final long serialVersionUID = 1L;
	protected psi27_GameFrame gameFrame;
	protected static int ID = 0;
	public int currentPlayerId;
	public int currentSecondPlayerId;
	protected boolean gameRunning = false;
	private boolean isPaused = false;

	protected void setup() {
		System.out.println("Hello! I'm " + getAID().getName() + " my body is ready.");
		this.addBehaviour(new Finder());
		gameFrame = new psi27_GameFrame(1280, 720, "GameFrame", this);
	}

	// Send the new game message to all the players
	public void SendNewGameMessage() {
		String defaultString = "#";
		defaultString += gameFrame.nPlayers + "," + gameFrame.gameMatrix.dimension + "," + gameFrame.nRounds + ","
				+ gameFrame.matrixTurnsChange + "," + (int) gameFrame.matrixChangePercentage;
		for (psi27_PlayerInfo info : gameFrame.playersInfoList) {
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.addReceiver(info.aid);
			message.setSender(getAID());
			message.setContent("Id#" + info.id + defaultString);
			send(message);
		}
	}

	// Send a message with the given text to the current players
	public void SendMessageToCurrentPlayers(String text) {
		for (psi27_PlayerInfo info : gameFrame.playersInfoList) {
			if (info.id == currentPlayerId || info.id == currentSecondPlayerId) {
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.addReceiver(info.aid);
				message.setSender(getAID());
				message.setContent(text);
				send(message);
			}
		}
	}

	// Send the Start Match message
	public void StartMatchMessage() {
		String defaultString = "NewGame#" + currentPlayerId + "," + currentSecondPlayerId;
		SendMessageToCurrentPlayers(defaultString);
	}

	// Send the EndMatchMessage
	public void EndMatchMessage() {
		String defaultString = "EndGame";
		SendMessageToCurrentPlayers(defaultString);
	}

	// Returns an int of the election of the first player
	public int SendChooseFirst() {
		return SendChooseMessagePlayer(currentPlayerId);
	}

	// Returns an int of the election of the second player
	public int SendChooseSecond() {
		return SendChooseMessagePlayer(currentSecondPlayerId);
	}

	// Send the Position message to the given player and return his choice
	public int SendChooseMessagePlayer(int id) {
		int toRet = 0;
		String defaultString = "Position";
		psi27_PlayerInfo info = GetInfoWithId(id);
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.addReceiver(info.aid);
		message.setSender(getAID());
		message.setContent(defaultString);
		send(message);
		while (message == null || !message.getContent().contains("Position#")) {
			message = this.receive();
		}
		String[] temp = message.getContent().split("#");
		toRet = Integer.parseInt(temp[1]);
		return toRet;
	}

	// Returns a psi27_PlayerInfo giving it and ID
	public psi27_PlayerInfo GetInfoWithId(int id) {
		psi27_PlayerInfo toRet = null;
		for (psi27_PlayerInfo info : gameFrame.playersInfoList) {
			if (id == info.id) {
				return info;
			}
		}
		return toRet;
	}

	// Returns a psi27_PlayerInfo giving it and ID
	public psi27_PlayerInfo GetInfoWithAID(AID aid) {
		psi27_PlayerInfo toRet = null;
		for (psi27_PlayerInfo info : gameFrame.playersInfoList) {
			if (aid == info.aid) {
				return info;
			}
		}
		return toRet;
	}

	public void Game() {
		gameRunning = true;
		gameFrame.ResetPlayers();
		this.SendNewGameMessage();
		for (int i = 0; i < gameFrame.playersInfoList.size(); i++) {
			for (int j = i + 1; j < gameFrame.playersInfoList.size(); j++) {
				psi27_PlayerInfo firstPlayer = gameFrame.playersInfoList.get(i);
				currentPlayerId = firstPlayer.id;
				psi27_PlayerInfo secondPlayer = gameFrame.playersInfoList.get(j);
				currentSecondPlayerId = secondPlayer.id;
				StartMatchMessage();
				GameLoop();
			}
		}
		GetTournamentWinner();
		gameRunning = false;
	}

	/// In this method I use a lot of java.awt.EventQueue.invokeAndWait() to get
	/// the GUI responsive
	/// invokeAndWait waits for the java EDT thread to end to then run the
	/// Runnables that I sent to the queue
	/// so it waits for the GUI thread to end (make buttons responsive)
	public void GameLoop() {
		int wonRoundsFirst = 0;
		int wonRoundsSecond = 0;
		int totalPayoffFirst = 0;
		int totalPayoffSecond = 0;
		String log = "";
		for (int k = 0; k < gameFrame.nRounds; k++) {
			// Paused active waiting
			while (isPaused) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// movement
			int row = SendChooseFirst();
			int column = SendChooseSecond();
			psi27_Vector2 vec = gameFrame.gameMatrix.matrix[row][column];
			// points
			int payOffFirst = vec.x;
			totalPayoffFirst += payOffFirst;
			int payOffSecond = vec.y;
			totalPayoffSecond += payOffSecond;
			// update log
			psi27_PlayerInfo firstPlayer = GetInfoWithId(currentPlayerId);
			psi27_PlayerInfo secondPlayer = GetInfoWithId(currentSecondPlayerId);
			log = "Player " + firstPlayer.name + " choose: " + row + " Player " + secondPlayer.name + " choose: "
					+ column;
			log += vec.ToString();
			// update list info
			firstPlayer.partial = totalPayoffFirst;
			firstPlayer.total += vec.x;
			secondPlayer.partial = totalPayoffSecond;
			secondPlayer.total += vec.y;
			// send results
			String temp = "Results#" + row + "," + column + "#" + vec.x + "," + vec.y;
			SendMessageToCurrentPlayers(temp);

			// Decide winner of round
			if (payOffFirst > payOffSecond) {
				wonRoundsFirst++;
			}

			if (payOffSecond > payOffFirst) {
				wonRoundsSecond++;
			}

			// Change the game matrix
			if (gameFrame.matrixChangePercentage > 0 && k > gameFrame.matrixTurnsChange) {
				float percent = gameFrame.matrixChangePercentage;
				gameFrame.gameMatrix.ChangeMatrix(percent);
				try {
					java.awt.EventQueue.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							gameFrame.matrixArea.setText(gameFrame.gameMatrix.ToString());
						}
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SendMessageToCurrentPlayers("Changed#" + (int) percent);
			}

			// Refresh GUI
			try {
				java.awt.EventQueue.invokeAndWait(new MessageToLogThread(log, psi27_LogMessage.LogLevel.DETAILED));
				java.awt.EventQueue.invokeAndWait(new RefreshMatchThread(wonRoundsFirst, wonRoundsSecond,
						totalPayoffFirst, totalPayoffSecond, k + 1));
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		// End of match
		log = "END MATCH ";
		// Decide winner of match
		if (totalPayoffFirst > totalPayoffSecond) {
			log += "Player " + GetInfoWithId(currentPlayerId).name + " won";
			GetInfoWithId(currentPlayerId).g++;
			GetInfoWithId(currentSecondPlayerId).p++;
		}

		if (totalPayoffSecond > totalPayoffFirst) {
			log += "Player " + GetInfoWithId(currentSecondPlayerId).name + " won";
			GetInfoWithId(currentPlayerId).p++;
			GetInfoWithId(currentSecondPlayerId).g++;
		}
		if (totalPayoffFirst == totalPayoffSecond) {
			log += "DRAW!!!!!!!!!!!!";
		}
		// Refresh GUI
		try {
			java.awt.EventQueue.invokeAndWait(new MessageToLogThread(log, null));
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					gameFrame.RefreshPlayersList();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		EndMatchMessage();
	}

	// Shows a message dialog of the winner
	public void GetTournamentWinner() {
		ArrayList<psi27_PlayerInfo> temp = (ArrayList<psi27_PlayerInfo>) gameFrame.playersInfoList.clone();
		temp.sort((p1, p2) -> p1.CompareTo(p2));
		gameFrame.nGames++;
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "THE WINNER IS " + temp.get(0).name);
				gameFrame.nGamesLabel.setText("nGames: " + gameFrame.nGames);
			}
		});

	}

	public void Pause() {
		isPaused = true;
	}

	public void Resume() {
		isPaused = false;
	}

	// Search for players and add them to the list
	private class Finder extends CyclicBehaviour {

		private static final int TIME_SLEEP_BEHAVIOUR = 2000;

		@Override
		public void action() {
			SearchPlayers();
		}

		private void SearchPlayers() {
			if (gameRunning) {
				return;
			}
			// Search for service with type player
			ServiceDescription service = new ServiceDescription();
			service.setType("Player");

			DFAgentDescription description = new DFAgentDescription();
			description.addLanguages("English");

			description.addServices(service);
			try {
				DFAgentDescription[] results = DFService.search(this.myAgent, description);

				if (results.length == 0) {
					System.out.println("NO PLAYERS AT THE MOMENT");
				}

				// Iterate over the list
				for (int i = 0; i < results.length; ++i) {
					AID aid = results[i].getName();
					if (this.myAgent instanceof psi27_Referee) {
						psi27_Referee referee = (psi27_Referee) this.myAgent;
						boolean found = false;
						// make sure if the list contains it or not
						for (psi27_PlayerInfo listAID : referee.gameFrame.playersInfoList) {
							if (listAID.aid.equals(aid)) {
								found = true;
							}
						}
						if (!found) {
							// If i didn't add him before
							String type = "";
							// in my case, I use another service to describe the
							// type of the player
							Iterator<ServiceDescription> it = results[i].getAllServices();
							while (it.hasNext()) {
								ServiceDescription agentDescription = it.next();
								type = agentDescription.getType();
								if (!type.equals("Player")) {
									break;
								}
							}
							if (type.equals("")) {
								type = "Unknown";
							}
							psi27_PlayerInfo newPlayer = new psi27_PlayerInfo(aid.getLocalName(), ID);
							newPlayer.type = type;
							ID++;
							newPlayer.aid = aid;
							referee.gameFrame.playersInfoList.add(newPlayer);
							referee.gameFrame.nPlayers++;
							referee.gameFrame.nPlayersLabel.setText("NPlayers: " + referee.gameFrame.nPlayers);
							referee.gameFrame.AddMessageToLog("PLAYER ADDED::" + aid.getName(), null);
							referee.gameFrame.RefreshPlayersList();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.block(TIME_SLEEP_BEHAVIOUR);
		}
	}

	// Runnable class to add a message to the log
	private class MessageToLogThread implements Runnable {
		String text;
		psi27_LogMessage.LogLevel logLevel;

		public MessageToLogThread(String text, psi27_LogMessage.LogLevel logLevel) {
			this.text = text;
			this.logLevel = logLevel;
		}

		@Override
		public void run() {
			gameFrame.AddMessageToLog(text, logLevel);
		}
	}

	// Runnable class to refresh the current match GUI
	private class RefreshMatchThread implements Runnable {
		int wonRoundsFirst;
		int wonRoundsSecond;
		int totalPayoffFirst;
		int totalPayoffSecond;
		int totalRounds;

		public RefreshMatchThread(int wonRoundsFirst, int wonRoundsSecond, int totalPayoffFirst, int totalPayoffSecond,
				int totalRounds) {
			this.wonRoundsFirst = wonRoundsFirst;
			this.wonRoundsSecond = wonRoundsSecond;
			this.totalPayoffFirst = totalPayoffFirst;
			this.totalPayoffSecond = totalPayoffSecond;
			this.totalRounds = totalRounds;
		}

		@Override
		public void run() {
			gameFrame.RefreshMatchGUI(wonRoundsFirst, wonRoundsSecond, totalPayoffFirst, totalPayoffSecond,
					totalRounds);
		}

	}
}
