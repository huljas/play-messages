package models;

import java.util.List;

import messageutils.SourceKeys;

public class IndexModel {

    public List<Localization> list;
    public List<String> langs;
    public SourceKeys sources;
    public List<String> keepList;
    public List<String> ignoreList;
    public List<String> newKeys;
    public List<String> obsoleteKeys;
}
