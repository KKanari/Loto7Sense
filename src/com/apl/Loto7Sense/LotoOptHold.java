package com.apl.Loto7Sense;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class LotoOptHold extends Activity {

    private final int ID_BALLVIEW = 1;
    private final int ID_SELECTBALLVIEW = 2;
    private final int ID_SCROLL = 3;
    private LotoProperty mProperty = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // プロパティファイル読み込み
        mProperty = new LotoProperty(LotoOptHold.this);

        // HOLD数字の反映
        String[] holdBall = mProperty.getHoldList();
        LotoBallSet tmpBallSet = new LotoBallSet();
        if( holdBall != null){
            for( int i=0; i < holdBall.length; i++){
                if( holdBall.length > 0 ){
                    LotoBall tmpBall = new LotoBall(getApplication(), Integer.valueOf(holdBall[i]).intValue(), 0);
                    tmpBallSet.setBall(tmpBall);
                }
            }
        }

        // メインレイアウト
        orgLinearLayout layoutMain = new orgLinearLayout(this);
        layoutMain.setOrientation(LinearLayout.VERTICAL);
        layoutMain.setBackgroundColor(Color.BLACK);
        layoutMain.setGravity(Gravity.CENTER | Gravity.TOP);
        layoutMain.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
        layoutMain.setFocusable(true);
        setContentView(layoutMain);

        // ラベル
        TextView tv = new TextView(getApplication());
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setText(getString(R.string.lbl_hold_info));
        tv.setTextSize(16);
        layoutMain.addView(tv);

        // 選択ボール表示VIEW
        selectNowView selBallView = new selectNowView( getApplication(),tmpBallSet );
        selBallView.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, 150));
        selBallView.setId(ID_SELECTBALLVIEW);
        layoutMain.addView(selBallView);

        // スクロールVIEW
        ScrollView scrollv = new ScrollView(getApplication());
        scrollv.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, 230));
        scrollv.setId(ID_SCROLL);
        layoutMain.addView(scrollv);
        layoutMain.setScr(scrollv);

        // ボール表示メインVIEW
        LinearLayout layoutBall = new LinearLayout(this);
        layoutBall.setOrientation(LinearLayout.VERTICAL);
        layoutBall.setGravity(Gravity.CENTER | Gravity.TOP);
        layoutBall.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
        scrollv.addView(layoutBall);

        // ボール描画VIEW
        selectListView mView = new selectListView( getApplication(),tmpBallSet,selBallView);
        mView.setLayoutParams(new LinearLayout.LayoutParams(LotoConst.FP, 4750));//TODO:20161006 2450 - 3000
        mView.setId(ID_BALLVIEW);
        layoutBall.addView(mView);
        selBallView.setBallListView(mView);

        // メインレイアウト
        LinearLayout layoutBottom = new LinearLayout(this);
        layoutBottom.setOrientation(LinearLayout.VERTICAL);
        layoutBottom.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        layoutBottom.setLayoutParams(
                new LinearLayout.LayoutParams(LotoConst.FP, LotoConst.FP));
        layoutMain.addView(layoutBottom);
    }

    @Override
    public void onStart() {
        super.onStart();
        //selectNowView selView = (selectNowView)findViewById(ID_SELECTBALLVIEW);
        //selView.invalidate();
    }
}

/**
 * 画面サイズ変更時に数字リストのサイズを変更する為のレイアウト
 * @author kms2
 */

class orgLinearLayout extends LinearLayout {
    private ScrollView pScr = null;

    /**
     *
     * @param context
     */
    public orgLinearLayout(Context context) {
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
class selectListView extends View {
    public int lotoType = 7; // TODO:20161006

    /** VIEWの状態 */
    private selectNowView mLinkView = null;

    /** ボール */
    private LotoBallList mBalls = null;

    /** 抽選番号 */
    private	LotoBallSet mSelBallSet = new LotoBallSet();

    private Context mContext = null;

    /**
     * コンストラクタ
     * @param c
     */
    public selectListView(Context c, LotoBallSet inBallSet, selectNowView inSelectBallView) {
        super(c);

        mContext = c;
        
        /* 画像の設定 */
        mBalls = new LotoBallList(c);
        mLinkView = inSelectBallView;
        mSelBallSet = inBallSet;

        for( int i=1; i <= LotoBallList.getBallMAX(); i++){
            LotoBall ballTmp = mBalls.getBall(i);
            // 透明度
            if(mSelBallSet.getBallNumbers().indexOf(ballTmp.getNumber())>-1){
                ballTmp.setArgb(LotoBall.TOUCH_ARGB_ON);
            }else{
                ballTmp.setArgb(LotoBall.TOUCH_ARGB_OFF);
            }
        }
    }

    /**
     * 描画処理
     */
    public void displayChgBall() {
        int pointX = 10;
        int pointY = 10;

        for( int i=1; i <= LotoBallList.getBallMAX(); i++){

            LotoBall ballTmp = mBalls.getBall(i);

            // X
            pointX = (getWidth() - ballTmp.getImageWidth()) / 2;
            ballTmp.setX(pointX);
            ballTmp.setY(pointY);

            // Y
            pointY = pointY + ballTmp.getImageHeight() + 15;
        }
    }

    /**
     * 描画処理
     */
    @SuppressLint("WrongCall")
    protected void onDraw(Canvas canvas) {
        for(int ballcnt = 0; ballcnt < LotoBallList.getBallMAX(); ballcnt++){
            LotoBall ball = mBalls.getBall(ballcnt+1);
            ball.onDraw(canvas);
        }
    }

    /**
     * 数字を未選択された状態にする
     */
    public void ballOFF(int inNumber, LotoBallSet inSelBallSet){
        LotoBall tmpBall = mBalls.getBall(inNumber);
        tmpBall.setArgb(LotoBall.TOUCH_ARGB_OFF);
        mSelBallSet.removeBall(tmpBall.getNumber());
        inSelBallSet = mSelBallSet;
        invalidate();
        updateFile();
    }

    /**
     * 数字を選択された状態にする
     */
    public void ballON(int inNumber){
        if( mSelBallSet.getSize() < lotoType){ // TODO:20161006
            LotoBall tmpSelect = mBalls.getBall(inNumber);
            tmpSelect.setArgb(LotoBall.TOUCH_ARGB_ON);
            LotoBall newBall = (LotoBall)mBalls.getBall(inNumber).copy();
            mSelBallSet.setBall(newBall);
            updateFile();
        }else{
            mBalls.getBall(inNumber).setArgb(LotoBall.TOUCH_ARGB_OFF);
        }
    }

    /**
     *
     */
    private void updateFile(){

        StringBuffer holdList = new StringBuffer("");
        for( int i=0; i< mSelBallSet.getSize(); i++){
            if( holdList.length() > 0 ){
                holdList.append(",");
            }
            holdList.append(mSelBallSet.getBall(i).getNumber());
        }

        SharedPreferences pref2 = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor2=pref2.edit();
        editor2.putString("hold_ball",holdList.toString());
        editor2.commit();
    }

    /**
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){

            case MotionEvent.ACTION_DOWN:
                for( int i = 1; i <= LotoBallList.getBallMAX(); ++i ) {
                    if( mBalls.getBall(i).onTouchEvent(event) ){
                        LotoBall tmpSelect = (LotoBall)mBalls.getBall(i);

                        if( mSelBallSet.chkBall(tmpSelect.getNumber())){
                            ballOFF(tmpSelect.getNumber(), mSelBallSet);
                        }else{
                            ballON(mBalls.getBall(i).getNumber());
                        }
                        mLinkView.setBallSet(mSelBallSet);
                        mLinkView.invalidate();
                        invalidate();
                    }
                }
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

/**
 * 上部の選択ボール描画
 * @author kms2
 */
class selectNowView extends View {

    /** 表示するボールリスト */
    private LotoBallSet mBallSet = null;

    /** 関連VIEW*/
    private selectListView mLinkView = null;

    /** Base Ball */
    private LotoBall mBaseBall = null;

    public int lotoType = 7; // TODO:20161006

    /** Max */
    private int mSelectBallMax = lotoType; // TODO:20161006

    /**
     * コンストラクタ
     * @param c
     */
    public selectNowView(Context c, LotoBallSet inBallSet) {
        super(c);
        mBallSet = inBallSet;

        // ベース取得
        mBaseBall = new LotoBall(c, 1, 0);
    }

    /**
     * ボール選択用VIEWを設定する。
     * @param inBallListView
     */
    public void setBallListView(selectListView inBallListView){
        mLinkView = inBallListView;
    }

    /**
     * 描画用のリストを取得する。
     * @return
     */
    public LotoBallSet getBallSet(){
        return mBallSet;
    }

    /**
     * ボール設定
     * @param inBallSet
     */
    public void setBallSet(LotoBallSet inBallSet){
        mBallSet = inBallSet;
    }

    /**
     * 描画処理
     */
    @SuppressLint("WrongCall")
    protected void onDraw(Canvas canvas) {

        // 下線描画用のメインペイント
        Paint linePaintMain = new Paint();
        linePaintMain.setColor( Color.rgb(150, 150, 150));

        // 下線描画用のサブペイント
        Paint linePaintSub = new Paint();
        linePaintSub.setColor(Color.rgb(100, 100, 100));

        // 横間隔
        int kankakuX = (getWidth() - mBaseBall.getImageWidth() * mSelectBallMax) / 10;

        // 下線
        int wkY_Base = 20;
        int wkX_Base = kankakuX * 2;
        int wkX_S = wkX_Base;
        int wkX_E = wkX_S+mBaseBall.getImageWidth();
        int wkY   = wkY_Base+mBaseBall.getImageHeight() + 2;

        // 数字表示箇所のアンダーライン
        for(int i=0 ; i < mSelectBallMax ; i++){
            canvas.drawLine(wkX_S + 10, wkY, wkX_E - 10, wkY, linePaintMain);
            canvas.drawLine(wkX_S + 10, wkY+1, wkX_E - 10, wkY+1, linePaintSub);
            wkX_S = wkX_E + kankakuX;
            wkX_E = wkX_S+mBaseBall.getImageWidth();
        }

        // 数字表示
        if( mBallSet != null){
            if( mBallSet.getSize() > 0){

                // 開始位置
                int setX = wkX_Base;

                // ボール描画
                for(int i = 0 ; i< mBallSet.getSize(); i++){
                    LotoBall ball = mBallSet.getBall(i);
                    ball.setX(setX);
                    ball.setY(wkY_Base);
                    ball.onDraw(canvas);
                    setX = setX + ball.getImageWidth() + kankakuX;
                }
            }
        }
    }

    /**
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if( mBallSet != null){
                    for( int i = 0; i < mBallSet.getSize(); ++i ) {
                        if( mBallSet.getBall(i).onTouchEvent(event) ){

                            // 選択された数字に対する処理
                            int delBallNumber = mBallSet.getBall(i).getNumber();

                            // OFF
                            mLinkView.ballOFF(delBallNumber,mBallSet);
                            break;
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }
}

