package report.rex_jgg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class TPopulation {
	private TIndividual[] fArray;

	public TPopulation() {
		fArray = new TIndividual[0];
	}

	public TPopulation(TPopulation population) {
		fArray = new TIndividual[population.getPopulationSize()];
		for (int i = 0; i < population.getPopulationSize(); i++) {
			fArray[i] = population.getIndividual(i).clone();
		}
	}

	public TPopulation copyFrom(TPopulation src) {
		if (getPopulationSize() != src.getPopulationSize())
			setPopulationSize(src.getPopulationSize());

		for (int i = 0; i < src.getPopulationSize(); i++) {
			fArray[i].copyFrom(src.getIndividual(i));
		}
		return this;
	}

	@Override
	public TPopulation clone() {
		return new TPopulation(this);
	}

	/**
	 * 個体数<br>
	 * 各個体の情報<br>
	 */
	@Override
	public String toString() {
		String string = getPopulationSize() + "\n";
		for (TIndividual tIndividual : fArray) {
			string += tIndividual;
		}
		return string;
	}

	public void readFrom(BufferedReader br) throws IOException {
		int numOfPopulation = Integer.parseInt(br.readLine());
		setPopulationSize(numOfPopulation);
		for (int i = 0; i < numOfPopulation; i++) {
			fArray[i] = new TIndividual();
			fArray[i].readFrom(br);
		}
	}

	/**
	 * 書式は<br>
	 * <br>
	 * 個体数<br>
	 * 各個体の情報<br>
	 * <br>
	 * となっている
	 *
	 * @see TBaseMethods#writeTo(java.io.PrintWriter)
	 */
	public void writeTo(PrintWriter pw) {
		pw.println(getPopulationSize());
		for (TIndividual tIndividual : fArray) {
			tIndividual.writeTo(pw);
		}
	}

	@Override
	public boolean equals(Object t) {
		if (t == null || getClass() != t.getClass())
			return false;

		TPopulation tPopulation = (TPopulation) t;
		if (getPopulationSize() != tPopulation.getPopulationSize())
			return false;

		for (int i = 0; i < getPopulationSize(); i++) {
			if (fArray[i] == null || !fArray[i].equals(tPopulation.getIndividual(i)))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int num = 1;
		for (TIndividual tIndividual : fArray) {
			num *= tIndividual.hashCode();
		}
		return num * getPopulationSize();
	}

	public int getPopulationSize() {
		return fArray.length;
	}

	/**
	 * num体の個体集団で初期化する
	 *
	 * @param num
	 */
	public void setPopulationSize(int num) {
		fArray = new TIndividual[num];
		for (int i = 0; i < fArray.length; i++) {
			fArray[i] = new TIndividual();
		}
	}

	public TIndividual getIndividual(int index) {
		return fArray[index];
	}

	public double getBestEvaluationValue() {
		Arrays.sort(fArray, (a, b) -> ((Double) a.getEvaluationValue()).compareTo((Double) b.getEvaluationValue()));
		return fArray[0].getEvaluationValue();
	}

	/**
	 * fArrayの要素をランダムに入れ替えるメソッド<br>
	 * これにより非復元抽出の処理が簡潔になる
	 *
	 */
	public void shuffle() {
		Random rand = new Random();
		for (int i = 0; i < fArray.length; i++) {
			int index = rand.nextInt(fArray.length - i) + i;
			swap(i, index);
		}
	}

	/**
	 * index1とindex2にある要素を入れ替える
	 *
	 * @param index1
	 * @param index2
	 */
	public void swap(int index1, int index2) {
		TIndividual temp = fArray[index1].clone();
		fArray[index1].copyFrom(fArray[index2]);
		fArray[index2].copyFrom(temp);
	}

	/**
	 * 指定した範囲の配列を返すメソッド
	 *
	 * @param startIndex
	 * @param count
	 * @return
	 */
	public TIndividual[] getRange(int startIndex, int count) {
		TIndividual[] selectedRange = new TIndividual[count];
		for (int i = startIndex; i < startIndex + count; i++) {
			selectedRange[i - startIndex] = fArray[i].clone();
		}
		return selectedRange;
	}
	// public static void main(String[] args) throws IOException {
	// //File file = new File("TPopulationTest.txt");
	// //PrintWriter pw = new PrintWriter(new BufferedWriter(new
	// FileWriter(file)));
	// //BufferedReader br = new BufferedReader(new FileReader(file));
	// TPopulation population = new TPopulation();
	// population.setnumberOfPopulation(5);
	// for (int i = 0; i < population.getNumberOfPopulation(); i++) {
	// TIndividual individual = population.getIndividual(i);
	// individual.getVector().setDimension(3);
	// for (int j = 0; j < individual.getVector().getDimension(); j++) {
	// individual.getVector().setElement(j, j * i);
	// }
	// individual.setEvaluationValue(100 * i);
	// }
	// TPopulation population2 = population.clone();
	// // population2.setnumberOfPopulation(5);
	// // for (int i = 0; i < population2.getNumberOfPopulation(); i++) {
	// // TIndividual individual = population2.geTIndividual(i);
	// // individual.getVector().setDimension(3);
	// // for (int j = 0; j < individual.getVector().getDimension(); j++) {
	// // individual.getVector().setElement(j, j * i * 10);
	// // }
	// // individual.setEvaluationValue(0.125 * i);
	// // }
	//
	// System.out.println(population.equals(population2));
	// //population2.getIndividual(1).setEvaluationValue(1000);
	// population2.getIndividual(2).getVector().setElement(1, 1.0005);
	//
	// System.out.println(population.equals(population2));
	// //System.out.println(population2);
	// //pw.close();
	// //br.close();
	// }

}
