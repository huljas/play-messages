package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import play.data.DynamicForm;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author huljas
 */
public class LocalizedApplication extends Controller {

    public static List<String> getLangs() {
        List<String> list = new ArrayList<>();
        for (Lang i : Lang.availables()) {
            list.add(i.code());
        }

        return list;
    }

    public static Result hello() {
        DynamicForm f = form().bindFromRequest();
        String name = f.get("name");
        if (StringUtils.isBlank(name)) {
            name = Messages.get("you");
        }
        String hello = Messages.get("hello.type.normal", name);
        return ok(views.html.hello.render(hello));
    }

    public static Result helloWithType() {
        DynamicForm f = form().bindFromRequest();
        String name = f.get("name");
        String type = f.get("type");
        String hello = Messages.get("hello.type." + type, name);
        return ok(views.html.hello.render(hello));
    }

    public static Result setLang(String lang) {
        changeLang(lang);
        return hello();
    }

}
