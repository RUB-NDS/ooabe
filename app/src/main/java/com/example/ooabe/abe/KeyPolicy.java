package com.example.ooabe.abe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * 
 * @author LiDuan
 *
 */
import it.unisa.dia.gas.jpbc.*;

public class KeyPolicy 
{
	/**
	 * 4 basic fields
	 * rho: the mapping function described in the original paper, which maps a row in the matrix to
	 * the name of an attribute
	 * 
	 * rho2ZP: the mapping function,  which directly maps a row in the matrix to
	 * the image in Zp of an attribute
	 * 
	 * InvRho: mapping an attribute string to the list of all indices mapped to this string.
	 * 
	 * M: The matrix, which stores the policy
	 * 
	 * The 5-th field operator stack is for speeding-up decryption
	 */
	HashMap<Integer, String> rho;
	HashMap<Integer, Element> rho2ZP;
	
//	mapping an attribute to a list of row indices
	HashMap<String, ArrayList<Integer>> InvRho;
	ArrayList<ArrayList<Integer>> M;
	
//	formula represented as a stack of integer string(as operands)�� "AND" and "OR" (opearators), 
//	used to efficiently select a stable integer solution during decryption
//	instead of solving indefinite linear equation system
	ArrayList<String> formulaArrayList;
	




	public KeyPolicy(HashMap<Integer, String> rho,
	HashMap<Integer, Element> rho2ZP, ArrayList<ArrayList<Integer>> M)
	{
		this.rho = rho;
		this.rho2ZP = rho2ZP;
		this.M = M;
	}
	
	public KeyPolicy(HashMap<Integer, String> rho,
	HashMap<Integer, Element> rho2ZP, ArrayList<ArrayList<Integer>> M,
	ArrayList<String> operatorStack)
	{
		this.rho = rho;
		this.rho2ZP = rho2ZP;
		this.M = M;
		this.formulaArrayList = operatorStack;
	}


	/**
	 * @return the rho
	 */
	public HashMap<Integer, String> getRho() {
		return rho;
	}


	/**
	 * @return the rho2ZP
	 */
	public HashMap<Integer, Element> getRho2ZP() {
		return rho2ZP;
	}


	/**
	 * @return the M matrix
	 */
	public ArrayList<ArrayList<Integer>> getM() {
		return M;
	}

	public HashMap<String, ArrayList<Integer>> getInvRho()
	{
		return this.InvRho;
	}
	
	public void setInvRho(HashMap<String, ArrayList<Integer>> InvRho)
	{
		this.InvRho = InvRho;
	}
	
	/**
	 * @return the operatorStack
	 */
	public ArrayList<String> getOperatorStack() {
		return formulaArrayList;
	}


	/**
	 * @param operatorStack the operatorStack to set
	 */
	public void setOperatorStack(ArrayList<String> operatorStack) {
		this.formulaArrayList = operatorStack;
	}
	
	public void printPolicy()
	{
		Integer index = 0;
		for(ArrayList<Integer> row: this.M)
		{
			System.out.print("row " +  index + " attribute " + rho.get(index)+" "+ rho2ZP.get(index)+":");
			for(Integer i: row)
			{
				System.out.print(i +" ");
			}
			System.out.println();
			index++;
		}
		
		for(String s: this.InvRho.keySet())
		{
			System.out.print("Attribute "+ s + " is mapped to row(s): ");
			for(Integer i : InvRho.get(s))
			{
				System.out.print(i+" ");
			}
			System.out.println();
		}
		
		if(null != this.formulaArrayList)
		{
			System.out.print("The formula for this policy can be printed in post-order as: ");
			for(String op: formulaArrayList)
			{
				System.out.print(op+" ");
			}
			System.out.println();
		}
		else
		{
			System.out.println("formulaArrayList not set or initialized!");
		}
	}
}
