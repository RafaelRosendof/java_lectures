package main.java.com.MyCripto.cripto;


/*
 * So this interface gonna have the key methods 
 * 
 */

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

}