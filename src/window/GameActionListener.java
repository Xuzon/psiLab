package window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class GameActionListener implements ActionListener {

	protected GameFrame gF;

	public GameActionListener() {
	}

	public GameActionListener(GameFrame gF) {
		this.gF = gF;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		System.out.println(command);
		switch (command) {
		case "About":
			String aboutMessage = "Game 1.0 \n Carlos Daniel Garrido Suárez";
			JOptionPane.showMessageDialog(gF, aboutMessage, "About", JOptionPane.INFORMATION_MESSAGE);
			break;
		case "Num. Partidas":
			ChangeRounds();
			break;
		case "Cambia Matriz cada...":
			ChangeTurns();
			break;
		case "Cambia Retardo":
			ChangeDelay();
			break;
		case "Num. Filas/Columnas":
			ChangeMatrixDimension();
			break;
		case "Borra Jugador":
			gF.DeletePlayer();
			break;
		case "Renombra Jugador":
			String newName = JOptionPane.showInputDialog(gF, "Select new name", "Player");
			gF.RenameSelectedPlayer(newName);
			break;
		case "Reset Jugs":
			gF.ResetPlayers();
			break;
		case "Nueva Partida":
			NewGame();
			break;
		case "Salir":
			System.exit(0);
			break;
		case "New Game":
			NewGame();
			break;
		case "Stop":
			PauseGame();
			break;
		case "Resume":
			ResumeGame();
			break;
		}

	}

	private void NewGame() {
		String message = "Acomódate, una nueva partida va a emepezar";
		JOptionPane.showMessageDialog(gF, message, "New game", JOptionPane.INFORMATION_MESSAGE);
		gF.AddMessageToLog(message);
	}

	private void ChangeRounds() {
		String question = "Introduzca número de partidas";
		String numPartidas = JOptionPane.showInputDialog(gF, question, "10");
		try {
			int iNumPartidas = Integer.parseInt(numPartidas);
			if (iNumPartidas < 1 || iNumPartidas > 1000) {
				JOptionPane.showMessageDialog(gF, "Introduzca un número entre 1 y 1000", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(gF, "Num partidas cambiadas a: " + iNumPartidas, "About",
						JOptionPane.INFORMATION_MESSAGE);
				gF.SetRounds(iNumPartidas);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(gF, "Introduzca un número", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void ChangeDelay() {
		String question = "Introduzca delay";
		String delay = JOptionPane.showInputDialog(gF, question, "10");
		try {
			int iDelay = Integer.parseInt(delay);
			if (iDelay < 0) {
				JOptionPane.showMessageDialog(gF, "Introduzca un número positivo", "ERROR", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(gF, "Delay cambiado a: " + iDelay, "About",
						JOptionPane.INFORMATION_MESSAGE);
				gF.ChangeDelay(iDelay);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(gF, "Introduzca un número", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void ChangeTurns() {
		String question = "Introduzca número de turnos para que cambie la matriz";
		String turns = JOptionPane.showInputDialog(gF, question, "10");
		try {
			int iTurns = Integer.parseInt(turns);
			if (iTurns < 0) {
				JOptionPane.showMessageDialog(gF, "Introduzca un número positivo", "ERROR", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(gF, "Turnos cambiados a: " + iTurns, "About",
						JOptionPane.INFORMATION_MESSAGE);
				gF.ChangeTurnsMatrixChange(iTurns);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(gF, "Introduzca un número", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void ChangeMatrixDimension() {
		String question = "Introduzca dimensión de la matriz";
		String dimension = JOptionPane.showInputDialog(gF, question, "10");
		try {
			int iDimension = Integer.parseInt(dimension);
			if (iDimension < 1 || iDimension > 200) {
				JOptionPane.showMessageDialog(gF, "Introduzca un número entre 1 y 200", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			} else {
				gF.ChangeMatrixDimension(iDimension);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(gF, "Introduzca un número", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void PauseGame() {
		String message = "GAME PAUSED";
		gF.AddMessageToLog(message);
	}

	private void ResumeGame() {
		String message = "GAME RESUMED";
		gF.AddMessageToLog(message);
	}

}
