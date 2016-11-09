package window;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import window.LogMessage.LogLevel;

public class GameItemListener implements ItemListener {
	protected GameFrame gF;
	protected boolean logActivated = true;
	protected LogLevel logLevel = LogLevel.INFO;

	public GameItemListener() {

	}

	public GameItemListener(GameFrame gF) {
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
			logLevel = (selected) ? LogLevel.DETAILED : LogLevel.INFO;
			gF.RefreshLog(logActivated, logLevel);
			break;
		}
	}

}
