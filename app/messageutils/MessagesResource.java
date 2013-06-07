package messageutils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import play.Play;

/**
 * Class for saving and loading the messages data.
 * <p>
 * Default implementation uses files. To override this you should extend this
 * class and configure the class with the messages.resource property in the
 * application.conf.
 * 
 * @author huljas
 */
public abstract class MessagesResource {

    /**
     * The default language without a language code. Localizations with this
     * language will be saved to the file "messages".
     */
    public final static String DEFAULT_LANGUAGE = "default";

    private static MessagesResource instance;

    /**
     * The singleton instance.
     * 
     * @return The singleton instance.
     */
    public static synchronized MessagesResource instance() {
        if (instance == null) {
            try {
                String resourceClass = MessagesUtil.getConfig(
                        "messages.resource", null);

                if (StringUtils.isBlank(resourceClass)) {
                    resourceClass = DefaultMessagesResource.class.getName();
                }

                Class<?> clazz = Class.forName(resourceClass, true, Play
                        .application().classloader());
                instance = (MessagesResource) clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    /**
     * Loads the keys in the keep list.
     * 
     * @return List of keys to keep.
     */
    public abstract List<String> loadKeepList();

    /**
     * Loads the keys in the ignore list.
     * 
     * @return List of keys to ignore.
     */
    public abstract List<String> loadIgnoreList();

    /**
     * Loads messages for given language.
     * 
     * @param language
     *            The language to load values and keys for.
     * @return Mapping of key to value.
     */
    public abstract Map<String, String> loadMessages(String language);

    /**
     * Saves new value for given key.
     * 
     * @param language
     *            The language of the value.
     * @param key
     *            The key to save.
     * @param value
     *            The value for the key in the given language.
     */
    public abstract void save(String language, String key, String value);

    /**
     * Adds key to keep list.
     * 
     * @param key
     *            The key to keep.
     */
    public abstract void keep(String key);

    /**
     * Removes key from keep list.
     * 
     * @param key
     *            The key to remove.
     */
    public abstract void removeKeep(String key);

    /**
     * Removes given keys.
     * 
     * @param language
     *            The language, where the keys will be removed from.
     * @param keys
     *            The keys to remove.
     */
    public abstract void removeAll(String language, List<String> keys);

    /**
     * Ignores given keys.
     * 
     * @param keys
     *            The keys to ignore.
     */
    public abstract void ignoreAll(List<String> keys);

    /**
     * Unignores given keys.
     * 
     * @param keys
     *            The keys to unignore.
     */
    public abstract void unignoreAll(List<String> keys);

}
