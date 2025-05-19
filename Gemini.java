import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;


public class Gemini {
    
    public static void main(String [] args) throws Exception{
        String api_key = "";
        String model = "gemini-2.5-flash-preview-04-17";
        String prompt = "Eai meu amigo como vc ta?";

        String resp = generateResp(api_key , model , prompt);

        System.out.println(resp);
    }

    public static String generateResp(String api_key , String model , String prompt) throws Exception{

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + 
                     model + ":generateContent?key=" + api_key;

        String requestB = String.format("""
            {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": "%s"
                            }
                        ]
                    }
                ]
            }
            """, prompt.replace("\"", "\\\""));


            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create(url))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(requestB))
                                    .build();

            HttpResponse<String> resp = client.send(request , BodyHandlers.ofString());

            return resp.body();
    }
}
