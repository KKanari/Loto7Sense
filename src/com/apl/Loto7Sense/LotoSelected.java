package com.apl.Loto7Sense;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by kms2 on 2016/10/18.
 */

public class LotoSelected extends Activity {

    private final int ID_BALLVIEW = 1;
    private final int ID_SELECTBALLVIEW = 2;
    private final int ID_SCROLL = 3;
    private final int ID_BTN_RESET   = 4;
    private final int ID_BTN_DELETE   = 5;
    private final int ID_LAYOUT_BTN   = 6;
    private final int ID_LAYOUT_BTN2   = 7;
    private LotoProperty mProperty = null;
    private ArrayList<LotoBallSet> ballsList = new ArrayList<LotoBallSet>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // プロパティファイル読み込み
        mProperty = new LotoProperty(LotoSelected.this);

        LotoProperty lp  = new LotoProperty(this);
        ArrayList<String[]> ballsStrList = lp.loadLastSelectedBallsList();

        for(int setCnt= 0; setCnt < ballsStrList.size(); setCnt++) {
            LotoBallSet ballSet = new LotoBallSet();
            String[] balls = ballsStrList.get(setCnt);
            for( int i=0; i < balls.length; i++){
                LotoBall ball = new LotoBall(getApplication(), Integer.valueOf(balls[i]).intValue(), 1);
                ballSet.setBall(ball);
            }
            ballsList.add(ballSet);
        }

        // メインレイアウト
        orgLinearLayout2 layoutMain = new orgLinearLayout2(this);
        layoutMain.setOrientation(LinearLayout.VERTICAL);
        layoutMain.setBackgroundColor(Color.BLACK);
        layoutMain.setGravity(Gravity.CENTER | Gravity.TOP);
        layoutMain.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
        layoutMain.setFocusable(true);
        setContentView(layoutMain);

        // ボタンレイアウト
        LinearLayout layoutBtn = new LinearLayout(this);
        layoutBtn.setId(ID_LAYOUT_BTN);
        layoutBtn.setOrientation(LinearLayout.VERTICAL);
        layoutBtn.setGravity(Gravity.CENTER | Gravity.TOP);
        layoutBtn.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.WC, LotoConst.WC));
        layoutBtn.setMinimumHeight(150);
        layoutMain.addView(layoutBtn);

        // ボタンレイアウト
        LinearLayout layoutBtnSub1 = new LinearLayout(this);
        layoutBtnSub1.setId(ID_LAYOUT_BTN2);
        layoutBtnSub1.setOrientation(LinearLayout.HORIZONTAL);
        layoutBtnSub1.setGravity(Gravity.CENTER | Gravity.TOP);
        layoutBtnSub1.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.WC, LotoConst.WC));
        layoutBtn.addView(layoutBtnSub1);

        // 初期化ボタン,
        Drawable mNormalBack = null;
        Resources mResources = null;
        ImageButton imgBtnStart = new ImageButton(this);
        mResources = getResources();
        mNormalBack = mResources.getDrawable(R.drawable.clearbtn,null);
        imgBtnStart.setBackground(mNormalBack);
        imgBtnStart.setId(ID_BTN_RESET);
        layoutBtnSub1.addView(imgBtnStart,new ViewGroup.LayoutParams(LotoConst.WC, LotoConst.WC));

        // リスナー作成
        TouchListener touchResetBtn = new TouchListener();
        imgBtnStart.setOnTouchListener(touchResetBtn);

        // 削除ボタン
        ImageButton imgBtnDel = new ImageButton(this);
        mResources = getResources();
        mNormalBack = mResources.getDrawable(R.drawable.deletebtn,null);
        imgBtnDel.setBackground(mNormalBack);
        imgBtnDel.setId(ID_BTN_DELETE);
        layoutBtnSub1.addView(imgBtnDel,new ViewGroup.LayoutParams(LotoConst.WC, LotoConst.WC));

        // リスナー作成
        //ClickListenerDel clickDel = new ClickListenerDel();
        //imgBtnDel.setOnClickListener(clickDel);
        TouchListenerDel touchDelBtn = new TouchListenerDel();
        imgBtnDel.setOnTouchListener(touchDelBtn);

        // スクロールVIEW
        ScrollView scrollv = new ScrollView(getApplication());
        scrollv.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, 1500));
        scrollv.setId(ID_SCROLL);
        layoutMain.addView(scrollv);
        layoutMain.setScr(scrollv);

        // ボール表示メインVIEW
        LinearLayout layoutBall = new LinearLayout(this);
        layoutBall.setOrientation(LinearLayout.VERTICAL);
        layoutBall.setGravity(Gravity.CENTER | Gravity.TOP);
        layoutBall.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
        scrollv.addView(layoutBall);

        //ClickListenerView clickTest01 = new ClickListenerView();
        //layoutBall.setOnClickListener(clickTest01);

        // ボール描画VIEW
        selectListView2 mView = new selectListView2( getApplication(), ballsList);
        mView.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, 1500));//TODO:20161006 2450 - 3000
        mView.setId(ID_BALLVIEW);
        layoutBall.addView(mView);

        // リスナー作成
        TouchListenerView touchView = new TouchListenerView();
        mView.setOnTouchListener(touchView);

    }

    @Override
    public void onStart() {
        super.onStart();
        //selectNowView selView = (selectNowView)findViewById(ID_SELECTBALLVIEW);
        //selView.invalidate();
    }

    /**
     *
     */
    class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            /** Startボタン画像 */
            Drawable mNormalBack; 	//通常時
            Drawable mPushBack; 		//押下時
            /** resources */
            Resources mResources = null;
            mResources = getResources();

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

                ClickListenerClearDialog clickClearDialog = new ClickListenerClearDialog();
                dialogSp("Clear","保存中の予想番号を初期化します。よろしいですか？", clickClearDialog);

            }
            return false;
        }

    }

    /**
     *
     */
    class TouchListenerDel implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /** Startボタン画像 */
            Drawable mNormalBack; 	//通常時
            Drawable mPushBack; 		//押下時
            /** resources */
            Resources mResources = null;
            mResources = getResources();

            ImageButton countBtn = (ImageButton) v;
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                mPushBack = mResources.getDrawable(R.drawable.deletebtn2, null);
                countBtn.setBackground(mPushBack);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)
            {
                mNormalBack = mResources.getDrawable(R.drawable.deletebtn, null);
                countBtn.setBackground(mNormalBack);

                boolean blnSelected = false;
                for(int iLine=0; iLine < ballsList.size(); iLine++){
                    LotoBallSet ballSet = ballsList.get(iLine);
                    if(ballSet.getSelected()){
                        blnSelected = true;
                    }
                }
                if(!blnSelected) {
                    dialogMsg(LotoConst.RTN_CODE_WARNING, "削除対象の予想番号が選択されていません。");
                } else {
                    ClickListenerDelDialog clickDelDialog = new ClickListenerDelDialog();
                    dialogSp("Delete","選択した予想番号を削除します。よろしいですか？", clickDelDialog);
                }
            }
            return false;
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
     * Clearボタンクリック
     */
    class ClickListenerDel implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mProperty.saveSelectBallsSelected(ballsList, false);
            ballsList = new ArrayList<LotoBallSet>();
            ArrayList<String[]> ballsStrList = mProperty.loadLastSelectedBallsList();
            for(int setCnt= 0; setCnt < ballsStrList.size(); setCnt++) {
                LotoBallSet ballSet = new LotoBallSet();
                String[] balls = ballsStrList.get(setCnt);
                for( int i=0; i < balls.length; i++){
                    LotoBall ball = new LotoBall(getApplication(), Integer.valueOf(balls[i]).intValue(), 1);
                    ballSet.setBall(ball);
                }
                ballsList.add(ballSet);
            }

            selectListView2 selView = (selectListView2)findViewById(ID_BALLVIEW);
            selView.setBallsList(ballsList);
            selView.invalidate();
        }
    }

    /**
     *
     */
    class TouchListenerView implements View.OnTouchListener {
        private int keepNumber = 0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            selectListView2 tv = (selectListView2)v;
            ArrayList<LotoBallSet> mSenceBalls = null;
            mSenceBalls = ballsList;

            if( event.getAction() == MotionEvent.ACTION_DOWN ) {
                for(int line = 0 ; line < mSenceBalls.size(); line++){
                    LotoBallSet lineBall = mSenceBalls.get(line);
                    for(int iBall = 0 ; iBall < lineBall.getSize(); iBall++){
                        LotoBall ball = lineBall.getBall(iBall);

                        int y = (int)event.getY();
                        int imgY = ball.dst.top;
                        if(	y-imgY >= 0 && y-imgY < ball.dst.height() + 50){
                            keepNumber = ball.getNumber();
                            System.out.println("onTouch line:" + ball.getNumber());
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
                        if(	y-imgY >= 0 && y-imgY < ball.dst.height() + 50){
                            System.out.println("onTouch line:" + ball.getNumber());
                            if(keepNumber == ball.getNumber() ){
                                lineBall.selected();
                                v.invalidate();
                            }
                            break;
                        }
                    }
                }

            }
            /*
            */
            return true;
        }
    }

    /**
     *
     */
    class ClickListenerTest implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int aaa=0;
            aaa = 1;
        }
    }

    /**
     * Setボタン押下時に表示される確認ダイアログの処理
     */
    class ClickListenerDelDialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
				/* OK */
                mProperty.saveSelectBallsSelected(ballsList, false);
                ballsList = new ArrayList<LotoBallSet>();
                ArrayList<String[]> ballsStrList = mProperty.loadLastSelectedBallsList();
                for(int setCnt= 0; setCnt < ballsStrList.size(); setCnt++) {
                    LotoBallSet ballSet = new LotoBallSet();
                    String[] balls = ballsStrList.get(setCnt);
                    for( int i=0; i < balls.length; i++){
                        LotoBall ball = new LotoBall(getApplication(), Integer.valueOf(balls[i]).intValue(), 1);
                        ballSet.setBall(ball);
                    }
                    ballsList.add(ballSet);
                }

                selectListView2 selView = (selectListView2)findViewById(ID_BALLVIEW);
                selView.setBallsList(ballsList);
                selView.invalidate();

            } else 	if (which == DialogInterface.BUTTON_NEGATIVE) {
				/* CANCEL */
            }
        }
    }

    /**
     * Clearボタン押下時に表示される確認ダイアログの処理
     */
    class ClickListenerClearDialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
				/* OK */
                // 新規に読み直す
                LotoProperty lp = new LotoProperty(LotoSelected.this);
                lp.resetSelectBallsList();


                // プロパティファイル読み込み
                mProperty = new LotoProperty(LotoSelected.this);

                lp  = new LotoProperty(LotoSelected.this);
                ArrayList<String[]> ballsStrList = lp.loadLastSelectedBallsList();
                ArrayList<LotoBallSet> ballsList = new ArrayList<LotoBallSet>();

                for(int setCnt= 0; setCnt < ballsStrList.size(); setCnt++) {
                    LotoBallSet ballSet = new LotoBallSet();
                    String[] balls = ballsStrList.get(setCnt);
                    for( int i=0; i < balls.length; i++){
                        LotoBall ball = new LotoBall(getApplication(), Integer.valueOf(balls[i]).intValue(), 1);
                        ballSet.setBall(ball);
                    }
                    ballsList.add(ballSet);
                }

                selectListView2 selView = (selectListView2)findViewById(ID_BALLVIEW);
                selView.setBallsList(ballsList);
                selView.invalidate();

            } else 	if (which == DialogInterface.BUTTON_NEGATIVE) {
				/* CANCEL */
            }
        }
    }
}


/**
 * 画面サイズ変更時に数字リストのサイズを変更する為のレイアウト
 * @author kms2
 */
class orgLinearLayout2 extends LinearLayout {
    private ScrollView pScr = null;

    /**
     *
     * @param context
     */
    public orgLinearLayout2(Context context) {
        super(context);
    }

    /**
     * サイズ変更時に影響のあるスクロールバー
     * @param inScr
     */
    public void setScr(ScrollView inScr){
        pScr = inScr;
    }

    /** 画面サイズが変更されたときに呼び出されるメソッド */
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
    }
    /**
     * 描画処理
     */
    @Override
    @SuppressLint("WrongCall")
    protected void onDraw(Canvas canvas) {
        pScr.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, getHeight() - 150));
    }
}

/**
 * 描画用のクラス
 */
class selectListView2 extends View {
    private ArrayList<LotoBallSet> ballsList = null;

    public int lotoType = 7; // TODO:20161006

    /** ボール */
    private LotoBallList mBalls = null;

    /** 抽選番号 */
    private	LotoBallSet mSelBallSet = new LotoBallSet();

    private Context mContext = null;

    /**
     * コンストラクタ
     * @param c
     */
    public selectListView2(Context c, ArrayList<LotoBallSet> inBallsList) {
        super(c);

        ballsList = inBallsList;
        mContext = c;

        /* 画像の設定 */
        mBalls = new LotoBallList(c);

        /*
        for( int i=1; i <= LotoBallList.getBallMAX(); i++){
            LotoBall ballTmp = mBalls.getBall(i);
            // 透明度
            if(mSelBallSet.getBallNumbers().indexOf(ballTmp.getNumber())>-1){
                ballTmp.setArgb(LotoBall.TOUCH_ARGB_ON);
            }else{
                ballTmp.setArgb(LotoBall.TOUCH_ARGB_OFF);
            }
        }
        */
    }

    public void setBallsList(ArrayList<LotoBallSet> inBallsList){
        ballsList = inBallsList;
    }

    /**
     * 描画処理
     */
    public void displayChgBall() {
        int pointX = 10;
        int pointY = 10;

        /*

        for( int i=1; i <= LotoBallList.getBallMAX(); i++){

            LotoBall ballTmp = mBalls.getBall(i);

            // X
            pointX = (getWidth() - ballTmp.getImageWidth()) / 2;
            ballTmp.setX(pointX);
            ballTmp.setY(pointY);

            // Y
            pointY = pointY + ballTmp.getImageHeight() + 15;
        }
        */
    }

    /**
     * 描画処理
     */
    @SuppressLint("WrongCall")
    protected void onDraw(Canvas canvas) {
        /*
        for(int ballcnt = 0; ballcnt < LotoBallList.getBallMAX(); ballcnt++){
            LotoBall ball = mBalls.getBall(ballcnt+1);
            ball.onDraw(canvas);
        }
        */

        int setX = 0;
        int setY = 0;
        int pY_kankaku = 150;

        // ライン左開始座標
        int lineX_Left  = 40;

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(64);
        textPaint.setColor( Color.WHITE);

        Paint textPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint2.setTextSize(32);
        textPaint2.setColor( Color.WHITE);

        int colorR = 255;
        int colorG = 255;
        int colorB = 255;
        int lineWidth = 5;
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(Color.rgb(colorR, colorG, colorB));
        textPaint.setColor(Color.rgb(colorR, colorG, colorB));

        int imgCheckOff=R.drawable.checkoff;
        int imgCheckOn=R.drawable.checkon;
        Bitmap bitOff = BitmapFactory.decodeResource(getResources(), imgCheckOff);
        Bitmap bitOn = BitmapFactory.decodeResource(getResources(), imgCheckOn);
        Rect src;
        Rect dst;
        src = new Rect(0,0,105,105);
        Paint pPaint = new Paint();
        int yyy = 0;

        for (int setCnt = 0; setCnt < ballsList.size(); setCnt++) {
            LotoBallSet balls = ballsList.get(setCnt);

            setY = 0 + setCnt * pY_kankaku;
            setX = lineX_Left;

            for (int ballCnt = 0; ballCnt < balls.getSize(); ballCnt++) {
                LotoBall ball = balls.getBall(ballCnt);
                ball.setX(setX);
                ball.setY(setY);

                ball.onDraw(canvas);

               // System.out.println("ball:" + ball.getNumber());

                int kankakuX = 10;
                setX = setX + ball.getImageWidth() + kankakuX;
            }

            canvas.drawText(String.valueOf(setCnt+1), 0, setY + balls.getBall(0).getImageHeight(), textPaint2);
            canvas.drawLine(0, setY + balls.getBall(0).getImageHeight(), 850, setY + balls.getBall(0).getImageHeight(), linePaint);

            yyy = (int)setY + 50;
            if(balls.getSelected()) {
                //canvas.drawText("選択", 850, setY + balls.getBall(0).getImageHeight(), textPaint);
                dst = new Rect(880,yyy,880+50,yyy+50);
                canvas.drawBitmap(bitOn, src, dst, pPaint);
            } else {
                dst = new Rect(880,yyy,880+50,yyy+50);
                canvas.drawBitmap(bitOff, src, dst, pPaint);
            }
        }

        if (ballsList.size() > 0) {
            this.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, ballsList.size() * (ballsList.get(0).getBall(0).getImageHeight() + 50)));

        }
    }

    /**
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){

            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
        }
        return true;
    }

    /** 画面サイズが変更されたときに呼び出されるメソッド */
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        displayChgBall();
    }

}
