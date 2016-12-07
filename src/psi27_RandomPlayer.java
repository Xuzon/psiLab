import java.util.Random;

public class psi27_RandomPlayer extends psi27_Player {

	@Override
	public void setup() {
		super.setup();
	}

	@Override
	protected int PlayGame() {
		Random random = new Random();
		return random.nextInt(this.matrixDimension);
	}

	@Override
	protected void ChangedMatrix(int percentage) {
		// This players doesn't react to this so...
	}

	@Override
	protected void Results(String message) {
		// This players doesn't react to this so...
	}
}
