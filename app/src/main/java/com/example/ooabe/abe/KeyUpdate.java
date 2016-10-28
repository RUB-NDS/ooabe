/**
 * 
 */
package com.example.ooabe.abe;

import java.util.ArrayList;

/**
 * @author LiDuan
 *
 */
public class KeyUpdate extends UserSecretKey {

	/**
	 * 	Ternary coding of encryption time
	 * 'X' at index i := both t_{i,0} and t_{i,1} are used in encryption
	 * '0' at index i := t_{i,0} is used in encryption
	 * '1' at index i := t_{i,1} is used in encryption
	 */	
	String timeCode;
	
	/**
	 * @param UID
	 */
	public KeyUpdate(String UID, String timeCode) {
		super(UID);
		this.timeCode = timeCode;
		// TODO Auto-generated constructor stub
	}
	
	public KeyUpdate(UserSecretKey usk, String timeCode)
	{
		super(usk.UserID);
		this.K0 = usk.K0;
		this.K1 = usk.K1;
		this.K2 = usk.K2;
		this.policy = usk.policy;
		this.timeCode = timeCode;
		
	}

	/**
	 * @return the timeCode
	 */
	public String getTimeCode() {
		return timeCode;
	}

	/**
	 * @param timeCode the timeCode to set
	 */
	public void setTimeCode(String timeCode) {
		this.timeCode = timeCode;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
