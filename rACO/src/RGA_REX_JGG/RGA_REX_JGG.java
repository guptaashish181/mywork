//======================================================================
// REX+JGGによる連続関数の最適化サーチエンジンのクラス
// 
// コスト関数が最小になるパラメータを探索する
// 確率分布関数Φとして一様分布を使用
// 
// 原典
// 小林重信：実数値GAのブレークスルーに向けて,
// 進化計算研究会進化計算シンポジウム2007講演論文集, pp.1-10 (2007)
//                                                                     |
//  Copyright 2008 Hajime Kimura, Kyushu Univ.                         |
//  http://sysplan.nams.kyushu-u.ac.jp/gen/index.html
//======================================================================
import java.lang.Math ;
import java.io.*;
import java.util.Vector ; // オブジェクトサイズの拡張・縮小を行うクラス
import java.util.*;
// import MT19937 ;
// import FitnessFunction ;

public class RGA_REX_JGG { 
	//-----------------------外部から指定されるパラメータ
	public int    param_dim;    // 最適化パラメータベクトルの次元数
	public int    numOfPop;     // 集団サイズ（個体数）（推奨値 10 x param_dim ～ 100 x param_dim )
	public int    numOfChild;   // １回の交叉あたりの生成子個体数（推奨値 10 x param_dim)
	public int    numOfParents; // １回の交叉あたりの親個体数（ param_dim+1 以上)
	public Vector individual ;  // 集団	
	public Vector parents ;     // 交叉を行う親個体集団	
	public Vector children ;    // 交叉で生成される子個体集団	
	public SMPINDV point_xg   ;   // 全個体の重心
	public SMPINDV point_best ;   // 探索で得た最良点
	public FitnessFunction objectiveFunc   ;

	//コンストラクタ------------------------------------------------
	RGA_REX_JGG( FitnessFunction InpFitnessFunction, int num_of_pop, int num_of_children, int num_of_parents ){ 
		//-------------------
		objectiveFunc = InpFitnessFunction ;
		objectiveFunc.Init() ;
		//---------探索すべきパラメータの次元数を設定
		param_dim = objectiveFunc.GetParameterDimension() ;
		//---------集団個体数を設定
		if( param_dim <  num_of_pop ){
			numOfPop = num_of_pop;
		}else{
			numOfPop = param_dim * 20;
		}
		//--------交叉の親個体数設定	
		if( param_dim <  num_of_parents ){
			numOfParents = num_of_parents;
		}else{
			numOfParents = param_dim + 2;
		}
		//-------１回の交叉あたりの生成子個体数を設定（推奨値 10 x param_dim)
		if( param_dim <  num_of_children ){
			numOfChild = num_of_children;
		}else{
			numOfChild = param_dim * 10;
		}
		//--------集団の各個体のインスタンスを生成
		individual = new Vector(); 
		SMPINDV an_indv ;
		for( int ai = 0; ai < numOfPop ; ai++ ){
			an_indv = new SMPINDV( param_dim ); 
			individual.addElement( (SMPINDV)an_indv );
		}
		//-------親個体集団を生成（インスタンスは生成しない）
		parents =  new Vector(); 
		//--------交叉で生成される子個体集団を生成	
		children = new Vector(); 
		//--------集団の重心座標を表す個体を設定
		point_xg = new SMPINDV( param_dim );
	}
	//-----------------------------------------------------------------
	public int init(){
		int ai, bi;
		double band ;
		double tmp ;
		//----------初期個体生成・評価
		SMPINDV an_indv ;
		for( ai = 0; ai < numOfPop ; ai++ ){
			for( bi = 0; bi < param_dim ; bi++ ){
				band =  objectiveFunc.GetParameterUpperBound(bi);
				band -= objectiveFunc.GetParameterLowerBound(bi);
				band *= MT19937.uniform();    // 0-1の一様乱数を掛ける
				tmp = objectiveFunc.GetParameterLowerBound(bi);
				an_indv = (SMPINDV)individual.elementAt( ai ) ;
				an_indv.param[bi] = tmp + band;
			}
		}
		//-----------個体を１個だけ評価して最良解としておく
		an_indv = (SMPINDV)individual.elementAt( 0 ) ;
		an_indv.loss = objectiveFunc.evaluate( an_indv.param );
		point_best = an_indv;
		//-----------
		return( 0 );
	}

	//=====================================
	double executeOneLoop(){
		int ai, bi, ci;
		SMPINDV an_indv ;
		SMPINDV indv_parent;
		//-------------------------------------
		// JGG 世代交代モデル
		//-----------集団から親個体を numOfParents 個ほど非復元抽出
		parents.removeAllElements() ; // 親集団の全エレメントを削除
		for( ai = 0; ai < numOfParents ; ai++ ){
			bi = individual.size() ;
			double tmp = (double)bi ;
			tmp = tmp * MT19937.uniform() ; // 0-1の一様乱数を掛ける
			bi = (int)tmp ;
			an_indv = (SMPINDV)individual.elementAt( bi ) ; // 集団からランダムに個体を選ぶ
			parents.addElement( (SMPINDV)an_indv );     // 集団から選んだ個体を親集団へ加える
			individual.removeElementAt( bi ) ; // 選んだ個体をもとの集団から削除
		}
		//-----------親個体集団の重心xgを計算
		for( bi = 0; bi < param_dim ; bi++ ){
			point_xg.param[bi] = 0.0 ;
			for( ai = 0; ai < numOfParents ; ai++ ){
				an_indv = (SMPINDV)parents.elementAt( ai ) ;
				point_xg.param[bi] += an_indv.param[bi] ;
			}
			point_xg.param[bi] /= ((double)numOfParents) ;
		}
		//-----------親個体集団から多親交叉REX(Φ)を用いて子個体集団を生成する
		double xi0 = Math.sqrt( 3/((double)numOfParents) ) ; // Φ＝一様乱数
		children.removeAllElements() ; // 子個体集団の全エレメントを削除
		for(ai = 0; ai < numOfChild ; ai++ ){
			an_indv = new SMPINDV( param_dim ); 
			for( bi = 0; bi < param_dim ; bi++ ){
				an_indv.param[bi] = point_xg.param[bi] ;
			}
			for(ci = 0; ci < numOfParents ; ci++ ){
				indv_parent = (SMPINDV)parents.elementAt( ci ) ; // 親個体
				double xi = xi0 * MT19937.uniform() ; // 0-1の一様乱数
				for( bi = 0; bi < param_dim ; bi++ ){
					an_indv.param[bi] += (xi * (indv_parent.param[bi] - point_xg.param[bi] ))  ;
				}
			}
			//-----生成した子個体を評価
			an_indv.loss = objectiveFunc.evaluate( an_indv.param );
			//-----生成した子個体を子個体集団へ加える
			children.addElement( an_indv );
		}
		//----------子個体集団中から上位 numOfParents 個分だけ選んでもとの集団 individual へ加える
		for(ai = 0; ai < numOfParents ; ai++ ){
			//---子個体集団の中で最良の個体を探す
			an_indv = (SMPINDV)children.elementAt( 0 ) ; // 子個体
			int index_best = 0;
			double loss_best = an_indv.loss ;
			for( ci = 0; ci < children.size(); ci++ ){
				an_indv = (SMPINDV)children.elementAt( ci ) ; // 子個体
				if( an_indv.loss < loss_best ){
					index_best = ci;
					loss_best = an_indv.loss ;
				}
			}
			//----
			an_indv = (SMPINDV)children.elementAt( index_best ) ; // 最良の子個体
			individual.addElement( an_indv );                     // 最良の子個体を集団へ加える
			children.removeElementAt( index_best ) ;              // 選んだ個体をもとの集団から削除
			if( point_best.loss > loss_best ){
				point_best = an_indv;
			}
		}
		//----------
		return( point_best.loss );
	}
	//----------------
	public double [] getBestParam(){
		return(  point_best.param );
	}
}

class SMPINDV {
	public double param[] ; // パラメータの解候補
	public double loss    ; // 上記パラメータの評価値
	//コンストラクタ------------------------------------------------
	SMPINDV( int   inp_dim         // 状態入力ベクトルの次元数
                 ){   
		param = new double[inp_dim] ;
		loss = 0.0 ;
	}
}


// [補足説明]===========================================================
