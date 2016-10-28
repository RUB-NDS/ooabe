package com.example.ooabe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ooabe.abe.RSABE;


public class SetUpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public static RSABE myRSABE;

    public SetUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetUpFragment newInstance(String param1, String param2) {
        SetUpFragment fragment = new SetUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_set_up, container, false);
        myRSABE = MainActivity.myRSABE;
        Button buttonSetup = (Button) v.findViewById(R.id.button_setup);
        Button buttonReset = (Button)v.findViewById(R.id.button_reset);
        final EditText n1 = (EditText)v.findViewById(R.id.Number1);
        final TextView result = (TextView)v.findViewById(R.id.Result);


        if(null != myRSABE)
        {
            onSetupButtonClick(n1, result);
        }

        buttonSetup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                    onSetupButtonClick(n1, result);
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onResetButtonClick(result);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    public void onResetButtonClick(TextView result)
    {
        myRSABE = null;
        MainActivity.myRSABE = myRSABE;
        MainActivity.security_level = 0;
        result.setText("All public parameter and public key has been cleared");

    }


    public void onSetupButtonClick(EditText n1, TextView result )
    {
//        EditText n1 = (EditText)findViewById(R.id.Number1);
//        EditText n1 = (EditText)v.findViewById(R.id.Number1);
//        TextView result = (TextView)v.findViewById(R.id.Result);


        try {
            if(null == n1 || null == result)
            {
                throw new Exception("onSetupButtonClick:EditText n1 or TextView result NOT FOUND!!");
            }

            if(null != myRSABE)
            {
                result.setText("Setup has already been executed before. Hit \"RESET \" to clear them" +
                        "if new tests for different parameters are wanted.");
                return;
            }

            if(null == n1.getText() || null == n1.getText().toString()
                    ||"".equals(n1.getText().toString())) {
                result.setText("Please enter the security level ");
                return;
            }
            int secLevel = Integer.parseInt(n1.getText().toString());
            String setupInfoStr="";
            String timeInfo="Setup finished in ";
            long startTime, endTime;
            int rBits=160, qBits=512;
            if(checkInput(secLevel))
            {
                if(null == myRSABE)
                {
                    switch (secLevel){
                    case 80:
                        rBits = 160;
                        qBits = 512;
                        break;
                    case 112:
                        rBits = 224;
                        qBits = 1024;
                        break;
                    case 128:
                        rBits = 256;
                        qBits = 1536;
                        break;
                    }

//                    time the generation process
                    startTime = System.nanoTime();
                    myRSABE = new RSABE(rBits, qBits);
                    endTime = System.nanoTime();

                    MainActivity.myRSABE = myRSABE;
                    MainActivity.security_level = secLevel;

                    timeInfo = timeInfo+((endTime-startTime)/1000000)+ " ms";
                }
                else
                {
                    timeInfo="Setup has already been executed before. ";
                }
                setupInfoStr = "PK = "+myRSABE.getPk().toString();
                result.setText("The security level is set as " + secLevel + "." +timeInfo+
                        "\nWith "+ setupInfoStr);
            }
            else
            {
                result.setText("Invalid security level! ");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean checkInput(int secLev)
    {
        if (secLev == 80) return true;
        if (secLev == 112) return true;
        if (secLev == 128) return true;
        return false;
    }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//}
