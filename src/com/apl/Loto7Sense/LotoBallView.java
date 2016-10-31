package com.apl.Loto7Sense;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * 描画用のクラス
 */
class LotoBallView extends View {
    private boolean pViewEndFlg = false;
    private int DEF_LINE_MAX = 5;

    /** VIEWの状態 */
    public static int DEF_MODE_INIT = 0;	    // Clear時
    public static int DEF_MODE_BALLVIEW = 1;	// 抽選番号表示
    private int pMode = DEF_MODE_INIT;

    /** 画像のx座標 */
    int y;

    /** 抽出番号表示可能ライン */
    private int pNowLine = 1;

    /** ボール */
    private LotoBallList pBalls = null;

    private int pX_Start = 40;
    private int pY_Start = 15;
    private int pY_kankaku = 150;

    private boolean mAnimationFlg = true;

    // 次のラインが表示されるタイミング(Max255)
    private int lineDisp = 100;

    /** 抽選番号 */
    private ArrayList<LotoBallSet> pSenceBalls = new ArrayList<LotoBallSet>();

    /**
     * コンストラクタ
     * @param c
     */
    public LotoBallView(Context c, LotoBallList inBalls) {
        super(c);

        /* イベントが取得できるようにFocusを有効にする */
        setFocusable(true);
       
        /* Resourceインスタンスの生成 */
        Resources res = this.getContext().getResources();
       
        /* 画像の設定 */
        pBalls = inBalls;
        for( int i=1; i <= LotoBallList.getBallMAX(); i++){
            LotoBall ballTmp = pBalls.getBall(i);
            ballTmp.setArgb(0);
            ballTmp.setBitmap(BitmapFactory.decodeResource(res, ballTmp.getBallImgID()));
        }

        pViewEndFlg = false;
        
        /* x,y座標の初期化 */
        y = pY_Start;

        pMode = DEF_MODE_INIT;

    }

    public void setLineCnt(int inLineCnt){
        DEF_LINE_MAX = inLineCnt;
    }

    public void setAnimationFlg(Boolean inFlg){
        mAnimationFlg = inFlg;
    }

    /**
     * 描画処理
     */
    protected void onDraw(Canvas canvas) {

        int tmpMaxSize = 0;
        drawBase(canvas);

        if(pMode == DEF_MODE_BALLVIEW){

            // ボール描画
            drawBall(canvas);

            tmpMaxSize = pSenceBalls.size();

            if( DEF_LINE_MAX < pSenceBalls.size()){
                tmpMaxSize = DEF_LINE_MAX;
            }

            if( pNowLine <= tmpMaxSize ){
                LotoBallSet balls = pSenceBalls.get(pNowLine-1);
                if( balls.getBall(0).getArgb() >= lineDisp ){
                    if( pNowLine < pSenceBalls.size() ){
                        pNowLine++;
                    }
                }
            }

            if( pNowLine >= tmpMaxSize ){
                LotoBallSet balls = pSenceBalls.get(tmpMaxSize-1);
                if( balls.getBall(0).getArgb() >= 255 ){
                    pViewEndFlg = true;
                }
            }
        }

        if(pMode == DEF_MODE_BALLVIEW){
            // ボール描画
            drawBall(canvas);

            tmpMaxSize = pSenceBalls.size();

            if( DEF_LINE_MAX < pSenceBalls.size()){
                tmpMaxSize = DEF_LINE_MAX;
            }

            if( pNowLine <= tmpMaxSize ){
                LotoBallSet balls = pSenceBalls.get(pNowLine-1);
                if( balls.getBall(0).getArgb() >= lineDisp ){
                    if( pNowLine < pSenceBalls.size() ){
                        pNowLine++;
                    }
                }
            }

            if( pNowLine >= tmpMaxSize ){
                LotoBallSet balls = pSenceBalls.get(tmpMaxSize-1);
                if( balls.getBall(0).getArgb() >= 255 ){
                    pViewEndFlg = true;
                }
            }
        }

    }

    /**
     *
     */
    public void setMode(int inMode) {
        pMode = inMode;
    }

    /**
     * 描画ボール設定 
     */
    public void setSenceBalls(ArrayList<LotoBallSet> inSenceBalls,int inStartArgb){

        pSenceBalls.clear();

        // 抽選番号(ball)クラスをコピーすると共に、各番号の表示情報を設定する。
        for(int line = 0; line < inSenceBalls.size(); line++){
            LotoBallSet oneBallList = inSenceBalls.get(line);
            LotoBallSet newBallList = new LotoBallSet();
            for(int ballcnt = 0; ballcnt < oneBallList.getSize(); ballcnt++){
                LotoBall ball = (LotoBall)oneBallList.getBall(ballcnt).copy();
                ball.setArgb(inStartArgb);
                newBallList.setBall(ball);
            }
            pSenceBalls.add(newBallList);
        }
        displayChgBall();
    }

    /**
     * 描画ボール
     */
    public ArrayList<LotoBallSet> getSenceBalls(){
        return pSenceBalls;
    }

    /**
     * 描画ボール
     */
    public void setSenceBalls(ArrayList<LotoBallSet> inSenceBalls){
        pSenceBalls = inSenceBalls;
    }

    /**
     * 描画ボール設定 
     */
    public void displayChgBall(){

        int setY = 0;

        // 抽選番号(ball)クラスをコピーすると共に、各番号の表示情報を設定する。
        for(int line = 0; line < pSenceBalls.size(); line++){
            LotoBallSet oneBallList = pSenceBalls.get(line);
            LotoBallSet newBallList = new LotoBallSet();

            setY = y + line * pY_kankaku;

            int setX = pX_Start;

            for(int ballcnt = 0; ballcnt < oneBallList.getSize(); ballcnt++){
                LotoBall ball = (LotoBall)oneBallList.getBall(ballcnt);

                ball.setX(setX);
                ball.setY(setY);
                newBallList.setBall(ball);

                //int kankakuX = (getWidth() - pX_Start - ball.getImageWidth() * 7 ) / 7 ;
                int kankakuX = 10;
                setX = setX + ball.getImageWidth() + kankakuX;

            }
        }
    }

    /**
     * 基本レイアウト
     */
    private void drawBase(Canvas canvas){

        // ライン左開始座標
        int lineX_Left  = 5;
        // ライン右終了座標
        int lineX_Right = 100;
        // ラインを下線調整
        int lineY_Ctr = 90;

        int colorR = 150;
        int colorG = 150;
        int colorB = 150;

        LotoBallSet ballLine = null;
        LotoBall ball = null;

        // 白線を引く為のy軸の設定
        if(pSenceBalls.size() > 0) {
            ballLine = pSenceBalls.get(0);
            ball = ballLine.getBall(0);
            lineY_Ctr = ball.dst.height() + 5;
        }

        // 背景色設定
        // canvas.drawColor(Color.BLACK);

        // Message の描画
        //   アンチエイリアスを設定すると、輪郭がぼやけてしまいますが滑らかに表示
        Paint textPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint2.setTextSize(32);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(32);
        textPaint.setColor( Color.WHITE);

        // Lineの描画
        Paint linePaint2 = new Paint();

        Paint linePaint = new Paint();

        Paint sPaint = new Paint();
        sPaint.setStyle(Paint.Style.STROKE);

        int now_y =0;
        int lineWidth = 5;

        // 画面基本レイアウト設定
        for(int i = 1; i <= DEF_LINE_MAX; i++){

            // 抽選番号存在有無
            if(i <= pSenceBalls.size()) {
                ballLine = pSenceBalls.get(i-1);
                if(ballLine.getSelected()){
                    colorR = 255;
                    colorG = 255;
                    colorB = 255;
                    lineWidth = 5;

                    //sPaint.setColor(Color.rgb(colorR, colorG, colorB));
                    //canvas.drawRect(lineX_Left, now_y - getHeight(), getWidth() - lineX_Right, now_y, sPaint);
                } else {
                    colorR = 255;
                    colorG = 255;
                    colorB = 255;
                    lineWidth = 5;
                }
            }
            textPaint.setColor(Color.rgb(colorR, colorG, colorB));
            textPaint2.setColor(Color.rgb(colorR, colorG, colorB));
            linePaint.setColor(Color.rgb(colorR, colorG, colorB));
            linePaint2.setColor(Color.rgb(colorR, colorG, colorB));
            linePaint2.setStrokeWidth(lineWidth);

            now_y = y + (i-1) * pY_kankaku + lineY_Ctr ;

            // 左列の番号
            canvas.drawText(getLeftTitle(i),0,now_y+1,textPaint);
            canvas.drawText(getLeftTitle(i),0,now_y+2,textPaint2);

            // 下線
            canvas.drawLine(lineX_Left, now_y, getWidth() - lineX_Right, now_y, linePaint);
            canvas.drawLine(lineX_Left, now_y+1, getWidth() - lineX_Right, now_y+1, linePaint2);

        }
    }

    /** 終了フラグ */
    public boolean getViewEndFlg(){
        return pViewEndFlg;
    }

    /**
     * ボール
     */
    @SuppressLint("WrongCall")
    private void drawBall(Canvas canvas){

        int wkNowLine = pNowLine;
        if( wkNowLine > this.pSenceBalls.size()){
            wkNowLine = this.pSenceBalls.size();
        }

        int imgCheckOff=R.drawable.checkoff;
        int imgCheckOn=R.drawable.checkon;
        Bitmap bitOff = BitmapFactory.decodeResource(getResources(), imgCheckOff);
        Bitmap bitOn = BitmapFactory.decodeResource(getResources(), imgCheckOn);
        Rect src;
        Rect dst;
        src = new Rect(0,0,105,105);
        Paint pPaint = new Paint();
        int yyy = 0;

        for(int i = 1; i <= wkNowLine; i++){
            LotoBallSet oneBallSet = this.pSenceBalls.get(i-1);
            if(oneBallSet.getSelected()) {
                float y = 0;
                for (int ballcnt = 0; ballcnt < oneBallSet.getSize(); ballcnt++) {
                    LotoBall ball = oneBallSet.getBall(ballcnt);
                    ball.onDraw(canvas);
                    y = ball.getY();
                }

                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setTextSize(64);
                textPaint.setColor( Color.WHITE);
                //canvas.drawText("選択", 850,  y + oneBallSet.getBall(0).getImageHeight(), textPaint);

                yyy = (int)(y) + 50;
                dst = new Rect(880,yyy, 880+50,yyy+50);
                canvas.drawBitmap(bitOn, src, dst, pPaint);

            } else {
                for (int ballcnt = 0; ballcnt < oneBallSet.getSize(); ballcnt++) {
                    LotoBall ball = oneBallSet.getBall(ballcnt);

                    int wkArgb = ball.getArgb() + 10;
                    ball.setArgb(wkArgb);
                    if (wkArgb > 255) {
                        ball.setArgb(255);
                    }

                    ball.onDraw(canvas);
                }

                yyy = (int)oneBallSet.getBall(0).getY() + 50;
                dst = new Rect(880,yyy, 880+50,yyy+50);
                canvas.drawBitmap(bitOff, src, dst, pPaint);
            }
        }
    }

    /**
     * 左列　番号の設定
     */
    private String getLeftTitle(int line){

        String rtnStr = "";
        rtnStr = String.valueOf(line);
        return rtnStr;
    }

    /**
     * 計算処理用メソッド
     */
    public void start(){

        if( mAnimationFlg ){
            pNowLine = 1;
        }else{
            pNowLine = pSenceBalls.size();
        }
        pViewEndFlg = false;
        this.pMode = DEF_MODE_BALLVIEW;
    }

    /**
     * 計算処理用メソッド
     */
    public void clear(){
        pSenceBalls = new ArrayList<LotoBallSet>();
        this.pMode = DEF_MODE_INIT;
        this.invalidate();
    }

    /** 画面サイズが変更されたときに呼び出されるメソッド */

    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        displayChgBall();
    }
}
