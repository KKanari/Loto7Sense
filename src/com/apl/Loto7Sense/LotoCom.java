package com.apl.Loto7Sense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LotoCom {
	/**
	 * 開始日時から終了日時までの経過日数を取得します。
	 * @param start 開始日時
	 * @param end 終了日時
	 * @return 開始日時から終了日時までの経過日数
	 */
	public static int getDays(Date start, Date end) {

		Calendar calStart = Calendar.getInstance();
		calStart.setTime(start);
		long lStart = (calStart.getTime()).getTime();

		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(end);
		long lEnd = (calEnd.getTime()).getTime();

		long result = (lEnd - lStart) / (1000*60*60*24);
		return new Long(result).intValue();
	}

	/**
	 *
	 * @param inDate
	 * @return
	 */
	public static String strFromDate(Date inDate) {
		SimpleDateFormat sdfTmp = new SimpleDateFormat("yyyy/MM/dd");
		return sdfTmp.format(inDate);
	}

	/**
	 *
	 * @param inDate
	 * @return
	 */
	public static Date dateFromStr(String inDat) {
		Date rtnDat = null;
		try {
			SimpleDateFormat sdfTmp = new SimpleDateFormat("yyyy/MM/dd");
			rtnDat = sdfTmp.parse(inDat);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return rtnDat;
	}

	/**
	 * 数値チェック
	 * double に変換でききない文字列が渡された場合は false を返します。
	 * @param str チェック文字列
	 * @return 引数の文字列が数値である場合 true を返す。
	 */
	public static boolean isNumber(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 年月日取得
	 */
	public static String getYYYYMM(){

		Calendar cal1 = Calendar.getInstance();    // オブジェクトの生成
		int year = cal1.get(Calendar.YEAR);        // 現在の年を取得
		int month = cal1.get(Calendar.MONTH) + 1;  // 現在の月を取得
		int day = cal1.get(Calendar.DATE);         // 現在の日を取得

		StringBuffer dow = new StringBuffer();
		dow.append(String.valueOf(year) + "/" + String.valueOf(month)+ "/" + String.valueOf(day));

		dow.append("(");
		switch (cal1.get(Calendar.DAY_OF_WEEK)) {  // 現在の曜日を取得
			case Calendar.SUNDAY:
				dow.append("Sun");
				break;
			case Calendar.MONDAY:
				dow.append("Mon");
				break;
			case Calendar.TUESDAY:
				dow.append("Tue");
				break;
			case Calendar.WEDNESDAY:
				dow.append("Wed");
				break;
			case Calendar.THURSDAY:
				dow.append("Thu");
				break;
			case Calendar.FRIDAY:
				dow.append("Fri");
				break;
			case Calendar.SATURDAY:
				dow.append("Sat");
				break;
		}
		dow.append(")");

		return dow.toString();
	}
}
