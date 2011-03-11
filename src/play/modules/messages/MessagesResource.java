package play.modules.messages;

import play.Play;

import java.util.List;
import java.util.Map;

/**
 * Class for saving and loading the messages data.
 * <p>
 * Default implementation uses files. To override this you should extend this class and configure the class
 * with the messages.resource property in the application.conf.
 *
 * @author huljas
 */
public abstract class MessagesResource {

    private static MessagesResource instance;

    public static synchronized MessagesResource instance() {
        if (instance == null) {
            try {
                String resourceClass = Play.configuration.getProperty("messages.resource", DefaultMessagesResource.class.getName());
                Class clazz = Class.forName(resourceClass);
                instance = (MessagesResource) clazz.newInstance();
            } catch (Exception e) {
                 throw new RuntimeException(e);
            }
        }
        return instance;
    }


    /**
     * Loads the keys in the keep list.
     */
    public abstract List<String> loadKeepList();

    /**
     * Loads the keys in the ignore list.
     */
    public abstract List<String> loadIgnoreList();

    /**
     * Loads messages for given language.
     */
    public abstract Map<String,String> loadMessages(String language);


    /**
     * Saves new value for given key.
     */
    public abstract void save(String language, String key, String value);

    /**
     * Adds key to keep list.
     */
    public abstract void keep(String key);

    /**
     * Removes key from keep list.
     */
    public abstract void removeKeep(String key);

    /**
     * Removes given keys.
     */
    public abstract void removeAll(String language, List<String> keys);

    /**
     * Ignores given keys.
     */
    public abstract void ignoreAll(List<String> keys);

    /**
     * Unignores given keys.
     */
    public abstract void unignoreAll(List<String> keys);

}
