package com.example.ooabe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ooabe.abe.AttributeUniverse;
import com.example.ooabe.abe.UserSecretKey;
import com.example.ooabe.utils.tree.PolicyTree;

import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;


/**
 * A simple {@link Fragment} subclass.
 */
public class DecryptionTestFragment extends Fragment {


    public DecryptionTestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_decryption_test, container, false);

        Button buttonDec = (Button) v.findViewById(R.id.button_test_dec);
        Button buttonClear = (Button) v.findViewById(R.id.button_enc_clear);
        final EditText num_of_attr_text = (EditText) v.findViewById(R.id.num_of_attr_text);
        final TextView result_dec = (TextView) v.findViewById(R.id.result_dec);


        buttonDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onEncButtonClick(num_of_attr_text, result_dec);

            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearButtonClick(result_dec);
            }
        });

        return v;
    }

    public boolean validateInputObejcts(EditText num_of_attr_text, TextView enc_result_text)
            throws Exception {
        if (null == num_of_attr_text || null == enc_result_text) {
            throw new Exception("onEncButtonClick:EditText num_of_attr_text " +
                    "or TextView enc_result_text NOT FOUND!!");
        }
        return true;
    }


    public void onEncButtonClick(EditText num_of_attr_text, TextView enc_result_text) {
        try {
            validateInputObejcts(num_of_attr_text, enc_result_text);

            if (null == MainActivity.myRSABE) {
                enc_result_text.setText("Setup has NOT been executed before. Please return to \"Generate PP and PK\"." +
                        "and generate all the public parameters.");
                return;
            }

            if (null != MainActivity.myRSABE && MainActivity.security_level > 0) {
                if (null == num_of_attr_text.getText() || null == num_of_attr_text.getText().toString()
                        || "".equals(num_of_attr_text.getText().toString())) {
                    enc_result_text.setText("Please enter the number of attributes ");
                    return;
                }

                int num_attr = Integer.parseInt(num_of_attr_text.getText().toString());
                if (num_attr < 1) {
                    enc_result_text.setText("Number of attributes should be at least 1. ");
                    return;
                }


//                contact attribute universe in MainActivity and add attribute names into it
                Field Zp = MainActivity.myRSABE.getZp();
                ArrayList<String> attributeNames =  new ArrayList<String>();
                String attrName;
                for(int i = 0; i< num_attr; i++)
                {
                    attrName="attr"+Integer.toBinaryString(i)+"0";
                    attributeNames.add(attrName);
                }

                if(null == MainActivity.U)
                    MainActivity.U = new AttributeUniverse(Zp,attributeNames);
                else
                {
                    for (String aName:attributeNames )
                    {
                        MainActivity.U.addNewAttribute(aName);
                    }
                }

//                construct the policy string
                String policy_str=attributeNames.get(0);
                for(int i=1; i< num_attr-1; i++)
                {
                        policy_str= "("+ policy_str +"*"+ attributeNames.get(i)+")";
                }
                policy_str += "*"+ attributeNames.get(num_attr-1)+";";

//                construct the policy tree ,generate the usk and measure the time;
                long uskGenStart_time, uskGenEnd_time, uskGenTime;
                long million = 1000000;
                uskGenStart_time = System.nanoTime();

                PolicyTree pt = new PolicyTree(policy_str);
                UserSecretKey usk = MainActivity.myRSABE.genUSK("uid", pt.toKeyPolicy(MainActivity.U, true));
                uskGenEnd_time = System.nanoTime();
                uskGenTime = (uskGenEnd_time-uskGenStart_time)/million;

//                construct big integer list for encryption



               ArrayList<BigInteger> attrList = new ArrayList<BigInteger>();
                for (String aName:attributeNames) {
                    attrList.add(MainActivity.U.getImageOf(aName));
                }

                Element m = MainActivity.myRSABE.getGT().newRandomElement();
//              set/update the reference ciphertext
                MainActivity.CT = MainActivity.myRSABE.encrypt(m, attrList);
                Element m_dec;

                long startTime, endTime;
                long decTime;
                startTime = System.nanoTime();
                m_dec =   MainActivity.myRSABE.decrypt(MainActivity.CT, usk);
                endTime = System.nanoTime();

                decTime = (endTime - startTime) / 1000000;

                String result_dec_str;
                if(m_dec.isEqual(m)) {
                    result_dec_str = "Usk generation and decryption test ends. Current security level is " +
                            +MainActivity.security_level + ". The number of attribute(s) is " + num_attr;
                    result_dec_str += ". Time to generate user secret key is " + uskGenTime + " ms. " +
                            " Time to decrypt the message is ";
                    result_dec_str += "" + decTime + " ms";
                }
                else
                {
                    result_dec_str = "Decryption failed!!";
                }

                enc_result_text.setText(result_dec_str);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClearButtonClick(TextView enc_result_text) {
        if (null != enc_result_text) {
            enc_result_text.setText("Result cleared");
        }
    }



}