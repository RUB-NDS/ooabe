package com.example.ooabe.abe;

import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

public class UserSecretKey 
{
	KeyPolicy policy;
	String UserID;
	ArrayList<Element> K0;
	ArrayList<Element> K1;
	ArrayList<Element> K2;
	
	public UserSecretKey(String UID)
	{
		this.UserID = UID;
		this.K0 = new ArrayList<Element>();
		this.K1 = new ArrayList<Element>();
		this.K2 = new ArrayList<Element>();
	}
	
	public void setupUSK(BigInteger msk, Element g, Element h, 
			Element u, Element w, KeyPolicy policy, Field Zp, Field G)
	{
//		Element g_imm = g.getImmutable();
//		Element u_imm = u.getImmutable();
//		Element h_imm = h.getImmutable();
//		Element w_imm = w.getImmutable();
		
//		get the shares from M
//		Build the vector to share
		this.policy = policy;
		ArrayList<BigInteger> Y = new ArrayList<BigInteger>();
		BigInteger alpha = msk;
//		y1 = alpha
		Y.add(alpha);
		
//		y2 ... yn random in ZP
		int numOfRows = policy.getM().size();
		for(int i=1; i<numOfRows; i++)
		{
			Y.add(Zp.newRandomElement().toBigInteger());
		}
		
//		System.out.println("The size of the sharing vector Y is "+ Y.size());
//		declare temporary key components for row tau
		Element K_tau0 , K_tau1 , K_tau2;
		
//		compute the key components
//		starting from the first row
		int rowIndex = 0;
		int columnIndex = 0;
		int numOfColomns = policy.getM().get(0).size();
		BigInteger lambda_tau;
		Element t_tau;
		
		for(ArrayList<Integer> row : policy.getM() )			
		{			
//			(re-)init all temporary variables
			lambda_tau = new BigInteger("0");
			t_tau = Zp.newRandomElement();
			K_tau0 = G.newElement();
			K_tau1 = G.newElement();
			K_tau2 = G.newElement();
			
//			compute the share of the current row:= M_tau * Y
			for(columnIndex=0; columnIndex<numOfColomns; columnIndex++)
			{
				lambda_tau= lambda_tau.add(Y.get(columnIndex).multiply(
						BigInteger.valueOf(row.get(columnIndex))));
				
			}
			lambda_tau=lambda_tau.mod(Zp.getOrder());
			
			
//			K_tau0 = g^{\lambda_tau} * w^{t_tau}
			K_tau0.set(g);
			K_tau0.pow(lambda_tau);
			K_tau0.mul(w.duplicate().powZn(t_tau));
			if(K_tau0.isEqual(g))
				System.out.println("K_tau0 unchanged!");
			
//			K_tau1={ u^{A_tau} * h } ^ {-t_tau}
			Element A_tau = policy.getRho2ZP().get(rowIndex);
			K_tau1.set(u);
//			Element t = t_tau.getImmutable();
			K_tau1.powZn(A_tau);
			K_tau1.mul(h);
			K_tau1.powZn(t_tau.duplicate().negate());
//			if(t_tau.isEqual(t))
//				System.out.println("t_tau unchanged after t_tau.duplicate().negate()");
			
//			K_tau2 = g^t_tau
			K_tau2.set(g);
			K_tau2.powZn(t_tau);
			
			this.K0.add(K_tau0);
			this.K1.add(K_tau1);
			this.K2.add(K_tau2);
			
//			move to the next row	
			rowIndex++;
		}
	}

	public void printKeyInfo()
	{
		String result="";
		result += "The usk information:\n";
		result += "User ID is "+ this.UserID + "\n";
		result += "The size of M is " + this.policy.getM().size()
				+ " * " + this.policy.getM().get(0).size()+"\n";
		result += "The number of elements in K0 component is " + this.K0.size()+"\n";
		result += "The first element of K0 is "+ this.K0.get(0).toString()+"\n";
		result += "The second element of K0 is "+ this.K0.get(1).toString()+"\n";
		System.out.println(result);
	}
	
	/**
	 * @return the policy
	 */
	public KeyPolicy getPolicy() {
		return policy;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return UserID;
	}

	/**
	 * @return the k0
	 */
	public ArrayList<Element> getK0() {
		return K0;
	}

	/**
	 * @return the k1
	 */
	public ArrayList<Element> getK1() {
		return K1;
	}

	/**
	 * @return the k2
	 */
	public ArrayList<Element> getK2() {
		return K2;
	}
	
}
