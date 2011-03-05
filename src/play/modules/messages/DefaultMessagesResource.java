package play.modules.messages;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import play.Play;
import play.libs.IO;

import java.io.*;
import java.util.*;

/**
 * Default messages resource uses the Play messages files.
 *
 * @author huljas
 */
public class DefaultMessagesResource extends MessagesResource {

    File targetDir;

    public DefaultMessagesResource() {
        targetDir = new File(Play.configuration.getProperty("messages.targetDir", "conf"));
    }

    public List<String> loadKeepList() {
        try {
            File file = new File(targetDir, "messages.keep");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            List<String> result = IOUtils.readLines(in, "UTF-8");
            IOUtils.closeQuietly(in);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> loadIgnoreList() {
        try {
            File file = new File(targetDir, "messages.ignore");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            List<String> result = IOUtils.readLines(in, "UTF-8");
            IOUtils.closeQuietly(in);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> loadMessages(String language) {
        try {
            File file = new File(targetDir, "messages." + language);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            Properties properties = IO.readUtf8Properties(in);
            IOUtils.closeQuietly(in);
            return new HashMap(properties);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveKeepList(List<String> list) {
        File file = new File(targetDir, "messages.keep");
        try {
            Collections.sort(list);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            IOUtils.writeLines(list, null, out, "UTF-8");
            IOUtils.closeQuietly(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveIgnoreList(List<String> list) {
        File file = new File(targetDir, "messages.ignore");
        try {
            Collections.sort(list);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            IOUtils.writeLines(list, null, out, "UTF-8");
            IOUtils.closeQuietly(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMessages(String language, Map<String, String> messages, List<String> removeList) {
        try {
            File file = new File(targetDir, "messages." + language);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            Properties properties = new Properties();
            properties.putAll(messages);
            for (String key : removeList) {
                properties.remove(key);
            }
            // This is ugly but the properties string formatting is so weird that I don't want to
            // start messing around with it.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer stringWriter = new OutputStreamWriter(baos, "UTF-8");
            properties.store(stringWriter, "");
            IOUtils.closeQuietly(stringWriter);
            InputStreamReader lineReader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()), "UTF-8");
            String propertiesAsString = IOUtils.toString(lineReader);
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
            BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            String content = StringUtils.join(list, "\n");
            content = new StringBuilder("# Saved by @messages on ").append(new Date()).append("\n").append(content).toString();
            IOUtils.write(content, fileWriter);
            IOUtils.closeQuietly(fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
