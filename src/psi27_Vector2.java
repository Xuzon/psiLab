
import java.util.Random;

public class psi27_Vector2 {

	public int x;
	public int y;

	public psi27_Vector2() {
		Random random = new Random();
		x = random.nextInt(10);
		y = random.nextInt(10);
	}

	public psi27_Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public psi27_Vector2(int dimension) {
		Random random = new Random();
		x = random.nextInt(dimension);
		y = random.nextInt(dimension);
	}

	public String ToString() {
		String toRet = "";
		toRet = "(" + x + "," + y + ")";
		return toRet;
	}
}
