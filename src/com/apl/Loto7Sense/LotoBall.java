package com.apl.Loto7Sense;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * 抽選用番号ボールの１つを管理する。
 * @author kms2
 *
 */
public class LotoBall extends View implements Cloneable{

	/** 画像ファイル */
	private final static int[] BALLIMG={
			R.drawable.ball01,
			R.drawable.ball02,
			R.drawable.ball03,
			R.drawable.ball04,
			R.drawable.ball05,
			R.drawable.ball06,
			R.drawable.ball07,
			R.drawable.ball08,
			R.drawable.ball09,
			R.drawable.ball10,
			R.drawable.ball11,
			R.drawable.ball12,
			R.drawable.ball13,
			R.drawable.ball14,
			R.drawable.ball15,
			R.drawable.ball16,
			R.drawable.ball17,
			R.drawable.ball18,
			R.drawable.ball19,
			R.drawable.ball20,
			R.drawable.ball21,
			R.drawable.ball22,
			R.drawable.ball23,
			R.drawable.ball24,
			R.drawable.ball25,
			R.drawable.ball26,
			R.drawable.ball27,
			R.drawable.ball28,
			R.drawable.ball29,
			R.drawable.ball30,
			R.drawable.ball31,
			R.drawable.ball32,
			R.drawable.ball33,
			R.drawable.ball34,
			R.drawable.ball35,
			R.drawable.ball36,
			R.drawable.ball37,
			R.drawable.ball38,
			R.drawable.ball39,
			R.drawable.ball40,
			R.drawable.ball41,
			R.drawable.ball42,
			R.drawable.ball43
	};

	/** bitmap 情報 */
	public Bitmap pBitmap;
	private int pImageWidth = 0;		// 幅
	private int pImageHeight = 0;	// 高さ
	private int pArgb = 0; 			//

	LotoBallBase pBallBase = null;

	/** paint */
	private Paint pPaint = new Paint();

	/** */
	public static int TOUCH_ARGB_ON = 150;
	public static int TOUCH_ARGB_OFF = 250;

	/** */
	private int touchAction;
	private boolean isDragable = false;
	private Point preXY;
	private Point touchXY;

	private int pSizeType = 5;

	/** 座標 */
	public Rect src;
	public Rect dst;

	/** 共通 */
	public Context pContext = null;

	/**
	 * @param inContext
	 * @param inNumber
	 */
	public LotoBall(Context inContext, int inNumber, int inSizeType) {
		super(inContext);
		pSizeType = inSizeType;
		pBallBase = new LotoBallBase(inNumber);
		pContext = inContext;
		preXY = new Point(0,0);
		touchXY = new Point(0,0);
		if( inNumber > 0){
			setBitmap(BitmapFactory.decodeResource(inContext.getResources(), getBallImgID()));
		}
	}

	@Override
	public Object clone() {	//throwsを無くす
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}

	}

	@Override
	public void onDraw(Canvas canvas) {
		if( pBitmap == null ) return;

		canvas.drawBitmap(pBitmap, src, dst, pPaint);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		touchAction = event.getAction();
		int x = (int)event.getX();
		int y = (int)event.getY();
		if( touchAction == MotionEvent.ACTION_MOVE && isDragable ){
			touchXY.set(x, y);
			int newX = dst.left + (touchXY.x - preXY.x);
			int newY = dst.top + (touchXY.y - preXY.y);
			move(newX,newY);
			preXY.set(touchXY.x,touchXY.y);
		}
		if(isTouchImage(x, y)){
			if( touchAction == MotionEvent.ACTION_DOWN ){
				preXY.set(x, y);
				isDragable = true;
				if(isDragable){
					if( pArgb == TOUCH_ARGB_ON ){
						setArgb(TOUCH_ARGB_OFF);
					}else{
						setArgb(TOUCH_ARGB_ON);
					}
				}
			}else if( touchAction == MotionEvent.ACTION_UP ) {
				isDragable = false;
			}
		}else{
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	public void move(int x, int y) {
		dst.left = x;
		dst.top = y;
		dst.right = x + pImageWidth;
		dst.bottom = y + pImageHeight;
		invalidate();
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isTouchImage(int x,int y) {
		int imgX = dst.left;
		int imgY = dst.top;
		return x-imgX >= 0 &&
				x-imgX < dst.width() &&
				y-imgY >= 0 &&
				y-imgY < dst.height();
	}

	/**
	 *
	 * @return
	 */
	public Object copy() {
		LotoBall newBall = new LotoBall(pContext, pBallBase.getNumber(), 0);
		newBall.setBitmap(pBitmap);
		return newBall;
	}

	/**
	 *
	 * @return
	 */
	public int getNumber() {
		return pBallBase.getNumber();
	}

	/**
	 *
	 * @return
	 */
	public void setNumber(int pNumber) {
		pBallBase.setNumber(pNumber);
	}

	/** set x */
	public void setX(int inX){
		dst.left = inX;
		dst.right = inX + pImageWidth;
	}

	/** get x */
	public float getX(){
		return dst.left;
	}

	/** set y */
	public void setY(int inY){
		dst.top = inY;
		dst.bottom = inY + pImageHeight;
	}

	/** get y */
	public float getY(){
		return dst.top;
	}

	/** set bitmap */
	public void setBitmap(Bitmap inBitMap){

		if( inBitMap == null ){
			return;
		}
		pBitmap = inBitMap;
		pImageWidth = pBitmap.getWidth();
		pImageHeight = pBitmap.getHeight();
		src = new Rect(0, 0, pImageWidth, pImageHeight);
		dst = new Rect(src);

		switch (pSizeType) {
			case 1:
				dst = new Rect(0,0,105,105);
				//pImageWidth = 100;
				//pImageHeight = 100;
				break;

			default:
				dst = new Rect(0,0,105,105);
				//dst = new Rect(src);
				break;
		}
		pImageWidth = dst.width();
		pImageHeight = dst.height();
	}

	public int getImageWidth() {
		return pImageWidth;
	}

	public void setImageWidth(int pImageWidth) {
		this.pImageWidth = pImageWidth;
	}

	public int getImageHeight() {
		return pImageHeight;
	}

	public void setImageHeight(int pImageHeight) {
		this.pImageHeight = pImageHeight;
	}

	/** get bitmap */
	public Bitmap getBitmap(){
		return this.pBitmap;
	}

	/** img id */
	public int getBallImgID(){

		int rtnVal = 0;
		if( pBallBase.getNumber() > 0 ){
			rtnVal = BALLIMG[pBallBase.getNumber()-1];
		}

		return rtnVal;
	}

	/** set paint */
	public void setPaint(Paint inPaint){
		pPaint = inPaint;
	}

	/** get paint */
	public Paint getPaint(){
		return pPaint;
	}

	/** set argb */
	public void setArgb(int inArgb){
		pArgb = inArgb;
		pPaint.setColor(Color.argb(inArgb, 255, 0, 0));
	}

	/** get argb */
	public int getArgb(){
		return pArgb;
	}

	/**
	 * 数字を未選択された状態にする
	 */
	public void ballOFF(){
		this.setArgb(LotoBall.TOUCH_ARGB_OFF);
		invalidate();
	}
	/**
	 * 数字を選択された状態にする
	 */
	public void ballON(){
		this.setArgb(LotoBall.TOUCH_ARGB_ON);
		invalidate();
	}

	/**
	 * 数字を選択された状態にする
	 */
	public void ballONTest(){
		this.setArgb(TOUCH_ARGB_ON);
		//invalidate();
	}

	/**
	 * 数字を選択された状態にする
	 */
	public void ballONTest2(){
		this.setArgb(TOUCH_ARGB_OFF);
		//invalidate();
	}
}
