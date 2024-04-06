package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.MyUtils.getfile;
import static gitlet.MyUtils.saveobjectfile;
import static gitlet.Utils.*;

public class Blob implements Serializable{

    private final File source;
    private final String id;
    private final File file;
    private final byte[] content;

    public Blob(File sourceFile) {
        source = sourceFile;
        String filePath = sourceFile.getPath();
        content = readContents(sourceFile);
        id = sha1(filePath, content);
        file = getfile(id);
    }

    public static String generateId(File sourceFile){
        String filePath = sourceFile.getPath();
        byte[] fileContent = readContents(sourceFile);
        return sha1(filePath, fileContent);
    }

    public String getId(){
        return id;
    }

    public File getFile(){
        return file;
    }

    public void save(){
        saveobjectfile(file, this);
    }

}
