
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HttpUtils {
    public static boolean push(Double[][] hiddenWeights, Double[][] outputWeights) {
        List<Double[][]> hiddenWeightsList = new ArrayList<>();
        hiddenWeightsList.add(hiddenWeights);
        JSONObject obj = new JSONObject();
        Config conf = new Config("config.conf");
        obj.put("baseModelId", conf.get("modelId"));
        obj.put("hiddenWeights", hiddenWeightsList);
        obj.put("outputWeights", outputWeights);

        FileHandler.writeStringToFile(obj.toString(), "request.json");

        try {
            HttpResponse<String> response = Unirest.post("http://34.232.66.227:3001/ffnn/push")
                    .header("Content-Type", "application/json")
                    .body(obj)
                    .asString();
            System.out.println(response);
        } catch(UnirestException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public static boolean pull(int modelId, String modelFilepath, Config config) {
        JSONObject obj = new JSONObject();
        obj.put("modelId", modelId);

        try {
            HttpResponse<JsonNode> response = Unirest.get("http://34.232.66.227:3001/ffnn/pull")
                    .header("Content-Type", "application/json")
                    .queryString("modelId", modelId)
                    .asJson();
            System.out.println(response.getBody().toString());

            if(response.getBody().getObject().has("status") && response.getBody().getObject().get("status").equals("FAILURE"))
                return false;

            FileHandler.writeStringToFile(response.getBody().toString(), modelFilepath);
            config.put("modelId", response.getBody().getObject().get("id").toString());
        } catch(UnirestException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
