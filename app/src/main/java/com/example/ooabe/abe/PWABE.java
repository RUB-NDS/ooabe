package com.example.ooabe.abe;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingParametersGenerator;

public class PWABE extends RABE{

	public PWABE() {
		
		super();
	}
	
	public PWABE(int rBits, int qBits)
	{
		super(rBits, qBits);
	}
	
	public PWABE(BigInteger msk, PairingParametersGenerator pg, 
			PairingParameters params, Field Zp, Field G, Field GT,
			Pairing e, Element g, Element u, Element w, Element h, Element pk, boolean isServer)
	{
		super(msk, pg, 
				params, Zp, G, GT,
				e, g, u, w, h, pk, isServer);
	}
	
	/**
	 * 
	 * @param UID is the user ID string
	 * @param uidImage is the unique image of UID in Zp
	 * @param policy the key policy
	 * @param side indicates which half of the key is generated, true = side "1", false = side "0"
	 * @return a UserPrivateKey object for (UID, side)
	 */
	
//	@Override
//	public UserSecretKey genUSK(String UID,
//			KeyPolicy policy) throws Exception
//	{
//		throw new Exception("PWABE.genUSK(String UID,"+
//			"KeyPolicy policy): Please call UserPrivateKey genUSK(String UID, "
//			+ "BigInteger uidImage, KeyPolicy policy, boolean side) ");
//
//	}
	

	
	public UserSecretKey genUSK(String UID, BigInteger uidImage, 
			KeyPolicy policy, boolean side)
	{
		if(side)
		{
//			BigInteger uidImage = this.getZp().newRandomElement().toBigInteger();
			UserSecretKey usk=new UserSecretKey(UID);
			usk.setupUSK(msk.subtract(uidImage), g, h, u, w, policy, Zp, G);
			return usk;
		}
		else if(!side)
		{
//			BigInteger uidImage = this.getZp().newRandomElement().toBigInteger();
			UserSecretKey usk=new UserSecretKey(UID);
			usk.setupUSK(uidImage, g, h, u, w, policy, Zp, G);
			return usk;
			
		}
		else
			return null;
	}
	
	public Element decrypt(CipherText ct, UserSecretKey K1, UserSecretKey K0)
	{
		return ct.C.getImmutable().div(decap(ct, K1)).div(decap(ct, K0));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
