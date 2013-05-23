package messageutils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    /**
     * Gets a list of all keys, that exist in the source but not in the given
     * messages.
     * 
     * @param sources
     *            The source to search for new keys.
     * @param messages
     *            The existing messages.
     * @return List of new keys.
     */
    public static Collection<String> getNewKeys(SourceKeys sources,
            Map<String, String> messages) {
        Set<String> set = new HashSet<String>();
        for (String newKey : sources.keySet()) {
            if (!messages.containsKey(newKey)) {
                set.add(newKey);
            }
        }
        return set;
    }

    /**
     * Gets a list of all obsolete keys, that exist in the given messages but
     * not in the source.
     * 
     * @param sources
     *            The source to search for existing keys.
     * @param messages
     *            The existing messages.
     * @return List of obsolete keys.
     */
    public static Collection<String> getObsoleteKeys(SourceKeys sources,
            Map<String, String> messages) {
        Set<String> set = new HashSet<String>();
        for (Object oldKey : messages.keySet()) {
            if (!sources.keySet().contains(oldKey)) {
                set.add((String) oldKey);
            }
        }
        return set;
    }

    /**
     * Gets a list of all keys, that exist both in the source and in the given
     * messages.
     * 
     * @param sources
     *            The source to search for keys.
     * @param messages
     *            The existing messages.
     * @return List of existing keys.
     */
    public static Collection<String> getExistingKeys(SourceKeys sources,
            Map<String, String> messages) {
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
