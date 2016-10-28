/**
 * 
 */
package com.example.ooabe.abe;

import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 
 * @author LiDuan
 *
 */
public class RSCipherText extends CipherText {

/**
 * 	Ternary coding of encryption time
 * 'X' at index i := both t_{i,0} and t_{i,1} are used in encryption
 * '0' at index i := t_{i,0} is used in encryption
 * '1' at index i := t_{i,1} is used in encryption
 */	
	String timeCode;
	
/**	index of first occurrence of time-related attribute in attrList 
 */
	int timeAttrStartAt;
	
	/**
	 * @param attrList
	 */
	public RSCipherText(ArrayList<BigInteger> attrList, int timeAttrStartAt) {
		super(attrList);
		this.timeCode = new String();
		this.timeAttrStartAt = timeAttrStartAt;
	}
	/**
	 * @return the timeCode
	 */
	public String getTimeCode() {
		return timeCode;
	}
	/**
	 * @param timeCode the timeCode to set
	 * TODO: strict input check
	 */
	public void setTimeCode(String timeCode) {
		this.timeCode = timeCode;
	}
	

	/**
	 * 
	 * @param child, another RSCipherText
	 * @return true if the timeCode of childCT represent a child node of "this" 
	 */
	
	public boolean isParentOf(RSCipherText childCT)
	{
		return isParentOf(childCT.getTimeCode());
	}
	
	
	public boolean isParentOf(String childCode)
	{
		if(null == this.timeCode || null == childCode || null == childCode )
			return false;
		
		if(this.timeCode.length()!=childCode.length() || childCode.length() < 1
				)
			return false;
		
		int codeLenParent = this.timeCode.length();
		int codeLenChild = childCode.length();
		
		if(codeLenParent == 1)
			if(this.timeCode.charAt(codeLenParent-1) == 'X'
					&& ( childCode.charAt(codeLenChild-1)== '1' 
					|| childCode.charAt(codeLenChild-1)== '0' ))
			return true;

		/**
		 * Criteria for "this" to be the (possible) parent of "child" 
		 * when the length of timeCode >= 2:
		 * 1. same prefix of timeCode, except the last char AND
		 * 2. the last char of "this" is '2' AND
		 * 3. the last char of childCode is '0' or '1'
		 */
		if(this.timeCode.substring(0, codeLenParent-2).equals(
				childCode.substring(0, codeLenChild-2))
				&& this.timeCode.charAt(codeLenParent-1) == 'X'
				&& ( childCode.charAt(codeLenChild-1)== '1' 
				|| childCode.charAt(codeLenChild-1)== '0' )
				)
			return true;
			
		return false;
	}
	
	public boolean isAncestorOf(RSCipherText descendantCT)
	{
		return isAncestorOf(descendantCT.getTimeCode());
	}
	
	/**
	 * 
	 * @param descendantCode, string represent the node in the time tree
	 * @return true if descendantCode represents a descendant node 
	 */
	
	public boolean isAncestorOf(String descendantCode)
	{
		if(null == this.timeCode || null == descendantCode || null == descendantCode )
			return false;
		
		/**
		 * return false if 
		 * their timeCode are of different length OR
		 * both are empty string
		 */
	
		if(this.timeCode.length()!= descendantCode.length() 
				|| descendantCode.length() < 1)
			return false;
		
		int codeLenAnc = this.timeCode.length();
	
		
		/**
		 * Special case, return true if : 
		 * 1. String "X" OR
		 * 2. identical timeCode
		 */
		if(codeLenAnc == 1)
			if(this.timeCode.charAt(0) == 'X')
				return true;

		if(this.timeCode.equals(descendantCode))
			return true;
		
		/**General Case
		 * Criteria for "this" to be the (possible) ancester of descendantCT 
		 * when the length of timeCode >= 2:
		 * 0. 'X' does occur in this.timeCode AND
		 * 1. same prefix of timeCode, before the first occurence of 'X' 
		 * in this.timeCode AND
		 * 2. all the remaining of this.timeCode are 'X' AND
		 */
		
		int firstXInd = this.timeCode.indexOf('X');
//		0., 1., if X does not occur (the case of identical strings has been examined)
		if( firstXInd == -1)
			return false;
//		2., (lazy solution) if any '0' or '1' appears after the first 'X', return false 				
		if(this.timeCode.lastIndexOf('0')> firstXInd || this.timeCode.lastIndexOf('1')> firstXInd)
			return false;
		
//		Special case, this.timeCode contains only 'X'
		if(firstXInd == 0)
			return true;
		
//		if the prefixes are not identical, return false	
		if(!this.timeCode.substring(0,firstXInd).equals(
				descendantCode.substring(0, firstXInd)))
			return false;
		


		
		
		return true;
	}

	/**
	 * 
	 * @return a deep copy of this RSCipherText object
	 */
	public RSCipherText deepCopy()
	{
		RSCipherText rsCT =  new RSCipherText(new ArrayList<BigInteger>(),
				this.timeAttrStartAt);
		rsCT.setTimeCode(this.timeCode);
		
		rsCT.C = this.C.duplicate();
		rsCT.C0 = this.C0.duplicate();
		
		int numAttributes = this.attributes.size();
		
//		deep-copy the "normal" parts of attributes, C1 and C2
		for(int i = 0; i< numAttributes ; i++)
		{
			rsCT.attributes.add(new BigInteger(this.attributes.get(i).toString()));
			rsCT.C1.add(this.C1.get(i).duplicate());
			rsCT.C2.add(this.C2.get(i).duplicate());
		}
		
		return rsCT;
		
	}
	
	
	public static void main(String[] args)
	{
//		if((new Integer(1))== 1)
//		{
//			System.out.println("Yes");
//		}
//		
//		if(Integer.toBinaryString(1024).length() == 10)
//		{
//			System.out.println("JAVA converts 1024 to a 01-String of len 10");
//		}
//		else
//		{
//			System.out.println("JAVA converts 1024 to "+ Integer.toBinaryString(1024) +", a 01-String of len "
//		+ Integer.toBinaryString(1024).length());
//		}
		
		
	}
}
