package window;

public class LogMessage {
	public enum LogLevel {
		INFO, DETAILED
	};

	public LogLevel myLogLevel;
	public String message;

	public LogMessage(String message, LogLevel logLevel) {
		this.message = message;
		myLogLevel = logLevel;
	}
}
