package window;

public class GameMatrix {

	protected Vector2[][] matrix;
	protected int dimension;

	public GameMatrix() {
		matrix = new Vector2[5][5];
		FillMatrix(5);
	}

	public GameMatrix(int n) {
		dimension = n;
		FillMatrix(n);
	}

	private void FillMatrix(int n) {
		matrix = new Vector2[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++) {
				if (i == j) {
					matrix[i][j] = new Vector2();
				} else {
					Vector2 v = new Vector2();
					matrix[i][j] = v;
					matrix[j][i] = new Vector2(v.x, v.y);
				}
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
