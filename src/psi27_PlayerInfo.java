
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
}
