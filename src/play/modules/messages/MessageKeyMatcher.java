package play.modules.messages;

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

    String REGEXP1 = "\\QMessages.get(\"\\E([^\"]*)\"";
    String REGEXP2 = "\\QValidation\\E\\Q.addError(\\E[^,]*,[^\"]*[\"]([^\"]*)\"";
    String REGEXP3 = "\\Q&{\\E[']([^']*)[']";
    String REGEXP4 = "\\Qmessages\\E\\Q.get(\"\\E([^\"]*)\"";
    String REGEXP5 = "\\Qmessages\\E\\Q.get(\'\\E([^\']*)\'";
    String REGEXP6 = "\\Qi18n(\'\\E([^\']*)\'";

    private List<Pattern> patterns = new ArrayList<Pattern>();

    public MessageKeyMatcher() {
        patterns.add(Pattern.compile(REGEXP1));
        patterns.add(Pattern.compile(REGEXP2));
        patterns.add(Pattern.compile(REGEXP3));
        patterns.add(Pattern.compile(REGEXP4));
        patterns.add(Pattern.compile(REGEXP5));                
        patterns.add(Pattern.compile(REGEXP6));                
    }

    /**
     * Returns all localization keys found on given string.
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
