package com.example.ooabe;

import junit.framework.TestCase;

import static org.junit.Assert.*;

//import org.junit.BeforeClass;
import org.junit.Test;

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
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;;

public class parameterGenTest {
	
	PairingParameters pp128,pp256,pp512,pp1024,pp2048;
	Pairing e128,e256,e512,e1024,e2048;
	
	
//	@Test
//	public void test128() 
//	{
//		PairingParametersGenerator pg = new TypeA1CurveGenerator(
//			    3,  // the number of primes
//			    128 // the bit length of each prime
//			);
//		pp128=pg.generate();
//		e128 = PairingFactory.getPairing(this.pp128);
//		
//		
//	}
	
//	@Test
//	public void test256()
//	{
//		PairingParametersGenerator pg = new TypeA1CurveGenerator(
//			    3,  // the number of primes
//			    256 // the bit length of each prime
//			);
//		pp256=pg.generate();
//		e256 = PairingFactory.getPairing(this.pp256);
//		
//	}
	
	@Test
	public void test512()
	{
		PairingParametersGenerator pg = new TypeA1CurveGenerator(
			    3,  // the number of primes
			    512 // the bit length of each prime
			);
		pp512=pg.generate();
		e512 = PairingFactory.getPairing(this.pp512);
	}
	
	@Test
	public void test1024()
	{
		PairingParametersGenerator pg = new TypeA1CurveGenerator(
			    3,  // the number of primes
			    1024 // the bit length of each prime
			);
		pg.generate();
		pp1024=pg.generate();
		e1024= PairingFactory.getPairing(this.pp1024);

	}
	
//	@Test
//	public void test2048()
//	{
//		PairingParametersGenerator pg = new TypeA1CurveGenerator(
//			    3,  // the number of primes
//			    2048 // the bit length of each prime
//			);
//		pg.generate();
//		pp2048=pg.generate();
//		e2048 = PairingFactory.getPairing(this.pp2048);
//
//	}

}
