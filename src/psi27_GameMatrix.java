import java.util.ArrayList;

public class psi27_GameMatrix {

	protected psi27_Vector2[][] matrix;
	protected int dimension;

	public psi27_GameMatrix() {
		matrix = new psi27_Vector2[5][5];
		FillMatrix(5);
	}

	public psi27_GameMatrix(int n) {
		dimension = n;
		FillMatrix(n);
	}

	private void FillMatrix(int n) {
		matrix = new psi27_Vector2[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++) {
				if (i == j) {
					matrix[i][j] = new psi27_Vector2();
				} else {
					psi27_Vector2 v = new psi27_Vector2();
					matrix[i][j] = v;
					matrix[j][i] = new psi27_Vector2(v.y, v.x);
				}
			}
		}
	}

	public void ChangeMatrix(float percentage) {
		ArrayList<psi27_Vector2> changedPos = new ArrayList<>();
		float percentageChanged = 0;
		float percentageEach = 100f / (dimension * dimension);
		while (percentageChanged < percentage) {
			psi27_Vector2 posToChange = new psi27_Vector2(dimension);
			psi27_Vector2 simetricPosToChange = new psi27_Vector2(posToChange.y, posToChange.x);
			if (changedPos.contains(posToChange) || changedPos.contains(simetricPosToChange)) {
				continue;
			}
			changedPos.add(posToChange);
			percentageChanged += (posToChange.x == posToChange.y) ? percentageEach : percentageEach * 2;
			psi27_Vector2 newPayoff = new psi27_Vector2();
			matrix[posToChange.x][posToChange.y] = newPayoff;
			if (posToChange.x != posToChange.y) {
				// only for simetric purposes
				matrix[posToChange.y][posToChange.x] = new psi27_Vector2(newPayoff.y, newPayoff.x);
			}
		}
	}

	public String ToString() {
		String toRet = "MATRIX \n";
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				toRet += matrix[i][j].ToString() + "   ";
			}
			toRet += "\n\n";
		}
		return toRet;
	}
}
