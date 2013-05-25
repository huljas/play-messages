package messageutils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import play.Play;

/**
 * Default messages resource uses the Play messages files.
 * 
 * @author huljas
 */
public class DefaultMessagesResource extends MessagesResource {

    protected File targetDir;
    private String defaultLanguage;

    /**
     * Gets the default language configured by the application or an empty
     * string if undefined.
     * 
     * @return The default language.
     */
    public static String getDefaultLanguage() {
        return MessagesUtil.getConfig("messages.defaultLanguage", "");
    }

    /**
     * Creates a new instance of the {@link DefaultMessagesResource}.
     */
    public DefaultMessagesResource() {
        String applicationPath = Play.application().path().toString();
        String separator = System.getProperty("file.separator");
        targetDir = new File(applicationPath + separator
                + MessagesUtil.getConfig("messages.targetDir", "conf"));
        defaultLanguage = getDefaultLanguage();
    }

    @Override
    public List<String> loadKeepList() {
        try {
            File file = new File(targetDir, "messages.keep");
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(file));
            List<String> result = IOUtils.readLines(in, "UTF-8");
            IOUtils.closeQuietly(in);
            return result;
        } catch (FileNotFoundException e) {
            return new ArrayList<String>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> loadIgnoreList() {
        try {
            File file = new File(targetDir, "messages.ignore");
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(file));
            List<String> result = IOUtils.readLines(in, "UTF-8");
            IOUtils.closeQuietly(in);
            return result;
        } catch (FileNotFoundException e) {
            return new ArrayList<String>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> loadMessages(String language) {
        BufferedReader in = null;
        try {
            File file = getMessagesFile(language);
            in = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file), "UTF-8"));
            Properties properties = new Properties();
            properties.load(in);
            in.close();
            HashMap<String, String> map = new HashMap<String, String>();
            for (Entry<Object, Object> i : properties.entrySet()) {
                map.put(i.getKey().toString(), i.getValue().toString());
            }

            return map;
        } catch (FileNotFoundException e) {
            return new HashMap<String, String>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void save(String language, String key, String value) {
        Map<String, String> messages = loadMessages(language);
        messages.put(key, value);
        saveMessages(language, messages);
    }

    @Override
    public void keep(String key) {
        List<String> keepList = loadKeepList();
        if (!keepList.contains(key)) {
            keepList.add(key);
            saveKeepList(keepList);
        }
    }

    @Override
    public void removeKeep(String key) {
        List<String> keepList = loadKeepList();
        if (keepList.contains(key)) {
            keepList.remove(key);
            saveKeepList(keepList);
        }
    }

    @Override
    public void removeAll(String language, List<String> keys) {
        Map<String, String> messages = loadMessages(language);
        messages.keySet().removeAll(keys);
        saveMessages(language, messages);
    }

    @Override
    public void ignoreAll(List<String> keys) {
        List<String> ignoreList = loadIgnoreList();
        ignoreList.removeAll(keys);
        ignoreList.addAll(keys);
        saveIgnoreList(ignoreList);
    }

    @Override
    public void unignoreAll(List<String> keys) {
        List<String> ignoreList = loadIgnoreList();
        ignoreList.removeAll(keys);
        saveIgnoreList(ignoreList);
    }

    protected void saveKeepList(List<String> list) {
        File file = new File(targetDir, "messages.keep");
        try {
            Collections.sort(list);
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file));
            IOUtils.writeLines(list, null, out, "UTF-8");
            IOUtils.closeQuietly(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void saveIgnoreList(List<String> list) {
        File file = new File(targetDir, "messages.ignore");
        try {
            Collections.sort(list);
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file));
            IOUtils.writeLines(list, null, out, "UTF-8");
            IOUtils.closeQuietly(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encode(String str) {
        return str.replace("\r", "\\r").replace("\n", "\\n")
                .replace("=", "\\=");
    }

    protected void saveMessages(String language, Map<String, String> messages) {
        try {
            String lineEnding = System.getProperty("line.separator");
            File file = getMessagesFile(language);
            Collection<String> lines = new ArrayList<>();
            lines.add("# Saved by play-messages");
            lines.add(String.format("# %s", new Date()));
            BufferedWriter fileWriter = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

            Object[] keys = messages.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                lines.add(String.format("%s = %s", encode(key.toString()),
                        encode(messages.get(key))));
            }

            IOUtils.writeLines(lines, lineEnding, fileWriter);
            IOUtils.closeQuietly(fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getMessagesFile(String language) {
        if (language.equals(DEFAULT_LANGUAGE)) {
            return new File(targetDir, defaultLanguage.isEmpty() ? "messages"
                    : "messages." + defaultLanguage);
        } else {
            return new File(targetDir, "messages." + language);
        }
    }
}
