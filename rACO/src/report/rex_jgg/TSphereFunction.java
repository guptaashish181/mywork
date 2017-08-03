package report.rex_jgg;

public class TSphereFunction implements IFunction {
	private int fDimension;

	public TSphereFunction(int dimension) {
		fDimension = dimension;
	}

	public int getDimension() {
		return fDimension;
	}

	/**
	 * Sphere関数の実体部分<br>
	 * 指定した次元数と異なる場合，Double.MAX_VALUEを返す
	 *
	 * @param x
	 *                ベクトルクラス
	 * @return 評価値
	 */
	public double evaluate(TVector x) {
		if (fDimension != x.getDimension())
			return Double.MAX_VALUE;

		return x.innerProduct(x);
	}

	// public static void main(String[] args) {
	// TVector vector = new TVector();
	// vector.setDimension(3);
	// for (int i = 0; i < vector.getDimension(); i++) {
	// vector.setElement(i, i * 15);
	// }
	//
	// TSphereFunction sphereFunction = new TSphereFunction(3);
	// System.out.println(sphereFunction.evaluate(vector));
	// }
}
