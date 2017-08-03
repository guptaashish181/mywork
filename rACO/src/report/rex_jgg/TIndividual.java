package report.rex_jgg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class TIndividual {
	private TVector fVector;
	private double fEvalationValue;
	private static final double EPSILON = 10e-9;

	public TIndividual() {
		fVector = new TVector();
		fEvalationValue = Double.NaN;
	}

	public TIndividual(TIndividual src) {
		fVector = new TVector(src.fVector);
		fEvalationValue = src.fEvalationValue;
	}

	@Override
	public TIndividual clone() {
		return new TIndividual(this);
	}

	public TIndividual copyFrom(TIndividual src) {
		fVector.copyFrom(src.fVector);
		fEvalationValue = src.fEvalationValue;
		return this;
	}

	@Override
	public String toString() {
		String str = fEvalationValue + "\n";
		str += fVector + "\n";
		return str;
	}

	public void readFrom(BufferedReader br) throws IOException {
		fVector.readFrom(br);
		fEvalationValue = Double.parseDouble(br.readLine());
	}

	/**
	 * 書式は<br>
	 * <br>
	 * TVectorの情報<br>
	 * 評価値<br>
	 * <br>
	 * となっている
	 *
	 * @see TBaseMethods#writeTo(java.io.PrintWriter)
	 */
	public void writeTo(PrintWriter pw) {
		fVector.writeTo(pw);
		pw.println(fEvalationValue);
	}

	public void setEvaluationValue(double eval) {
		fEvalationValue = eval;
	}

	public double getEvaluationValue() {
		return fEvalationValue;
	}

	public TVector getVector() {
		return fVector;
	}

	@Override
	public boolean equals(Object t) {
		if (t == null || getClass() != t.getClass())
			return false;

		TIndividual tVector = (TIndividual) t;
		if (Math.abs(getEvaluationValue() - tVector.getEvaluationValue()) > EPSILON)
			return false;

		return tVector.getVector().equals(fVector);
	}

	@Override
	public int hashCode() {
		return fVector.hashCode() * (int) fEvalationValue;
	}

	// public static void main(String[] args) throws IOException{
	// File file = new File("TIndividualTest.txt");
	// PrintWriter pw = new PrintWriter(new BufferedWriter(new
	// FileWriter(file)));
	// //BufferedReader br = new BufferedReader(new FileReader(file));
	//
	// TIndividual individual = new TIndividual();
	// individual.setEvaluationValue(100.0);
	// individual.getVector().setDimension(3);
	// for (int i = 0; i < individual.getVector().getDimension(); i++) {
	// individual.getVector().setElement(i, i+1);
	// }
	//
	// individual.writeTo(pw);
	//
	// //br.close();
	// pw.close();
	//
	// }
}
