package models;

public class Localization {

    public Localization(String key, String value, String locale) {
        this.key = key;
        this.value = value;
        this.locale = locale;
    }

    public String key;
    public String value;
    public String locale;
}
