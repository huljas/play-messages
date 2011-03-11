package controllers;

import org.apache.commons.lang.StringUtils;
import play.Play;
import play.modules.messages.MessagesResource;
import play.modules.messages.MessagesUtil;
import play.modules.messages.SourceKeys;
import play.mvc.Before;
import play.mvc.Controller;

import java.io.IOException;
import java.util.*;

/**
 * @author huljas
 */
public class MessagesController extends Controller {

    @Before
    public static void disableInProduction() {
        if (Play.mode == Play.Mode.PROD) {
            error(404, "Page not found");
        }
    }

    public static void index(String language, String defaultLanguage) {
        if (StringUtils.isBlank(defaultLanguage)) {
            defaultLanguage = Play.langs.get(0);
        }
        if (StringUtils.isBlank(language)) {
            language = defaultLanguage;
        }
        MessagesResource messagesResource = MessagesResource.instance();
        Map<String,String> values = messagesResource.loadMessages(language);
        Map<String,String> defaultValues = messagesResource.loadMessages(defaultLanguage);
        List<String> keepList = messagesResource.loadKeepList();
        List<String> ignoreList = messagesResource.loadIgnoreList();

        SourceKeys sources = SourceKeys.lookUp();

        Collection<String> newKeys = MessagesUtil.getNewKeys(sources, values);
        Collection<String> obsoleteKeys = MessagesUtil.getObsoleteKeys(sources, values);
        Collection<String> existingKeys = MessagesUtil.getExistingKeys(sources, values);

        for (String key : keepList) {
            if (obsoleteKeys.contains(key) || existingKeys.contains(key)) {
                existingKeys.add(key);
            } else {
                newKeys.add(key);
            }
        }
        obsoleteKeys.removeAll(keepList);
        newKeys.removeAll(ignoreList);

        render(language, defaultLanguage, values, defaultValues, sources, newKeys, existingKeys, obsoleteKeys, keepList, ignoreList);
    }

    public static void save(String language, String key, String value, boolean keep) {
        if (!StringUtils.isBlank(value) && !StringUtils.isBlank(key)) {
            MessagesResource messagesResource = MessagesResource.instance();
            messagesResource.save(language, key, value);
            if (keep) {
                messagesResource.keep(key);
            } else {
                messagesResource.removeKeep(key);
            }
        }
        render(value);
    }

    public static void applyChanges(String language, String defaultLanguage, MessagesAction action, List<String> keys) {
        if (action == MessagesAction.REMOVE) {
            MessagesResource messagesResource = MessagesResource.instance();
            messagesResource.removeAll(language, keys);
        } else if (action == MessagesAction.IGNORE) {
            MessagesResource messagesResource = MessagesResource.instance();
            messagesResource.ignoreAll(keys);
        } else if (action == MessagesAction.UNIGNORE) {
            MessagesResource messagesResource = MessagesResource.instance();
            messagesResource.unignoreAll(keys);
        }
        index(language, defaultLanguage);
    }

    public static void addKey(String language, String defaultLanguage, String key) {
        MessagesResource messagesResource = MessagesResource.instance();
        Map<String,String> values = messagesResource.loadMessages(language);
        Map<String,String> defaultValues = messagesResource.loadMessages(defaultLanguage);
        List<String> keepList = new ArrayList<String>();
        keepList.add(key);
        render("_row.html", language, defaultLanguage, values, defaultValues, key, keepList);
    }

    public static void sources(String key) {
        SourceKeys sources = SourceKeys.lookUp();
        render(key, sources);
    }

    private static List<String> removeDuplicates(List<String> list) {
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<String>(new HashSet<String>(list));
    }
}
