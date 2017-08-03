package report.rex_jgg;

public class TK_Tablet implements IFunction {
	private int fDimension;

	public TK_Tablet(int dimension) {
		fDimension = dimension;
	}

	@Override
	public int getDimension() {
		return fDimension;
	}

	/**
	 * k-tablet関数 (k=n/4)の実体部分<br>
	 * 指定した次元数と異なる場合，Double.MAX_VALUEを返す
	 * k=n/4はレポート1の課題2にて指定されたものと同一
	 *
	 * @param x
	 *                ベクトルクラス
	 * @return 評価値
	 */
	@Override
	public double evaluate(TVector x) {
		if (fDimension != x.getDimension())
			return Double.MAX_VALUE;

		int k = (int)((double)x.getDimension() /4.0); //k=n/4
		double result = 0.0; //評価値を初期化
		for (int i = 0; i < x.getDimension(); ++i) {
			double xi = x.getElement(i); //i番目の次元の要素
			if (i < k) {
				result += xi * xi;
			} else {
				result += 10000.0 * xi * xi;
			}
		}
		return result;
	}

}
