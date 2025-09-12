import java.util.Map;

public class InvocationData {
    private String methodName;
    private Map<String, Object> params;

    public InvocationData(String methodName, Map<String, Object> params) {
        this.methodName = methodName;
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
