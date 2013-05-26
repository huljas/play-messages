package messageutils;

import java.util.regex.Pattern;

/**
 * Matcher for checking the sources for messages keys.
 * 
 * @author huljas
 */
public class MessageKeyMatcher extends KeyMatcher {

    final String REGEXP1 = "\\QMessages.get(\"\\E([^\"]*)\"";
    final String REGEXP2 = "\\QValidation\\E\\Q.addError(\\E[^,]*,[^\"]*[\"]([^\"]*)\"";
    final String REGEXP3 = "\\QMessages(\"\\E([^\"]*)\"";
    final String REGEXP4 = "\\Qmessages\\E\\Q.get(\"\\E([^\"]*)\"";
    final String REGEXP5 = "\\Qmessages\\E\\Q.get(\'\\E([^\']*)\'";
    final String REGEXP6 = "\\Qi18n(\'\\E([^\']*)\'";
    final String REGEXP7 = "\\Qmessages.getMessage(\\E[^,]*,[\\s]*'([^']*)'";

    /**
     * Creates a new instance of the {@link MessageKeyMatcher}.
     */
    public MessageKeyMatcher() {
        addPattern(Pattern.compile(REGEXP1));
        addPattern(Pattern.compile(REGEXP2));
        addPattern(Pattern.compile(REGEXP3));
        addPattern(Pattern.compile(REGEXP4));
        addPattern(Pattern.compile(REGEXP5));
        addPattern(Pattern.compile(REGEXP6));
        addPattern(Pattern.compile(REGEXP7));
    }

}
