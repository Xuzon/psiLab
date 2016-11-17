package players;

import jade.core.Agent;

public class Player extends Agent {

	private static final long serialVersionUID = 1L;
	public int id;

	public Player() {
	}

	protected void setup() {
		System.out.println("Hello! I'm " + getAID().getName() + " my body is ready.");
	}
}
