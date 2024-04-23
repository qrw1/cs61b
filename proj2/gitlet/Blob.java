package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.Utils.*;
import static gitlet.MyUtils.getObjectFile;
import static gitlet.MyUtils.saveObjectFile;
@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public class Blob implements Serializable {

    private final File source;
    private final byte[] content;
    private final String id;
    private final File file;

    public Blob(File sourceFile) {
        source = sourceFile;
        String filePath = sourceFile.getPath();
        content = readContents(sourceFile);
        id = sha1(filePath, content);
        file = getObjectFile(id);
    }

    public String getId(){
        return id;
    }

    public File getFile(){
        return file;
    }

    public static String generateId(File sourceFile) {
        String filePath = sourceFile.getPath();
        byte[] fileContent = readContents(sourceFile);
        return sha1(filePath, fileContent);
    }

    public static Blob fromFile(String id) {
        return readObject(getObjectFile(id), Blob.class);
    }


    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }


    public void writeContentToSource() {
        writeContents(source, content);
    }

    public void save() {
        saveObjectFile(file, this);
    }

}
