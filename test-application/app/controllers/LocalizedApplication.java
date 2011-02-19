package controllers;

import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Controller;

/**
 * @author heikkiu
 */
public class LocalizedApplication extends Controller {

    public static void hello(String name) {
        String hello = Messages.get("hello", name);
        render(hello);
    }

    public static void helloWithType(String name, String type) {
        String hello = Messages.get("hello.type." + type, name);
        render("@hello", hello);
    }

    public static void setLang(String lang) {
        Lang.change(lang);
        hello(null);
    }

}
