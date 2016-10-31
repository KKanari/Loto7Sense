package com.apl.Loto7Sense;

public class LotoBallBase {

	/** 番号 */
	private int number;

	/** 有効/無効 */
	private boolean enabled;

	/** Statusコード */
	private int status;

	/** 抽出されている状態 */
	private boolean selected;

	public LotoBallBase(int inNumber){
		number = inNumber;
		enabled = true;
		selected = false;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
