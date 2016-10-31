package com.apl.Loto7Sense;

import java.util.ArrayList;
import java.util.Arrays;

public class LotoGrp {

	// TODO:20161006

	public static Integer[][] gGROUP = {
			{1,7,13,19,25,31,37},
			{2,8,14,20,26,32},
			{3,9,15,21,27,33},
			{4,10,16,22,28,34},
			{5,11,17,23,29,35},
			{6,12,18,24,30,36}
	};

	/*
	public static Integer[][] gGROUP = {
			{1,8,15,22,29,36,43},
			{2,9,16,23,30,37},
			{3,10,17,24,31,38},
			{4,11,18,25,32,39},
			{5,12,19,26,33,40},
			{6,13,20,27,34,41},
			{7,14,21,28,35,42}
	};
	*/

	/** グループ数に伴う抽選番号抽出件数 */

	// 1 group
	public static Integer[][] gGROUP1_BallCnt = {
			{6}
	};

	// 2 group
	public static Integer[][] gGROUP2_BallCnt = {
			{1,5},
			{2,4},
			{3,3}
	};

	// 3 group
	public static Integer[][] gGROUP3_BallCnt = {
			{1,1,4},
			{1,2,3},
			{2,2,2}
	};

	// 4 group
	public static Integer[][] gGROUP4_BallCnt = {
			//{1,1,1,3},
			{1,1,2,2}
	};

	// 5 group
	public static Integer[][] gGROUP5_BallCnt = {
			{1,1,1,1,2}
	};

	// 6 group
	public static Integer[][] gGROUP6_BallCnt = {
			{1,1,1,1,1,1}
	};

	public static  Integer DEF_GROUP_A = 0;
	public static  Integer DEF_GROUP_B = 1;
	public static  Integer DEF_GROUP_C = 2;
	public static  Integer DEF_GROUP_D = 3;
	public static  Integer DEF_GROUP_E = 4;
	public static  Integer DEF_GROUP_F = 5;
	public static  Integer DEF_GROUP_G = 6;

	/** property */
	// グループタイプ(A～G)
	private Integer   pGroupType = 0;
	// グループタイプに対応する数字リスト
	private Integer[] pGroupBalls = null;
	// 現在抽出されているボール数
	private Integer   pSelectBallCnt = 0;
	// ボール詳細
	private ArrayList<LotoBallBase> pBallDetail = null;
	// 有効ボール件数
	private Integer   pBallEnableCnt = 0;
	// グループ抽出済み
	private Boolean   pEnable = true;

	/**
	 *
	 * @param inGroupType
	 */
	public LotoGrp(int inGroupType){
		pGroupType = inGroupType;
		pGroupBalls = gGROUP[pGroupType];
		pBallDetail = new ArrayList<LotoBallBase>();
		pEnable = true;

		for( int i=0; i<pGroupBalls.length; i++){
			LotoBallBase ballTmp = new LotoBallBase(pGroupBalls[i]);
			ballTmp.setEnabled(false);
			pBallDetail.add(ballTmp);
		}
		pBallEnableCnt = 0;
	}

	/**
	 * 数字がグループに所属するかどうか確認
	 * @param inBallNumber
	 */
	public boolean searchBall(int inBallNumber){
		boolean rtnVal = false;
		if(Arrays.binarySearch(pGroupBalls, inBallNumber) >= 0){
			rtnVal = true;
		}
		return rtnVal;
	}

	/**
	 *
	 * @param inBallNumber
	 */
	public void setBallEnable(int inBallNumber, boolean inEnable){
		for( int i=0; i<pBallDetail.size(); i++ ){
			if( inBallNumber == pBallDetail.get(i).getNumber()){
				if( pBallDetail.get(i).getEnabled() != inEnable){
					if( inEnable ){
						pBallEnableCnt = pBallEnableCnt+1;
					}else{
						pBallEnableCnt = pBallEnableCnt-1;
					}
				}
				pBallDetail.get(i).setEnabled(inEnable);
				break;
			}
		}
	}

	/**
	 *
	 * @param inBallNumber
	 */
	public void setBallSelect(int inBallNumber, boolean inEnable){
		for( int i=0; i<pBallDetail.size(); i++ ){
			if( inBallNumber == pBallDetail.get(i).getNumber()){
				if( pBallDetail.get(i).isSelected() != inEnable){
					if( inEnable ){
						pSelectBallCnt = pSelectBallCnt+1;
					}else{
						pSelectBallCnt = pSelectBallCnt-1;
					}
				}
				pBallDetail.get(i).setSelected(inEnable);
				break;
			}
		}
	}

	public Integer getBallEnableCnt() {
		return pBallEnableCnt;
	}

	public void setBallEnableCnt(Integer pBallEnableCnt) {
		this.pBallEnableCnt = pBallEnableCnt;
	}

	public Integer getSelectBallCnt() {
		return pSelectBallCnt;
	}

	public void setSelectBallCnt(Integer pSelectBallCnt) {
		this.pSelectBallCnt = pSelectBallCnt;
	}

	public Boolean getEnable() {
		return pEnable;
	}

	public void setEnable(Boolean inEnable) {
		this.pEnable = inEnable;
	}

	public ArrayList<LotoBallBase> getGroupOkBalls() {
		ArrayList<LotoBallBase> rtnValList = new ArrayList<LotoBallBase>();

		for( int i=0; i < pBallDetail.size(); i++){
			LotoBallBase lotoBallTmp = pBallDetail.get(i);
			if( lotoBallTmp.getEnabled() && lotoBallTmp.isSelected() == false ){
				rtnValList.add(lotoBallTmp);
			}
		}
		return rtnValList;
	}

	/**
	 * 有効なグループ番号から１つのグループ番号をランダムで抽出する 
	 */
	public static Integer[][] getGrpFormat(Integer inGrpNum){

		Integer[][] grpFormat = null;

		switch(inGrpNum){
			case 1:
				grpFormat = LotoGrp.gGROUP1_BallCnt;
				break;
			case 2:
				grpFormat = LotoGrp.gGROUP2_BallCnt;
				break;
			case 3:
				grpFormat = LotoGrp.gGROUP3_BallCnt;
				break;
			case 4:
				grpFormat = LotoGrp.gGROUP4_BallCnt;
				break;
			case 5:
				grpFormat = LotoGrp.gGROUP5_BallCnt;
				break;
			case 6:
				grpFormat = LotoGrp.gGROUP6_BallCnt;
				break;
		}

		return grpFormat;
	}

}
