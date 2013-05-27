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
    final String REGEXP4 = "\\QMessages(\'\\E([^\']*)\"";

    /**
     * Creates a new instance of the {@link MessageKeyMatcher}.
     */
    public MessageKeyMatcher() {
        addPattern(Pattern.compile(REGEXP1));
        addPattern(Pattern.compile(REGEXP2));
        addPattern(Pattern.compile(REGEXP3));
        addPattern(Pattern.compile(REGEXP4));
    }

}
