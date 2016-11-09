package window;

import java.util.Random;

public class Vector2 {

	public int x;
	public int y;

	public Vector2() {
		Random random = new Random();
		x = random.nextInt(10);
		y = random.nextInt(10);
	}

	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String ToString() {
		String toRet = "";
		toRet = "(" + x + "," + y + ")";
		return toRet;
	}
}
