import java.util.Random;

public class RandomPlayer extends Player {

	@Override
	public void setup() {
		super.setup();
	}

	@Override
	protected int PlayGame() {
		Random random = new Random();
		return random.nextInt(this.matrixDimension);
	}
}
