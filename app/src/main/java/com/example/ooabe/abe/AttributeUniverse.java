package com.example.ooabe.abe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import it.unisa.dia.gas.jpbc.Field;

/**
 * This AttributeUniverse class is used to 
 * store information about the mapping and inverted mapping from strings to elements
 * in Z_p^*
 * @author LiDuan
 *
 */
public class AttributeUniverse 
{
	
	public Field Zp;
	HashMap<String, BigInteger> StrHash2ZP;
	HashMap<BigInteger, String> ZPHash2Str;
	ArrayList<String> ListOfAttributeNames;
	
	public AttributeUniverse(Field Zp, ArrayList<String> ListOfAttributeNames)
	{
//		for every attribute in the list, randomly pick up an integer in Zp
//		make sure the mapping is a bijection
		this.Zp = Zp;
		this.StrHash2ZP = new HashMap<String, BigInteger>();
		this.ZPHash2Str = new HashMap<BigInteger, String>();
		this.ListOfAttributeNames = new ArrayList<String>();
		for(String s: ListOfAttributeNames)
		{
			this.addNewAttribute(s);
		}
		
	}
	
	public void addNewAttribute(String newAttribute)
	{
		BigInteger value;
		if(this.StrHash2ZP.containsKey(newAttribute))
		{
			
		}
		else
		{
			ListOfAttributeNames.add(newAttribute);
			do
			{
				value = this.Zp.newRandomElement().toBigInteger();
			}while(this.StrHash2ZP.containsValue(value));
			
			this.StrHash2ZP.put(newAttribute, value);
			this.ZPHash2Str.put(value, newAttribute);
		
		}
	}
	
	public void addNewAttribute(String newAttribute, BigInteger value)
	{
		if(this.StrHash2ZP.containsKey(newAttribute))
		{
			
		}
		else
		{
			ListOfAttributeNames.add(newAttribute);
			do
			{
				value = this.Zp.newRandomElement().toBigInteger();
			}while(this.StrHash2ZP.containsValue(value));
			
			this.StrHash2ZP.put(newAttribute, value);
			this.ZPHash2Str.put(value, newAttribute);
		
		}
	}
	
	
	public void updateAttribute(String attribute)
	{
		if(this.ListOfAttributeNames.contains(attribute))
		{
			this.ZPHash2Str.remove(this.StrHash2ZP.get(attribute));
			this.StrHash2ZP.remove(attribute);
			this.addNewAttribute(attribute);
		}
		
		
	}
	
	public void removeAttribute(String attribute)
	{
		this.ListOfAttributeNames.remove(attribute);
		this.ZPHash2Str.remove(this.StrHash2ZP.get(attribute));
		this.StrHash2ZP.remove(attribute);
	}

	
	public BigInteger getImageOf(String attribute)
	{
		return this.StrHash2ZP.get(attribute);
	}
	
	public boolean HasImage(String attribute)
	{
		return (null != this.StrHash2ZP.get(attribute));
	}
	
	public boolean HasPreImage(BigInteger z)
	{
		return (null != this.ZPHash2Str.get(z));
	}
	
}
