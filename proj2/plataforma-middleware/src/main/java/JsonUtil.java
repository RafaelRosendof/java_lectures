import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

// wrapper para o Jackson

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> parseJson(String json) throws Exception {
        return mapper.readValue(json, Map.class);
    }

    public static String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }
}
