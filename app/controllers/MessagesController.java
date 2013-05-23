package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import messageutils.MessagesResource;
import messageutils.MessagesUtil;
import messageutils.SourceKeys;
import models.IndexModel;
import models.Localization;
import models.SaveModel;
import models.SaveModel.Notification;

import org.apache.commons.lang3.StringUtils;

import play.data.Form;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author huljas
 */
public class MessagesController extends Controller {

    private static List<String> getLangs() {
        ArrayList<String> langs = new ArrayList<String>();
        langs.add(MessagesResource.DEFAULT_LANGUAGE);
        for (Lang i : Lang.availables()) {
            langs.add(i.code());
        }

        return langs;
    }

    /**
     * The index view with all localizations in a datatable.
     * 
     * @return The index view.
     */
    public static Result index() {
        MessagesResource messagesResource = MessagesResource.instance();
        List<String> keepList = messagesResource.loadKeepList();
        List<String> ignoreList = messagesResource.loadIgnoreList();

        SourceKeys sources = SourceKeys.lookUp();

        List<String> langs = getLangs();

        IndexModel model = new IndexModel();
        model.list = new ArrayList<Localization>();
        model.langs = langs;
        model.sources = sources;
        model.keepList = keepList;
        model.ignoreList = ignoreList;
        model.newKeys = new ArrayList<>();
        model.obsoleteKeys = new ArrayList<>();

        for (String i : langs) {
            Map<String, String> values = messagesResource.loadMessages(i);
            Collection<String> newKeys = MessagesUtil.getNewKeys(sources,
                    values);
            Collection<String> obsoleteKeys = MessagesUtil.getObsoleteKeys(
                    sources, values);
            Collection<String> existingKeys = MessagesUtil.getExistingKeys(
                    sources, values);

            for (String key : keepList) {
                if (obsoleteKeys.contains(key) || existingKeys.contains(key)) {
                    existingKeys.add(key);
                } else {
                    newKeys.add(key);
                }
            }
            obsoleteKeys.removeAll(keepList);
            newKeys.removeAll(ignoreList);

            model.obsoleteKeys.addAll(obsoleteKeys);
            model.newKeys.addAll(newKeys);
            for (Entry<String, String> j : values.entrySet()) {
                Localization loc = new Localization(j.getKey(), j.getValue(), i);
                model.list.add(loc);
            }
        }

        for (Localization i : model.list) {
            while (model.newKeys.contains(i.key)) {
                model.newKeys.remove(i.key);
            }
        }

        for (String i : model.newKeys) {
            Localization loc = new Localization(i, null,
                    MessagesResource.DEFAULT_LANGUAGE);
            model.list.add(loc);
        }

        return ok(views.html.MessagesController.index.render(model));
    }

    /**
     * Saves the {@link Localization} value. Only saves if a non-empty value was
     * submitted.
     * 
     * @return Result with the current value of the saved key and a
     *         notification.
     */
    public static Result save() {
        final Form<Localization> model = form(Localization.class)
                .bindFromRequest();
        final Localization m = model.get();

        SaveModel response = new SaveModel();
        response.notification = new Notification();

        boolean restore = false;
        MessagesResource messagesResource = MessagesResource.instance();
        if (!StringUtils.isBlank(m.key) && !StringUtils.isBlank(m.value)) {

            try {
                messagesResource.save(m.locale, m.key, m.value);
                response.value = m.value;
                response.notification.notificationType = "success";
                response.notification.message = Messages.get("save.success",
                        m.key, m.locale);
            } catch (Exception e) {
                response.notification.message = Messages.get("save.error",
                        e.getMessage());
                response.notification.notificationType = "error";

                restore = true;
            }
        } else {
            restore = true;
            response.notification.notificationType = "alert";
            response.notification.message = Messages.get("save.novalue", m.key);
        }

        if (restore) {
            Map<String, String> msgs = messagesResource.loadMessages(m.locale);
            if (msgs.containsKey(m.key)) {
                response.value = msgs.get(m.key);
            } else {
                response.value = "";
            }
        }

        return ok(views.html.MessagesController.save.render(response));
    }

    /**
     * Deletes a key from all localizations.
     * 
     * @return Result with the key (if delete failed) or an empty string (if
     *         delete successful) and a notification.
     */
    public static Result delete() {
        String key = form().bindFromRequest().get("key");

        SaveModel response = new SaveModel();
        response.notification = new Notification();
        response.value = key;

        MessagesResource messagesResource = MessagesResource.instance();
        if (!StringUtils.isBlank(key)) {

            try {
                for (String lang : getLangs()) {
                    messagesResource.removeAll(lang,
                            Arrays.asList(new String[] { key }));
                }

                response.value = "";
                response.notification.notificationType = "success";
                response.notification.message = Messages.get("delete.success",
                        key);
            } catch (Exception e) {
                response.notification.message = Messages.get("delete.error",
                        e.getMessage());
                response.notification.notificationType = "error";
            }
        } else {
            response.notification.notificationType = "alert";
            response.notification.message = Messages.get("delete.novalue", key);
        }

        return ok(views.html.MessagesController.save.render(response));
    }

    /**
     * Gets all occurrences of the requested key in the source.
     * 
     * @return Result with all source snippets.
     */
    public static Result sources() {
        String key = form().bindFromRequest().get("key");
        SourceKeys sources = SourceKeys.lookUp();
        return ok(views.html.MessagesController.sources.render(sources, key));
    }
}
