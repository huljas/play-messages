package controllers.playmessages;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import messageutils.DefaultMessagesResource;
import messageutils.MessagesResource;
import messageutils.MessagesUtil;
import messageutils.SourceKeys;
import models.IndexModel;
import models.Localization;
import models.Notification;
import models.SuccessModel;
import models.ValueModel;

import org.apache.commons.lang3.StringUtils;

import play.data.Form;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;

/**
 * @author huljas
 */
public class MessagesController extends Controller {

    private static List<String> getLangs() {
        ArrayList<String> langs = new ArrayList<String>();
        for (Lang i : Lang.availables()) {
            langs.add(i.code());
        }

        String defaultLang = MessagesUtil.getConfig("messages.defaultLanguage",
                "");

        if (StringUtils.isBlank(defaultLang) || !langs.contains(defaultLang)) {
            langs.add(0, MessagesResource.DEFAULT_LANGUAGE);
        }

        return langs;
    }

    /**
     * The index view with all localizations in a datatable.
     * 
     * @return The index view.
     */
    public static Result index() {
        IndexModel model = getIndexModel();

        return ok(views.html.MessagesController.index.render(model));
    }

    private static IndexModel getIndexModel() {
        MessagesResource messagesResource = MessagesResource.instance();
        List<String> keepList = messagesResource.loadKeepList();
        List<String> ignoreList = messagesResource.loadIgnoreList();

        SourceKeys sources = SourceKeys.lookUp();

        List<String> langs = getLangs();

        IndexModel model = new IndexModel();
        model.list = new ArrayList<Localization>();
        model.langs = langs;
        model.defaultLang = DefaultMessagesResource.getDefaultLanguage();
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
                if (!ignoreList.contains(j.getKey())) {
                    Localization loc = new Localization(j.getKey(),
                            j.getValue(), i);
                    model.list.add(loc);
                }
            }
        }

        model.newKeys = new ArrayList<>(new HashSet<>(model.newKeys));
        model.obsoleteKeys = new ArrayList<>(new HashSet<>(model.obsoleteKeys));
        for (Localization i : model.list) {
            while (model.newKeys.contains(i.key)) {
                model.newKeys.remove(i.key);
            }
        }

        while (model.newKeys.removeAll(ignoreList))
            ;
        for (String i : model.newKeys) {
            Localization loc = new Localization(i, null,
                    MessagesResource.DEFAULT_LANGUAGE);
            model.list.add(loc);
        }
        return model;
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

        ValueModel response = new ValueModel();
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

        return ok(Json.toJson(response));
    }

    /**
     * Deletes a key from all localizations.
     * 
     * @return Result with a success flag and a notification.
     */
    public static Result delete() {
        String key = form().bindFromRequest().get("key");

        SuccessModel response = new SuccessModel();
        response.notification = new Notification();
        response.success = false;

        MessagesResource messagesResource = MessagesResource.instance();
        if (!StringUtils.isBlank(key)) {

            try {
                for (String lang : getLangs()) {
                    messagesResource.removeAll(lang,
                            Arrays.asList(new String[] { key }));
                }

                response.success = true;
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

        return ok(Json.toJson(response));
    }

    /**
     * Adds a key to the keep list.
     * 
     * @return Result with a success flag and a notification.
     */
    public static Result keep() {
        String key = form().bindFromRequest().get("key");

        SuccessModel response = new SuccessModel();
        response.notification = new Notification();
        response.success = false;

        MessagesResource messagesResource = MessagesResource.instance();
        if (!StringUtils.isBlank(key)) {

            try {
                messagesResource.keep(key);

                response.success = true;
                response.notification.notificationType = "success";
                response.notification.message = Messages.get("keep.success",
                        key);
            } catch (Exception e) {
                response.notification.message = Messages.get("keep.error",
                        e.getMessage());
                response.notification.notificationType = "error";
            }
        } else {
            response.notification.notificationType = "alert";
            response.notification.message = Messages.get("keep.novalue", key);
        }

        return ok(Json.toJson(response));
    }

    /**
     * Removes a key from the keep list.
     * 
     * @return Result with the key (if unkeep failed) or an empty string (if
     *         unkeep successful) and a notification.
     */
    public static Result unkeep() {
        String key = form().bindFromRequest().get("key");

        SuccessModel response = new SuccessModel();
        response.notification = new Notification();
        response.success = false;

        MessagesResource messagesResource = MessagesResource.instance();
        if (!StringUtils.isBlank(key)) {

            try {
                messagesResource.removeKeep(key);

                response.success = true;
                response.notification.notificationType = "success";
                response.notification.message = Messages.get("unkeep.success",
                        key);
            } catch (Exception e) {
                response.notification.message = Messages.get("unkeep.error",
                        e.getMessage());
                response.notification.notificationType = "error";
            }
        } else {
            response.notification.notificationType = "alert";
            response.notification.message = Messages.get("unkeep.novalue", key);
        }

        return ok(Json.toJson(response));
    }

    /**
     * Adds a key to the ignore list.
     * 
     * @return Result with the key (if ignore failed) or an empty string (if
     *         ignore successful) and a notification.
     */
    public static Result ignore() {
        String key = form().bindFromRequest().get("key");

        SuccessModel response = new SuccessModel();
        response.notification = new Notification();
        response.success = false;

        MessagesResource messagesResource = MessagesResource.instance();
        if (!StringUtils.isBlank(key)) {

            try {
                ArrayList<String> list = new ArrayList<>();
                list.add(key);
                messagesResource.ignoreAll(list);

                response.success = true;
                response.notification.notificationType = "success";
                response.notification.message = Messages.get("ignore.success",
                        key);
            } catch (Exception e) {
                response.notification.message = Messages.get("ignore.error",
                        e.getMessage());
                response.notification.notificationType = "error";
            }
        } else {
            response.notification.notificationType = "alert";
            response.notification.message = Messages.get("ignore.novalue", key);
        }

        return ok(Json.toJson(response));
    }

    /**
     * Removes a key or all keys from the ignore list.
     * 
     * @return Result with the unignored keys and a notification.
     */
    public static Result unignore() {
        String key = form().bindFromRequest().get("key");

        IndexModel model = null;
        Notification n = new Notification();

        MessagesResource messagesResource = MessagesResource.instance();

        List<String> keys = new ArrayList<>();
        if (!StringUtils.isBlank(key)) {
            keys.add(key);
        } else {
            keys = messagesResource.loadIgnoreList();
        }

        if (keys.size() > 0) {
            try {
                messagesResource.unignoreAll(keys);

                model = getIndexModel();
                List<Localization> newList = new ArrayList<>();
                for (Localization i : model.list) {
                    if (keys.contains(i.key)) {
                        newList.add(i);
                    }
                }
                model.list = newList;

                n.notificationType = "success";
                if (keys.size() == 1) {
                    n.message = Messages.get("unignore.success", keys.get(0));
                } else {
                    n.message = Messages.get("unignore.all.success", key);
                }
            } catch (Exception e) {
                n.message = Messages.get("unignore.error", e.getMessage());
                n.notificationType = "error";
            }
        } else {
            n.notificationType = "alert";
            n.message = Messages.get("unignore.novalue");
        }

        return ok(views.html.MessagesController.addRows.render(model, n));
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
