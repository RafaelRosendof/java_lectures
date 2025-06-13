package main.java.com.MyCripto.cripto;


/*
 * So this interface gonna have the key methods 
 * 
 */

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.List;
import org.bitcoinj.crypto.MnemonicException;
//import  main.java.com.MyCripto.cripto.WalletModel;
import java.util.ArrayList;

public interface KeyInterface {

    //Assic get methods
    public List<List<Integer>> getAssic(List<String> seed);
    public Integer getAssicKey(String passworld);

    // Operations assic methods
    public List<List<Integer>> addAssic(String passworld);
    public List<List<Integer>> subAssic(String passworld);
    public List<List<Integer>> multiAssic(String passworld);

    public List<List<Integer>> RevaddAssic(String passworld);
    public List<List<Integer>> RevsubAssic(String passworld);
    public List<List<Integer>> RevmultiAssic(String passworld); 

    public List<String> getKey();

    //https://medium.com/@olelewexpressrules/creating-a-simple-bitcoin-wallet-service-using-the-bitcoin-java-library-bitcoinj-2cebb82ac08d
    WalletModel createWallet() throws MnemonicException.MnemonicLengthException;
    WalletModel restoreWallet(ArrayList<String> mnemonicCode);


    // AES 

    public String Encrypt(String text , SecretKey key);
    public String Decrypt(String encrypted , SecretKey key);
    public SecretKey generateSecretKey(String password , String salt);
    private static String bytesToHex(byte[] bytes);



}