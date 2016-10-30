package com.example.ooabe;

import com.example.ooabe.abe.AttributeUniverse;
import com.example.ooabe.abe.KeyPolicy;
import com.example.ooabe.abe.KeyUpdate;
import com.example.ooabe.abe.RSABE;
import com.example.ooabe.abe.RSCipherText;
import com.example.ooabe.abe.UserSecretKey;
import com.example.ooabe.utils.tree.PolicyTree;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import it.unisa.dia.gas.jpbc.Element;

import static org.junit.Assert.assertTrue;


public class RSABECorrectNessTest {

    static RSABE myABE2;
    static ArrayList<BigInteger> listAttributes2;
    static ArrayList<BigInteger> timeAttributes_init;

    static ArrayList<String> timePolicyStrList;
    static ArrayList<ArrayList<BigInteger>> listOf_TimeAttributesList;
    static UserSecretKey usk_uid2;
    static KeyPolicy myPolicy2;
    static ArrayList<KeyUpdate> kus;
    static AttributeUniverse U2;
    static ArrayList<KeyPolicy> keyUpdatePolicies;

    static BigInteger uid2Image;


    @BeforeClass
    public static void setup() {

        try {
            String policyStr = "((Alice*Research)*Engineer)+(Alice*ID198);";
            PolicyTree pt;


            myABE2 = new RSABE();
            int T_MAX_LOG2 = 5;
            int T_MAX = 32;
            myABE2.setT_Max_Log(T_MAX_LOG2);

            pt = new PolicyTree(policyStr);
            String[] attributes2 = {"Alice", "Research", "Engineer", "ID198"};

            U2 = new AttributeUniverse(myABE2.getZp(), new ArrayList<String>(Arrays.asList(attributes2)));

            listAttributes2 = new ArrayList<BigInteger>();

//			set up the list of normal attributes
            for (String attribute : attributes2) {
                listAttributes2.add(U2.getImageOf(attribute));
            }


//			prepare attributes to encode time point 0 to T_MAX-1
//			and set up the initial list of time attributes for time 0

            ArrayList<String> timeAttrStrings = new ArrayList<String>();
            timeAttributes_init = new ArrayList<BigInteger>();
            String attrName;
            for (int i = 0; i < T_MAX_LOG2; i++) {
                attrName = "t" + Integer.toBinaryString(i) + "0";
                U2.addNewAttribute(attrName);
                timeAttributes_init.add(U2.getImageOf(attrName));
                timeAttrStrings.add(attrName);

                attrName = "t" + Integer.toBinaryString(i) + "1";
                U2.addNewAttribute(attrName);
                timeAttributes_init.add(U2.getImageOf(attrName));
                timeAttrStrings.add(attrName);
            }

            ArrayList<String> timeCodes = RSABE.genTimeCodes(T_MAX, T_MAX_LOG2);
            int attribute_index = 0;
            String currentPolicyStr = new String();


//			generate all time policy string
            timePolicyStrList = new ArrayList<String>();
            for (String s : timeCodes) {
                for (char ch : s.toCharArray()) {
                    attrName = "t" + Integer.toBinaryString(attribute_index) + ch;

                    if (attribute_index <= 0) {
                        currentPolicyStr = "" + attrName;
                    } else if (attribute_index < T_MAX_LOG2 - 1) {
                        currentPolicyStr = "(" + currentPolicyStr + "*" + attrName + ")";
                    } else {
                        currentPolicyStr = currentPolicyStr + "*" + attrName;
                    }

                    attribute_index++;


                }


                timePolicyStrList.add(currentPolicyStr + ";");
                attribute_index = 0;
                currentPolicyStr = new String();


            }

//			use the enhanced version for policy generation
            myPolicy2 = pt.toKeyPolicy(U2, true);
            myPolicy2.printPolicy();
            uid2Image = myABE2.getZp().newRandomElement().toBigInteger();

            keyUpdatePolicies = new ArrayList<KeyPolicy>();
            for (String aPolicyStr : timePolicyStrList) {
                keyUpdatePolicies.add((new PolicyTree(aPolicyStr)).toKeyPolicy(U2, true));
            }
            usk_uid2 = myABE2.genUSK("uid2", uid2Image, myPolicy2, false);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testDelegationDec80() {
        int T_MAX_LOG2 = 5;
        int T_MAX = 32;

        ArrayList<String> timeCodes = RSABE.genTimeCodes(T_MAX, T_MAX_LOG2);
        String startTime = "XXXXX";

        Element m = myABE2.getGT().newRandomElement();
        RSCipherText ct0 = myABE2.encrypt(m, listAttributes2, timeAttributes_init, startTime);
        KeyUpdate ku0 = myABE2.genKU("uid2", uid2Image, keyUpdatePolicies.get(0), startTime);

        ArrayList<RSCipherText> c_list_t0 = new ArrayList<RSCipherText>();
        c_list_t0.add(ct0);
        try {
            assertTrue(m.equals(myABE2.decrypt(c_list_t0, usk_uid2, ku0)));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        KeyUpdate ku_t1;
        ArrayList<RSCipherText> c_list_t1 = c_list_t0;
        for (int t = 0; t < T_MAX - 1; t++) {
//			delegate the ciphertext to the next time point t+1
            c_list_t1 = myABE2.delegate(c_list_t1, timeCodes.get(t), T_MAX_LOG2);

//			get the key update for t+1
            ku_t1 = myABE2.genKU("uid2", uid2Image, keyUpdatePolicies.get(t + 1), timeCodes.get(t + 1));

//			validate the decrypted result
            try {

                Element m_dec = myABE2.decrypt(c_list_t1, usk_uid2, ku_t1);
                assertTrue(m.equals(m_dec));
            } catch (Exception e) {

                e.printStackTrace();
            }
//			System.out.println("RSABE.decryption succeeds for time "+ (t+1));
        }
    }

    @Test
    public void testDelegationOnly80() {
        int T_MAX_LOG2 = 5;
        int T_MAX = 32;

        ArrayList<String> timeCodes = RSABE.genTimeCodes(T_MAX, T_MAX_LOG2);
        String startTime = "XXXXX";

        Element m = myABE2.getGT().newRandomElement();
        RSCipherText ct0 = myABE2.encrypt(m, listAttributes2, timeAttributes_init, startTime);
        ArrayList<RSCipherText> c_list_t0 = new ArrayList<RSCipherText>();
        c_list_t0.add(ct0);
        ArrayList<RSCipherText> c_list_t1 = c_list_t0;
        for (int t = 0; t < T_MAX - 1; t++) {
//			delegate the ciphertext to the next time point t+1
            c_list_t1 = myABE2.delegate(c_list_t1, timeCodes.get(t), T_MAX_LOG2);

//			System.out.println("RSABE.decryption succeeds for time "+ (t+1));
        }

    }
}
