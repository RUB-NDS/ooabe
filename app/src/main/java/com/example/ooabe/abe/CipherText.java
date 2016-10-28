package com.example.ooabe.abe;
import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;


public class CipherText 
{
	public ArrayList<BigInteger> attributes;
	public Element C, C0;
	public ArrayList<Element> C1;
	public ArrayList<Element> C2;
	public CipherText(ArrayList<BigInteger> attrList)
	{
		this.attributes = attrList;
		C1 = new ArrayList<Element>();
		C2 = new ArrayList<Element>();
		
//		C and C0 are not initiated at this stage
	}
}
