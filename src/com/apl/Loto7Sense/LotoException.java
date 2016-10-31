package com.apl.Loto7Sense;

public class LotoException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4890013966837904097L;
	public static int HTTPFILE_SIZE0 = 10;
	public static int HTTPFILE_NOITEMCNT = 12;
	
	public int subExceptionNum = 0;
	
	public LotoException(int inExceptionNo) {
		subExceptionNum = inExceptionNo;
	}
}
