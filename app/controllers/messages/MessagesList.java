package controllers.messages;

import org.apache.commons.lang.StringUtils;
import play.Play;
import play.i18n.Messages;
import play.modules.messages.ApplicationMessages;
import play.modules.messages.SourceMessageKeys;
import play.mvc.Controller;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author heikkiu
 */
public class MessagesList extends Controller {

    public static void index(String language) {
        if (language == null) {
            language = Play.langs.get(0);
        }
        File workingFile = ApplicationMessages.getWorkingFile(language);
        Properties localizations;
        if (workingFile.exists()) {
            localizations = ApplicationMessages.readUtf8Properties(workingFile);
        } else {
            localizations = Messages.all(language);
        }
        SourceMessageKeys sources = ApplicationMessages.lookUp();
        List<String> newKeys = ApplicationMessages.findNewKeys(sources, localizations);
        List<String> obsoleteKeys = ApplicationMessages.findObsoleteKeys(sources, localizations);
        List<String> existingKeys = ApplicationMessages.findExistingKeys(sources, localizations);
        List<String> keepList = ApplicationMessages.readKeys(ApplicationMessages.getKeepFile());

        obsoleteKeys.removeAll(keepList);
        existingKeys.addAll(keepList);
        
        List<String> ignoreList = ApplicationMessages.readKeys(ApplicationMessages.getIgnoreFile());
        render(workingFile, language, localizations, sources, newKeys, existingKeys, obsoleteKeys, keepList, ignoreList);
    }

    public static void save(String language, Map<String,String> values, List<String> ignoreList, List<String> removeList, List<String> keepList) throws IOException {
        if (ignoreList == null) {
            ignoreList = Collections.EMPTY_LIST;
        }
        if (removeList == null) {
            removeList = Collections.EMPTY_LIST;
        }
        if (keepList == null) {
            keepList = Collections.EMPTY_LIST;
        }
        
        File workingFile = ApplicationMessages.getWorkingFile(language);
        Properties localizations;
        if (workingFile.exists()) {
            localizations = ApplicationMessages.readUtf8Properties(workingFile);
        } else {
            localizations = Messages.all(language);
        }

        for (Map.Entry<String,String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!StringUtils.isBlank(value) && !removeList.contains(key) && !ignoreList.contains(key)) {
                localizations.put(entry.getKey(), entry.getValue());
            }
        }

        for (String remove : removeList) {
            localizations.remove(remove);
        }
        ApplicationMessages.writeUtf8Properties(localizations, ApplicationMessages.getWorkingFile(language), "Created by @messages on " + new Date());
        ApplicationMessages.writeKeys(ignoreList, ApplicationMessages.getIgnoreFile());
        ApplicationMessages.writeKeys(keepList, ApplicationMessages.getKeepFile());
        index(language);
    }
}
