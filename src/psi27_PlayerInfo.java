
import jade.core.AID;

public class psi27_PlayerInfo {
	public AID aid;
	public String type = "Fijo";
	public String name;
	public int id;
	public int g;
	public int p;
	public int partial;
	public int total;

	public psi27_PlayerInfo() {
		name = "";
		id = g = p = partial = total = 0;
	}

	public psi27_PlayerInfo(String name, int id) {
		this.name = name;
		this.id = id;
		g = p = partial = total = 0;
	}

	public String ToString() {
		String toRet = "";
		toRet = this.type + "      " + this.name + "    " + id + "  " + g + "  " + p + "    " + partial + "        "
				+ total;
		return toRet;
	}

	public int CompareTo(psi27_PlayerInfo arg0) {
		int gamesWon = arg0.g - this.g;
		int gamesLost = this.p - arg0.p;
		int payOff = arg0.total - this.total;
		if (gamesWon != 0) {
			return gamesWon;
		}
		if (gamesLost != 0) {
			return gamesLost;
		}

		if (payOff != 0) {
			return payOff;
		}
		return 0;
	}
}
