package main.java.com.MyCripto.cripto;



public class WalletModel {

    private String walletName;
    private String walletAddress;
    private String myKeyAES;
    private String myKeyAESIV;

    private String publicKey;
    private String privateKey;
    private String mnemonicCode;
    private String passphrase;
    private String walletType; // e.g., "HD", "Single Address" 



    public WalletModel(String walletName, String walletAddress, String myKeyAES, String myKeyAESIV,
                       String publicKey, String privateKey, String mnemonicCode, String passphrase, String walletType) {
        this.walletName = walletName;
        this.walletAddress = walletAddress;
        this.myKeyAES = myKeyAES;
        this.myKeyAESIV = myKeyAESIV;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.mnemonicCode = mnemonicCode;
        this.passphrase = passphrase;
        this.walletType = walletType;
    }
    public String getWalletName() {
        return walletName;
    }
    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }
    public String getWalletAddress() {
        return walletAddress;
    }
    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    public String getMyKeyAES() {
        return myKeyAES;
    }
    public void setMyKeyAES(String myKeyAES) {
        this.myKeyAES = myKeyAES;
    }
    public String getMyKeyAESIV() {
        return myKeyAESIV;
    }
    public void setMyKeyAESIV(String myKeyAESIV) {
        this.myKeyAESIV = myKeyAESIV;
    }
    public String getPublicKey() {
        return publicKey;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    public String getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    public String getMnemonicCode() {
        return mnemonicCode;
    }
    public void setMnemonicCode(String mnemonicCode) {
        this.mnemonicCode = mnemonicCode;
    }   
    public String getPassphrase() {
        return passphrase;
    }
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
    public String getWalletType() {
        return walletType;
    }
    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }
    @Override
    public String toString() {
        return "WalletModel{" +
                "walletName='" + walletName + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                ", myKeyAES='" + myKeyAES + '\'' +
                ", myKeyAESIV='" + myKeyAESIV + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", mnemonicCode='" + mnemonicCode + '\'' +
                ", passphrase='" + passphrase + '\'' +
                ", walletType='" + walletType + '\'' +
                '}';
    }
}
