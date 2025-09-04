public class Transaction implements Comparable<Transaction>{

    private final String from;
    private final String to;
    private final double value;
    private final double fee;
    private final long timestamp;


    public Transaction(String from, String to, double value, double fee) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.fee = fee;
        this.timestamp = System.nanoTime(); // usado para desempate
    }

    public String getTo() {
        return to;
    }
    public String getFrom() {
        return from;
    }
    public double getValue() {
        return value;
    }
    public double getFee() {
        return fee;
    }   

    @Override
    public int compareTo(Transaction other) {
        
        int feeCompare = Double.compare(other.fee, this.fee);
        if (feeCompare != 0) return feeCompare;

        return Long.compare(this.timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        return String.format("Tx[from=%s, to=%s, value=%.2f, fee=%.2f]", from, to, value, fee);
    }


}