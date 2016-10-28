package com.example.ooabe.utils.tree;

import java.math.BigInteger;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.LinkedList;
//import java.util.Set;

import com.example.ooabe.abe.AttributeUniverse;

public class UidTree {
	
//	the size of the user group, assume it's less than 2^10-1
	public static final int maxNumOfUsers=1024;
	public static final int maxLevelOfTree=10;

	UserNode rootUserNode;
//	record of the available leaf slots in the current tree
	LinkedList<UserNode> availabeLeaves;
//	list of the current users
	HashMap<String, UserNode> users;

	int currentNumUsers;
	int currentLevelOfTree;
	
	AttributeUniverse U;
	
	public UidTree(String rootUID) {
		this.rootUserNode = new UserNode();
		users = new HashMap<String,UserNode>();
		this.rootUserNode.uid = rootUID;
		this.rootUserNode.u_id_Z=new BigInteger("0");
		this.availabeLeaves = new LinkedList<UserNode>() ;
	}
	/**
	 * 
	 * @param NumOfUsers, the initial number of users
	 * @param U, attribute universe, which contains the allowed user names 
	 * 	and the image space Zp
	 */
	public void setupUidTree(int NumOfUsers, AttributeUniverse U)
	{
//		compute log_2 ( maxNumberOfUsers)
		
//		set up the root uid
	}
	
	public void removeUser(String uid)
	{
		UserNode currentUserNode = this.users.get(uid);
		

		if(null != currentUserNode && 
				!currentUserNode.uid.equals(this.rootUserNode.uid)
				)
		{
//			add this leaf to the list of available leaves
			if(!currentUserNode.isRemoved())
			{
				this.availabeLeaves.add(this.users.get(uid));
			}
			
//			mark all node as "removed" on the path from root to this user node
			while(!currentUserNode.isRemoved()) 
			{
				currentUserNode.setRemoved();
				if(currentUserNode != this.rootUserNode)
					currentUserNode = (UserNode)currentUserNode.parent;				
			}
			
			this.users.remove(uid);
			this.currentNumUsers--;
			
		}
	}
	
	public void addUser(String uid)
	{
//		add the new user as a leaf node,
//		and extend the sub-tree at the possible lowest level
		if(this.currentNumUsers >= maxNumOfUsers)
			return;
		
		if(users.containsKey(uid))
			return;
		
//		if there is an available leaf slot, use it
		if(!this.availabeLeaves.isEmpty())
		{
//			pop out the leaf slot
			UserNode node = this.availabeLeaves.pop();
//			add the new user to the mapping
			this.U.addNewAttribute(uid);
//			re-setup the parameters of the leaf node
			node.uid=uid;
			node.u_id_Z =U.getImageOf(uid);
//			book keeping of the newly added user
			this.users.put(uid, node);
//			set the status to be ACTIVE
			node.setActive();
		}
		else
		{
			this.U.addNewAttribute(uid);
			UserNode node = new UserNode(uid, this.U);
			
			this.users.put(uid, node);
			
//			find the inner node on the lowest level which doesn't have a right child
//			starting from the last new user
			
		}
		
	}
	
	public void updateUser()
	{
		
	}

}
