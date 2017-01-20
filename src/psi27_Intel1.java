import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class psi27_Intel1 extends psi27_Player {

	protected psi27_GameMatrix matrix;
	protected boolean rowTurn = true;
	// *************************************
	// **************FACTORS****************
	protected int enemyPlayingSameMove = 4;
	protected int INITIAL_SAME_MOVE = 4;
	protected float percentageSetUnknown = 50;
	protected boolean checkEnemyPlayingSameMove = false;
	// percentage that triggers my want to know movement (0-1)
	protected float wantToKnowMovement = 1f;
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
		// RESET VARIABLES
		matrix = new psi27_GameMatrix(matrixDimension);
		matrix.SetUnknown();
		rowTurn = (id < enemyId) ? true : false;
		winning = false;
		myPayoff = 0;
		enemyPayoff = 0;
		positionsLog.clear();
		enemyPlayingSameMove = INITIAL_SAME_MOVE;
	}

	@Override
	protected int PlayGame() {
		// ACTUAL PLAY
		int toRet = 0;
		// Decide if we wan't to discover a new position
		toRet = DiscoverMatrix();
		// We want don't want to discover a new position choose a strategy
		if (toRet == -1) {
			toRet = Strategy();
		}
		return toRet;
	}

	protected int turnsTillDiscovered = 0;
	protected boolean info = false;

	/*
	 * DISCOVER THE MATRIX OR CHECK DOMINANT
	 */
	protected int DiscoverMatrix() {
		int toRet = -1;

		List<Integer> dominant = GetDominant();
		if (dominant.size() > 0) {
			// If now I have a movement where always win or draw doesn't keep
			// discovering matrix
			return GetBetterDominant(dominant);
		}
		// GET UNKNOWN PERCENTAGE OF EVERY MOVEMENT
		int[][] unknownPerMovement = new int[matrixDimension][1];
		for (int i = 0; i < matrixDimension; i++) {
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(i, j) : matrix.GetPosition(j, i);
				if (vec.x == -1) {
					unknownPerMovement[i][0]++;
				}
			}
		}
		// PONDERATE THE PERCENTAGES TO DECIDE IF I'M INTERESTED INTO OR NOT
		List<Integer> knowCandidates = new ArrayList<Integer>();
		for (int i = 0; i < matrixDimension; i++) {
			float percentage = (float) unknownPerMovement[i][0] / (float) matrixDimension;
			if (percentage > (1 - wantToKnowMovement)) {
				knowCandidates.add(new Integer(i));
			}

		}
		// IF I WANT TO KNOW ONE
		if (knowCandidates.size() > 0) {
			turnsTillDiscovered++;
			info = false;
			// TRY TO DECIDE A GOOD MOVEMENT TO KNOW (HAVE MORE PAYOFF)
			int maxDiff = -10;
			for (int strategy = 0; strategy < knowCandidates.size(); strategy++) {
				int coord = knowCandidates.get(strategy);
				int currMinDiff = 10;
				for (int i = 0; i < matrixDimension; i++) {
					psi27_Vector2 vec = rowTurn ? matrix.GetPosition(coord, i) : matrix.GetPosition(i, coord);
					int myPay = rowTurn ? vec.x : vec.y;
					int enemyPay = rowTurn ? vec.y : vec.x;
					int diff = myPay - enemyPay;
					if (diff < currMinDiff) {
						currMinDiff = diff;
					}
				}
				if (currMinDiff > maxDiff) { //
					toRet = coord;
					maxDiff = currMinDiff;
				}
			}
		} else {
			turnsTillDiscovered = 0;
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

	/*
	 * MAKES THE ACTUAL CHOICE FOR DOMINANT, A MAX MIN, MOST WINNER AND PAST
	 */
	protected int Choice(List<Integer> dominant, List<psi27_Vector2> nash) {
		int toRet = 0;

		// IF I HAVE A "DOMINANT" OR MORE CHOOSE THE BEST
		int bestDominant = GetBetterDominant(dominant);
		if (bestDominant != -1) {
			return bestDominant;
		}
		// GET MAX MIN
		List<Integer> maxMin = GetMaxMin();

		// TODO something with nash
		// If I'm winning I want to minimize my loses but if I'm losing I want
		// to maximize my profits trying to reach the other player
		if (winning) {
			toRet = maxMin.get(new Random().nextInt(maxMin.size()));
		} else {
			toRet = GetMostWinnerMove();
		}
		// DECIDE IF I'M MAKING A GOOD CHOICE SEEING THE PAST
		toRet = GetStrategyBasedOnPast(toRet, maxMin);

		return toRet;
	}

	/*
	 * SEE THE PAST AND DECIDE A BETTER RESPONSE FOR THAT
	 */
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
					checkEnemyPlayingSameMove = true;
					toRet = BetterResponse(enemyPos);
				}
			}
		}
		return toRet;
	}

	/*
	 * GET THE BETTER RESPONSE FOR AN ENEMY STRATEGY
	 */
	protected int BetterResponse(int enemy) {
		int toRet = 0;
		float payDiff = -10;
		for (int j = 0; j < matrixDimension; j++) {
			psi27_Vector2 vec = rowTurn ? matrix.GetPosition(j, enemy) : matrix.GetPosition(enemy, j);
			int myPay = rowTurn ? vec.x : vec.y;
			int enemyPay = rowTurn ? vec.y : vec.x;
			int diff = myPay - enemyPay;
			if (diff > payDiff) {
				toRet = j;
				payDiff = diff;
			}
		}
		// System.out.println("Choosing best response diff: " + payDiff);
		return toRet;
	}

	/*
	 * GET A LIST OF THE NASH EQUILIBRIUMS OF THE GAME
	 */
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
		// MINIMIZE MY WORST PAYOFF
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

	/*
	 * GET THE MOVEMENT WHERE I WIN OR DRAW MOST
	 */
	protected int GetMostWinnerMove() {
		int toRet = 0;
		float maxWins = 0;
		for (int i = 0; i < matrixDimension; i++) {
			float iteration = 0;
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(i, j) : matrix.GetPosition(j, i);
				int pay = rowTurn ? vec.x : vec.y;
				int enPay = rowTurn ? vec.y : vec.x;
				if (pay >= enPay) {
					iteration++;
				}
			}
			if (iteration > maxWins) {
				toRet = i;
				maxWins = iteration;
			}
		}

		return toRet;
	}

	/*
	 * GET THE MOVEMENT WHERE I HAVE THE BEST AVERAGE
	 */
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

	// **********************DOMINANT*****************************************************************
	/*
	 * RETURN A LIST OF POSSIBLE DOMINANTS MY DEF OF DOMINANT IS A MOVEMENT
	 * WHERE YOU ALWAYS WIN OR DRAW WITH THE ENEMY
	 */
	protected List<Integer> GetDominant() {
		List<Integer> toRet = new ArrayList<Integer>();
		for (int i = 0; i < matrixDimension; i++) {
			boolean toAdd = false;
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(i, j) : matrix.GetPosition(j, i);
				int myPay = rowTurn ? vec.x : vec.y;
				int enemyPay = rowTurn ? vec.y : vec.x;
				if (enemyPay > myPay || myPay < 0) {
					break;
				}

				if (j == matrixDimension - 1) {
					toAdd = true;
				}
			}
			if (toAdd) {
				toRet.add(new Integer(i));
			}
		}
		// if (toRet.size() > 0) {
		// System.out.println("I found a movement where I always win");
		// }
		return toRet;
	}

	/*
	 * If I have some dominant strategies choose the one that harms the most to
	 * the enemy (maximize my difference)
	 */
	protected int GetBetterDominant(List<Integer> dominant) {
		int toRet = -1;

		int maxDiff = 0;
		for (int strategy = 0; strategy < dominant.size(); strategy++) {
			int coord = dominant.get(strategy);
			int currMinDiff = 10;
			for (int i = 0; i < matrixDimension; i++) {
				psi27_Vector2 vec = rowTurn ? matrix.GetPosition(coord, i) : matrix.GetPosition(i, coord);
				int myPay = rowTurn ? vec.x : vec.y;
				int enemyPay = rowTurn ? vec.y : vec.x;
				int diff = myPay - enemyPay;
				if (diff < currMinDiff) {
					currMinDiff = diff;
				}
			}
			if (currMinDiff > maxDiff) {
				// System.out.println("Choosing dominant strategy");
				toRet = dominant.get(coord);
				maxDiff = currMinDiff;
			}
		}
		return toRet;
	}

	// ***********************MESSAGES**************************************************

	/*
	 * I DON'T SET THE MATRIX TO UNKNOWN UNLESS IT SURPASS MY FACTOR
	 * 
	 * @see psi27_Player#ChangedMatrix(int)
	 */
	@Override
	protected void ChangedMatrix(int percentage) {
		if (percentage > percentageSetUnknown) {
			matrix.SetUnknown();
		}
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
		winning = myPayoff > enemyPayoff ? true : false;
		matrix.ChangePosition(x, y, new psi27_Vector2(xPayoff, yPayoff));
		// Learning of the past factor
		if (checkEnemyPlayingSameMove) {
			checkEnemyPlayingSameMove = false;
			int enemyPos = rowTurn ? y : x;
			psi27_Vector2 vec = positionsLog.get(positionsLog.size() - 1);
			int lastEnemyPos = rowTurn ? vec.y : vec.x;
			if (enemyPos != lastEnemyPos) {
				// penalize because you failed in predict the next movement
				System.out.println("LEARNING");
				enemyPlayingSameMove++;
			}
		}
		positionsLog.add(position);
	}

	/*
	 * AUX METHOD
	 */
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

}
