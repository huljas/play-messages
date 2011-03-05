package play.modules.messages;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import play.Play;
import play.libs.IO;
import play.utils.HTML;

import java.io.*;
import java.util.*;

/**
 * @author heikkiu
 */
public class MessagesUtil {

    public static SourceMessageKeys lookUp() {
        SourceMessageKeys foundKeys = new SourceMessageKeys();
        MessageKeyMatcher matcher = new MessageKeyMatcher();
        File srcPath = new File("app");
        Iterator<File> iterator = FileUtils.iterateFiles(srcPath, new String[]{"java", "html"}, true);
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

    public static File getWorkingDir() {
        File dir = new File(Play.configuration.getProperty("messages.working.dir", "conf"));
        return dir;
    }

    public static File getWorkingFile(String language) {
        return new File(getWorkingDir(), "messages." + language);
    }

    public static File getKeepFile() {
        return new File(getWorkingDir(), "messages.keep");
    }

    public static File getIgnoreFile() {
        return new File(getWorkingDir(), "messages.ignore");
    }

    public static Properties readUtf8Properties(File file) {
        try {
            return IO.readUtf8Properties(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeUtf8Properties(Properties properties, File file, String comment) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer stringWriter = new OutputStreamWriter(baos, "UTF-8");
            properties.store(stringWriter, "");
            IOUtils.closeQuietly(stringWriter);
            String propertiesAsString = IOUtils.toString(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()), "UTF-8"));
            String[] lines = StringUtils.split(propertiesAsString, "\n");
            List<String> list = new ArrayList<String>();
            for (String line : lines) {
                if (line.trim().length() > 0) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    list.add(line);
                }
            }
            Collections.sort(list);
            PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            String content = StringUtils.join(list, "\n");
            if (comment != null) {
                content = String.format("# %s\n%s", comment, content);
            }
            IOUtils.write(content, fileWriter);
            IOUtils.closeQuietly(fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readKeys(File file) {
        try {
            return IOUtils.readLines(new FileInputStream(file), "UTF-8");
        } catch (FileNotFoundException e) {
            return new ArrayList<String>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeKeys(Collection<String> keys, File file) {
        try {
            List<String> list = new ArrayList<String>(new HashSet(keys));
            Collections.sort(list);
            IOUtils.writeLines(list, null, new FileOutputStream(file), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Collection<String> findNewKeys(SourceMessageKeys sources, Properties messages) {
        Set<String> set = new HashSet<String>();
        for (String newKey : sources.keySet()) {
            if (newKey.startsWith("ads.calendarDialogHeader")) {
                int i = 0;
            }

            if (!messages.containsKey(newKey)) {
                set.add(newKey);
            }
        }
        return set;
    }

    public static Collection<String> findObsoleteKeys(SourceMessageKeys sources, Properties messages) {
        Set<String> set = new HashSet<String>();
        for (Object oldKey : messages.keySet()) {
            if (!sources.keySet().contains(oldKey)) {
                set.add((String) oldKey);
            }
        }
        return set;
    }


    public static Collection<String> findExistingKeys(SourceMessageKeys sources, Properties messages) {
        Collection<String> obsoleteKeys = findObsoleteKeys(sources, messages);
        Set<String> set = new HashSet<String>();
        for (Object oldKey : messages.keySet()) {
            if (!obsoleteKeys.contains(oldKey)) {
                set.add((String) oldKey);
            }
        }
        return set;
    }
}
