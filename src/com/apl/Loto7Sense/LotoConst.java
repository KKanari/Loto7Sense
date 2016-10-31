package com.apl.Loto7Sense;

import android.view.ViewGroup;

public class LotoConst {

	public static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	public static int FP = ViewGroup.LayoutParams.MATCH_PARENT;

	public static String KAIGYOU = "\\r\\n";
	public static String APP_MODE_DEBUG = "debug";
	public static String APP_MODE_FREE = "free";
	public static String APP_MODE_PAY = "pay";
	public static String APP_MODE_HIT = "hit";

	// 設定ファイル項目
	public static String INI_KEY_SENSE_CNT   = "list_cnt";
	public static String INI_KEY_REPEAT_CNT   = "list_rep";
	public static String INI_KEY_ANIME_SPEED = "list_speed";
	public static String INI_KEY_SENSE_TYPE  = "list_sense";
	public static String INI_KEY_HOLD_NUMBER = "hold_ball";
	public static String INI_KEY_DB_UPDATE   = "update_date";

	public static int RTN_CODE_SUCCESS = 0;
	public static int RTN_CODE_ERROR   = 100;
	public static int RTN_CODE_WARNING = 50;
}
