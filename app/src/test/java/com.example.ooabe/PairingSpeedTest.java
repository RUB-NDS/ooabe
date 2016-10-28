package com.example.ooabe;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingParametersGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
//import it.unisa.dia.gas.jpbc.Element;
//import it.unisa.dia.gas.jpbc.Field;
//import it.unisa.dia.gas.jpbc.Pairing;
//import it.unisa.dia.gas.jpbc.PairingParameters;
//import it.unisa.dia.gas.jpbc.PairingParametersGenerator;
//import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;

public class PairingSpeedTest  {

	static PairingParametersGenerator pg128,pg256,pg512,pg1024;
	static PairingParameters pp128,pp256,pp512,pp1024;
	static Field G128, GT128, G256, GT256, G512, GT512;
	static Pairing e256, e512,e1024;
	static Element gA256,gB256,
	gA512,gB512,gA1024,gB1024;
	
	@BeforeClass
	public static void initPairing() 
	{
		pg256 = new TypeA1CurveGenerator(
			    3,  // the number of primes
			    256 // the bit length of each prime
			);
		pp256 = pg256.generate();
		e256 = PairingFactory.getPairing(pp256);
		gA256=e256.getG1().newRandomElement();
		gB256=e256.getG1().newRandomElement();
		
		
		pg512 = new TypeA1CurveGenerator(
			    3,  // the number of primes
			    512 // the bit length of each prime
			);
		pp512 = pg512.generate();
		e512 = PairingFactory.getPairing(pp512);
		gA512=e512.getG1().newRandomElement();
		gB512=e512.getG1().newRandomElement();
		
		
		pg1024 = new TypeA1CurveGenerator(
			    3,  // the number of primes
			   1024 // the bit length of each prime
			);
		pp1024=pg1024.generate();
		e1024 = PairingFactory.getPairing(pp1024);
		gA1024=e1024.getG1().newRandomElement();
		gB1024=e1024.getG1().newRandomElement();
		
		
	}
	
	@Test
	public void test256Pairing()
	{
		for(int i=0;i<100;i++)
		{
			e256.pairing(gA256, gB256);
		}
	}
	
	
	@Test
	public void test512Pairing()
	{
		for(int i=0;i<100;i++)
		{
			e512.pairing(gA512, gB512);
		}
	}
	
	@Test
	public void test1024Pairing()
	{
		for(int i=0;i<100;i++)
		{
			e1024.pairing(gA1024, gB1024);
		}
	}
	

}
