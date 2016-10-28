package com.example.ooabe.abe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingParametersGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

/**
 * Class implementing waters' ABE
 * @author LiDuan
 *
 */

public class RABE 
{
	
	/** fields of this class
	 * msk: master secret key
	 * 
	 * public parameters include
	 * 	e: pairing
	 * 	G: group
	 * 	GT: target group
	 * 	Zp: integer field Zp, the bit-length of p = qBits
	 * 	rbit: bit-length of order of the group
	 * 	qbit: bit-length of the integer q in F_q
	 * 	P: order of group
	 * 	U: Attribute universe
	 * 
	 * public keys 
	 * u: random element in G
	 * g: random element in G
	 * h: random element in G
	 * w: random element in G
	 * pk: = e(g,g)^msk
	 * 
	 * flag for client or server
	 * 
	 * 
	 */
	BigInteger msk;
	
	PairingParametersGenerator pg;
	PairingParameters params;
	Field Zp, G, GT;
	Pairing e;
	Element g, u, w, h;
	Element pk;
	
	boolean isServer;
	
	AttributeUniverse Universe;



	public RABE(BigInteger msk, PairingParametersGenerator pg, 
			PairingParameters params, Field Zp, Field G, Field GT,
			Pairing e, Element g, Element u, Element w, Element h, Element pk, boolean isServer) 
	{
		this.msk = msk;
		this.pg = pg;
		this.params = params;
		this.Zp = Zp;
		this.G = G;
		this.GT = GT;
		this.e = e;
		this.g = g;
		this.u = u;
		this.w = w;
		this.h = h;
		this.pk = pk;
		this.isServer = isServer;
	}


	/**
	 * @return the universe
	 */
	public AttributeUniverse getUniverse() {
		return Universe;
	}


	/**
	 * @param universe the universe to set
	 */
	public void setUniverse(AttributeUniverse universe) {
		Universe = universe;
	}


	/**
	 * Important methods
	 * 
	 * 1. Constructor
	 * 2. Encrypt
	 * 3. Decrypt
	 */
	
	public RABE()
	{
//		default generator, assuming server flag
//		security parameter 80
		int rBits = 160;
		int qBits = 512;
		this.pg = new TypeACurveGenerator(rBits, qBits);
		this.params = pg.generate();
		this.e = PairingFactory.getPairing(this.params);
		
		this.G = e.getG1();
//		P is NOT "mod P" as in the EC formula, but the order of the group 
		this.Zp = e.getZr();
		this.GT = e.getGT();
		
//		make sure that all public keys are immutable
		this.g = G.newRandomElement().getImmutable();
		this.u = G.newRandomElement().getImmutable();
		this.h = G.newRandomElement().getImmutable();
		this.w = G.newRandomElement().getImmutable();
		
		this.msk=this.Zp.newRandomElement().toBigInteger();
		
		this.pk = (e.pairing(g, g).pow(msk)).getImmutable();
		
		
		this.isServer = true;
	}
	
	/**
	 * 
	 * @param rBits bit-length of large prime used in mod-op
	 * @param qBits bit-length of field order
	 */
	public RABE(int rBits, int qBits)
	{
//		default generator, assuming server flag
//		security parameter 80

		this.pg = new TypeACurveGenerator(rBits, qBits);
		this.params = pg.generate();
		this.e = PairingFactory.getPairing(this.params);
		
		this.G = e.getG1();
//		P is NOT "mod P" as in the EC formula, but the order of the group 
		this.Zp = e.getZr();
		this.GT = e.getGT();
		
//		make sure that all public keys are immutable
		this.g = G.newRandomElement().getImmutable();
		this.u = G.newRandomElement().getImmutable();
		this.h = G.newRandomElement().getImmutable();
		this.w = G.newRandomElement().getImmutable();
		
		this.msk=this.Zp.newRandomElement().toBigInteger();
		
		this.pk = (e.pairing(g, g).pow(msk)).getImmutable();
		
		
		this.isServer = true;
	}
	
	
	/**
	 * 
	 * @param m, plaintext, given as element in the group
	 * @param attributes, list of BigInteger used in computing ciphertexts
	 * @return an object of class CipherText, containing the list of attributes as BigInteger
	 */
	public CipherText encrypt(Element m, ArrayList<BigInteger> attributes)
	{
		CipherText ct = new CipherText(attributes);
		
//		pick up an s		
		Element s = this.Zp.newRandomElement().getImmutable();
		Element pkImm = this.pk.getImmutable();
		
//		C = m . e(g,g)^(msk . s)
		Element C= this.GT.newElement();
//		Element.set is better than "=" to ensure "pass by value"
		C.set(pkImm.powZn(s));
		C.mul(m);
		ct.C = C;
		
		
//		C0 = g^s
		ct.C0 = this.g.powZn(s);
		Element r_tau=this.Zp.newElement();
		Element C2_tau = this.getG().newElement();
		
		
		for(BigInteger A_tau: attributes)
		{
//			C1_tau = g^{r_\tau}
			r_tau.setToRandom();
			ct.C1.add(this.g.powZn(r_tau));

//			C2_tau = (u^A_tau * h )^ r_tau * w^{-s}
			C2_tau.set(this.u);
			C2_tau.pow(A_tau);
			C2_tau.mul(this.h);
			C2_tau.powZn(r_tau);
			C2_tau.mul(this.w.powZn(s.negate()));
			ct.C2.add(C2_tau.duplicate());
		}

//		System.out.println("Encryption ends!");
		return ct;
	}
	
	public UserSecretKey genUSK(String UID, KeyPolicy policy)
			throws Exception
	{
		UserSecretKey usk=new UserSecretKey(UID);
		usk.setupUSK(msk, g, h, u, w, policy, Zp, G);
		return usk;
	}
	
	public Element decrypt(CipherText ct, UserSecretKey usk)
	{
		return ct.C.getImmutable().div(decap(ct,usk));
	}
	
	public Element decap(CipherText ct, UserSecretKey usk)
	{
		Element m = this.GT.newOneElement();
		ArrayList<Integer> Wi=getWi(usk.getPolicy(),ct.attributes);
		int lenWi = Wi.size();
//		3 temp variables to hold the 3 pairing result
		Element p1,p2,p3;
//		temp variable to hold the product of 3 elements listed above
		Element product;
		
//		 m = C/B
//		now start the loop to compute B
		
		Element B = this.GT.newOneElement();
		int C_index;
		for(int i=0; i<lenWi; i++)
		{
//			(re-)init the temp variable for product
			product = this.GT.newOneElement();
			
			if(Wi.get(i) == 1)
			{
//				find the right c0, c1 in the ciphertext 
				
				C_index = ct.attributes.indexOf(usk.policy.rho2ZP.get(i).toBigInteger());
				
				p1 = this.e.pairing(ct.C0, usk.K0.get(i));
				p2 = this.e.pairing(ct.C1.get(C_index), usk.K1.get(i));
				p3 = this.e.pairing(ct.C2.get(C_index), usk.K2.get(i));
				
				product.mul(p1);
				product.mul(p2);
				product.mul(p3);
				
				B.mul(product);
			}
			
		}
		
		
//		System.out.println("B is "+B);
//		System.out.println("C is "+ct.C);
//		do not change the content of ct
//		m = ct.C.getImmutable().div(B);
//		System.out.println("The decipherd m is "+m);
		return B;
	}
	
	public ArrayList<Integer> getWi(KeyPolicy policy, ArrayList<BigInteger> attributes)
	{
		ArrayList<Integer> Wi = new ArrayList<Integer>();
		if(null == policy.formulaArrayList || policy.formulaArrayList.size()<1)
		{
//			if the policy is "raw", go for linear equation solving
//			(1,1,1, ...) as the default returning value
//			TODO: solve LES
			for(int  i = 0; i < policy.getM().size(); i++)
			{
				Wi.add(new Integer(1));
			}
			return Wi;
		}

//		use the SAT-oracle (here, the evalBooleanFormula() method) 
//		to select a minimum sized solution
		int NumOfLeaves = policy.getM().size();
		ArrayList<Boolean> values = new ArrayList<Boolean>();
//		initialize the entries. If the corresponding attribute is presented, set 
//		the value at the position as "true".
		for(int  i = 0; i<NumOfLeaves; i++ )
		{
			if(attributes.contains(policy.rho2ZP.get(i).toBigInteger()))
			{
				values.add(true);
			}
			else
			{
				values.add(false);
			}
		}
		
		try
		{
			for(int  i = 0; i<NumOfLeaves; i++ )
			{
				if(values.get(i))
				{
					values.set(i, false);
					if(!evalBooleanFormular(policy.formulaArrayList, values))
					{
						values.set(i, true);
					}
				}	
			}
			
			for(int  i = 0; i<NumOfLeaves; i++ )
			{
				if(values.get(i))
				{
					Wi.add(1);
				}
				else
				{
					Wi.add(0);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
//		System.out.println("The size of Wi is "+ Wi.size());
		return Wi;
	}
	
	public static boolean evalBooleanFormular(ArrayList<String> formula, 
			ArrayList<Boolean> values)
	throws Exception
	{
		if(null == formula || formula.size() < 1 || null == values
				|| values.size() < 1)
		{
			throw new Exception("evalBooleanFormular(): "
					+ "Invalid input, formula/values not initialized or contains no operands!");
		}
		
		
		int formLen = formula.size();
		Stack<Boolean> evalStack = new Stack<Boolean>();
		Boolean op1, op2;
		for(int i=0; i<formLen; i++)
		{
			if("AND".equals(formula.get(i)))
			{
				op1 = evalStack.pop();
				op2 = evalStack.pop();
				evalStack.push(new Boolean(op1 && op2));
			}
			else if("OR".equals(formula.get(i)))
			{
				op1 = evalStack.pop();
				op2 = evalStack.pop();
				evalStack.push(new Boolean(op1 || op2));
			}
			else
			{
				evalStack.push(values.get(Integer.valueOf(formula.get(i))));
			}
		}
		
		return evalStack.pop();
	}

	public IntermediateCipherText generateIntermediateCipherText( int num_of_attributes)
	{
		IntermediateCipherText ict = new IntermediateCipherText();

//		pick up an s
		Element s = this.Zp.newRandomElement().getImmutable();
		Element pkImm = this.pk.getImmutable();

//		EK = e(g,g)^(msk . s)
		Element EK= this.GT.newElement();
//		Element.set is better than "=" to ensure "pass by value"
		EK.set(pkImm.powZn(s));

		ict.EK = EK;


//		C0 = g^s
		ict.C0 = this.g.powZn(s);
		Element r_tau=this.Zp.newElement();
        Element x_tau = this.Zp.newElement();
		Element C2_tau = this.getG().newElement();


		for(int i = 0; i< num_of_attributes; i++)
		{

			r_tau.setToRandom();
            ict.Rj.add(r_tau.duplicate());
//          C1_tau = g^{r_\tau}
			ict.C1.add(this.g.powZn(r_tau));


//			C2_tau = (u^x_tau * h )^ r_tau * w^{-s}

            x_tau.setToRandom();
            ict.Xj.add(x_tau.duplicate());

            C2_tau.set(this.u);
			C2_tau.powZn(x_tau);
			C2_tau.mul(this.h);
			C2_tau.powZn(r_tau);
			C2_tau.mul(this.w.powZn(s.negate()));
			ict.C2.add(C2_tau.duplicate());
		}

//		System.out.println("Encryption ends!");
		return ict;

	}

    public CipherText onlineEncrypt(IntermediateCipherText ict, Element m, ArrayList<BigInteger> attributes)
    {
        if(null == ict|| 0 == ict.Rj.size() || attributes.size() > ict.Rj.size())
            return null;

        CipherText ct = new CipherText(attributes);

//		C = m .EK
        Element C= this.GT.newElement();
//		Element.set is better than "=" to ensure "pass by value"
        C.set(ict.EK);
        C.mul(m);
        ct.C = C;


//		C0 set as ict.C0
        ct.C0=ict.C0.duplicate();
        Element cj3 = Zp.newElement();



        int i = 0;
        for(BigInteger A_tau: attributes)
        {
//			cj3 = (A_tau - x_tau) * r_tau
            cj3.set(A_tau);
            cj3.sub(ict.Xj.get(i));
            cj3.mul(ict.Rj.get(i));

            ct.C1.add(ict.C1.get(i).duplicate());
//            C2 = C2 * u^cj3
            ct.C2.add(ict.C2.get(i).duplicate().mul(this.u.powZn(cj3)));
            i++;
        }

//		System.out.println("Encryption ends!");
        return ct;
    }


	public Field getZp() {
		return Zp;
	}


	public void setZp(Field zp) {
		Zp = zp;
	}


	public Field getG() {
		return G;
	}

	public Element getGeneratorG()
	{
		return g;
	}

	public Field getGT() {
		return GT;
	}


	public Pairing getE() {
		return e;
	}


	public void setE(Pairing e) {
		this.e = e;
	}


	public Element getg() {
		return g;
	}


	public void setG(Element g) {
		this.g = g;
	}


	public Element getU() {
		return u;
	}


	public void setU(Element u) {
		this.u = u;
	}


	public Element getW() {
		return w;
	}


	public void setW(Element w) {
		this.w = w;
	}


	public Element getH() {
		return h;
	}

	public BigInteger getMSK()
	{
		return this.msk;
	}

	public void setH(Element h) {
		this.h = h;
	}


	public Element getPk() {
		return pk;
	}


	public void setPk(Element pk) {
		this.pk = pk;
	}
	
	public static void main(String[] args)
	{
		System.out.println("Starting test the basic enc/dec");
		RABE myABE = new RABE();

		
//		check if g, u, h, w are indeed immutable;
		Element original_h = myABE.getH().duplicate();
		myABE.getH().pow(new BigInteger("2"));
		
		if(original_h.isEqual(myABE.getH()))
		{
			System.out.println("h is indeed immutable");
		}
		
		Element g = myABE.getG().newElement();
		g.set(myABE.getg());
		g.pow(new BigInteger("2"));
		
		if(g.isEqual(myABE.getg()))
			System.out.println("set(Element) uses assign by reference");
		
//		try genUSK
		
//	try the enc and dec	
		
		ArrayList<BigInteger> listAttributes = new ArrayList<BigInteger>();
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
		
		HashMap<Integer, String> rho = new HashMap<Integer, String>();
		rho.put(new Integer(0), "A");
		HashMap<Integer, Element> rho2ZP = new HashMap<Integer, Element>();
		rho2ZP.put(new Integer(0),myABE.Zp.newElement().set(new BigInteger("1")));
		rho2ZP.put(new Integer(1),myABE.Zp.newElement().set(new BigInteger("2")));
		rho2ZP.put(new Integer(2),myABE.Zp.newElement().set(new BigInteger("3")));
		
		KeyPolicy myPolicy = new KeyPolicy(rho, rho2ZP, M);
		
		
		UserSecretKey usk = new UserSecretKey("uid");
		usk.setupUSK(myABE.msk, myABE.g, myABE.h, myABE.u, myABE.w, myPolicy,
				myABE.Zp, myABE.G);
//		usk.printKeyInfo();
		System.out.println("usk setup completed!");



		Element m = myABE.getGT().newRandomElement();
		System.out.println("The message m is (as group element)");
		System.out.println(m);
		
//		If m is encrypted with A, B, C		
//		Wi should be {1,1,1}
		CipherText ct = myABE.encrypt(m, listAttributes);
		Element m_dec = myABE.decrypt(ct, usk);
		
		if(m_dec.isEqual(m))
		{
			System.out.println("Decryption is correct!");
		}
		else
		{
			System.out.print("m is ");
			System.out.println(m);
			System.out.print("m_dec is ");
			System.out.println(m_dec);
		}
	}
}
