package window;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GameWindowListener implements WindowListener {
	GameFrame gF;
	protected static final Color unFocusedBackgroundColor = Color.BLACK;

	public GameWindowListener() {
	}

	public GameWindowListener(GameFrame gF) {
		this.gF = gF;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		gF.ChangeComponentsBackgroundColor(GameFrame.backgroundColor);
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		gF.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		gF.ChangeComponentsBackgroundColor(unFocusedBackgroundColor);
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
