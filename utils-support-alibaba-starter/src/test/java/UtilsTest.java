import java.util.HashMap;
import java.util.Map;

/**
 * @author cn-src
 */
public class UtilsTest {

    public void toJsonStr() {
        final Map<String, String> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        map.put("k3", "v3");
        map.put("k\"", "v\"");
//        assertEquals("{\"k1\":\"v1\",\"k2\":\"v2\",\"k\\\"\":\"v\\\"\",\"k3\":\"v3\"}", Utils.toJsonStr(map));
    }
}