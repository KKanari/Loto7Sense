package com.apl.Loto7Sense;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class LotoTmpDB extends SQLiteOpenHelper{

	SQLiteDatabase db = null;
	public LotoTmpDB(Context context) {
		super(context, "TmpDB", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.beginTransaction();
		try {
			db.execSQL("create table tmptbl "
					+   "(num  string not null);");

			db.execSQL("create table tmptbl2 "
					+   "(num  string,"
					+	"cnt number);");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * tmptblテーブルのインデックスを作成する。
	 * @param num
	 */
	public void createIdx(){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("CREATE INDEX tmp_idx ON tmptbl (num);");

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	/**
	 * tmptblテーブルに対してのINSERTを行う。
	 * @param
	 */
	public void insertDB2(int kai, int hyo, LotoSense mSense,LotoBallList mBalls){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		// 繰り返し回数が合計10万回を超える場合はインデックスを作成する
		if((kai*hyo) > 100000){
			createIdx();
		}

		try {
			SQLiteStatement stmt = db.compileStatement("insert into tmptbl values (?);");

			for(int jjj=0; jjj < kai; jjj++){
				for( int i=0; i < hyo; i++){
					// 抽選番号予想処理開始
					LotoBallSet aryTmp = mSense.getNumber(mBalls);

					// 予想番号をカンマ区切りに変更
					StringBuilder sb = new StringBuilder();
					for(int lll=0; lll < aryTmp.getSize(); lll++){
						LotoBall ball = aryTmp.getBall(lll);
						int val = ball.getNumber();
						if(val < 10){
							sb.append("0").append(val).append(",");
						}else{
							sb.append(val).append(",");
						}
					}

					// 一時保存用テーブルに予想番号を保存
					sb.delete(sb.length()-1, sb.length());
					stmt.bindString(1, sb.toString());
					stmt.executeInsert();

				}
			}

			db.setTransactionSuccessful();

		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 2回以上重複した組を入れておくテーブルを作成
	 * */
	public void createTmp2() {
		final SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("create table tmptbl2 "
					+   "(num  string not null,"
					+	"cnt number not null);");

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 2回以上重複した組をtmptbl2にINSERTする
	 * */
	public void insertTmp2() {
		// DB
		final SQLiteDatabase db = getReadableDatabase();

		db.beginTransaction();
		try {
			db.execSQL("INSERT INTO tmptbl2 select num, count(num) as cnt  from tmptbl"
					+ " group by num"
					+ " having cnt >= 2;");

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 指定された回数以上の重複回数の件数が何件なのかを返す
	 * @return
	 */
	public String maxOverlap(String num){
		SQLiteDatabase db = getWritableDatabase();
		String sql = "select count(cnt) as kai from tmptbl2 where cnt >= " + num;
		Cursor c = db.rawQuery(sql, null);
		String rtnDat = "";
		try {
			if(c.moveToFirst()){
				for (int i = 0; i < c.getCount(); i++) {
					rtnDat = c.getString(0);
				}
			}else{
				return rtnDat;
			}
		}catch(Exception e){

		}finally {
			c.close();
			db.close();
		}

		return rtnDat;
	}

	/**
	 * 指定回数以上の重複回数の組を取得する
	 * @param
	 * @return
	 */
	public String[][] overlapGroupGet(String num, String kensuu){
		SQLiteDatabase db = getWritableDatabase();
		String sql = "select num, cnt from tmptbl2 where cnt >= " + num;
		Cursor c = db.rawQuery(sql, null);
		String[][] rtnDat = new String[Integer.parseInt(kensuu)][2];
		try {
			c.moveToFirst();
			for (int i = 0; i < c.getCount(); i++) {
				rtnDat[i][0] = c.getString(0);
				rtnDat[i][1] = c.getString(1);
				c.moveToNext();
			}
		} finally {
			c.close();
			db.close();
		}

		return rtnDat;
	}

	/**
	 * 番号のスコアを返す
	 * @param num
	 * @return
	 */
	public int numScoreGet(String num){
		SQLiteDatabase db = getWritableDatabase();
		String sql = "select count(num) from tmptbl2 where num like '%" + num + "%'";
		Cursor c = db.rawQuery(sql, null);
		String rtnDat;
		try {
			c.moveToFirst();
			rtnDat = c.getString(0);
		} finally {
			c.close();
			db.close();
		}

		return Integer.parseInt(rtnDat);
	}

	/**
	 * テーブルの削除
	 * */
	public void droptbl() {
		SQLiteDatabase db = getReadableDatabase();

		try {
			String sql = "drop table if exists tmptbl";
			db.execSQL(sql);
			sql = "drop table if exists tmptbl2";
			db.execSQL(sql);
			onCreate(db);
		} catch (Exception e) {

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}