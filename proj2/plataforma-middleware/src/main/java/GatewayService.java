public class GatewayService  {

    @RequestMapping(path = "/add_transaction", method = "POST")
    public String handleGatewayRequest(String from, String to, double value, double fee) {
        MinerProxy minerProxy = new MinerProxy("localhost", 8081);
        return minerProxy.addTransaction(from, to, value, fee);
    }

    @RequestMapping(path = "/getBlock", method = "GET")
    public String getBlock() {
        MinerProxy minerProxy = new MinerProxy("localhost", 8081);
        return minerProxy.getBlock();
    }
}