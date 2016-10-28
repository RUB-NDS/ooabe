package com.example.ooabe.utils.tree;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.example.ooabe.abe.AttributeUniverse;
import com.example.ooabe.abe.KeyPolicy;
import com.example.ooabe.abe.RABE;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import com.example.ooabe.parsers.f2t.*;

public class PolicyTree {
	
	public BNode rootNode;
	public String policyStr;
	public Form2Tree parserTree;
	public int codeLen = 0;

	public PolicyTree() {
		this.rootNode = new BNode();
	}
	
	public PolicyTree(String policyStr) throws Exception{
		
		parserTree = new Form2Tree((Reader)(new StringReader(policyStr))); 
		System.out.println(parserTree.Start(parserTree.rootNode));
		this.rootNode = parserTree.rootNode;
		PolicyTree.encodePolicyTree(this);
	}
	
	public static int encodePolicyTree(PolicyTree pt )
	throws Exception
	{
		BNode rootNode = pt.rootNode;
		if(null == rootNode)
		{
			throw new Exception("The policy (sub-)tree has not been initialized");
		}
		
		int counterC = 1;
		Queue<BNode> BNodeQ = new LinkedList<BNode>();
		BNodeQ.add(rootNode);
		rootNode.code.add(new Integer(1));
		BNode node;
		
		while(!BNodeQ.isEmpty())
		{
			node = BNodeQ.remove();
			
			if(node.category == (byte)0x01) //if OR
			{
				ArrayList<Integer> parentCode = node.code;
				if(null != node.leftChild)
					BNodeQ.add(node.leftChild);
				if(null != node.rightChild)
					BNodeQ.add(node.rightChild);
				for(Integer i:parentCode)
				{
					node.leftChild.code.add(new Integer(i.intValue()));
					node.rightChild.code.add(new Integer(i.intValue()));
				}
				
				continue;
			}
			
			
			if(node.category == (byte)0x00) //if AND
			{					
				counterC++;
				int CodeLenDiff = counterC - node.code.size();
			
				if(null != node.leftChild)
				{
					BNodeQ.add(node.leftChild);
					
					ArrayList<Integer> parentCode = node.code;
					
					for(Integer i:parentCode)
					{
						node.leftChild.code.add(new Integer(i.intValue()));
					}
	//				pad the code with 0 if necessary
					
					for(int j = 0; j<CodeLenDiff-1;j++)
					{
						node.leftChild.code.add(new Integer(0));
					}
//					append 1
					node.leftChild.code.add(new Integer(1));
					
				}
				
				if(null != node.rightChild)
				{
					BNodeQ.add(node.rightChild);
					ArrayList<Integer> parentCode = node.code;
					for(Integer i:parentCode)
					{
						node.rightChild.code.add(new Integer(0));
					}
	//				pad the code if necessary
					
					for(int j = 0; j<CodeLenDiff-1;j++)
					{
						node.rightChild.code.add(new Integer(0));
					}
//					append -1 
					node.rightChild.code.add(new Integer(-1));
				}
			}
		}
		
		pt.codeLen = counterC;
		
//		postorderIter(pt.rootNode);
		
		return counterC;

	}
	
	public static void printTree(BNode rootNode)
	{
//		pre-order traverse the tree
		if(null != rootNode )
		{
			if(rootNode.category == (byte)0x02)
				System.out.print(rootNode.Attribute + ":");
			else if(rootNode.category == (byte)0x00)
				System.out.print("AND :");
			else if(rootNode.category == (byte)0x01)
				System.out.print("OR :");
			
			for(Integer i:rootNode.code)
			{
				System.out.print(i);
			}
			
			System.out.println();
			
			
			if(null != rootNode.leftChild)
				printTree(rootNode.leftChild);
			
			if(null != rootNode.rightChild)
				printTree(rootNode.rightChild);
		}
	}
	
	public KeyPolicy toKeyPolicy(AttributeUniverse U, boolean Enhanced)
	{
		System.out.println("**Start transferring PolicyTree to Policy");

		HashMap<Integer, String> rho =  new HashMap<Integer, String>();
		HashMap<Integer, Element> rho2ZP =new HashMap<Integer, Element>();
		HashMap<String, ArrayList<Integer>> InvRho = new HashMap<String, ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> M = new ArrayList<ArrayList<Integer>>();
		ArrayList<String> formulaArrayList = new ArrayList<String>();
		
		
		if(this.rootNode == null 
				|| this.rootNode.category == (byte)0xFF || this.codeLen == 0)
			return null;
		
		int rowIndex = 0;
		
	   
		Stack<BNode> s = new Stack<BNode>( );
	    BNode node = this.rootNode;
//	    s.push(node);
   
	    
	    do
	    {
	    	
          if( node != null ) 
          {
              if( node.rightChild != null ) 
				s.push( node.rightChild );
				s.push( node );
              	node = node.leftChild;
              continue;
          }

          node = s.pop( );
   
          if( node.rightChild != null && ! s.isEmpty( ) 
        		  && node.rightChild == s.peek( ) ) 
          {
              s.pop( );
              s.push( node );
              node = node.rightChild;
          } 
          else 
          {
        	  if(node.category == (byte)0x00)
        	  {
        		  System.out.println("AND");
        		  formulaArrayList.add("AND");
        	  }
        	  else if(node.category == (byte)0x01)
        	  {
        		  System.out.println("OR");
        		  formulaArrayList.add("OR");
        	  }
        	  else
        	  {
        		  rho.put(new Integer(rowIndex), node.Attribute);
  				
  				if(!U.HasImage(node.Attribute))
  					U.addNewAttribute(node.Attribute);
  				rho2ZP.put(new Integer(rowIndex), U.Zp.newElement(U.getImageOf(node.Attribute)));
  				
  				if(InvRho.containsKey(node.Attribute))
  					InvRho.get(node.Attribute).add(rowIndex);
  				else
  				{
  					ArrayList<Integer> indexList = new ArrayList<Integer>();
  					indexList.add(rowIndex);
  					InvRho.put(node.Attribute, indexList);
  				}
  				
  			
  				ArrayList<Integer> currentRow = new ArrayList<Integer>();
  				for(Integer i: node.code)
  				{
  					currentRow.add(i);
  				}
  				for(int j=0; j<this.codeLen-node.code.size();j++)
  				{
  					currentRow.add(new Integer(0));
  				}
  				M.add(currentRow);
  				formulaArrayList.add(Integer.toString(rowIndex));
  				rowIndex++;
        		  System.out.println( node.Attribute);
        		  
        	  }
        	  node = null;
          }
      }while( !s.isEmpty( ) ) ;

		KeyPolicy myPolicy =  new KeyPolicy(rho,
				rho2ZP, M, formulaArrayList);
		myPolicy.setInvRho(InvRho);
		
		System.out.println("**End transferring PolicyTree to Policy");
		
		return myPolicy;
	   
  }
	
	
	public KeyPolicy toKeyPolicy(AttributeUniverse U)
	{
		HashMap<Integer, String> rho =  new HashMap<Integer, String>();
		HashMap<Integer, Element> rho2ZP =new HashMap<Integer, Element>();
		HashMap<String, ArrayList<Integer>> InvRho = new HashMap<String, ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> M = new ArrayList<ArrayList<Integer>>();
		
//		if not encoded, return null
		if(this.rootNode.category == (byte)0xFF || this.codeLen == 0)
			return null;
		
		int rowIndex = 0;
		Queue<BNode> BNodeQ = new LinkedList<BNode>();
		BNodeQ.add(this.rootNode);
		BNode node;
		

		while(!BNodeQ.isEmpty())
		{
			node = BNodeQ.remove();
//			if an attribute node
			if(node.category == (byte)0x02)
			{
				rho.put(new Integer(rowIndex), node.Attribute);
				
				if(!U.HasImage(node.Attribute))
					U.addNewAttribute(node.Attribute);
				rho2ZP.put(new Integer(rowIndex), U.Zp.newElement(U.getImageOf(node.Attribute)));
				
				if(InvRho.containsKey(node.Attribute))
					InvRho.get(node.Attribute).add(rowIndex);
				else
				{
					ArrayList<Integer> indexList = new ArrayList<Integer>();
					indexList.add(rowIndex);
					InvRho.put(node.Attribute, indexList);
				}
				
			
				ArrayList<Integer> currentRow = new ArrayList<Integer>();
				for(Integer i: node.code)
				{
					currentRow.add(i);
				}
				for(int j=0; j<this.codeLen-node.code.size();j++)
				{
					currentRow.add(new Integer(0));
				}
				M.add(currentRow);
				
				rowIndex++;
			}
		
			if(null != node.leftChild)
				BNodeQ.add(node.leftChild);
			if(null != node.rightChild)
				BNodeQ.add(node.rightChild);
		}
		
		KeyPolicy myPolicy =  new KeyPolicy(rho,
				rho2ZP, M);
		myPolicy.setInvRho(InvRho);
		
		return myPolicy;
	}
	
	public static void main(String[] args) 
	{
		String policyStr="((Alice*Research)*Engineer)+(Alice*ID198);";
		try {
			PolicyTree pt = new PolicyTree(policyStr);
			
//			pt.codeLen = PolicyTree.encodePolicyTree(pt);
			PolicyTree.printTree(pt.rootNode);
			
			
			RABE myABE = new RABE();
			String[] attributes = {"Alice","Research","Engineer","ID198"};
			ArrayList<String> attrList = new ArrayList<String>( Arrays.asList(attributes));
			AttributeUniverse U = new AttributeUniverse(myABE.getZp(), attrList);
			KeyPolicy myPolicy = pt.toKeyPolicy(U,true);
			myPolicy.printPolicy();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	

}
