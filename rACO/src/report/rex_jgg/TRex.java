package report.rex_jgg;

import java.util.Random;

public class TRex {
	private Random fRandom;

	public TRex(Random rand) {
		fRandom = rand;
	}

	public TIndividual[] makeOffspring(TIndividual[] parents, int noOfOffspring) {
		// 初期化
		TIndividual[] children = new TIndividual[noOfOffspring];
		int dimension = parents[0].getVector().getDimension();
		for (int i = 0; i < children.length; i++) {
			children[i] = new TIndividual();
			children[i].getVector().setDimension(dimension);
		}

		// はじめに重心を計算(すべてのベクトルは長さが同じと仮定)
		TVector gVector = new TVector();
		gVector.setDimension(dimension);
		for (int i = 0; i < parents.length; i++) {
			gVector.add(parents[i].getVector());
		}
		gVector.scalarProduct(1 / (double) parents.length);

		for (int i = 0; i < children.length; i++) {
			// 第2項のベクトルを計算
			TVector term2Vector = new TVector();
			term2Vector.setDimension(dimension);
			for (int j = 0; j < parents.length; j++) {
				TVector subVector = parents[j].getVector().clone();
				term2Vector.add(subVector.substract(gVector).scalarProduct(normalDist(parents.length)));
			}
			children[i].getVector().copyFrom(gVector.clone().add(term2Vector));
		}
		return children;
	}

	/**
	 * 平均0，分散1/(η-1)とした正規分布の確率密度関数
	 *
	 * @param noOfParents
	 * @return
	 */
	private double normalDist(int noOfParents) {
		return fRandom.nextGaussian() / Math.sqrt(noOfParents - 1);
	}

	/**
	 * 平均0，分散1/(η-1)とした一様分布の確率密度関数
	 *
	 * @param noOfParents
	 * @return
	 */
	private double uniformDist(int noOfParents) {
		double threshold = Math.sqrt(3 / (double) (noOfParents - 1));
		return fRandom.nextDouble() * threshold * 2 - threshold;
	}

	// public static void main(String[] args) {
	// Random rand = new Random();
	// TRex rex = new TRex(rand);
	//
	// //親個体を生成
	// TIndividual[] parents = new TIndividual[3];
	// for (int i = 0; i < parents.length; i++) {
	// parents[i] = new TIndividual();
	// parents[i].getVector().setDimension(3);
	// for (int j = 0; j < parents[i].getVector().getDimension(); j++) {
	// parents[i].getVector().setElement(j, rand.nextInt(100));
	// }
	// }
	//
	// TIndividual[] children = rex.makeOffspring(parents, 2);
	//
	// for (int i = 0; i < children.length; i++) {
	// System.out.println(children[i]);
	// }
	// }
}
