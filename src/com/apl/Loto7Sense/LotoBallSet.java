package com.apl.Loto7Sense;

import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * 抽選用番号ボールを1セット(ボール6個)で管理する。
 * @author kms2
 *
 */
public class LotoBallSet {

	/** 抽選回 */
	private int pLoto_count = 0;

	/** 抽選番号 */
	private ArrayList<LotoBall> pBallList = null;

	/** 抽選番号 */
	private ArrayList<Integer> pNumberList = null;

	/** */
	private boolean selectedFlg = false;

	/**
	 *
	 */
	public LotoBallSet(){
		pBallList = new ArrayList<LotoBall>();
		pNumberList = new ArrayList<Integer>();
	}

	public boolean getSelected(){
		return selectedFlg;
	}

	/**
	 *
	 */
	public void selected(){
		if(selectedFlg) {
			selectedFlg = false;
		} else {
			selectedFlg = true;
		}

		for(int ballCnt = 0; ballCnt < pBallList.size(); ballCnt++){
			LotoBall ball = pBallList.get(ballCnt);
			if(selectedFlg) {
				ball.ballONTest();
			} else {
				ball.ballONTest2();
			}
		}
	}
	/**
	 * 抽選回
	 */
	public void setLotCount(Integer inLoto_count){
		pLoto_count = inLoto_count;
	}
	public Integer getLotCount(){
		return pLoto_count;
	}

	/**
	 *
	 */
	public void setBallNumber(Integer inNumber){
		pNumberList.add(inNumber);
	}

	/**
	 *
	 */
	public ArrayList<Integer> getBallNumbers(){
		return pNumberList;
	}

	/**
	 *
	 */
	public void setBall(LotoBall inBall){
		pBallList.add(inBall);
		pNumberList.add(inBall.getNumber());
	}

	/**
	 *
	 */
	public LotoBall getBall(Integer inCnt){
		LotoBall rtnBall = null;
		if( pBallList.size() > inCnt ){
			rtnBall = pBallList.get(inCnt);
		}
		return rtnBall;
	}

	/**
	 *
	 */
	public int getSize(){
		return pBallList.size();
	}

	/**
	 *
	 */
	public boolean chkBall(int inBallNumber){

		boolean rtnVal = false;
		if( pNumberList.indexOf(inBallNumber) > -1 ){
			rtnVal = true;
		}

		return rtnVal;
	}

	/**
	 *
	 */
	public void removeBall(int inBallNumber){
		pNumberList.remove(pNumberList.indexOf(inBallNumber));
		for( int i=0; i<pBallList.size(); i++){
			if( inBallNumber == pBallList.get(i).getNumber()){
				pBallList.remove(i);
				break;
			}
		}
	}
}
