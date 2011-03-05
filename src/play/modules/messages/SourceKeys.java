package play.modules.messages;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import play.Play;
import play.utils.HTML;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Class for finding localization keys in the application sources.
 *
 * @author huljas
 */
public class SourceKeys {

    public static SourceKeys lookUp() {
        SourceKeys foundKeys = new SourceKeys();
        MessageKeyMatcher matcher = new MessageKeyMatcher();
        String s = Play.configuration.getProperty("messages.srcDir", "app");
        String[] paths = s.split(Pattern.quote(","));
        for (String path : paths) {
            Iterator<File> iterator = FileUtils.iterateFiles(new File(path), new String[]{"java", "html"}, true);
            while (iterator.hasNext()) {
                try {
                    File file = iterator.next();
                    List<String> lines = IOUtils.readLines(new FileReader(file));
                    int i = 0;
                    for (String line : lines) {
                        List<String> keys = matcher.match(line);
                        for (String key : keys) {
                            String snippet = getSnippet(key, i, lines);
                            foundKeys.addKey(key, file, snippet, i);
                        }
                        i++;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return foundKeys;
    }

    private static String getSnippet(String key, int line, List<String> lines) {
        int start = Math.max(0, line - 2);
        int end = Math.min(line + 2, lines.size() - 1);
        StringBuilder snippet = new StringBuilder();
        for (int i = start; i <= end; i++) {
            String s = lines.get(i);
            s = HTML.htmlEscape(s);
            if (i == line) {
                s = s.replace(key, "</pre><pre class=\"k\">" + key + "</pre><pre class=\"co\">");
            }
            snippet.append("<div class=\"line").append(i % 2 == 0 ? " even" : " odd").append(i == line ? " focus" : "").append("\"><pre class=\"lno\">").append(String.format("% 4d", line + 1)).append(":</pre><pre class=\"co\">").append(s).append("</pre></div>");
        }
        return snippet.toString();
    }

    private Map<String, KeySourceList> keys = new HashMap<String, KeySourceList>();

    public SourceKeys() {
    }

    public void addKey(String foundKey, File file, String snippet, int lineNo) {
        KeySourceList key = keys.get(foundKey);
        if (key == null) {
            key = new KeySourceList(foundKey);
            keys.put(foundKey, key);
        }
        key.addSource(file, snippet, lineNo);
    }

    public KeySourceList getKeySourceList(String key) {
        KeySourceList list = keys.get(key);
        if (list == null) {
            list = new KeySourceList(key);
        }
        return list;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        TreeMap<String, KeySourceList> map = new TreeMap<String, KeySourceList>(keys);
        for (String key : map.keySet()) {
            builder.append(map.get(key));
            builder.append("\n");
        }
        return builder.toString();
    }

    public Set<String> keySet() {
        return keys.keySet();
    }

    /**
     * List of sourceFiles for localization key.
     */
    public static class KeySourceList {

        public HashMap<String, SourceFile> sourceFiles = new HashMap<String, SourceFile>();

        public String foundKey;

        public KeySourceList(String foundKey) {
            this.foundKey = foundKey;
        }

        public List<SourceFile> listSourceFiles() {
            ArrayList<SourceFile> list = new ArrayList<SourceFile>(sourceFiles.values());
            Collections.sort(list);
            return list;
        }

        public void addSource(File file, String snippet, int lineNo) {
            SourceFile sourceFile = sourceFiles.get(file.getPath());
            if (sourceFile == null) {
                sourceFile = new SourceFile(file.getPath());
                sourceFiles.put(file.getPath(), sourceFile);
            }
            sourceFile.addSnippet(snippet, lineNo);
        }

        /**
         * Single source file.
         */
        public static class SourceFile implements Comparable<SourceFile> {
            public String path;

            public List<Snippet> snippets = new ArrayList<Snippet>();

            public SourceFile(String path) {
                this.path = path;
            }

            public void addSnippet(String snippet, int lineNo) {
                snippets.add(new Snippet(snippet, lineNo));
            }

            public List<Snippet> listSnippets() {
                Collections.sort(snippets);
                return snippets;
            }

            public int compareTo(SourceFile o) {
                return path.compareTo(o.path);
            }

            /**
             * Code snippet in the source file.
             */
            public static class Snippet implements Comparable<Snippet> {
                public String snippet;
                public int lineNo;

                public Snippet(String snippet, int lineNo) {
                    this.snippet = snippet;
                    this.lineNo = lineNo;
                }

                public int compareTo(Snippet o) {
                    return new Integer(lineNo).compareTo(o.lineNo);
                }
            }
        }
    }
}
