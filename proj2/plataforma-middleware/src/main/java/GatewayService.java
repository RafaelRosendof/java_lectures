public class GatewayService  {

    // abstração do serviço de gateway
    // expõe as rotas que o gateway oferece

    @RequestMapping(path = "/add_transaction", method = "POST")
    public String handleGatewayRequest(String from, String to, double value, double fee) {
        MinerProxy minerProxy = new MinerProxy("localhost", 8081);
        return minerProxy.addTransaction(from, to, value, fee); // definido no proxy
    }

    @RequestMapping(path = "/getBlock", method = "GET")
    public String getBlock() {
        MinerProxy minerProxy = new MinerProxy("localhost", 8081);
        return minerProxy.getBlock();
    }
}