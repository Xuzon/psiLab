
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class psi27_GameWindowListener implements WindowListener {
	psi27_GameFrame gF;
	protected static final Color unFocusedBackgroundColor = Color.BLACK;

	public psi27_GameWindowListener() {
	}

	public psi27_GameWindowListener(psi27_GameFrame gF) {
		this.gF = gF;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		gF.ChangeComponentsBackgroundColor(psi27_GameFrame.backgroundColor);
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		gF.dispose();
		System.exit(0);
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
