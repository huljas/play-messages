package messageutils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matcher for checking the sources for messages keys.
 * 
 * @author huljas
 */
public class MessageKeyMatcher {

    final String REGEXP1 = "\\QMessages.get(\"\\E([^\"]*)\"";
    final String REGEXP2 = "\\QValidation\\E\\Q.addError(\\E[^,]*,[^\"]*[\"]([^\"]*)\"";
    final String REGEXP3 = "\\QMessages(\"\\E([^\"]*)\"";
    final String REGEXP4 = "\\Qmessages\\E\\Q.get(\"\\E([^\"]*)\"";
    final String REGEXP5 = "\\Qmessages\\E\\Q.get(\'\\E([^\']*)\'";
    final String REGEXP6 = "\\Qi18n(\'\\E([^\']*)\'";
    final String REGEXP7 = "\\Qmessages.getMessage(\\E[^,]*,[\\s]*'([^']*)'";

    private List<Pattern> patterns = new ArrayList<Pattern>();

    /**
     * Creates a new instance of the {@link MessageKeyMatcher}.
     */
    public MessageKeyMatcher() {
        patterns.add(Pattern.compile(REGEXP1));
        patterns.add(Pattern.compile(REGEXP2));
        patterns.add(Pattern.compile(REGEXP3));
        patterns.add(Pattern.compile(REGEXP4));
        patterns.add(Pattern.compile(REGEXP5));
        patterns.add(Pattern.compile(REGEXP6));
        patterns.add(Pattern.compile(REGEXP7));
    }

    /**
     * Returns all localization keys found on given string.
     * 
     * @param s
     *            The line to search for keys.
     * @return List of all found keys.
     */
    public List<String> match(String s) {
        List<String> list = new ArrayList<String>();
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(s);
            int start = 0;
            while (matcher.find(start) && matcher.groupCount() == 1) {
                list.add(matcher.group(1));
                start = matcher.end();
            }
        }
        return list;
    }

}
