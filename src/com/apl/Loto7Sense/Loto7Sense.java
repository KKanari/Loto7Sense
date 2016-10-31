package com.apl.Loto7Sense;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDiskIOException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Loto7Sense extends Activity {

	/** view ID */
	private final int ID_LAYOUT_MAIN = 1;	//Main layout
	private final int ID_BTN_START   = 2;	// Start button
	private final int ID_BTN_CLEAR   = 3;	// Clear button
	private final int ID_VIEW_BALL   = 4;	// View
	private final int ID_BTN_SET     = 5;	// Set
	private final int ID_LAYOUT_BTN  = 6;	// Btn layout

	/** メニュー */
    private final int MENU_ID_HOLD    = Menu.FIRST;
    private final int MENU_ID_SETTING = Menu.FIRST + 1;
	private final int MENU_ID_SELECT  = Menu.FIRST + 2;

    /** 警告メッセージ */
    private StringBuffer mWarningMsg = new StringBuffer("");

    /** エラーメッセージ */
    private StringBuffer mErrorMsg   = new StringBuffer("");

	/** Startボタン画像 */
	private static Drawable mNormalBack; 	//通常時
	private static Drawable mPushBack; 		//押下時

	/** タイマー用 */
	private RedrawHandler mHandler = null;

    /** 共通情報 */
    private LotoBallList mBalls = null;

    private ArrayList<LotoBallSet> mSenceBalls = null;

    /** resources */
	private Resources mResources = null;

	/** プロパティファイル */
	private LotoProperty mProperty = null;

	/** 予想計算処理 */
	private LotoSense mSense = null;

    /** Log出力処理 */
	private LotoSdLog mSdLog = null;

    /** Debug処理 */
	private LotoDebug mDebug = null;

    /** DB */
	private LotoCtrDB mCtrDB = null;

	/** アプリモード */
	private static String appMode = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

	    try{
	        // ---------------------
	        // 初期処理
	        // ---------------------

	        // SDへのログ出力設定
	        //    debug = ログ出力  etc = ログ出力しない
	        if( getString(R.string.app_mode).equals(LotoConst.APP_MODE_DEBUG)){
	        	mSdLog = new LotoSdLog(true);
	        }else{
	        	mSdLog = new LotoSdLog(false);
	        }

	        // Debug用
	        mDebug = new LotoDebug(mSdLog);

	        // アプリモード設定
	        appMode = getString(R.string.app_mode);

	        // ---------------------
	        // アプリタイトルの設定
	        // ---------------------

	     	// パッケージの値参照
	     	PackageManager pm = getPackageManager();
	     	PackageInfo info = null;

	     	// パッケージ名と参照したい属性の指定
	     	info = pm.getPackageInfo("com.apl.Loto7Sense", 0);

	     	// タイトルバーをカスタマイズする
			//   Window.FEATURE_NO_TITLE	タイトルバーを非表示にする
			//   Window.FEATURE_CUSTOM_TITLE	任意のレイアウトファイルを設定する
			//   Window.FEATURE_LEFT_ICON	アイコン画像を付与する
			//   Window.FEATURE_RIGHT_ICON	アイコン画像を付与する
			//   setContentViewより前にWindowにActionBar表示を設定
	        requestWindowFeature(Window.FEATURE_ACTION_BAR);

			// 部品を配置
	        setContentView(R.layout.custom_layout);

			// タイトルバーのレイアウトを XML で定義
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_layout);

	        //final TextView rightText = (TextView) findViewById(R.id.right_text);
	        //rightText.setTextColor(Color.WHITE);
	        //rightText.setTypeface(Typeface.DEFAULT_BOLD);
	        //rightText.setText("v"+info.versionName);

	        //final TextView leftText = (TextView) findViewById(R.id.left_text);
	        //leftText.setTextColor(Color.WHITE);
	        //leftText.setTypeface(Typeface.DEFAULT_BOLD);

	        // ---------------------
	        // レイアウト作成処理
	        // ---------------------

	        // main
	        LinearLayout layoutMainTop = new LinearLayout(this);
			layoutMainTop.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
	        setContentView(layoutMainTop);

	        // scroll
	        ScrollView scrollv = new ScrollView(getApplication());
	        scrollv.setBackgroundColor(Color.BLACK);
	        scrollv.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
	        layoutMainTop.addView(scrollv);

	        // メインレイアウト
	        LinearLayout layoutMain = new LinearLayout(this);
	        layoutMain.setId(ID_LAYOUT_MAIN);
	        layoutMain.setOrientation(LinearLayout.VERTICAL);
	        layoutMain.setGravity(Gravity.CENTER | Gravity.TOP);
	        layoutMain.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
	        scrollv.addView(layoutMain);

	        // 日付表示ラベル
	        TextView tvToday = new TextView(this);
	        tvToday.setText(LotoCom.getYYYYMM());
	        tvToday.setTextSize(16);
			tvToday.setTextColor(Color.WHITE);
	        tvToday.setTypeface(Typeface.DEFAULT_BOLD);
	        layoutMain.addView(tvToday);

	        // 説明ラベル
	        //TextView tvInfo = new TextView(this);
	        //tvInfo.setText(R.string.lbl_msg01);
	        //tvInfo.setTextSize(20);
	        //tvInfo.setGravity(Gravity.CENTER);
	        //tvInfo.setTypeface(Typeface.DEFAULT_BOLD);
	        //layoutMain.addView(tvInfo);

			// ボタンレイアウト
			LinearLayout layoutBtn = new LinearLayout(this);
			layoutBtn.setId(ID_LAYOUT_BTN);
			layoutBtn.setOrientation(LinearLayout.VERTICAL);
			layoutBtn.setGravity(Gravity.CENTER | Gravity.TOP);
			layoutBtn.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
			layoutBtn.setMinimumHeight(150);
			layoutMain.addView(layoutBtn);

			// ボタンレイアウト
			LinearLayout layoutBtnSub1 = new LinearLayout(this);
			layoutBtnSub1.setId(ID_LAYOUT_BTN);
			layoutBtnSub1.setOrientation(LinearLayout.HORIZONTAL);
			layoutBtnSub1.setGravity(Gravity.CENTER | Gravity.TOP);
			layoutBtnSub1.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
			layoutBtn.addView(layoutBtnSub1);

	        // スタートボタン,
	        ImageButton imgBtnStart = new ImageButton(this);
	    	mResources = getResources();

			mNormalBack = mResources.getDrawable(R.drawable.startbtn,null);
			//mNormalBack = mResources.getDrawable(R.drawable.startbtn);

			imgBtnStart.setBackground(mNormalBack);
			imgBtnStart.setId(ID_BTN_START);
			layoutBtnSub1.addView(imgBtnStart,new LayoutParams(LotoConst.WC, LotoConst.WC));

	        // リスナー作成
	        ClickListener clickStartBtn = new ClickListener();
	        imgBtnStart.setOnClickListener(clickStartBtn);
	        TouchListenerStart touchStartBtn = new TouchListenerStart();
	        imgBtnStart.setOnTouchListener(touchStartBtn);

			// 設定ボタン
			ImageButton imgBtnSet = new ImageButton(this);
			Drawable drawSet = mResources.getDrawable(R.drawable.setbtn,null);
			imgBtnSet.setBackground(drawSet);
			imgBtnSet.setVisibility(View.GONE);
			imgBtnSet.setId(ID_BTN_SET);
			layoutBtnSub1.addView(imgBtnSet,new LayoutParams(LotoConst.WC, LotoConst.WC));

			// リスナー作成
			TouchListenerSet touchSetBtn = new TouchListenerSet();
			imgBtnSet.setOnTouchListener(touchSetBtn);

	        // クリアー ボタン
	        ImageButton imgBtnClear = new ImageButton(this);
	    	mResources = getResources();
	    	Drawable drawClear = mResources.getDrawable(R.drawable.clearbtn, null);
	    	imgBtnClear.setBackground(drawClear);
	    	imgBtnClear.setVisibility(View.GONE);
	    	imgBtnClear.setId(ID_BTN_CLEAR);
			layoutBtnSub1.addView(imgBtnClear,new LayoutParams(LotoConst.WC, LotoConst.WC));

	        // リスナー作成
	        ClickListenerClear clickClear = new ClickListenerClear();
	        imgBtnClear.setOnClickListener(clickClear);
	        TouchListenerClear touchClear = new TouchListenerClear();
	        imgBtnClear.setOnTouchListener(touchClear);

	        // ---------------------
	        // DB処理
	        // ---------------------

	        // DB設定
	        mCtrDB = new LotoCtrDB(getApplication());

	        // ---------------------
	        // HTTP通信
	        // ---------------------

	        httpMain();

	        // ---------------------
	        // 計算処理クラス準備
	        // ---------------------

	        mSense = new LotoSense(getApplication(),new Random(new Random().nextInt()),mSdLog);
			mSense.setGetDBDat1(mCtrDB.getDat1());
	        mSense.setGetDBDat5(mCtrDB.getDat5());
			mSense.setGetDBDat10(mCtrDB.getDat10());
	        mSense.setGetDBDat50(mCtrDB.getDat50());
			mSense.setGetDBDatAll(mCtrDB.getDatALL());

	        // ---------------------
	        // 描画クラスのインスタンスを生成
	        // ---------------------

	        mBalls = new LotoBallList(this);
	        LotoBallView mView = new LotoBallView( getApplication(),mBalls);
	        mView.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
	        mView.setId(ID_VIEW_BALL);
	        layoutMain.addView(mView);

			// リスナー作成
			TouchListenerBalls touchBalls = new TouchListenerBalls();
			mView.setOnTouchListener(touchBalls);

	        // ---------------------
	        // 前回表示数字表示処理
	        // ---------------------

	        // タイマー準備
	        mHandler = new RedrawHandler(mView, Integer.valueOf(getString(R.string.dat_timer)).intValue());

	        // 前回表示数字取得
	        ArrayList<String[]> getTmpList = mProperty.loadLastViewBallSetList();

	        if( getTmpList.size() > 0){

	        	// 前回表示していた数字が存在する場合
		        ArrayList<LotoBallSet> tmpBallSetList = new ArrayList<LotoBallSet>();

	        	// 前回表示していた数字を表示する
		        imgBtnStart.setVisibility(View.VISIBLE);

		        // Clearボタン表示
		        imgBtnClear.setVisibility(View.VISIBLE);

				// Setボタン表示
				//imgBtnSet.setVisibility(View.VISIBLE);

		        for(int i=0; i<getTmpList.size(); i++){
		        	String[] getLine = getTmpList.get(i);
		        	LotoBallSet tmpBallSet = new LotoBallSet();
		        	for(int ii=0; ii<getLine.length; ii++){
		        		int tmpBallNum = Integer.valueOf(getLine[ii]).intValue();
		        		LotoBall tmpBall = new LotoBall(this, tmpBallNum, 5);
		        		tmpBallSet.setBall(tmpBall);
		        	}
		        	tmpBallSetList.add(tmpBallSet);
		        }

		        mView.setSenceBalls(tmpBallSetList,256);
		        mView.setAnimationFlg(false);
		        mView.start();
		        mHandler.start();
	        }

	    }catch(SQLiteDiskIOException e){
	    	System.out.println(e.getStackTrace());
	    	mErrorMsg.append(getString(R.string.msg_error_database));
        }catch(Exception e){
	    	System.out.println(e.getStackTrace());
	    	mErrorMsg.append(getString(R.string.msg_error_system));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

    	System.out.println(keyCode);

    	//if( keyCode == 4 ){
        //    // 取得
		//LotoBallView tmpView = (LotoBallView)findViewById(ID_VIEW_BALL);
        //    ArrayList<LotoBallSet> getBalls = tmpView.getSenceBalls();
        //    mProperty.saveLastViewBallSetList(getBalls);
    	//}
    	return super.onKeyDown(keyCode, event);
    }

    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();

        // エラー処理
        if( mErrorMsg.length() > 0 ){
        	dialogMsg(LotoConst.RTN_CODE_ERROR,mErrorMsg.toString());
	    }else{

		    try{
		        // ---------------------
		        // プロパティファイル読み込み
		        // ---------------------

		    	// 新規に読み直す
		        mProperty = new LotoProperty(this);

		        // HOLD数字
			    if( mProperty.getHoldList() != null){
			        mSense.setHoldList(mProperty.getHoldList());
			    }else{
			    	mSense.setHoldList(null);
			    }

			    // 計算方法
		        mSense.setSenseType(mProperty.getSenseType());

		        // 表示数
		        LotoBallView tmpView = (LotoBallView)findViewById(ID_VIEW_BALL);
		        tmpView.setLineCnt(mProperty.getViewCnt());

		        // ボール表示領域のサイズ調整
		        tmpView.setLayoutParams(
		        		new LinearLayout.LayoutParams(LotoConst.FP, 15+ mProperty.getViewCnt() * 150));

		        // 表示スピード
		        mHandler.setSleep((this.getResources().getStringArray(
		        		R.array.loto_speed).length - mProperty.getViewSpeed()) * 1);

				LotoBallView selView = (LotoBallView)findViewById(ID_VIEW_BALL);
				selView.invalidate();

		    }catch(Exception e){
		    	System.out.println(e.getStackTrace());
		    	mErrorMsg.append(getString(R.string.msg_error_system));
		    }

	        // エラー処理
	        if( mErrorMsg.length() > 0 ){
	        	dialogMsg(LotoConst.RTN_CODE_ERROR,mErrorMsg.toString());
	        	mErrorMsg.setLength(0);
	        }else if( mWarningMsg.length() > 0 ){
	        	dialogMsg(LotoConst.RTN_CODE_WARNING,mWarningMsg.toString());
	        	mWarningMsg.setLength(0);
	        }
	    }
    }

    /**
     * ダイアログボックスを表示
     */
    private void dialogMsg(int inMsgType,String inMsg){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        if( inMsgType == LotoConst.RTN_CODE_ERROR ){
    	    // error
    	    alertDialogBuilder.setIcon( android.R.drawable.ic_delete);
            alertDialogBuilder.setTitle("system error");

        	// OK時に処理を終了します。
	        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		       	@Override
		       	public void onClick(DialogInterface dialog, int which) {
		       		finish();
		       		}
	       	});

        }else if( inMsgType == LotoConst.RTN_CODE_WARNING ){
    	    // warning
			alertDialogBuilder.setIcon( android.R.drawable.ic_menu_info_details);
			alertDialogBuilder.setTitle("warning");

			// OK時に処理を継続します。
			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		}

        alertDialogBuilder.setMessage(inMsg);
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * 重複版結果表示用ダイアログボックスを表示
     */
    public void hitDialog(String inMsg){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("重複組取得処理結果");

    	// OK時に処理を継続します。
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	       	@Override
	       	public void onClick(DialogInterface dialog, int which) {
	       		}
       	});

        alertDialogBuilder.setMessage(inMsg);
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * @throws Exception
     *
     */
    private void httpSub(LotoCtrDB inCtrDB) throws Exception{

        // HTTP通信でデータを取得
        String wkURL= getString(R.string.url_file);
        String[][] wkHttpDatSp = null;
        try{
            String wkHttpDat = LotoHttp.http2str(this,wkURL);

            if( wkHttpDat.length() == 0 ){
            	throw new LotoException(LotoException.HTTPFILE_SIZE0);
            }

            String[] wkHttpDatSp1 = wkHttpDat.split(LotoConst.KAIGYOU);
            wkHttpDat = wkHttpDat.replaceAll(LotoConst.KAIGYOU, ",");

            int wkNowMax = inCtrDB.getNewCount();
            if( wkHttpDatSp1.length - wkNowMax > 0){
	            wkHttpDatSp = new String[wkHttpDatSp1.length - wkNowMax][];
	            int wkDatSpCnt = 0;
	            for( int i=wkNowMax; i < wkHttpDatSp1.length; i++){
	            	if( wkHttpDatSp1[i].length() > 0){
	            		wkHttpDatSp[wkDatSpCnt] = wkHttpDatSp1[i].split(",");
	            		wkDatSpCnt++;
	            	}
	            }
            }

            /**
             * String[] → ArrayList変換
             * String[] array=(String[])list.toArray(new String[0]);
             * String str[] 	= {"a","b","c","d","e","f","g","h"};
             * ArrayList list 	= new ArrayList(Arrays.asList(str));
             */

            inCtrDB.insertDB(wkHttpDatSp);

        }catch(Exception e){
        	throw new FileNotFoundException();
        	//throw e;
        }
    }

    /**
     *
     */
    private void httpMain() throws Exception{

        // 抽選番号更新処理日取得
        Date wkToDat = new Date();
        Date wkFromDat = null;

        // プロパティファイル読み込み
        // ここのみ使用
        mProperty = new LotoProperty(this);
        wkFromDat = LotoCom.dateFromStr(mProperty.getUpdateDate());

		if( httpChk(wkFromDat,wkToDat) ){

			// HTTP関連のエラー時は、処理を継続する。
			try{
				httpSub(mCtrDB);
				mProperty.setUpdateDate(LotoCom.strFromDate(wkToDat));
	            mProperty.save();

			}catch(FileNotFoundException e){
				// URLのファイル無しエラー
				mWarningMsg.append(getString(R.string.msg_warning_nofile));

			}catch(UnknownHostException e){
				// サーバ接続エラー
				mWarningMsg.append(getString(R.string.msg_warning_nofile));

			}catch(NumberFormatException e){
				// 不正データ　文字列データが存在する
				mWarningMsg.append(getString(R.string.msg_warning_nofile));

			}catch(LotoException e){

				if( e.subExceptionNum == LotoException.HTTPFILE_NOITEMCNT ){
					// 不正データ HTTPファイルの項目数が異常
					mWarningMsg.append(getString(R.string.msg_warning_nofile));

				}else if( e.subExceptionNum == LotoException.HTTPFILE_SIZE0 ){
					// 不正データ HTTPファイルのサイズが異常
					mWarningMsg.append(getString(R.string.msg_warning_nofile));
				}
			}
		}
    }

    /**
     * DB更新タイミングかどうかを返す
     * @param  inFrom  日付１
     * @param  inTo    日付２
     * @return boolean ture=更新タイミング false=それ以外
     */
    private boolean httpChk(Date inFrom,Date inTo){

    	boolean rtnVal = false;
    	int longDay = 0;

    	if( getString(R.string.app_mode).equals(LotoConst.APP_MODE_FREE)){
        	rtnVal = false;
    	}else if( inFrom == null){
        	rtnVal = true;
    	}else{

	        longDay = LotoCom.getDays(inFrom, inTo);

	        // 経過日数が7日以内のみ
	        if( longDay <= 7 ){

//2010/02/16 修正
//			    Calendar calFrom = Calendar.getInstance();
//			    calFrom.set(inFrom.getYear(), inFrom.getMonth()-1, inFrom.getDay());
//			    int fromWeekNo = calFrom.get(Calendar.DAY_OF_WEEK);
//
//			    Calendar calTo = Calendar.getInstance();
//			    calTo.set(inTo.getYear(), inTo.getMonth()-1, inTo.getDay());
//			    int toWeekNo = calTo.get(Calendar.DAY_OF_WEEK);
//
//			    // 木曜日を過ぎているかどうか
//			    if(fromWeekNo <= Calendar.THURSDAY && Calendar.THURSDAY < toWeekNo ){
//			    	rtnVal = true;
//			    }

	        	// 2010/02/16 修正：曜日を取得するためにカレンダーを使用する
			    Calendar calFrom = Calendar.getInstance();
			    calFrom.setTime(inFrom);

			    // 2010/02/16 修正：DB更新日付の次の日からアプリ起動日までに金曜日があるか
			    for(int iii=1; iii <= longDay; iii++){
			    	calFrom.add(Calendar.DATE,1);

			    	if(calFrom.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
			    		rtnVal = true;
			    		break;
			    	}
			    }
	        }else{
	        	rtnVal = true;
	        }
    	}
	    return rtnVal;
    }

    /**
     * Timer
     */
    class RedrawHandler extends Handler {

        private LotoBallView view;
        private int delayTime=0;
        private int frameRate=0;
        private int sleep=0;
        private long count=0;

        /** timer Redraw */
        public RedrawHandler(LotoBallView view, int frameRate) {
            this.view = view;
            this.frameRate = frameRate;
        }

        /** timer start */
        public void start() {
            this.delayTime = 1000 / frameRate;
            count = 0;
            this.sendMessageDelayed(obtainMessage(0), delayTime);
        }

        /** timer stop */
        public void stop() {
            delayTime = 0;
        }

        /** timer */
        @Override
        public void handleMessage(Message msg) {
        	if( count > sleep){
        		view.invalidate();
        		count = 0;
        	}

            count=count+1;
            if(view.getViewEndFlg()){
            	delayTime = 0;
            }
            if (delayTime == 0){
                mSenceBalls = view.getSenceBalls();
    	        ImageButton imgBtnClear = (ImageButton)findViewById(ID_BTN_CLEAR);
    	        imgBtnClear.setVisibility(View.GONE);

				// Setボタン表示
				ImageButton imgBtnSet = (ImageButton)findViewById(ID_BTN_SET);
				imgBtnSet.setVisibility(View.VISIBLE);

				/* プロパティファイル読込 */
				//LotoProperty lp  = new LotoProperty(Loto7Sense.this);
				//ArrayList<String> ballsStrList = lp.loadSelectBalls();
				//if(10 <= ballsStrList.size()) {
				//	// Setボタン非表示
				//	imgBtnSet.setVisibility(View.GONE);
				//}

				ImageButton imgBtnStart = (ImageButton)findViewById(ID_BTN_START);
				imgBtnStart.setVisibility(View.VISIBLE);
            	return;
            }
            sendMessageDelayed(obtainMessage(0), delayTime);
        }

        public void setSleep(int inSleep){
        	sleep = inSleep;
        }
    }

	/**
	 * STARTボタン画像制御
	 */
	class TouchListenerStart implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			ImageButton countBtn = (ImageButton) v;
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mPushBack = mResources.getDrawable(R.drawable.startbtn2, null);
				countBtn.setBackground(mPushBack);
			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				lotoSenseStart(v);
				mNormalBack = mResources.getDrawable(R.drawable.startbtn, null);
				countBtn.setBackground(mNormalBack);
			}
			return false;
		}

	}

	/**
     * STARTボタンクリック
     */
    class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

		}
    };

	/**
	 * START処理
	 */
	public void lotoSenseStart(View v) {
		LotoBallView ballview = (LotoBallView)findViewById(ID_VIEW_BALL);
		ArrayList<LotoBallSet> viewBallList = new ArrayList<LotoBallSet>();

		// 重複版とその他で処理を分ける
		if( getString(R.string.app_mode).equals(LotoConst.APP_MODE_HIT)){
			LotoTmpDB tdb = new LotoTmpDB(getApplication());

			// 一時保存用テーブルの削除
			tdb.droptbl();

			// 抽選番号抽出
			tdb.insertDB2(mProperty.getRepeatCnt(), mProperty.getViewCnt(), mSense, mBalls);

			// 出力対象の取得処理
			LotoHit lh = new LotoHit();
			ArrayList<String> outputGroup = lh.lotoHit(tdb);

			// 出力対象が無かった場合は処理を終了する
			if (outputGroup.size() == 0) {
				dialogMsg(LotoConst.RTN_CODE_WARNING, "重複組を取得できませんでした。");
				return;
			}

			// ボール表示用のリストへ登録
			viewBallList = lh.setBallList(outputGroup, mBalls);

			// メッセージダイアログで結果表示
			String[][] dispData = lh.getDispData();
			String msg = "";
			for(int lll=0; lll < dispData.length; lll++){
				msg += dispData[lll][0] + "   " + dispData[lll][1] + "   " + dispData[lll][2] + "\n";
			}
			hitDialog(msg);
		}else{
			// 抽選番号抽出
			for( int i=0; i < mProperty.getViewCnt(); i++){
				// 抽選番号予想処理開始
				LotoBallSet aryTmp = mSense.getNumber(mBalls);
				viewBallList.add(aryTmp);
				mDebug.debug_LotoBallSet(aryTmp);
			}
		}

		// 抽出結果表示開始
		ballview.setSenceBalls(viewBallList,0);
		ballview.setAnimationFlg(true);
		ballview.start();
		mHandler.start();

		// Startボタン非表示
		v.setVisibility(View.INVISIBLE);

		// Setボタン非表示
		ImageButton imgBtnSet = (ImageButton)findViewById(ID_BTN_SET);
		imgBtnSet.setVisibility(View.GONE);

		// 保存
		LotoBallView tmpView = (LotoBallView)findViewById(ID_VIEW_BALL);
		mSenceBalls = tmpView.getSenceBalls();
		mProperty.saveLastViewBallSetList(mSenceBalls);
	}

    /**
     * Clearボタンクリック
     */
    class ClickListenerClear implements OnClickListener {
		@Override
		public void onClick(View v) {

			// Setボタン表示
			ImageButton imgBtnSet = (ImageButton)findViewById(ID_BTN_SET);
			imgBtnSet.setVisibility(View.GONE);

			// Ball view 初期化
			LotoBallView ballview = (LotoBallView)findViewById(ID_VIEW_BALL);
	        ballview.clear();

	        // Clearボタン非表示
	        v.setVisibility(View.GONE);

	        // Startボタン表示
	        ImageButton imgB = (ImageButton)findViewById(ID_BTN_START);
	        imgB.setVisibility(View.VISIBLE);

			// 保存
			LotoBallView tmpView = (LotoBallView)findViewById(ID_VIEW_BALL);
			ArrayList<LotoBallSet> getBalls = ballview.getSenceBalls();
			mProperty.saveLastViewBallSetList(getBalls);
		}
    };

    /**
     * CLEARボタン画像制御
     */
    class TouchListenerClear implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			ImageButton countBtn = (ImageButton) v;
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mPushBack = mResources.getDrawable(R.drawable.clearbtn2, null);
				countBtn.setBackground(mPushBack);
			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				mNormalBack = mResources.getDrawable(R.drawable.clearbtn, null);
				countBtn.setBackground(mNormalBack);
			}
			return false;
		}
    }

	/**
	 * Setボタン画像制御
	 */
	class TouchListenerSet implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			ImageButton countBtn = (ImageButton) v;
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mPushBack = mResources.getDrawable(R.drawable.setbtn2, null);
				countBtn.setBackground(mPushBack);
			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				mNormalBack = mResources.getDrawable(R.drawable.setbtn, null);
				countBtn.setBackground(mNormalBack);

				/* プロパティファイル読込 */
				LotoProperty lp  = new LotoProperty(Loto7Sense.this);
				ArrayList<String> ballsStrList = lp.loadSelectBalls();
				if(10 <= ballsStrList.size()) {
					dialogMsg(LotoConst.RTN_CODE_WARNING, "予想番号は10件までしか保存出来ません。");

				} else {

					/* */
					LinkedHashMap chkDat = new LinkedHashMap<String,String>();
					for(int testi = 0; testi < ballsStrList.size(); testi++){
						String tmp = ballsStrList.get(testi);
						chkDat.put(tmp, "");
					}

					LinkedHashMap wDat = new LinkedHashMap<String,String>();
					int setCnt = ballsStrList.size();
					boolean blnSelected = false;
					boolean isErrorW = false;
					boolean isSudeniSet = false;
					String errMsg = "";
					String errMsgW = "";
					for (int iLine = 0; iLine < mSenceBalls.size(); iLine++) {
						LotoBallSet lineBall = mSenceBalls.get(iLine);
						if (lineBall.getSelected()) {
							if(chkDat.size() > 0) {
								if(chkDat.containsKey(lp.cnvBallSetToStr(lineBall))){
									isSudeniSet = true;
								}
							}
							if(wDat.size() > 0) {
								if(wDat.containsKey(lp.cnvBallSetToStr(lineBall))){
									isErrorW = true;
								}
							}

							wDat.put(lp.cnvBallSetToStr(lineBall),"");
							blnSelected = true;

							if(!isSudeniSet) {
								setCnt = setCnt + 1;
							}
							isSudeniSet = false;
						}
					}

					if(10 < setCnt) {
						dialogMsg(LotoConst.RTN_CODE_WARNING, "予想番号は10件までしか保存出来ません。現登録数(" + ballsStrList.size() + "/10)");
					} else if (!blnSelected) {
						dialogMsg(LotoConst.RTN_CODE_WARNING, "予想番号をタップして選択して下さい。");
					} else if (isErrorW) {
						dialogMsg(LotoConst.RTN_CODE_WARNING, "選択されている予想番号は重複しています。");
					} else {
						/* プロパティファイル読込 */
						ClickListenerSetDialog clickSetDialog = new ClickListenerSetDialog();
						dialogSp("Set", "選択した予想番号を保存します。よろしいですか？", clickSetDialog);
					}
				}
			}
			return false;
		}
	}

    /**
     * 画面、縦/横変更時に再起動しないように設定
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }

    /**
     * メニューを作成する
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);

        menu.add(0,
                MENU_ID_HOLD,
                0,
                getString(R.string.menu_hold)).setIcon(android.R.drawable.ic_menu_mylocation);

        menu.add(0,
                MENU_ID_SETTING,
                1,
                getString(R.string.menu_setting)).setIcon(android.R.drawable.ic_menu_preferences);

		menu.add(0,
				MENU_ID_SELECT,
				2,
				getString(R.string.menu_select)).setIcon(android.R.drawable.ic_menu_preferences);

 		//getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * メニューボタン押下時の処理
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    /**
     * メニューを選択時の処理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
        switch (item.getItemId()) {
        case MENU_ID_HOLD:
            //次の画面に遷移させる
            intent.setClassName(
                    "com.apl.Loto7Sense",
                    "com.apl.Loto7Sense.LotoOptHold");
            //startActivity(intent);
            //リクエストコード付きインテントの呼び出し
            startActivityForResult(intent,0);
            return true;

        case MENU_ID_SETTING:
            //次の画面に遷移させる
            intent.setClassName(
                    "com.apl.Loto7Sense",
                    "com.apl.Loto7Sense.LotoSetting");
            startActivityForResult(intent,0);
            return true;

		case MENU_ID_SELECT:
			intent.setClassName(
					"com.apl.Loto7Sense",
					"com.apl.Loto7Sense.LotoSelected");
			startActivityForResult(intent,0);
			return true;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * アプリモードを返す
     */
    public static String retAppMode(){
		return appMode;

    }

    // アクティビティ呼び出し結果の取得
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent) {
        if (requestCode == 0 && resultCode==RESULT_OK) {
            //戻り値パラメータの取得
            SharedPreferences pref=getSharedPreferences("PREVIOUS_RESULT",MODE_PRIVATE);
            //String aaa = pref.getString("test","");
            //System.out.println(aaa);
        }
    }

	/**
	 *　設定ボタンクリック処理
	 */
	class TouchListenerBalls implements OnTouchListener {
		private int keepNumber = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			LotoBallView tv = (LotoBallView)v;
			mSenceBalls = tv.getSenceBalls();
			if( event.getAction() == MotionEvent.ACTION_DOWN ){
				for(int line = 0 ; line < mSenceBalls.size(); line++){
					LotoBallSet lineBall = mSenceBalls.get(line);
					for(int iBall = 0 ; iBall < lineBall.getSize(); iBall++){
						LotoBall ball = lineBall.getBall(iBall);

						int y = (int)event.getY();
						int imgY = ball.dst.top;
						if(	y-imgY >= 0 && y-imgY < ball.dst.height()+50){
							keepNumber = ball.getNumber();
							System.out.println("onTouch line:" + ball.getNumber() );

							break;
						}
					}
				}
			} else	if( event.getAction() == MotionEvent.ACTION_UP ){
				for(int line = 0 ; line < mSenceBalls.size(); line++){
					LotoBallSet lineBall = mSenceBalls.get(line);
					for(int iBall = 0 ; iBall < lineBall.getSize(); iBall++){
						LotoBall ball = lineBall.getBall(iBall);

						int y = (int)event.getY();
						int imgY = ball.dst.top;
						if(	y-imgY >= 0 && y-imgY < ball.dst.height()+50){
							System.out.println("onTouch line:" + ball.getNumber() +":"+keepNumber);

							if(keepNumber == ball.getNumber() ){
								lineBall.selected();
								v.invalidate();
							}
							break;
						}
					}
				}
			}

			return true;
		}
	}

	/**
	 *
	 * @param inTitle
	 * @param inMsg
	 */
	private void dialogSp(String inTitle, String inMsg, DialogInterface.OnClickListener inClickListener){

		new AlertDialog.Builder(this)
				.setTitle(inTitle)
				.setMessage(inMsg)
				.setPositiveButton("OK", inClickListener)
				.setNegativeButton("Cancel",inClickListener)
				.show();
	}

	/**
	 * Setボタン押下時に表示される確認ダイアログの処理
	 */
	class ClickListenerSetDialog implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				/* OK */

				/* プロパティファイル読込 */
				LotoProperty lp  = new LotoProperty(Loto7Sense.this);
				ArrayList<String> ballsStrList = lp.loadSelectBalls();
				LinkedHashMap test = new LinkedHashMap<String,String>();
				for(int testi = 0; testi < ballsStrList.size(); testi++){
					String tmp = ballsStrList.get(testi);
					test.put(tmp, "");
				}

				/* 画面で設定中の番号取込 */
				ArrayList<LotoBallSet> saveBallsList = new ArrayList<LotoBallSet>();
				for(int line = 0 ; line < mSenceBalls.size(); line++) {
					LotoBallSet lineBall = mSenceBalls.get(line);
					if(lineBall.getSelected()){
						test.put(lp.cnvBallSetToStr(lineBall), "");
						lineBall.selected();
					}
				}

				/* プロパティファイル書込 */
				if(test.size() > 0){
					lp.saveSelectBallsList(test);
				}

				if(test.size() >= 10) {
					// Setボタン表示
					//ImageButton imgBtnSet = (ImageButton)findViewById(ID_BTN_SET);
					//imgBtnSet.setVisibility(View.GONE);
				}

				LotoBallView lotoView = (LotoBallView)findViewById(ID_VIEW_BALL);
				lotoView.invalidate();

			} else 	if (which == DialogInterface.BUTTON_NEGATIVE) {
				/* CANCEL */

			}
		}
	}

}











