package report.rex_jgg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class TVector {
	private double[] fArray;
	private static final double EPSILON = 10e-9;

	public TVector() {
		fArray = new double[0];
	}

	public TVector(TVector src) {
		fArray = new double[src.fArray.length];
		for (int i = 0; i < fArray.length; i++) {
			fArray[i] = src.fArray[i];
		}
	}

	public TVector copyFrom(TVector src) {
		if (fArray.length != src.fArray.length) {
			fArray = new double[src.fArray.length];
		}

		for (int i = 0; i < fArray.length; i++) {
			fArray[i] = src.fArray[i];
		}
		return this;
	}

	@Override
	public TVector clone() {
		return new TVector(this);
	}

	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < fArray.length; i++) {
			str += fArray[i] + " ";
		}
		return str;
	}

	public void writeTo(PrintWriter pw) {
		pw.println(fArray.length);
		for (int i = 0; i < fArray.length; i++) {
			pw.print(fArray[i] + " ");
		}
		pw.println();
	}

	public void readFrom(BufferedReader br) throws IOException {
		int dimension = Integer.parseInt(br.readLine());
		String[] strArray = br.readLine().split(" ");
		if (fArray.length != dimension) {
			fArray = new double[dimension];
		}

		for (int i = 0; i < fArray.length; i++) {
			fArray[i] = Double.parseDouble(strArray[i]);
		}
	}

	public int getDimension() {
		return fArray.length;
	}

	public void setDimension(int dimension) {
		if (fArray.length != dimension) {
			fArray = new double[dimension];
		}
	}

	public double getElement(int index) {
		return fArray[index];
	}

	public void setElement(int index, double x) {
		fArray[index] = x;
	}

	/**
	 * 自分自身に引数のベクトルを足し，自分自身を返す<br>
	 * 演算子オーバーロードができないため，y += x; のように単項演算子として定義
	 *
	 * @param x
	 * @return
	 */
	public TVector add(TVector x) {
		assert x.fArray.length == fArray.length;
		for (int i = 0; i < fArray.length; i++) {
			fArray[i] += x.fArray[i];
		}
		return this;
	}

	/**
	 * 自分自身から引数のベクトルを引き，自分自身を返す<br>
	 * y -= x;
	 *
	 * @param x
	 * @return
	 */
	public TVector substract(TVector x) {
		assert x.fArray.length == fArray.length;
		for (int i = 0; i < fArray.length; i++) {
			fArray[i] -= x.fArray[i];
		}
		return this;
	}

	/**
	 * 引数のベクトルとの内積を返す
	 *
	 * @param x
	 * @return
	 */
	public double innerProduct(TVector x) {
		assert x.fArray.length == fArray.length;
		double innerProductValue = 0;
		for (int i = 0; i < fArray.length; i++) {
			innerProductValue += fArray[i] * x.fArray[i];
		}
		return innerProductValue;
	}

	/**
	 * 各要素にaをかけて，自分自身を返す
	 *
	 * @param a
	 * @return
	 */
	public TVector scalarProduct(double a) {
		for (int i = 0; i < fArray.length; i++) {
			fArray[i] *= a;
		}
		return this;
	}

	/**
	 * L2ノルム(ユークリッド距離)を返す
	 *
	 * @return
	 */
	public double getL2Norm() {
		double L2Norm = 0;
		for (double d : fArray) {
			L2Norm += d * d;
		}
		return Math.sqrt(L2Norm);
	}

	/**
	 * 単位ベクトル化し，自分自身を返す
	 *
	 * @return
	 */
	public TVector normalize() {
		double L2Norm = getL2Norm();
		for (int i = 0; i < fArray.length; i++) {
			fArray[i] /= L2Norm;
		}
		return this;
	}

	/**
	 * 比較対象は次元数と各要素
	 *
	 * @param t
	 * @return
	 */
	@Override
	public boolean equals(Object t) {
		if (t == null || getClass() != t.getClass())
			return false;

		TVector tVector = (TVector) t;
		if (getDimension() != tVector.getDimension())
			return false;
		for (int i = 0; i < fArray.length; i++) {
			if (Math.abs(fArray[i] - tVector.fArray[i]) > EPSILON)
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return fArray.hashCode();
	}

	// public static void main(String[] args) throws IOException{
	// // File file = new File("VectorTest.txt");
	// // BufferedReader br = new BufferedReader(new FileReader(file));
	//
	// TVector vector = new TVector();
	// vector.setDimension(3);
	// for (int i = 0; i < vector.getDimension(); i++) {
	// vector.setElement(i, i+1);
	// }
	//
	// TVector vector2 =vector.clone();
	// vector.setElement(1, 5);
	// System.out.println(vector);
	// System.out.println(vector2);
	//
	// for (int i = 0; i < 100; i++) {
	// vector.add(vector2);
	// }
	// System.out.println(vector);
	// }
}
