public class psi27_Fixed extends psi27_Player {

	@Override
	public void setup() {
		type = "Fixed";
		super.setup();
	}

	@Override
	protected int PlayGame() {
		return 0;
	}

	@Override
	protected void ChangedMatrix(int percentage) {
		// This players doesn't react to this so...
	}

	@Override
	protected void Results(String position, String payoff) {
		// This players doesn't react to this so...
	}

	@Override
	protected void NewGame() {

	}

}
