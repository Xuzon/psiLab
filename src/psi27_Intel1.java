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
		List<psi27_Vector2> dominant = GetDominant();
		List<psi27_Vector2> enemyDominant = GetEnemyDominant();
		toRet = Choice(paretoOptimal, dominant, enemyDominant);
		return toRet;
	}

	protected int Choice(List<psi27_Vector2> pareto, List<psi27_Vector2> dominant, List<psi27_Vector2> enemyDominant) {
		int toRet = 0;
		// TODO

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

	protected List<psi27_Vector2> GetDominant() {
		List<psi27_Vector2> toRet = new ArrayList<psi27_Vector2>();
		// TODO get dominant strategy
		return toRet;
	}

	protected List<psi27_Vector2> GetEnemyDominant() {
		List<psi27_Vector2> toRet = new ArrayList<psi27_Vector2>();
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
