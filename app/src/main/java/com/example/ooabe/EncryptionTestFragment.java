package com.example.ooabe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ooabe.abe.CipherText;
import com.example.ooabe.abe.IntermediateCipherText;

import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EncryptionTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EncryptionTestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public EncryptionTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EncryptionTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EncryptionTestFragment newInstance(String param1, String param2) {
        EncryptionTestFragment fragment = new EncryptionTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_encryption_test, container, false);

        Button buttonEnc = (Button) v.findViewById(R.id.button_test_enc);
        Button buttonClear = (Button)v.findViewById(R.id.button_enc_clear);
        final EditText num_of_attr_text = (EditText)v.findViewById(R.id.num_of_attr_text);
        final TextView result_enc = (TextView)v.findViewById(R.id.result_enc);
        final CheckBox check_on_off = (CheckBox)v.findViewById(R.id.check_on_off);
//        if(null != MainActivity.myRSABE)
//        {
//            onEncButtonClick(n1, result);
//        }

        buttonEnc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!check_on_off.isChecked())
                    onEncButtonClick(num_of_attr_text, result_enc);
                else
                    onEncButtonClick_on_off(num_of_attr_text,result_enc);
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClearButtonClick(result_enc);
            }
        });

        return v;
    }

    public boolean validateInputObejcts(EditText num_of_attr_text, TextView enc_result_text)
            throws Exception
    {
        if(null == num_of_attr_text || null == enc_result_text)
        {
            throw new Exception("onEncButtonClick:EditText num_of_attr_text " +
                    "or TextView enc_result_text NOT FOUND!!");
        }
        return true;
    }


    public void onEncButtonClick(EditText num_of_attr_text, TextView enc_result_text)
    {
        try
        {
            validateInputObejcts(num_of_attr_text, enc_result_text);

            if(null == MainActivity.myRSABE)
            {
                enc_result_text.setText("Setup has NOT been executed before. Please return to \"Generate PP and PK\"." +
                        "and generate all the public parameters.");
                return;
            }

            if (null != MainActivity.myRSABE && MainActivity.security_level > 0)
            {
                if(null == num_of_attr_text.getText() || null == num_of_attr_text.getText().toString()
                        || "".equals(num_of_attr_text.getText().toString())) {
                    enc_result_text.setText("Please enter the number of attributes ");
                    return;
                }

                int num_attr = Integer.parseInt(num_of_attr_text.getText().toString());
                if( num_attr < 1)
                {
                    enc_result_text.setText("Number of attributes should be at least 1. ");
                    return;
                }


                Field Zp = MainActivity.myRSABE.getZp();
                ArrayList<BigInteger> attrList = new ArrayList<BigInteger>();
                for(int i=0; i<num_attr; i++)
                {
                    attrList.add(Zp.newRandomElement().toBigInteger());
                }

                Element m = MainActivity.myRSABE.getGT().newRandomElement();

                long startTime, endTime;
                long enc_time;
//                time the encryption process
                startTime = System.nanoTime();

                int repeat_time;
                if(num_attr < 10)
                    repeat_time = 10;
                else
                    repeat_time = 5;
                for(int j = 0; j< repeat_time; j++)
                    MainActivity.myRSABE.encrypt(m,attrList);
                endTime = System.nanoTime();

                enc_time = (endTime - startTime)/1000000;
                double timePerEnc = enc_time*1.0/repeat_time;

                String result_enc_str = "Encryption test ends. Current security level is " +
                        +MainActivity.security_level +". The number of attribute(s) is " + num_attr;
                result_enc_str += ". Total encryption time is "+ enc_time + " ms for encrypting message for "
                        +repeat_time+" times. Average time per enc-operation is ";
                result_enc_str += ""+timePerEnc+" ms";

                enc_result_text.setText(result_enc_str);

            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onClearButtonClick(TextView enc_result_text)
    {
        if(null != enc_result_text )
        {
            enc_result_text.setText("Result cleared");
        }
    }


    public void onEncButtonClick_on_off(EditText num_of_attr_text, TextView enc_result_text)
    {
        try
        {
            validateInputObejcts(num_of_attr_text,enc_result_text);

            if(null == MainActivity.myRSABE)
            {
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


                Field Zp = MainActivity.myRSABE.getZp();
                ArrayList<BigInteger> attrList = new ArrayList<BigInteger>();
                for (int i = 0; i < num_attr; i++) {
                    attrList.add(Zp.newRandomElement().toBigInteger());
                }



                long offline_startTime, offline_endTime;
                long offline_enc_time;
                long million = 1000000;

//              time the offline ciphertext generation
                offline_startTime = System.nanoTime();
                IntermediateCipherText ict = MainActivity.myRSABE.generateIntermediateCipherText(num_attr);
                offline_endTime = System.nanoTime();

                offline_enc_time=(offline_endTime - offline_startTime)/million;

                long startTime, endTime;
                long enc_time;

                int repeat_time;
                if (num_attr < 10)
                    repeat_time = 10;
                else
                    repeat_time = 5;

//               time the offline phase
                Element m = MainActivity.myRSABE.getGT().newRandomElement();
                startTime = System.nanoTime();
                for(int j = 0; j< repeat_time; j++)
                    MainActivity.myRSABE.onlineEncrypt(ict, m, attrList);
                endTime = System.nanoTime();

                enc_time = (endTime - startTime) / million;
                double timePerEnc = enc_time * 1.0 / repeat_time;

                String result_enc_str = "Encryption test ends. Current security level is " +
                        +MainActivity.security_level + ". The number of attribute(s) is " + num_attr;
                result_enc_str += ". Total online-encryption time is " + enc_time + " ms for encrypting message for "
                        + repeat_time + " times. Average time per online enc-operation is ";
                result_enc_str += "" + timePerEnc + " ms. The off-line phase took "+ offline_enc_time + " ms.";

                enc_result_text.setText(result_enc_str);

            }

        } catch (Exception e)
            {
            e.printStackTrace();
        }
    }
}
