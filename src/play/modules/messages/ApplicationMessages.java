package play.modules.messages;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import play.Play;
import play.libs.IO;

import java.io.*;
import java.util.*;

/**
 * @author heikkiu
 */
public class ApplicationMessages {

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
                    i++;
                    if (line.startsWith("//")) {
                        continue;
                    }
                    List<String> keys = matcher.match(line);
                    for (String key : keys) {
                        foundKeys.addKey(key, file, i);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return foundKeys;
    }

    public static File getWorkingDir() {
        File dir = new File(Play.configuration.getProperty("messages.working.dir", "tmp"));
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

    public static void writeKeys(List<String> keys, File file) {
        try {
            IOUtils.writeLines(keys, null, new FileOutputStream(file), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<String> findNewKeys(SourceMessageKeys sources, Properties messages) {
        List<String> list = new ArrayList<String>();
        for (String newKey : sources.keySet()) {
            if (newKey.startsWith("ads.calendarDialogHeader")) {
                int i = 0;
            }

            if (!messages.containsKey(newKey)) {
                list.add(newKey);
            }
        }
        return list;
    }

    public static List<String> findObsoleteKeys(SourceMessageKeys sources, Properties messages) {
        List<String> list = new ArrayList<String>();
        for (Object oldKey : messages.keySet()) {
            if (!sources.keySet().contains(oldKey)) {
                list.add((String) oldKey);
            }
        }
        return list;
    }


    public static List<String> findExistingKeys(SourceMessageKeys sources, Properties messages) {
        List<String> obsoleteKeys = findObsoleteKeys(sources, messages);
        List<String> list = new ArrayList<String>();
        for (Object oldKey : messages.keySet()) {
            if (!obsoleteKeys.contains(oldKey)) {
                list.add((String) oldKey);
            }
        }
        return list;
    }
}
