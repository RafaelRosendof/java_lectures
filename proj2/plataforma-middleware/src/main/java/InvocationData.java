import java.util.Map;

// classe para encapsular os dados da invocação do método remoto

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
