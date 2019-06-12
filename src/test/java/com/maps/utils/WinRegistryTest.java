import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

/**
 * @Description
 * @Author mapsh
 * @Date 2019/6/12 22:50
 * @Version 1.0
 **/

public class WinRegistryTest {

    @Test
    public void valueForKeyPathTest() throws Exception{
        String val = WinRegistry.valueForKeyPath(WinRegistry.HKEY_CURRENT_USER, "Software\\Mozilla", "PathToExe");
        System.out.println(val);
    }

    @Test
    public void subKeysForPathTest() throws Exception{
        Map<String, String> map = WinRegistry.valuesForPath(WinRegistry.HKEY_LOCAL_MACHINE,"Software\\Mozilla");
        Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
           System.out.println(entry.getKey()+entry.getValue());
        }
    }
}
