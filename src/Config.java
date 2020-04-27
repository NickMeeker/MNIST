import java.util.Map;

public class Config {
    Map<String, String> map;

    public Config(String filepath) {
        map = FileHandler.parseConfigFile(filepath);
    }

    public String get(String key) {
        return map.get(key);
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public Map<String, String> getMap() { return this.map; }
}
