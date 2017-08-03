
//=========FitnessFunction�̊�{�N���X=========================
//                                                                     |
// ���̃N���X��h�������A���z�֐���Ǝ��ɋL�q���邱�ƂŁA              |
// ���ʃC���^�[�t�F�[�X���������l�X�ȁuFitnessFunction�v����������     |
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
	public int   paramDim;           // ��ԓ��̓x�N�g���̎�����
	public int   evaluationCounter ; // �]����
	FitnessFunction(){
		evaluationCounter=0;
	}
	//�p�����[�^�̎�������m�点��----------------------------
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

	// �����ݒ�----(�Ԃ�l true=����I��, false=�ُ�I��)
	public boolean Init(){ return( true );}
	// �㏈��
	public void finish(){}

	//�p�����[�^�̕]���l���o�͂���----------------------------------
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



// [�⑫����]===========================================================
//
