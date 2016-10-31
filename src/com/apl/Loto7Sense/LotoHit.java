package com.apl.Loto7Sense;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class LotoHit {
	/** 定数 */
	private static Integer NUMBER_OF_LOTO6 = 43;
	private static Integer RANKING_THRESHOLD = 5;
	private static String KAIGYOU = "\r\n";
	private static String FILE_PATH = "/data/data/com.apl.Loto7Sense/Loto7Results.txt";

	/** 変数 */
	String[][] dispData;

	/**
	 * 重複組取得処理
	 * @param
	 * @return
	 **/
	public ArrayList<String> lotoHit(LotoTmpDB tdb){
		ArrayList<String> outputGroup = new ArrayList<String>();
		String[] overlapNum = {"3",""};
		int[] groupScore = new int[5];
		try{
			// 一時保存用テーブル中の重複している組を重複用テーブルに保存
			tdb.insertTmp2();

			// 3回以上重複している組の件数を取得する
			overlapNum[1] = tdb.maxOverlap(overlapNum[0]);
		}catch(Exception e){
			return outputGroup;
		}

		// 3回以上重複した組が無かった場合は処理を終了する
		if (overlapNum[1].equals("")) {
			return outputGroup;
		}

		// 出力候補となる組を取得
		String[][] overlapGroup = tdb.overlapGroupGet(overlapNum[0], overlapNum[1]);

		// 出力候補の組数が5組より多かった場合のみランキング処理を実行する
		if(Integer.parseInt(overlapNum[1]) > RANKING_THRESHOLD){
			int[] numScore = new int[NUMBER_OF_LOTO6 + 1];

			// 各番号のスコアを取得する
			for(int iii=1; iii <= NUMBER_OF_LOTO6; iii++){
				numScore[iii] = tdb.numScoreGet(((iii < 10) ? "0" : "") + iii);
			}

			// 各組のスコアを算出する
			groupScore = new int[overlapGroup.length];
			for(int iii = 0; iii < overlapGroup.length; iii++){
				String[] str = overlapGroup[iii][0].split(",");
				int score = 0;
				for(int lll = 0; lll < str.length; lll++){
					score += numScore[Integer.parseInt(str[lll])];
				}
				groupScore[iii] = score;
			}

			// TreeSetを降順ソートするためのコンパレータ
			java.util.Comparator<Integer> comp = new java.util.Comparator<Integer>(){
				public int compare(Integer i1, Integer i2) {
					return i1.compareTo(i2)*-1;
				}
			};

			// 重複を削除し降順にソートする
			java.util.TreeSet<Integer> scoreRank = new java.util.TreeSet<Integer>(comp);
			for(int iii = 0; iii < groupScore.length; iii++){
				scoreRank.add(groupScore[iii]);
			}

			// 出力する組を取得
			java.util.Iterator<Integer> ite = scoreRank.iterator();
			int num;
			while (ite.hasNext()) {
				num = ite.next();
				for(int iii = 0; iii < overlapGroup.length; iii++){
					if(groupScore[iii] == num){
						outputGroup.add(overlapGroup[iii][0]);
					}
				}

				// 5件以上になったら取得処理を終了
				if(outputGroup.size() >= 5){
					break;
				}
			}
		}else{
			// 出力する組を取得
			for(int iii = 0; iii < overlapGroup.length; iii++){
				outputGroup.add(overlapGroup[iii][0]);
			}
		}

		// ファイル出力
		String fileStr = "";
		for(int lll=0; lll < outputGroup.size(); lll++){
			fileStr += outputGroup.get(lll) + KAIGYOU;
		}

		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH, false)));
			bw.write(fileStr);
			bw.close();
		} catch (IOException e) {
		}

		// ダイアログ表示用のデータを返すためにデータを加工
		dispData = new String[outputGroup.size()][3];
		for(int iii = 0; iii < outputGroup.size(); iii++ ){
			for(int lll = 0; lll < overlapGroup.length; lll++){
				if(outputGroup.get(iii).equals(overlapGroup[lll][0])){
					dispData[iii][0] = overlapGroup[lll][0];
					dispData[iii][1] = overlapGroup[lll][1];
					dispData[iii][2] = String.valueOf(groupScore[lll]);
				}
			}

		}

		return outputGroup;
	}

	/**
	 * ボール表示用のリストへ登録する処理
	 * @param
	 * @return
	 **/
	public ArrayList<LotoBallSet> setBallList(ArrayList<String> tmpGroup, LotoBallList balls){
		ArrayList<LotoBallSet> tmpBallList = new ArrayList<LotoBallSet>();
		for( int i = 0; i < tmpGroup.size(); i++ ){
			LotoBallSet aryTmp = new LotoBallSet();
			String num[] = tmpGroup.get(i).split(",");
			for(int lll=0; lll < num.length; lll++){
				LotoBall tmpBall = balls.getBall(Integer.parseInt(num[lll]));
				aryTmp.setBall(tmpBall);
			}
			// ボール表示用の番号のリストに追加
			tmpBallList.add(aryTmp);
		}

		return tmpBallList;

	}

	/**
	 * ダイアログ表示用のデータを渡す
	 * @return
	 **/
	public String[][] getDispData(){
		return dispData;
	}
}
