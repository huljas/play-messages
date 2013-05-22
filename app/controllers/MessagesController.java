package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static play.data.Form.form;

import org.apache.commons.lang3.StringUtils;

import play.data.Form;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.helper.form;

import messageutils.MessagesResource;
import messageutils.MessagesUtil;
import messageutils.SourceKeys;
import models.Localization;

/**
 * @author huljas
 */
public class MessagesController extends Controller {

    public static Result index() {
        String language = null;
        String defaultLanguage = null;
        if (StringUtils.isBlank(defaultLanguage)) {
            if (Lang.availables().size() == 0) {
                // return
                // internalServerError("ERROR: Required application.langs property is not set!");
            }
            defaultLanguage = MessagesResource.DEFAULT_LANGUAGE;
        }
        if (StringUtils.isBlank(language)) {
            language = MessagesResource.DEFAULT_LANGUAGE;
        }
        MessagesResource messagesResource = MessagesResource.instance();
        Map<String, String> values = messagesResource.loadMessages(language);
        Map<String, String> defaultValues = messagesResource
                .loadMessages(defaultLanguage);
        List<String> keepList = messagesResource.loadKeepList();
        List<String> ignoreList = messagesResource.loadIgnoreList();

        SourceKeys sources = SourceKeys.lookUp();

        Collection<String> newKeys = MessagesUtil.getNewKeys(sources, values);
        Collection<String> obsoleteKeys = MessagesUtil.getObsoleteKeys(sources,
                values);
        Collection<String> existingKeys = MessagesUtil.getExistingKeys(sources,
                values);

        for (String key : keepList) {
            if (obsoleteKeys.contains(key) || existingKeys.contains(key)) {
                existingKeys.add(key);
            } else {
                newKeys.add(key);
            }
        }
        obsoleteKeys.removeAll(keepList);
        newKeys.removeAll(ignoreList);

        List<String> langs = new ArrayList<String>();
        for (Lang i : Lang.availables()) {
            langs.add(i.code());
        }
        langs.add("default");
        
        List<Localization> model = new ArrayList<Localization>();

        return ok(views.html.MessagesController.index.render(model));
    }

    public static Result save() {
        final Form<Localization> model = form(Localization.class)
                .bindFromRequest();
        final Localization m = model.get();

        if (!StringUtils.isBlank(m.key) && !StringUtils.isBlank(m.value)) {
            MessagesResource messagesResource = MessagesResource.instance();
            messagesResource.save(m.locale, m.key, m.value);
        }

        return ok(m.value);
    }

    public static Result sources(String key) {
        SourceKeys sources = SourceKeys.lookUp();
        return ok(views.html.MessagesController.sources.render());
    }

    private static List<String> removeDuplicates(List<String> list) {
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<String>(new HashSet<String>(list));
    }
}
