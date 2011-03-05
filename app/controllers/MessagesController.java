package controllers;

import org.apache.commons.lang.StringUtils;
import play.Play;
import play.i18n.Messages;
import play.mvc.Controller;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author heikkiu
 */
public class MessagesController extends Controller {

    public static void index(String language, String defaultLanguage) {
        if (StringUtils.isBlank(defaultLanguage)) {
            defaultLanguage = Play.langs.get(0);
        }
        if (StringUtils.isBlank(language)) {
            language = defaultLanguage;
        }
        File workingFile = MessagesUtil.getWorkingFile(language);
        Properties localizations;
        if (workingFile.exists()) {
            localizations = MessagesUtil.readUtf8Properties(workingFile);
        } else {
            localizations = Messages.all(language);
        }
        Properties defaultLocalizations = Messages.all(defaultLanguage);
        SourceMessageKeys sources = MessagesUtil.lookUp();
        Collection<String> newKeys = MessagesUtil.findNewKeys(sources, localizations);
        Collection<String> obsoleteKeys = MessagesUtil.findObsoleteKeys(sources, localizations);
        Collection<String> existingKeys = MessagesUtil.findExistingKeys(sources, localizations);
        Collection<String> keepList = MessagesUtil.readKeys(MessagesUtil.getKeepFile());
        Collection<String> ignoreList = MessagesUtil.readKeys(MessagesUtil.getIgnoreFile());

        for (String key : keepList) {
            if (obsoleteKeys.contains(key)) {
                existingKeys.add(key);
            } else {
                newKeys.add(key);
            }
        }
        obsoleteKeys.removeAll(keepList);
        newKeys.removeAll(ignoreList);

        render(language, defaultLanguage, localizations, defaultLocalizations, sources, newKeys, existingKeys, obsoleteKeys, keepList, ignoreList);
    }

    public static void save(String language, String defaultLanguage, Map<String,String> values, List<String> ignoreList, List<String> removeList, List<String> keepList) throws IOException {
        if (ignoreList == null) {
            ignoreList = Collections.EMPTY_LIST;
        }
        if (removeList == null) {
            removeList = Collections.EMPTY_LIST;
        }
        if (keepList == null) {
            keepList = Collections.EMPTY_LIST;
        }
        
        File workingFile = MessagesUtil.getWorkingFile(language);
        Properties localizations;
        if (workingFile.exists()) {
            localizations = MessagesUtil.readUtf8Properties(workingFile);
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
        MessagesUtil.writeUtf8Properties(localizations, MessagesUtil.getWorkingFile(language), "Created by @messages on " + new Date());
        MessagesUtil.writeKeys(ignoreList, MessagesUtil.getIgnoreFile());
        MessagesUtil.writeKeys(keepList, MessagesUtil.getKeepFile());
        flash.success("Localizations saved to %s", MessagesUtil.getWorkingFile(language).getPath());
        index(language, defaultLanguage);
    }
}
