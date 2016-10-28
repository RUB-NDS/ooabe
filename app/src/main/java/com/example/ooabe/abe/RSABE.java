package com.example.ooabe.abe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingParametersGenerator;

public class RSABE extends PWABE{

	/**
	 * T_MAX_log, the integer value of Log2(MAX_TIME)
	 */
	int T_Max_Log = 20;
	
	public RSABE() {
		super();
	}
	
	public RSABE(int rBits, int qBits)
	{
		super(rBits, qBits);
	}
	
	public RSABE(BigInteger msk, PairingParametersGenerator pg, 
			PairingParameters params, Field Zp, Field G, Field GT,
			Pairing e, Element g, Element u, Element w, Element h, Element pk, boolean isServer)
	{
		super(msk, pg, 
				params, Zp, G, GT,
				e, g, u, w, h, pk, isServer);
	}
	/**
	 * @return the t_Max_Log
	 */
	public int getT_Max_Log() {
		return T_Max_Log;
	}

	/**
	 * @param t_Max_Log the t_Max_Log to set
	 */
	public void setT_Max_Log(int t_Max_Log) {
		T_Max_Log = t_Max_Log;
	}

	/**
	 * 
	 * @param timeCode_t_1 {'0','1'}* string representing a time point t-1
	 * @return An ArrayList of strings, representing T_t
	 */
	public static ArrayList<String> getTt(String timeCode_t_1, int t_max_log)
	throws Exception
	{
		if(timeCode_t_1.length() > t_max_log || timeCode_t_1.length() < 1)
		{
			throw new Exception("RSABE.getTt: timeCode_t out of range" );
		}
		
		ArrayList<String> Tt = new ArrayList<String>();
		char[] ones = new char[timeCode_t_1.length()];
		Arrays.fill(ones, '1');
		String oneStr = new String(ones);
		
		/**
		 * Special case, timeCode with length 1
		 */
		if("X".equals(timeCode_t_1) )
		{
			Tt.add(timeCode_t_1);
			return Tt;
		}
		else if("0".equals(timeCode_t_1))
		{
			Tt.add("1");
			return Tt;
		}
		else if(oneStr.equals(timeCode_t_1))
		{
			throw new Exception("RSABE.getTt: timeCode_t represents the right-most leaf" );
		}
			
		/**
		 * General case, timeCode with length >= 2
		 */
		
		String parentStr, xStr;
		
//		String rChildStr_old;
		String rChildStr_new = new String(timeCode_t_1);
		
		int codeLen = timeCode_t_1.length();		
		char[] x_array=new char[codeLen];
		
		Arrays.fill(x_array, 'X');
		xStr = new String(x_array);
		parentStr = timeCode_t_1.substring(0,codeLen-1)+"X";
		
		for(int j = codeLen; j>0 ; j--)
		{
			
			rChildStr_new = timeCode_t_1.substring(0,j-1)+"1";
			if(j<codeLen)
			{
				rChildStr_new+=parentStr.substring(j);
			}
			if(rChildStr_new.equals(parentStr)|| rChildStr_new.equals(timeCode_t_1))
				;
			else
			{
				
				Tt.add(rChildStr_new);
				
			}
			
//			rChildStr_old = rChildStr_new;
			parentStr = timeCode_t_1.substring(0,j-1)+"X";
			if(j<codeLen)
			{
				parentStr += xStr.substring(j);
			}
		}
		
		return Tt;
	}
	
	public static void printTt(String timeCode, int t_max_log)
	{
		
		try {
			
			ArrayList<String> Tt = getTt(timeCode, t_max_log);
			if(null == Tt || Tt.size() < 1)
				return;
			for(String code: Tt)
			{
				System.out.println(code);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param CT, a list of RSCipherText objects
	 * @param newTimeCode, string for a time
	 * @return a RSCipherText object corresponding to the newTimeCode
	 */
	public RSCipherText delegateTo(ArrayList<RSCipherText> CT, String newTimeCode)
			throws Exception
	{
		/**
		 * 
		 * 0. Check if CurrentTimeCode represents a descendant of an ciphertext in CT
		 * 1. Copy all the normal attributes and the corresponding parts of C, C0,C1
		 * 2. Copy only the C0, C1 part if the time attributes remains in that timeCode
		 * using Element duplicate = e.duplicate();
		 * TODO: re-randomization
		 */
		if(newTimeCode.length() != this.T_Max_Log )
			throw new Exception("RSABE.delegateTo: CurrentTimeCode out of range!\n");
		
		if(null == CT || CT.size()<1)
			throw new Exception("RSABE.delegateTo: CT is not initialized or empty!\n");
		
		for(RSCipherText ct: CT)
		{
			if(ct.getTimeCode().equals(newTimeCode))
				return ct.deepCopy();
			
			
			if(ct.isAncestorOf(newTimeCode))
			{
				RSCipherText rsCT =  new RSCipherText(new ArrayList<BigInteger>(),
						ct.timeAttrStartAt);
				rsCT.setTimeCode(newTimeCode);
				
				rsCT.C = ct.C.duplicate();
				rsCT.C0 = ct.C0.duplicate();
				
//				deep-copy the "normal" parts of attributes, C1 and C2
				for(int i = 0; i< ct.timeAttrStartAt ; i++)
				{
					rsCT.attributes.add(new BigInteger(ct.attributes.get(i).toString()));
					rsCT.C1.add(ct.C1.get(i).duplicate());
					rsCT.C2.add(ct.C2.get(i).duplicate());
				}
				
//				select and deep-copy the "time" parts of attributes, C1 and C2				
				int k = ct.timeAttrStartAt;
				int timeCodeLen = newTimeCode.length();
				for(int j = 0; j < timeCodeLen; j++)
				{
					if(newTimeCode.charAt(j)== 'X')
					{
						rsCT.attributes.add( new BigInteger(
								ct.attributes.get(k).toString()));
						rsCT.attributes.add( new BigInteger(
								ct.attributes.get(k+1).toString()));
						
						rsCT.C1.add(ct.C1.get(k).duplicate());
						rsCT.C1.add(ct.C1.get(k+1).duplicate());
						
						rsCT.C2.add(ct.C2.get(k).duplicate());
						rsCT.C2.add(ct.C2.get(k+1).duplicate());
						
						k+=2;
					}
					else if (ct.getTimeCode().charAt(j) == 'X')
					{
						if(newTimeCode.charAt(j)== '0')
						{
							rsCT.attributes.add( new BigInteger(
									ct.attributes.get(k).toString()));
							rsCT.C1.add(ct.C1.get(k).duplicate());
							rsCT.C2.add(ct.C2.get(k).duplicate());
						}
						else
						{
							rsCT.attributes.add( new BigInteger(
									ct.attributes.get(k+1).toString()));
							rsCT.C1.add(ct.C1.get(k+1).duplicate());
							rsCT.C2.add(ct.C2.get(k+1).duplicate());
						}
						k+=2;
					}
					else
					{
						rsCT.attributes.add( new BigInteger(
								ct.attributes.get(k).toString()));
						rsCT.C1.add(ct.C1.get(k).duplicate());
						rsCT.C2.add(ct.C2.get(k).duplicate());
						k+=1;
					}
				}
				
//				TODO:re-randomization
				
				return rsCT; 	
			}
		}
		
		return null;
	}
	

	public ArrayList<RSCipherText> delegate(ArrayList<RSCipherText> CT_old, String t_1, int timeCodeLen )
	{
		ArrayList<RSCipherText> CT_new = new ArrayList<RSCipherText>();
		
		try {
			ArrayList<String> timeCodes = getTt(t_1, timeCodeLen);
			for(String code : timeCodes)
			{
				CT_new.add(delegateTo(CT_old, code));
			}
			return CT_new;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	public UserSecretKey genUSK(String UID, BigInteger uidImage, 
			KeyPolicy policy)
	{
		return genUSK(UID, uidImage, policy, false);
	}
	
	public KeyUpdate genKU(String UID, BigInteger uidImage, 
			KeyPolicy policy, String timeCode)
	{
		return new KeyUpdate(genUSK(UID, uidImage, policy, true),timeCode);
	}
	
	/**
	 * 
	 * @param m, the message
	 * @param normalAttributes, list of normal attributes
	 * @param timeAttributes, list of time-related attributes
	 * @param timeCode, the string represent the time
	 * @return
	 */
	
	public RSCipherText encrypt(Element m, ArrayList<BigInteger> normalAttributes,
			ArrayList<BigInteger> timeAttributes, String timeCode)
	{
		ArrayList<BigInteger> allAttributes = new ArrayList<BigInteger>();
		
		for(BigInteger bigInt: normalAttributes)
		{
			allAttributes.add(new BigInteger(bigInt.toString()));
		}
		
		for(BigInteger bigInt: timeAttributes)
		{
			allAttributes.add(new BigInteger(bigInt.toString()));
		}

		 
		CipherText ct_org = this.encrypt(m, allAttributes);
		RSCipherText ct = new RSCipherText(allAttributes, normalAttributes.size());
		ct.C = ct_org.C;
		ct.C0 = ct_org.C0;
		ct.C1 = ct_org.C1;
		ct.C2 = ct_org.C2;
		ct.setTimeCode(timeCode);
		
		return ct;
	}
	
	
	public boolean isSatisfiable(String keyTimeCode, String ctTimeCode)
	{
		if(keyTimeCode.length() != ctTimeCode.length())
			return false;
		else
		{
			int NumOfAttributes = keyTimeCode.length();
			for(int i=0; i< NumOfAttributes; i++)
			{
				if(keyTimeCode.charAt(i)== ctTimeCode.charAt(i)
						|| ctTimeCode.charAt(i)== 'X')
					continue;
				else
					return false;
						
			}
		}
		return true;
	}
	
	public Element decrypt(ArrayList<RSCipherText> CT,UserSecretKey usk, KeyUpdate keyUpdate )
			throws Exception
	{
		if(null == CT || null == usk || null == keyUpdate)
			return null;
		
		try
		{

			for(RSCipherText ct: CT)
			{
				if(isSatisfiable(keyUpdate.getTimeCode(),ct.getTimeCode()))
				{
					return decrypt(ct,usk, keyUpdate);
				}
			}

		}catch(Exception e)
		{
			throw e;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param T_max maximal time point to be reached, in decimal
	 * @param t_max_log_len length of the code
	 * @return a list of all time code strings from 0 to T_max-1 (exactly T_max strings)
	 */
	public static ArrayList<String> genTimeCodes(int T_max, int t_max_log_len)
	{
		
		if(T_max < 0 || t_max_log_len < 1 
				|| t_max_log_len < Integer.toBinaryString(T_max-1).length())
			return null;
		
		ArrayList<String> timeCodes= new ArrayList<String>();
		String code;
		char[] zeroChars = new char[t_max_log_len];
		Arrays.fill(zeroChars, '0');
		String zeroStr = new String(zeroChars);
		int lenDiff;
		for(int i = 0; i < T_max; i++)
		{
			code = Integer.toBinaryString(i);
			lenDiff = t_max_log_len - code.length();
//			pad the code if necessary
			if(lenDiff > 0 )
				code = zeroStr.substring(0,lenDiff)+code;
			timeCodes.add(code);
		}
		
		return timeCodes;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> ls = new ArrayList<String>();
		String a = "a";
		ls.add(a);
		a="bc";
		ls.add(a);
		System.out.println(ls.get(0));
		
		String[] T = {"000","001","010","011",
				"100","101","110","111"};
		
		
		
		ArrayList<String> codes= genTimeCodes(64,6);
		int  i = 0;
		for(String code: codes)
		{
			System.out.println("code for time "+i+" : is: "+ code); 
			i++;
			
		}
		
		for(String t: T)
		{
			System.out.println("***The set T_t , t-1="+t+") includes:"); 
			printTt(t,3);
			
		}
	}

}
