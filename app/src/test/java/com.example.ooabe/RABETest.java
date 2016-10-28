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
import com.example.ooabe.abe.RABE;
import com.example.ooabe.abe.UserSecretKey;
import it.unisa.dia.gas.jpbc.Element;
import com.example.ooabe.utils.tree.PolicyTree;


public class RABETest {
	static RABE myABE, myABE2;
	static ArrayList<BigInteger> listAttributes, listAttributes2;
	static UserSecretKey usk, usk2;
	static KeyPolicy myPolicy, myPolicy2;
	static HashMap<Integer, String> rho;
	
	
	@BeforeClass
	public static void setup()
	{
//		System.out.println("Starting test the basic enc/dec");
		myABE = new RABE();
		

		listAttributes = new ArrayList<BigInteger>();
		listAttributes.add(new BigInteger("1"));
		listAttributes.add(new BigInteger("2"));
		listAttributes.add(new BigInteger("3"));
//		listAttributes.add(new BigInteger("4"));
		
//		test case 
//		policy formula =  A AND B AND C, 
//		mapping to Zp: A --> 1, B-->2, C-->3
//		M = {{1,1,1}, {0,0,-1}, {0,-1,0}}
//		rho , rho2Zp: 0 --> A --> 1, 1--> B --> 2, 2 --> C --> 3
		Integer M0[] = {1,1,1};
		Integer M1[] = {0,0,-1};
		Integer M2[] = {0, -1, 0};
		ArrayList<ArrayList<Integer>> M = new ArrayList<ArrayList<Integer>>();
		M.add(new ArrayList<Integer>( Arrays.asList(M0)));
		M.add(new ArrayList<Integer>( Arrays.asList(M1)));
		M.add(new ArrayList<Integer>( Arrays.asList(M2)));
//		System.out.println("The first element of M is "+ M.get(0).get(0));
//		System.out.println("The second-last element of M is "+ M.get(2).get(1));
		
		rho = new HashMap<Integer, String>();
		rho.put(new Integer(0), "A");
		HashMap<Integer, Element> rho2ZP = new HashMap<Integer, Element>();
		rho2ZP.put(new Integer(0),myABE.getZp().newElement().set(new BigInteger("1")));
		rho2ZP.put(new Integer(1),myABE.getZp().newElement().set(new BigInteger("2")));
		rho2ZP.put(new Integer(2),myABE.getZp().newElement().set(new BigInteger("3")));
		
		myPolicy = new KeyPolicy(rho, rho2ZP, M);
		
		
		usk = new UserSecretKey("uid");
		usk.setupUSK(myABE.getMSK(), myABE.getGeneratorG(), myABE.getH(), myABE.getU(), myABE.getW(), myPolicy,
				myABE.getZp(), myABE.getG());
		
		String policyStr="((Alice*Research)*Engineer)+(Alice*ID198);";
		PolicyTree pt;
		try {
			myABE2 = new RABE();
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
			usk2 = myABE2.genUSK("uid2",myPolicy2); 
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
//	@Test
//	public void encTest()
//	{
//		Element m = myABE.getGT().newRandomElement();
//		
//		for(int i=0; i<100; i++)
//		{
//			myABE.encrypt(m, listAttributes);
//		}
//		
////		CipherText ct = myABE.encrypt(m, listAttributes);
//	}
	

//	@Test
//	public void decTest() {
//		Element m = myABE.getGT().newRandomElement();
//		CipherText ct = myABE.encrypt(m, listAttributes);
//		Element m_dec;
//		
//		for(int i=0; i<99; i++)
//		{
//			myABE.decrypt(ct, usk);
//		}
//		 m_dec=myABE.decrypt(ct, usk);
//		 
//		 assertTrue(m_dec.isEqual(m));
//		 
////		 if(m_dec.isEqual(m))
////		{
////			System.out.println("Decryption is correct!");
////		}
////		else
////		{
////			System.out.print("m is ");
////			System.out.println(m);
////			System.out.print("m_dec is ");
////			System.out.println(m_dec);
////		}
//	}
	
	@Test
	public void decTest2()
	{
		Element m2 = myABE2.getGT().newRandomElement();
		CipherText ct2 = myABE2.encrypt(m2, listAttributes2);
		Element m2_dec;
		
		for(int i=0; i<99; i++)
		{
			myABE2.decrypt(ct2, usk2);
		}
		 m2_dec=myABE2.decrypt(ct2, usk2);
		 
		 assertTrue(m2_dec.isEqual(m2));
	}

}
