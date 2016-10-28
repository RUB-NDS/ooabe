/**
 * 
 */
package com.example.ooabe;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import com.example.ooabe.abe.AttributeUniverse;
import com.example.ooabe.abe.CipherText;
import com.example.ooabe.abe.KeyPolicy;
import com.example.ooabe.abe.PWABE;
import com.example.ooabe.abe.RABE;
import com.example.ooabe.abe.UserSecretKey;
import it.unisa.dia.gas.jpbc.Element;
import com.example.ooabe.utils.tree.PolicyTree;



/**
 * @author LiDuan
 *
 */
public class PWABETest {

	static PWABE myABE2;
	static ArrayList<BigInteger> listAttributes2;
	static UserSecretKey usk1, usk0;
	static KeyPolicy  myPolicy2;
	
	
	@BeforeClass
	public static void setup()
	{

		
		String policyStr="((Alice*Research)*Engineer)+(Alice*ID198);";
		PolicyTree pt;
		try {
			myABE2 = new PWABE();
			pt = new PolicyTree(policyStr);
			String[] attributes2 = {"Alice","Research","Engineer","ID198"};
			
			AttributeUniverse U2 = new AttributeUniverse(myABE2.getZp(), new ArrayList<String>( Arrays.asList(attributes2)));
			
			listAttributes2 = new ArrayList<BigInteger>();
			for(String attribute:attributes2)
			{
				listAttributes2.add(U2.getImageOf(attribute));
			}
			
			myPolicy2 = pt.toKeyPolicy(U2,true);
			myPolicy2.printPolicy();
			BigInteger uid2Image = myABE2.getZp().newRandomElement().toBigInteger();
//			BigInteger uid2Image = (new BigInteger("2")).add(myABE2.getMSK());
			usk1 = myABE2.genUSK("uid2",uid2Image,myPolicy2, true); 
			usk0 = myABE2.genUSK("uid2",uid2Image,myPolicy2, false); 
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	@Test
	public void decTest2()
	{
		Element m2 = myABE2.getGT().newRandomElement();
		CipherText ct2 = myABE2.encrypt(m2, listAttributes2);
		Element m2_dec;
		
		for(int i=0; i<99; i++)
		{
			myABE2.decrypt(ct2, usk1, usk0);
		}
		 m2_dec=myABE2.decrypt(ct2, usk1, usk0);
		 
		 assertTrue(m2_dec.isEqual(m2));
	}

}
