import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public abstract class psi27_Player extends Agent {

	private static final long serialVersionUID = 1L;
	public int id;
	protected String type = "";
	protected int enemyId;
	protected int totalPlayers = 0;
	protected int matrixDimension = 0;
	protected int nRounds = 0;
	protected int matrixTurnsChange = 0;
	protected float matrixChangePercentage = 0;
	protected int currentRoundPayoff = 0;
	protected int totalPayoff = 0;

	public psi27_Player() {
	}

	protected void setup() {
		System.out.println("Hello! I'm " + getAID().getName() + " my body is ready.");
		this.addBehaviour(new MessageListener());
		RegisterDf();
	}

	protected void RegisterDf() {
		DFAgentDescription agentDescription = new DFAgentDescription();
		agentDescription.setName(getAID());
		agentDescription.addLanguages("English");

		ServiceDescription service = new ServiceDescription();
		service.setType("Player");
		service.setName(this.getAID().getName());

		ServiceDescription myTypeService = new ServiceDescription();
		myTypeService.setType(type);
		myTypeService.setName(this.getAID().getName());

		agentDescription.addServices(service);
		agentDescription.addServices(myTypeService);

		try {
			DFService.register(this, agentDescription);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	protected abstract int PlayGame();

	protected abstract void ChangedMatrix(int percentage);

	protected abstract void Results(String message);

	// Normal behaviour (get the new match and new game message)
	protected class MessageListener extends CyclicBehaviour {
		public void action() {
			ProcessMessage();
		}

		protected void ProcessMessage() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				String text = msg.getContent();
				String[] splittedMessage = text.split("#");
				switch (splittedMessage[0]) {
				case "Id":
					NewGame(splittedMessage);
					break;
				case "NewGame":
					NewMatch(splittedMessage);
					break;
				default:
					break;
				}
			}
		}

		protected void NewGame(String[] splittedMessage) {
			psi27_Player player = (psi27_Player) myAgent;
			player.id = Integer.parseInt(splittedMessage[1]);
			String[] temp = splittedMessage[2].split(",");
			totalPlayers = Integer.parseInt(temp[0]);
			matrixDimension = Integer.parseInt(temp[1]);
			nRounds = Integer.parseInt(temp[2]);
			matrixTurnsChange = Integer.parseInt(temp[3]);
			matrixChangePercentage = Integer.parseInt(temp[4]);
		}

		protected void NewMatch(String[] splittedMessage) {
			psi27_Player player = (psi27_Player) myAgent;
			String[] temp = splittedMessage[1].split(",");
			int firstId = Integer.parseInt(temp[0]);
			int secondId = Integer.parseInt(temp[1]);
			player.enemyId = (firstId == player.id) ? secondId : firstId;
			player.addBehaviour(new PlayBehaviour());
			player.removeBehaviour(this);
		}
	}

	// Behaviour that is active only when playing a match
	protected class PlayBehaviour extends SimpleBehaviour {

		@Override
		public void action() {
			psi27_Player player = (psi27_Player) myAgent;
			while (true) {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					String text = msg.getContent();
					String[] splittedMessage = text.split("#");
					switch (splittedMessage[0]) {
					case "EndGame":
						return;
					case "Position":
						int pos = player.PlayGame();
						ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
						reply.setContent("Position#" + pos);
						reply.setSender(getAID());
						reply.addReceiver(msg.getSender());
						send(reply);
						break;
					case "Changed":
						int changed = Integer.parseInt(splittedMessage[1]);
						player.ChangedMatrix(changed);
						break;
					case "Results":
						player.Results(splittedMessage[1]);
						break;
					default:
						break;
					}
				}
			}
		}

		@Override
		public boolean done() {
			myAgent.addBehaviour(new MessageListener());
			myAgent.removeBehaviour(this);
			return false;
		}
	}
}
