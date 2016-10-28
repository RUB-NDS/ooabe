# ooabe
This project includes:
1. the implementation of Rouselakis key policy attribute-based encryption (KP-ABE) scheme [1]
    with online/offline encryption [3]
   (Class com.example.ooabe.RABE )
2. the extension of RABE to pair-wise KP-ABE, using idea in [2]
    (Class com.example.ooabe.PWABE )
3. the extension of PWABE to revocable storage KP-ABE using idea in [2]
    (Class com.example.ooabe.RSABE )
4. JUnit test cases for RABE, PWABE, RSABE
    (in  /app/src/test/java/com.example.ooabe)
5. Test GUI runnable on Android devices with API level 17+ (inclusive)
    (the whole Android project)

After being cloned and upzipped , this project can be directly imported into Android Studio (V2.2.2+)

#Reference
[1]Y. Rouselakis and B. Waters,
"New constructions and proof methods for large universe attribute-based
   encryption."IACR Cryptology ePrint Archive, vol. 2012, p. 583, 2012

[2]Sahai, Amit, Hakan Seyalioglu, and Brent Waters.
"Dynamic credentials and ciphertext delegation for attribute-based encryption."
Advances in Cryptology–CRYPTO 2012. Springer Berlin Heidelberg, 2012. 199-217.

[3]Hohenberger, Susan, and Brent Waters. "Online/offline attribute-based encryption."
International Workshop on Public Key Cryptography. Springer Berlin Heidelberg, 2014.