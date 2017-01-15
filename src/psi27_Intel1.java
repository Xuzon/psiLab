import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class psi27_Intel1 extends psi27_Player {

	protected psi27_GameMatrix matrix;
	protected boolean rowTurn = true;
	// *************************************
	// **************FACTORS****************
	protected int enemyPlayingSameMove = 4;
	// percentage that triggers my want to know movement (0-1)
	protected float wantToKnowMovement = .7f;
	// ******************************************
	protected boolean winning = false;
	protected int myPayoff = 0;
	protected int enemyPayoff = 0;
	protected List<psi27_Vector2> positionsLog = new ArrayList<psi27_Vector2>();

	@Override
	public void setup() {
		type = "Intel1";
		super.setup();
	}

	@Override
	protected void NewMatch() {
		matrix = new psi27_GameMatrix(matrixDimension);
		matrix.SetUnknown();
		rowTurn = (id < enemyId) ? true : false;
		winning = false;
		myPayoff = 0;
		enemyPayoff = 0;
		positionsLog.clear();
	}

	@Override
	protected int PlayGame() {
		int toRet = 0;
		toRet = DiscoverMatrix();
		// We want to discover a new position
		if (toRet != -1) {
			return toRet;
		}
		toRet = Strategy();
		return toRet;
	}

	protected int turnsTillDiscovered = 0;
	protected boolean info = false;

	protected int DiscoverMatrix() {
		int toRet = -1;
		int[][] unknownPerMovement = new int[matrixDimension][1];
		for (int i = 0; i < matrixDimension; i++) {
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(i, j) : matrix.GetPosition(j, i);
				if (vec.x == -1) {
					unknownPerMovement[i][0]++;
				}
			}
		}

		List<Integer> knowCandidates = new ArrayList<Integer>();
		for (int i = 0; i < matrixDimension; i++) {
			float percentage = (float) unknownPerMovement[i][0] / (float) matrixDimension;
			if (percentage > (1 - wantToKnowMovement)) {
				knowCandidates.add(new Integer(i));
			}

		}

		// TODO not randomly
		if (knowCandidates.size() > 0) {
			toRet = knowCandidates.get(new Random().nextInt(knowCandidates.size()));
			float percentage = (float) unknownPerMovement[toRet][0] / (float) matrixDimension;
			turnsTillDiscovered++;
			info = false;
		} else {
			if (!info) {
				// System.out.println("Already know matrix in " +
				// turnsTillDiscovered);
				info = true;
				turnsTillDiscovered = 0;
			}
		}
		return toRet;
	}

	protected int Strategy() {
		int toRet = 0;
		List<Integer> dominant = GetDominant();
		List<psi27_Vector2> nashEquilibriums = GetNashEquilibrium();
		toRet = Choice(dominant, nashEquilibriums);
		return toRet;
	}

	protected int Choice(List<Integer> dominant, List<psi27_Vector2> nash) {
		int toRet = 0;

		int bestDominant = GetBetterDominant(dominant);
		if (bestDominant != -1) {
			return bestDominant;
		}
		// GET MAX MIN
		List<Integer> maxMin = GetMaxMin();

		if (nash.size() == 0) {
			// System.out.println("Nash is empty using max
			// min");
			return maxMin.get(new Random().nextInt(maxMin.size()));
		}

		// If I'm winning I want to minimize my loses but if I'm losing I want
		// to maximize my profits trying to reach the other player
		if (winning) {
			toRet = maxMin.get(new Random().nextInt(maxMin.size()));
		} else {
			toRet = GetBetterAverageMovement();
		}

		toRet = GetStrategyBasedOnPast(toRet, maxMin);

		return toRet;
	}

	protected int GetBetterDominant(List<Integer> dominant) {
		int toRet = -1;
		// If I have some dominant strategies choose the one that harms the most
		// to the enemy (minimize his max payoff)
		int maxEnemyGain = 10;
		for (int strategy = 0; strategy < dominant.size(); strategy++) {
			int coord = dominant.get(strategy);
			int currMaxEnemyGain = 0;
			for (int i = 0; i < matrixDimension; i++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(i, coord) : matrix.GetPosition(coord, i);
				int pay = rowTurn ? vec.y : vec.x;
				if (pay > currMaxEnemyGain) {
					currMaxEnemyGain = pay;
				}
			}
			if (currMaxEnemyGain < maxEnemyGain) {
				System.out.println("Choosing dominant strategy");
				toRet = coord;
				maxEnemyGain = currMaxEnemyGain;
			}
		}
		return toRet;
	}

	protected int GetStrategyBasedOnPast(int pos, List<Integer> maxMin) {
		int toRet = pos;

		if (positionsLog.size() >= enemyPlayingSameMove) {
			psi27_Vector2 vec = positionsLog.get(positionsLog.size() - 1);
			int lastEnemyPos = rowTurn ? vec.y : vec.x;
			for (int i = 1; i < enemyPlayingSameMove; i++) {
				vec = positionsLog.get(positionsLog.size() - 1 - i);
				int enemyPos = rowTurn ? vec.y : vec.x;
				if (enemyPos != lastEnemyPos) {
					break;
				}
				if (i == enemyPlayingSameMove - 1) {
					System.out.println("Enemy is playing same move for: " + enemyPlayingSameMove + " turns");
					vec = rowTurn ? matrix.GetPosition(toRet, enemyPos) : matrix.GetPosition(enemyPos, toRet);
					int myPay = rowTurn ? vec.x : vec.y;
					int enPay = rowTurn ? vec.y : vec.x;
					if (myPay <= enPay) {
						// If I have less payoff don't choose that movement
						System.out.println("choosing another strategy");
						int temp = ChooseAnotherFromlist(maxMin, toRet);
						// If I have the same value get by better average
						// movement
						if (toRet == temp) {
							toRet = GetBetterAverageMovement();
						}
					}
				}
			}
		}
		return toRet;
	}

	private int ChooseAnotherFromlist(List<Integer> list, int value) {
		int toRet = value;
		if (list.size() > 1) {
			for (int j = 0; j < list.size(); j++) {
				int temp = list.get(j);
				if (temp != value) {
					toRet = temp;
					break;
				}
			}
		}
		return toRet;
	}

	protected List<psi27_Vector2> GetNashEquilibrium() {
		List<psi27_Vector2> toRet = new ArrayList<psi27_Vector2>();
		for (int i = 0; i < matrixDimension; i++) {
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = matrix.GetPosition(i, j);
				boolean maxOfRow = false;
				boolean maxOfColumn = false;
				for (int k = 0; k < matrixDimension; k++) {
					if (k == i) {
						continue;
					}
					psi27_Vector2 temp = matrix.GetPosition(i, k);
					if (temp.y > vec.y) {
						break;
					}
					if (k == (matrixDimension - 1)) {
						maxOfRow = true;
					}
				}

				for (int k = 0; k < matrixDimension; k++) {
					if (k == j) {
						continue;
					}
					psi27_Vector2 temp = matrix.GetPosition(k, j);
					if (temp.x > vec.x) {
						break;
					}
					if (k == (matrixDimension - 1)) {
						maxOfColumn = true;
					}
				}
				if (maxOfRow && maxOfColumn) {
					toRet.add(new psi27_Vector2(i, j));
				}
			}
		}
		return toRet;
	}

	/*
	 * Get a list of possible MaxMin strategies, it gives you in int format
	 * (movement)
	 */
	protected List<Integer> GetMaxMin() {
		List<Integer> toRet = new ArrayList<Integer>();
		int worstPayOff = 0;
		for (int i = 0; i < matrixDimension; i++) {
			int iterationWorstPayoff = 10;
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(i, j) : matrix.GetPosition(j, i);
				int pay = rowTurn ? vec.x : vec.y;
				if (pay != -1 && pay < iterationWorstPayoff) {
					iterationWorstPayoff = pay;
				}
			}
			if (worstPayOff == iterationWorstPayoff) {
				toRet.add(new Integer(i));
			}
			if (worstPayOff < iterationWorstPayoff) {
				toRet.clear();
				worstPayOff = iterationWorstPayoff;
				toRet.add(new Integer(i));
			}
		}

		return toRet;
	}

	protected int GetBetterAverageMovement() {
		int toRet = 0;
		float maxAverage = 0;
		for (int i = 0; i < matrixDimension; i++) {
			float iteration = 0;
			int counted = 0;
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(i, j) : matrix.GetPosition(j, i);
				int pay = rowTurn ? vec.x : vec.y;
				if (pay != -1) {
					iteration += pay;
					counted++;
				}
			}
			iteration /= (float) counted;
			if (iteration > maxAverage) {
				maxAverage = iteration;
				toRet = i;
			}
		}

		return toRet;
	}

	protected List<Integer> GetDominant() {
		List<Integer> toRet = new ArrayList<Integer>();

		int[][] payoffs = new int[matrixDimension][matrixDimension];
		// get payoff vectors
		for (int i = 0; i < matrixDimension; i++) {
			int[] currPayoffs = new int[matrixDimension];
			// get payoffs of row / column
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(i, j) : matrix.GetPosition(j, i);
				int pay = rowTurn ? vec.x : vec.y;
				currPayoffs[j] = pay;
			}
			payoffs[i] = currPayoffs.clone();
		}

		for (int i = 0; i < matrixDimension; i++) {
			boolean notDominant = false;
			for (int j = 0; i < matrixDimension; j++) {
				if (i == j) {
					continue;
				}
				boolean hasToBreak = false;
				for (int k = 0; k < matrixDimension; k++) {
					// TODO sometimes it goes til 5, is impossible
					if (payoffs[i][k] < payoffs[j][k]) {
						hasToBreak = true;
						notDominant = true;
						break;
					}
				}
				if (hasToBreak) {
					break;
				}
			}
			if (!notDominant) {
				toRet.add(new Integer(i));
			}
		}
		return toRet;
	}

	@Override
	protected void ChangedMatrix(int percentage) {
		matrix.SetUnknown();
		// TODO try to do it better
	}

	@Override
	protected void Results(String positions, String payoffs) {
		String[] temp;
		temp = positions.split(",");
		int x = Integer.parseInt(temp[0]);
		int y = Integer.parseInt(temp[1]);
		temp = payoffs.split(",");
		int xPayoff = Integer.parseInt(temp[0]);
		int yPayoff = Integer.parseInt(temp[1]);
		myPayoff += rowTurn ? xPayoff : yPayoff;
		enemyPayoff += rowTurn ? yPayoff : xPayoff;
		psi27_Vector2 position = new psi27_Vector2(x, y);
		positionsLog.add(position);
		winning = myPayoff > enemyPayoff ? true : false;
		matrix.ChangePosition(x, y, new psi27_Vector2(xPayoff, yPayoff));
	}

}
