package report.rex_jgg;

import java.util.Arrays;
import java.util.Random;

public class TJgg {
	private Random fRandom;
	private TPopulation fpopulation;
	private TRex fRex;
	private IFunction fFunction;
	private int fNoOfParents;
	private int fNoOfKids;

	public TJgg(IFunction function, TPopulation initialPopulation, Random rand, int noOfParents, int noOfKids) {
		fRandom = rand;
		fpopulation = initialPopulation;
		fFunction = function;
		fNoOfParents = noOfParents;
		fNoOfKids = noOfKids;

		fRex = new TRex(fRandom);
	}

	public void doOneIteration() {
		// 複製選択
		fpopulation.shuffle();
		TIndividual[] parents = fpopulation.getRange(0, fNoOfParents);

		// 子個体の生成
		TIndividual[] children = fRex.makeOffspring(parents, fNoOfKids);

		// 子個体の評価
		for (int i = 0; i < children.length; i++) {
			children[i].setEvaluationValue(fFunction.evaluate(children[i].getVector()));
		}

		// 生存選択
		// 上位n+1個体をparentsの個体と置き換える
		Arrays.sort(children, (a, b) -> ((Double) a.getEvaluationValue()).compareTo((Double) b.getEvaluationValue()));
		for (int i = 0; i < parents.length; i++) {
			fpopulation.getIndividual(i).copyFrom(children[i]);
		}
	}

	public TPopulation getPopulation() {
		return fpopulation;
	}
}
