package com.example.ooabe.utils.tree;

import java.math.BigInteger;
import java.util.ArrayList;

import com.example.ooabe.abe.AttributeUniverse;
import com.example.ooabe.abe.UserSecretKey;
import it.unisa.dia.gas.jpbc.Field;

/**
 * 
 * @author LiDuan
 *
 */
public class UserNode extends BNode {
	
//	inheritated parameters/fields
	
//	public String name;
//	
////	catagory : 0x00 AND 0x01 OR 0x02 attribute
//	public byte catagory;
//	
//	public ArrayList<Integer> code;
//	public BNode leftChild;
//	public BNode rightChild;
//	public BNode parent;
	
	public String uid;
	public BigInteger u_id_Z; 
	public UserNodeStatus status;
	
	UserSecretKey usk;

	public UserNode() {
		// TODO Auto-generated constructor stub
		super();
		
	}
	
	public UserNode(String uid, AttributeUniverse U)
	{
		this.uid = uid;
		U.addNewAttribute(uid);
		u_id_Z = U.getImageOf(uid);
		this.status = UserNodeStatus.ACTIVE;
	}

//	inheritated methods
//	addLC(BNode), addRC(BNode), setParent(BNode)
	
	public void setActive()
	{
		this.status = UserNodeStatus.ACTIVE;
	}
	
	public void setRemoved()
	{
		this.status = UserNodeStatus.REMOVED;
	}
	
	public boolean isActive()
	{
		return (this.status == UserNodeStatus.ACTIVE);
	}
	
	public boolean isRemoved()
	{
		return (this.status == UserNodeStatus.REMOVED);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
