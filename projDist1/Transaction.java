

public class Transaction implements Comparable<Transaction>{

    private String to;
    private String from;
    private double amount;
    private double fee;


    public Transaction(String to, String from, double amount, double fee) {
        this.to = to;
        this.from = from;
        this.amount = amount;
        this.fee = fee;
    }


    public String getTo() {
        return to;
    }
    public String getFrom() {
        return from;
    }
    public double getAmount() {
        return amount;
    }
    public double getFee() {
        return fee;
    }   
    @Override
    public String toString() {
        return "Transaction [to=" + to + ", from=" + from + ", amount=" + amount + ", fee=" + fee + "]";
    }

    @Override
    public int compareTo(Transaction o) {
        return Double.compare(o.fee , this.fee);
    }
}