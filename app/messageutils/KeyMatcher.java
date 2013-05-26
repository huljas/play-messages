package messageutils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import play.Play;

/**
 * Matcher for checking the sources for messages keys.
 * 
 * @author corux
 */
public abstract class KeyMatcher {

    private static KeyMatcher instance;

    /**
     * The singleton instance.
     * 
     * @return The singleton instance.
     */
    public static synchronized KeyMatcher instance() {
        if (instance == null) {
            try {
                String resourceClass = MessagesUtil.getConfig(
                        "messages.keyMatcher", null);

                if (StringUtils.isBlank(resourceClass)) {
                    instance = new MessageKeyMatcher();
                } else {
                    Class<?> clazz = Class.forName(resourceClass, true, Play
                            .application().classloader());
                    instance = (KeyMatcher) clazz.newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    /**
     * List of patterns, that will be matched by {@link #match(String)}.
     */
    private List<Pattern> patterns = new ArrayList<Pattern>();

    /**
     * Adds a pattern to the list that will be used by {@link #match(String)}.
     * 
     * @param pattern
     *            The pattern to add.
     */
    protected void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    /**
     * Returns all localization keys found on the given string.
     * 
     * @param line
     *            The line to search for keys.
     * @return List of all found keys.
     */
    public List<String> match(String line) {
        List<String> list = new ArrayList<String>();
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(line);
            int start = 0;
            while (matcher.find(start) && matcher.groupCount() >= 1) {
                for (int i = 1; i <= matcher.groupCount(); ++i) {
                    String key = matcher.group(i);
                    if (!StringUtils.isBlank(key)) {
                        list.add(key);
                    }
                }
                start = matcher.end();
            }
        }
        return list;
    }

}