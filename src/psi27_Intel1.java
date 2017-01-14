import java.util.ArrayList;
import java.util.List;

public class psi27_Intel1 extends psi27_Player {

	protected psi27_GameMatrix matrix;
	protected boolean rowTurn = true;
	// *************************************
	// **************FACTORS****************
	protected int enemyPlayingSameMove = 4;
	// ******************************************
	protected boolean winning = false;
	protected int myPayoff = 0;
	protected int enemyPayoff = 0;
	protected List<psi27_Vector2> positionsLog = new ArrayList<psi27_Vector2>();

	@Override
	protected void NewGame() {
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

	protected int DiscoverMatrix() {
		int toRet = -1;
		for (int i = 0; i < matrixDimension; i++) {
			for (int j = 0; j < matrixDimension; j++) {
				psi27_Vector2 vec = matrix.GetPosition(i, j);
				// TODO calculate a factor to set to discover this
			}
		}
		return toRet;
	}

	protected int Strategy() {
		int toRet = 0;
		List<psi27_Vector2> paretoOptimal = GetParetoOptimal();
		List<Integer> dominant = GetDominant();
		List<Integer> enemyDominant = GetEnemyDominant();
		List<psi27_Vector2> nashEquilibriums = GetNashEquilibrium();
		toRet = Choice(paretoOptimal, dominant, enemyDominant, nashEquilibriums);
		return toRet;
	}

	protected int Choice(List<psi27_Vector2> pareto, List<Integer> dominant, List<Integer> enemyDominant,
			List<psi27_Vector2> nash) {
		int toRet = 0;
		int probablyPayoff = -1;
		// Get dominant strategies with pareto optimal
		List<psi27_Vector2> paretoInDominant = new ArrayList<psi27_Vector2>();
		for (psi27_Vector2 vec : pareto) {
			int coord = rowTurn ? vec.y : vec.x;
			if (dominant.contains(new Integer(coord))) {
				paretoInDominant.add(new psi27_Vector2(vec.x, vec.y));
				break;
			}
		}
		// Get enemy dominant strategies with pareto optimal
		List<psi27_Vector2> paretoInEnemyDominant = new ArrayList<psi27_Vector2>();
		for (psi27_Vector2 vec : pareto) {
			int coord = rowTurn ? vec.x : vec.y;
			if (enemyDominant.contains(new Integer(coord))) {
				paretoInEnemyDominant.add(new psi27_Vector2(vec.x, vec.y));
				break;
			}
		}
		// Get common paretos in dominant strategies
		List<psi27_Vector2> commonParetos = new ArrayList<psi27_Vector2>();
		for (int i = 0; i < paretoInDominant.size(); i++) {
			for (int j = 0; j < paretoInEnemyDominant.size(); j++) {
				psi27_Vector2 pMy = paretoInDominant.get(i);
				psi27_Vector2 pEnemy = paretoInEnemyDominant.get(j);
				if (pMy.x == pEnemy.x && pMy.y == pEnemy.y) {
					commonParetos.add(new psi27_Vector2(i, j));
				}
			}
		}

		// I'm gonna select the pareto (in common dominant strategies) with most
		// value for me
		int selectedPareto = -1;
		for (int i = 0; i < commonParetos.size(); i++) {
			psi27_Vector2 vec = matrix.GetPosition(commonParetos.get(i));
			int pay = rowTurn ? vec.x : vec.y;
			if (pay > probablyPayoff) {
				selectedPareto = i;
			}
		}

		int paretoCoord = -1;
		if (selectedPareto != -1) {
			psi27_Vector2 vec = commonParetos.get(selectedPareto);
			paretoCoord = rowTurn ? vec.y : vec.x;
		}
		// GET MAX MIN
		int maxMin = GetMaxMin();

		// If I have a common pareto and it is in my maxMin choose directly this
		if (paretoCoord != -1) {
			if (paretoCoord == maxMin) {
				toRet = maxMin;
				return toRet;
			}
		}

		int past = GetStrategyBasedOnPast();

		if (maxMin != past) {
			toRet = past;
		} else {
			toRet = maxMin;
		}

		if (dominant.size() > 0) {

		}

		// TODO

		return toRet;
	}

	protected int GetStrategyBasedOnPast() {
		int toRet = 0;
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

	protected int GetMaxMin() {
		int toRet = -1;
		int worstPayOff = 0;
		if (rowTurn) {
			for (int i = 0; i < matrixDimension; i++) {
				for (int j = 0; j < matrixDimension; j++) {
					psi27_Vector2 vec = matrix.GetPosition(j, i);
					if (worstPayOff < vec.y) {
						worstPayOff = vec.y;
						toRet = j;
					}
				}
			}
		} else {
			for (int i = 0; i < matrixDimension; i++) {
				for (int j = 0; j < matrixDimension; j++) {
					psi27_Vector2 vec = matrix.GetPosition(i, j);
					if (worstPayOff < vec.x) {
						worstPayOff = vec.x;
						toRet = i;
					}
				}
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
				if (vec.x >= xMax && vec.y >= yMax) {
					psi27_Vector2 add = new psi27_Vector2(i, j);
					toRet.add(add);
				}
			}
		}
		return toRet;
	}

	protected List<Integer> GetDominant() {
		List<Integer> toRet = new ArrayList<Integer>();
		// TODO get dominant strategy
		return toRet;
	}

	protected List<Integer> GetEnemyDominant() {
		List<Integer> toRet = new ArrayList<Integer>();
		// TODO get enemy dominant strategy
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
		int y = Integer.parseInt(temp[0]);
		int x = Integer.parseInt(temp[1]);
		temp = payoffs.split(",");
		int yPayoff = Integer.parseInt(temp[0]);
		int xPayoff = Integer.parseInt(temp[1]);
		if (rowTurn) {
			myPayoff += yPayoff;
			enemyPayoff += xPayoff;
		} else {
			enemyPayoff += yPayoff;
			myPayoff += xPayoff;
		}
		psi27_Vector2 position = new psi27_Vector2(x, y);
		positionsLog.add(position);
		winning = myPayoff > enemyPayoff ? true : false;
		matrix.ChangePosition(x, xPayoff, new psi27_Vector2(xPayoff, yPayoff));
	}

}
