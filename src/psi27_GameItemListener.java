
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class psi27_GameItemListener implements ItemListener {
	protected psi27_GameFrame gF;
	protected boolean logActivated = true;
	protected psi27_LogMessage.LogLevel logLevel = psi27_LogMessage.LogLevel.INFO;

	public psi27_GameItemListener() {

	}

	public psi27_GameItemListener(psi27_GameFrame gF) {
		this.gF = gF;
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		boolean selected = arg0.getStateChange() == 1 ? true : false;
		String item = arg0.getItem().toString();
		switch (item) {
		case "Log":
			logActivated = selected;
			gF.RefreshLog(logActivated, logLevel);
			break;
		case "Log Detallado":
			logLevel = (selected) ? psi27_LogMessage.LogLevel.DETAILED : psi27_LogMessage.LogLevel.INFO;
			gF.RefreshLog(logActivated, logLevel);
			break;
		}
	}

}
