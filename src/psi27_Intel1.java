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
		List<psi27_Vector2> paretoOptimal = GetParetoOptimal();
		List<Integer> dominant = GetDominant();
		List<psi27_Vector2> nashEquilibriums = GetNashEquilibrium();
		toRet = Choice(paretoOptimal, dominant, nashEquilibriums);
		return toRet;
	}

	protected int Choice(List<psi27_Vector2> pareto, List<Integer> dominant, List<psi27_Vector2> nash) {
		int toRet = 0;

		int bestDominant = GetBetterDominant(dominant);
		if (bestDominant != -1) {
			return bestDominant;
		}
		// GET MAX MIN
		List<Integer> maxMin = GetMaxMin();

		toRet = GetStrategyBasedOnPast(pareto, nash, maxMin);

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
				toRet = coord;
				maxEnemyGain = currMaxEnemyGain;
			}
		}
		return toRet;
	}

	protected int GetStrategyBasedOnPast(List<psi27_Vector2> pareto, List<psi27_Vector2> nash, List<Integer> maxMin) {
		int toRet = 0;
		if (nash.size() == 0 && pareto.size() == 0) {
			System.out.println("Pareto optimal and nash are empty using max min");
			return maxMin.get(new Random().nextInt(maxMin.size()));
		}

		// TODO
		// If I'm winning I want to minimize my loses but if I'm losing I want
		// to maximize my profits trying to reach the other player
		if (winning) {
			toRet = maxMin.get(new Random().nextInt(maxMin.size()));
		} else {
			toRet = GetBetterAverageMovement();
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
					psi27_Vector2 temp = matrix.GetPosition(k, j);
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
					psi27_Vector2 temp = matrix.GetPosition(i, k);
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

	protected List<psi27_Vector2> GetParetoOptimal() {
		List<psi27_Vector2> toRet = new ArrayList<psi27_Vector2>();
		int xMax = 0;
		int yMax = 0;
		for (int i = 0; i < matrixDimension; i++) {
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = matrix.GetPosition(i, j);
				psi27_Vector2 add = new psi27_Vector2(i, j);
				// TODO CHECK
				if (vec.x == xMax && vec.y == yMax) {
					toRet.add(add);
				}
				if (vec.x > xMax || vec.y > yMax) {

				}
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
