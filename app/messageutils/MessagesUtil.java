package messageutils;

import java.util.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Helper class for messages.
 *
 * @author huljas
 */
public class MessagesUtil {

    @SuppressWarnings("unchecked")
    public static <T> T getConfig(String key, T defaultValue) {
        try {
            Config config = ConfigFactory.load();
            return (T) config.getAnyRef(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Collection<String> getNewKeys(SourceKeys sources, Map<String,String> messages) {
        Set<String> set = new HashSet<String>();
        for (String newKey : sources.keySet()) {
            if (!messages.containsKey(newKey)) {
                set.add(newKey);
            }
        }
        return set;
    }

    public static Collection<String> getObsoleteKeys(SourceKeys sources, Map<String,String> messages) {
        Set<String> set = new HashSet<String>();
        for (Object oldKey : messages.keySet()) {
            if (!sources.keySet().contains(oldKey)) {
                set.add((String) oldKey);
            }
        }
        return set;
    }

    public static Collection<String> getExistingKeys(SourceKeys sources, Map<String,String> messages) {
        Collection<String> obsoleteKeys = getObsoleteKeys(sources, messages);
        Set<String> set = new HashSet<String>();
        for (Object oldKey : messages.keySet()) {
            if (!obsoleteKeys.contains(oldKey)) {
                set.add((String) oldKey);
            }
        }
        return set;
    }
}
