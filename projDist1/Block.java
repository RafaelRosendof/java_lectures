public class Block {

    private String previousHash;
    private String hash;
    private long timestamp;
    private List<Transaction> transactions;

    public Block(String previousHash, String hash, long timestamp, List<Transaction> transactions) {
        this.previousHash = previousHash;
        this.hash = hash;
        this.timestamp = timestamp;
        this.transactions = transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return "Block [previousHash=" + previousHash + ", hash=" + hash + ", timestamp=" + timestamp
                + ", transactions=" + transactions + "]";
    }
}