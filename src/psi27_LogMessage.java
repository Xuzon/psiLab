

public class psi27_LogMessage {
	public enum LogLevel {
		INFO, DETAILED
	};

	public LogLevel myLogLevel;
	public String message;

	public psi27_LogMessage(String message, LogLevel logLevel) {
		this.message = message;
		myLogLevel = logLevel;
	}
}
