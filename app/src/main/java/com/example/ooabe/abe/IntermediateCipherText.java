package com.example.ooabe.abe;

import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;

/**
 * Created by LiDuan on 10/27/2016.
 */

public class IntermediateCipherText
{
//    public ArrayList<BigInteger> attributes;
    public Element EK, C0;
    public ArrayList<Element> C1;
    public ArrayList<Element> C2;
    public ArrayList<Element> Rj;
    public ArrayList<Element> Xj;

    /**
     * Default constructor, all list members are initialized
     */
    public IntermediateCipherText()
    {

        C1 = new ArrayList<Element>();
        C2 = new ArrayList<Element>();
        Rj = new ArrayList<Element>();
        Xj = new ArrayList<Element>();

//		EK and C0 are not initiated at this stage
    }
}
