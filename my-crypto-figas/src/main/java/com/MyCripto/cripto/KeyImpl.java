package main.java.com.MyCripto.cripto;



public class KeyImpl implements KeyInterface {

    // rafael , figas , senha == 12 23 4 5 6 & ... & ... the & mark delimiter for the end of the word
    @Override
    public List<List<Integer>> getAssic(List<String> seed) {
        
        for (String s : seed){
            List<Integer> asciiValues = new ArrayList<>();
            
            for (char c : s.toCharArray()) {
                asciiValues.add((int) c);
            }

            asciiValues.add( (int) '&');
            
            return List.of(asciiValues);
        }
    }

    @Override
    public Integer getAssicKey(String password) {
        int sum = 0;

        for (char c : password.toCharArray()) {
            sum += (int) c;
        }

        return sum;
    }

    @Override
    public List<List<Integer>> addAssic(String password) {
        // Implement the logic for adding ASCII values based on the password
        return null; // Placeholder return
    }
    @Override
    public List<List<Integer>> subAssic(String password) {
        // Implement the logic for subtracting ASCII values based on the password
        return null; // Placeholder return
    }
    @Override
    public List<List<Integer>> multiAssic(String password) {
        // Implement the logic for multiplying ASCII values based on the password
        return null; // Placeholder return
    }
    @Override
    public List<List<Integer>> RevaddAssic(String password) {
        // Implement the logic for reverse adding ASCII values based on the password
        return null; // Placeholder return
    }
    @Override
    public List<List<Integer>> RevsubAssic(String password) {
        // Implement the logic for reverse subtracting ASCII values based on the password
        return null; // Placeholder return
    }
    @Override
    public List<List<Integer>> RevmultiAssic(String password) {
        // Implement the logic for reverse multiplying ASCII values based on the password
        return null; // Placeholder return
    }
    @Override
    public List<String> getKey() {
        // Implement the logic to return the key
        return null; // Placeholder return
    }
    @Override
    public WalletModel createWallet() throws MnemonicException.MnemonicLengthException {
        // Implement the logic to create a wallet
        return null; // Placeholder return
    }
    @Override
    public WalletModel restoreWallet(ArrayList<String> mnemonicCode) {
        // Implement the logic to restore a wallet from mnemonic code
        return null; // Placeholder return
    }
    @Override
    public String Encrypt(String text, SecretKey key) {
        // Implement the logic to encrypt the text using the provided key
        return null; // Placeholder return
    }
    @Override
    public String Decrypt(String encrypted, SecretKey key) {
        // Implement the logic to decrypt the text using the provided key
        return null; // Placeholder return
    }
    @Override
    public SecretKey generateSecretKey(String password, String salt) {
        // Implement the logic to generate a secret key based on the password and salt
        return null; // Placeholder return
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    


}