package com.example.ooabe;

/**
 * Created by LiDuan on 10/30/2016.
 */

import com.example.ooabe.abe.AttributeUniverse;
import com.example.ooabe.abe.CipherText;
import com.example.ooabe.abe.KeyPolicy;
import com.example.ooabe.abe.RSABE;
import com.example.ooabe.abe.UserSecretKey;
import com.example.ooabe.utils.tree.PolicyTree;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;

import static org.junit.Assert.assertTrue;

public class RSABETest128 {
    public static RSABE myRSABE;
    public static AttributeUniverse U;
    public static KeyPolicy myPolicy;
    public static UserSecretKey myUSK;
    public static ArrayList<BigInteger> listOfAttrBigInt;
    public static Element m;
    public static CipherText myCT;

    @BeforeClass
    public static void setup() {
        myRSABE = new RSABE(256, 1536);
//        generate 30 attribute names;
        ArrayList<String> attributeNames = new ArrayList<String>();
        String attrName;
        int num_attr = 30;
        for (int i = 0; i < num_attr; i++) {
            attrName = "attr" + Integer.toBinaryString(i) + "0";
            attributeNames.add(attrName);
        }
        U = new AttributeUniverse(myRSABE.getZp(), attributeNames);

        listOfAttrBigInt = new ArrayList<BigInteger>();
        for (String aName : attributeNames) {
            listOfAttrBigInt.add(U.getImageOf(aName));
        }

        String policy_str = attributeNames.get(0);
        for (int i = 1; i < num_attr - 1; i++) {
            policy_str = "(" + policy_str + "*" + attributeNames.get(i) + ")";
        }
        policy_str += "*" + attributeNames.get(num_attr - 1) + ";";

        PolicyTree pt = null;
        try {
            pt = new PolicyTree(policy_str);
            myPolicy = pt.toKeyPolicy(U, true);
            myUSK = myRSABE.genUSK("uid", myPolicy);
        } catch (Exception e) {
            e.printStackTrace();
        }

        m = myRSABE.getGT().newRandomElement();
        m = myRSABE.getGT().newRandomElement();
        myCT = myRSABE.encrypt(m, listOfAttrBigInt);

    }


    @Test
    public void testEnc30Attr() {
        CipherText ct = myRSABE.encrypt(m, listOfAttrBigInt);
        assertTrue(true);
    }

    @Test
    public void testDec30Attr() {

        assertTrue(myRSABE.decrypt(myCT, myUSK).isEqual(m));
    }

    @Test
    public void testUSK30Attr() {
        try {
            UserSecretKey usk = myRSABE.genUSK("uid2", myPolicy);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

