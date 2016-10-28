package com.example.ooabe.utils.tree;

import java.util.ArrayList;

public class BNode {
	
	public String Attribute;
	
//	catagory : 0x00 AND 0x01 OR 0x02 attribute
	public byte category;
	
	public ArrayList<Integer> code;
	public BNode leftChild;
	public BNode rightChild;
	public BNode parent;
	
	
	public BNode()
	{
		this.Attribute = "";
//		by default, an invalid node
		this.category = (byte)0xFF;
		this.code = new ArrayList<Integer>();
		this.leftChild = null;
		this.rightChild = null;
		this.parent = null;
	}
	
	public void addLC(BNode lc)
	{
		this.leftChild = lc;
		lc.parent=this;
	}
	
	public void addRC(BNode rc)
	{
		this.rightChild = rc;
		rc.parent = this;
	}
	
	public void setParent(BNode parent)
	{
		this.parent = parent;
	}
	


	/**
	 * @param category the category to set
	 */
	public void setCategory(byte category) {
		this.category = category;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
