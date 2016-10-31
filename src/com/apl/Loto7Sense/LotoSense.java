package com.apl.Loto7Sense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.content.Context;

public class LotoSense {

	private Random gRand = null;
	private int senseType = 0;
	private static Integer SENSETYPE_OLDDAT = 0;
	private static Integer SENSETYPE_RANDOM = 1;

	/** ボールステータス */
	private Map<Integer,Integer> pBallStatus = new HashMap<Integer, Integer>();
	private static Integer DEF_STATUS_NORMAL  = 0;
	private static Integer DEF_STATUS_BUN5_01 = 1;
	private static Integer DEF_STATUS_BUN50_01 = 10;
	private int lotoType = 7; // TODO:20161006

	/** DBデータ */
	private Map<Integer, Integer> pGetDBDat1 = null;
	// 直近5回データ
	private Map<Integer, Integer> pGetDBDat5 = null;
	private Map<Integer, Integer> pGetDBDat10 = null;

	// 直近50回データ
	private Map<Integer, Integer> pGetDBDat50 = null;

	private Map<Integer, Integer> pGetDBDatAll = null;

	private ArrayList<LotoGrp>    pLotoGrpList = null;

	private String[] pHoldList = null;


	/** コンテキスト */
	public LotoSense(Context con, Random inRand, LotoSdLog inSdLog) {
		this.gRand = inRand;
	}

	/** DBデータ設定 */
	public void setGetDBDat1(Map<Integer, Integer> inGetDBDat){
		pGetDBDat1 = inGetDBDat;
	}

	/** DBデータ設定 */
	public void setGetDBDat5(Map<Integer, Integer> inGetDBDat){
		pGetDBDat5 = inGetDBDat;
	}

	/** DBデータ設定 */
	public void setGetDBDat10(Map<Integer, Integer> inGetDBDat){
		pGetDBDat10 = inGetDBDat;
	}

	/** DBデータ設定 */
	public void setGetDBDat50(Map<Integer, Integer> inGetDBDat){
		pGetDBDat50 = inGetDBDat;
	}

	/** DBデータ設定 */
	public void setGetDBDatAll(Map<Integer, Integer> inGetDBDat){
		pGetDBDatAll = inGetDBDat;
	}

	/**
	 * ボールステータス初期化
	 *
	 **/
	private void initStatus(){
		for( int i=0; i <= LotoBallList.getBallMAX(); i++){
			pBallStatus.put(i, DEF_STATUS_NORMAL);
		}
	}

	/**
	 * 固定数字
	 *
	 **/
	public void setHoldList(String[] inHoldList){
		pHoldList = inHoldList;
	}

	/** 直近5回分析総合 */
	private void bunseki1(){
		if(!pGetDBDat1.isEmpty()){
			// 直近5回中、3回以上当選番号になっている番号は対象外。
			for( int i = 1; i <= pGetDBDat1.size(); i++){
				Integer senceCnt = pGetDBDat1.get(i);
				if( senceCnt >= 1 ){
					pBallStatus.put(i, DEF_STATUS_BUN5_01);
				}
			}
		}
	}

	/** 直近5回分析総合 */
	private void bunseki5(){
		if(!pGetDBDat5.isEmpty()){
			// 直近5回中、3回以上当選番号になっている番号は対象外。
			for( int i = 1; i <= pGetDBDat5.size(); i++){
				Integer senceCnt = pGetDBDat5.get(i);
				if( senceCnt >= 2 ){
					pBallStatus.put(i, DEF_STATUS_BUN5_01);
				}
			}
		}
	}

	/** 直近5回分析総合 */
	private void bunseki10(){
		if(!pGetDBDat10.isEmpty()){
			// 直近5回中、3回以上当選番号になっている番号は対象外。
			for( int i = 1; i <= pGetDBDat10.size(); i++){
				Integer senceCnt = pGetDBDat10.get(i);
				if( senceCnt == 0 ){
					pBallStatus.put(i, DEF_STATUS_BUN5_01);
				}
			}
		}
	}

	/** 直近50回分析総合 */
	public void bunseki50(){
		if(!pGetDBDat50.isEmpty()){
			Integer intTmp = 0;
			// 直近50回中、当選番号が3回以下の番号を保持
			ArrayList<Integer> threeDownList = new ArrayList<Integer>();
			ArrayList<Integer> etcList = new ArrayList<Integer>();

			// 50回分の当選番号情報を元に、1～43の番号の中から抽選番号対象外の番号を決定する。
			for( int i = 1; i <= pGetDBDat50.size(); i++){

				Integer senceCnt = pGetDBDat50.get(i);

				// 直近50回中、11回以上当選番号になっている番号は対象外。
				if( senceCnt >= 13){
					pBallStatus.put(i, DEF_STATUS_BUN50_01);

				//} else	if( senceCnt <= 3 ){
				//		// 直近50回中、当選番号が3回以下の番号は、１つのみランダムの対象として他は対象外。
				//	threeDownList.add(i);
				//	pBallStatus.put(i, DEF_STATUS_BUN50_01);
				} else {
					etcList.add(i);
				}
			}

			// 3回以下の番号は、１つのみランダム
			Integer okNum;
			if ( threeDownList.size() > 0 ) {
				okNum = gRand.nextInt(threeDownList.size());
				intTmp =  threeDownList.get(okNum);
			} else {
				okNum = gRand.nextInt(etcList.size());
				intTmp =  etcList.get(okNum);
			}

			pBallStatus.put(intTmp, DEF_STATUS_NORMAL);
		}
	}


	/** 直近50回分析総合 */
	public void bunsekiAll(){
		if(!pGetDBDatAll.isEmpty()){
			Integer intTmp = 0;
			// 直近50回中、当選番号が3回以下の番号を保持
			ArrayList<Integer> threeDownList = new ArrayList<Integer>();
			ArrayList<Integer> etcList = new ArrayList<Integer>();

			List<Map.Entry> entries = new ArrayList<Map.Entry>(pGetDBDatAll.entrySet());
			Collections.sort(entries , new Comparator(){
				public int compare(Object o1, Object o2){
					Map.Entry e1 = (Map.Entry)o1;
					Map.Entry e2 = (Map.Entry)o2;
					return ((Integer)e1.getValue()).compareTo((Integer)e2.getValue());
				}
			});

			int entryCnt = 0;
			for(Map.Entry entry : entries){
				entryCnt = entryCnt + 1;
				if (entryCnt == 5){
					pBallStatus.put((Integer)entry.getKey(), DEF_STATUS_BUN50_01);
				} else	if (entryCnt == 10){
					pBallStatus.put((Integer)entry.getKey(), DEF_STATUS_BUN50_01);
				} else	if (entryCnt == 15){
					pBallStatus.put((Integer)entry.getKey(), DEF_STATUS_BUN50_01);
				} else	if (entryCnt == 20){
					pBallStatus.put((Integer)entry.getKey(), DEF_STATUS_BUN50_01);
				} else	if (entryCnt == 21){
					pBallStatus.put((Integer)entry.getKey(), DEF_STATUS_BUN50_01);
				} else	if (entryCnt == 30){
					pBallStatus.put((Integer)entry.getKey(), DEF_STATUS_BUN50_01);
				} else	if (entryCnt == 35){
					pBallStatus.put((Integer)entry.getKey(), DEF_STATUS_BUN50_01);
				}
			}
		}
	}

	/**
	 * 抽選番号の予想結果を、1セット(ボール6個)取得
	 * @param  Balls    ボールリスト
	 * @return ArrayList<Ball>  抽出したボールクラスリスト
	 * */
	/** 予想抽出 */
	public LotoBallSet getNumber(LotoBallList balls){

		LotoBallSet rtnBallSet = new LotoBallSet();
		// 予想結果保持リスト
		ArrayList<Integer> ballList = new ArrayList<Integer>();

		initStatus();

		if( senseType == SENSETYPE_OLDDAT){

			bunsekiAll();

			// 分析50
			bunseki50();

			bunseki10();

			// 分析5
			bunseki5();

			// 分析5
			bunseki1();

			pLotoGrpList = new ArrayList<LotoGrp>();

			// グループ単位のクラス作成
			for( int i = 0; i < LotoGrp.gGROUP.length; i++ ){
				LotoGrp lotoGrpTmp = new LotoGrp(i);
				pLotoGrpList.add(lotoGrpTmp);
			}

			// グループ単位のクラスに有効な数字を設定
			for( int i = 1; i < pBallStatus.size(); i++ ){
				if(pBallStatus.get(i) == DEF_STATUS_NORMAL){
					for( int ii = 0; ii < LotoGrp.gGROUP.length; ii++ ){
						LotoGrp lotoGrpTmp = pLotoGrpList.get(ii);
						if( lotoGrpTmp.searchBall(i) ){
							lotoGrpTmp.setBallEnable(i,true);
						}
					}
				}
			}

			// A～Gグループから、抽選番号を選択するグループ数(4 or 5)をランダムで決定する。
			Integer groupNum = 4 + gRand.nextInt(2);

			// グループフォーマット取得
			Integer[][] grpFormatList = LotoGrp.getGrpFormat(groupNum);
			Integer[] grpFormat = grpFormatList[gRand.nextInt(grpFormatList.length)];

			Boolean ErrFlg = false;

			ArrayList<Integer> grpTmp = new ArrayList<Integer>();

			Integer[] setFormat = new Integer[grpFormat.length];
			for( int i=0; i < grpFormat.length; i++){
				setFormat[i] = grpFormat[i];
			}

			// HOLD 処理
			if( pHoldList != null){
				for( int i=0 ; i<pHoldList.length ; i++){
					String  strBall = pHoldList[i];
					Integer intBall = Integer.valueOf(strBall).intValue();
					ballList.add(intBall);

					for( int ii = 0; ii < pLotoGrpList.size(); ii++ ){
						LotoGrp lotoGrpTmp = pLotoGrpList.get(ii);
						if( lotoGrpTmp.searchBall(intBall) ){
							lotoGrpTmp.setBallSelect(intBall, true);

							if( grpTmp.indexOf(ii) < 0){
								grpTmp.add(ii);
							}

							break;
						}
					}
				}
				int nowSelCnt = pHoldList.length;
				if( grpTmp.size() >= 3 && nowSelCnt >= 3){
					for( int i=0; i < grpFormat.length; i++){
						setFormat[i] = grpFormat[grpFormat.length - 1 - i];
					}
				}
			}

			//setFormat.length
			int formatCnt = 0;
			while(ballList.size() < lotoType){ // TODO:20161006
				LotoGrp lotoGrpTmp = null;

				// グループ
				if ( setFormat.length == formatCnt) {
					ErrFlg = true;
				} else {
					lotoGrpTmp = getOkGroup(pLotoGrpList,setFormat[formatCnt]);
				}

				if( lotoGrpTmp == null){
					// 対象グループが存在しない場合
					ErrFlg = true;
				}else{
					// 1グループ当たりの抽出ボール
					for( int ballCnt=lotoGrpTmp.getSelectBallCnt(); ballCnt < setFormat[formatCnt]; ballCnt++){

						if( ballList.size() < lotoType){ // TODO:20161006
							LotoBallBase lotoBallBaseTmp = getOneBall(lotoGrpTmp);
							if( lotoBallBaseTmp != null){
								ballList.add(lotoBallBaseTmp.getNumber());
								// グループの数字選択フラグをＯＮ
								lotoGrpTmp.setBallSelect(lotoBallBaseTmp.getNumber(), true);
								// グループの有効数字フラグをＯＦＦ
								lotoGrpTmp.setBallEnable(lotoBallBaseTmp.getNumber(), false);
							}else{
								// 対象グループが存在しない場合
								ErrFlg = true;
								break;
							}
						}else{
							break;
						}

					}
					formatCnt = formatCnt + 1;
				}

				if( ErrFlg ){
					break;
				}
			}

			if( ErrFlg ){
				ArrayList<Integer> etcNumber = new ArrayList<Integer>();
				for( int i = 1; i < pBallStatus.size(); i++ ){
					if( ballList.indexOf(i) == -1 ){
						etcNumber.add(i);
					}
				}

				for(int i = ballList.size() + 1; i <= lotoType; i++){// TODO:20161006
					Integer idx = gRand.nextInt(etcNumber.size());
					ballList.add(etcNumber.get(idx));
					etcNumber.remove(etcNumber.indexOf(etcNumber.get(idx)));
				}
			}

		}else if(senseType == SENSETYPE_RANDOM){

			// HOLD 処理
			if( pHoldList != null){
				for( int i=0 ; i<pHoldList.length ; i++){
					String  strBall = pHoldList[i];
					Integer intBall = Integer.valueOf(strBall).intValue();
					ballList.add(intBall);
				}
			}

			ArrayList<Integer> etcNumber = new ArrayList<Integer>();
			for( int i = 1; i < pBallStatus.size(); i++ ){
				if( ballList.indexOf(i) == -1 ){
					etcNumber.add(i);
				}
			}

			for(int i = ballList.size() + 1; i <= lotoType; i++){// TODO:20161006
				Integer idx = gRand.nextInt(etcNumber.size());
				ballList.add(etcNumber.get(idx));
				etcNumber.remove(etcNumber.indexOf(etcNumber.get(idx)));
			}
		}

		// sort
		ballList = funSort(ballList);

		// 登録
		for( int i = 0; i < ballList.size(); i++ ){
			LotoBall tmpBall = balls.getBall(ballList.get(i));
			rtnBallSet.setBall(tmpBall);
		}

		return rtnBallSet;
	}

	/** 有効なグループ番号から１つのグループ番号をランダムで抽出する */
	private LotoGrp getOkGroup(ArrayList<LotoGrp> inLotoGrpList, Integer inBallOkCnt){

		LotoGrp lotoGrpRtn = null;

		ArrayList<LotoGrp> tmpGrpList = new ArrayList<LotoGrp>();

		// 有効なグループを抽出する。
		for( int i = 0; i < inLotoGrpList.size(); i++ ){

			LotoGrp lotoGrpTmp = inLotoGrpList.get(i);

			if( lotoGrpTmp.getEnable()){
				if( lotoGrpTmp.getBallEnableCnt() >= inBallOkCnt){
					tmpGrpList.add(lotoGrpTmp);
				}
			}
		}

		if(tmpGrpList.size() > 0){

			// グループ選択
			Integer intTmp = gRand.nextInt(tmpGrpList.size());
			lotoGrpRtn = tmpGrpList.get(intTmp);
			lotoGrpRtn.setEnable(false);
		}

		return lotoGrpRtn;
	}

	/**
	 * ボール番号取得
	 * @param  inGrpIdx グループ番号
	 * @return Integer  抽出したボール番号
	 * */
	private LotoBallBase getOneBall(LotoGrp inLotoGrp){

		ArrayList<LotoBallBase> ballBaseListTmp = inLotoGrp.getGroupOkBalls();
		LotoBallBase ballBaseTmp = null;

		if( ballBaseListTmp.size() > 0 ){
			Integer intTmp = gRand.nextInt(ballBaseListTmp.size());
			ballBaseTmp = ballBaseListTmp.get(intTmp);
		}

		return ballBaseTmp;
	}

	/** 配列ソート */
	private ArrayList<Integer> funSort(ArrayList<Integer> inBallList){
		Collections.sort(inBallList, new Comparator<Integer>() {
			public int compare(Integer tc1, Integer tc2) {
				if (tc1 < tc2) {
					return -1;
				} else if (tc1 >= tc2) {
					return 1;
				}
				return 0;
			}
		});
		return inBallList;
	}

	public void setSenseType(int inSenseType){
		senseType = inSenseType;
	}
}
