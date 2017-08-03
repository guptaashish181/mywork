
//=========FitnessFunctionの基本クラス=========================
//                                                                     |
// このクラスを派生させ、仮想関数を独自に記述することで、              |
// 共通インターフェースを持った様々な「FitnessFunction」を実現する     |
//                                                                     |
//  Copyright 2006 Hajime Kimura, Kyushu Univ.                         |
//  http://sysplan.nams.kyushu-u.ac.jp/gen/index.html                  |
//======================================================================
import java.awt.Graphics ;
import java.awt.Image ;
import java.awt.Color ;
import java.applet.* ;
import java.io.*;

public class FitnessFunction {
	public int   paramDim;           // 状態入力ベクトルの次元数
	public int   evaluationCounter ; // 評価回数
	FitnessFunction(){
		evaluationCounter=0;
	}
	//パラメータの次元数を知らせる----------------------------
	public int GetParameterDimension(){ return( paramDim ); }

	public int GetEvaluationCounter(){return( evaluationCounter );}
	public void IncrementEvaluationCounter(){
		evaluationCounter++ ;
	}
	public void ResetEvaluationCounter(){
		evaluationCounter = 0 ;
	}

	public double GetParameterUpperBound( int dim ){ return( 1.0 ); }
	public double GetParameterLowerBound( int dim ){ return( -1.0 ); }

	// 初期設定----(返り値 true=正常終了, false=異常終了)
	public boolean Init(){ return( true );}
	// 後処理
	public void finish(){}

	//パラメータの評価値を出力する----------------------------------
	public double evaluate( double[] param ){ return(0.0); }  

	//-----------Main
	public static void main(String args[]){
	 	FitnessFunction objectiveFunc = new FitnessFunction(); // Cart2PoleFitness();
		if( args.length < objectiveFunc.paramDim ){
			System.out.println("This function needs " + objectiveFunc.paramDim +" parameters." );
			return ;
		}
		objectiveFunc.Init() ;
		double[] param = new double[objectiveFunc.paramDim];
		for( int bi = 0; bi < objectiveFunc.paramDim ; bi++ ){
			param[bi] = Double.valueOf( args[bi] ).doubleValue() ;
		}
//		loop_max = Integer.valueOf( args[1] ).intValue() ;
		System.out.println( "value = " + objectiveFunc.evaluate( param ) );
	}

}



// [補足説明]===========================================================
//
