package window;

import java.util.Random;

public class PlayerInfo {
	public String type = "Fijo";
	public String name;
	public int id;
	public int g;
	public int p;
	public int partial;
	public int total;

	public PlayerInfo() {
		name = "";
		Random random = new Random();
		id = g = p = partial = total = random.nextInt(100);
	}

	public PlayerInfo(String name, int id) {
		this.name = name;
		this.id = id;
		Random random = new Random();
		g = p = partial = total = random.nextInt(100);
	}

	public String ToString() {
		String toRet = "";
		toRet = this.type + "      " + this.name + "    " + id + "  " + g + "  " + p + "    " + partial + "        "
				+ total;
		return toRet;
	}
}
