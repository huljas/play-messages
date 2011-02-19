package play.modules.messages;

import java.io.File;
import java.util.*;

/**
 * List of localization keys in the sources.
 */
public class SourceMessageKeys {

    private Map<String, KeySourceList> keys = new HashMap<String, KeySourceList>();

    public SourceMessageKeys() {
    }

    public SourceMessageKeys(Map<String, KeySourceList> keys) {
        this.keys = keys;
    }

    public void addKey(String foundKey, File file, int line) {
        KeySourceList key = keys.get(foundKey);
        if (key == null) {
            key = new KeySourceList(foundKey);
            keys.put(foundKey, key);
        }
        key.addSource(file, line);
    }

    public KeySourceList getSources(String key) {
        KeySourceList list = keys.get(key);
        if (list == null) {
            list = new KeySourceList(key);
        }
        return list;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        TreeMap<String, KeySourceList> map = new TreeMap<String, KeySourceList>(keys);
        for (String key : map.keySet()) {
            builder.append(map.get(key));
            builder.append("\n");
        }
        return builder.toString();
    }

    public SourceMessageKeys notInSet(Set set) {
        Map<String, KeySourceList> map = new HashMap<String, KeySourceList>();
        for (KeySourceList key : keys.values()) {
            if (!set.contains(key.getKey())) {
                map.put(key.getKey(), key);
            }
        }
        return new SourceMessageKeys(map);
    }


    public List<String> notInSelf(Set set) {
        Set<String> missing = new HashSet<String>();
        for (Object key : set) {
            if (!keys.containsKey(key)) {
                missing.add((String)key);
            }
        }
        List<String> list = new ArrayList<String>(missing);
        Collections.sort(list);
        return list;
    }


    public SourceMessageKeys findInvalidKeys() {
        Map<String, KeySourceList> map = new HashMap<String, KeySourceList>();
        for (KeySourceList key : keys.values()) {
            if (!isValidKey(key.getKey())) {
                map.put(key.getKey(), key);
            }
        }
        return new SourceMessageKeys(map);
    }

    public static boolean isValidKey(String key) {
        return key.matches("(\\p{Alnum}*\\.)*\\p{Alnum}+");
    }

    public int size() {
        return keys.size();
    }

    public Set<String> keySet() {
        return keys.keySet();        
    }

    public Map<String, String> keyMap() {
        Map<String, String> map = new HashMap<String,String>();
        for (String key : keys.keySet()) {
            map.put(key, key);
        }
        return map;
    }

    /**
     * List of sources for localization key.
     */
    private class KeySourceList {

        private HashSet<KeySource> sources = new HashSet<KeySource>();

        private String foundKey;

        public KeySourceList(String foundKey) {
            this.foundKey = foundKey;
        }

        public void addSource(File file, int line) {
            sources.add(new KeySource(file.getPath(), line));
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (KeySource source : sources) {
                i++;
                builder.append(source);
                if (i < sources.size()) {
                    builder.append(",\n");
                }
                if (i > 5) {
                    builder.append(" ...");
                    break;
                }
            }
            return builder.toString();
        }

        public String getKey() {
            return foundKey;
        }

        /**
         * Source for localization key.
         */
        private class KeySource {
            private String name;
            private int line;

            public KeySource(String name, int line) {
                this.name = name;
                this.line = line;
            }

            public String toString() {
                return name + "(" + line + ")";
            }
        }
    }
}
