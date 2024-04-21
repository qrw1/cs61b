package gitlet;

// TODO: any imports you need here
import static gitlet.Utils.*;
import static gitlet.MyUtils.*;


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The created date.
     */
    private final Date date;

    /**
     * The message of this Commit.
     */
    private final String message;

    /**
     * The parent commits SHA1 id.
     */
    private final List<String> parents;

    /**
     * The tracked files Map with file path as key and SHA1 id as value.
     */
    private final Map<String, String> tracked;

    /**
     * The SHA1 id.
     */
    private final String id;

    /**
     * The file of this instance with the path generated from SHA1 id.
     */
    private final File file;

    public Commit(String message, List<String> parents, Map<String, String> trackedFilesMap) {
        date = new Date();
        this.message = message;
        this.parents = parents;
        this.tracked = trackedFilesMap;
        id = generateId();
        file = getObjectFile(id);
    }

    /**
     * Initial commit.
     */
    public Commit() {
        date = new Date(0);
        message = "initial commit";
        parents = new ArrayList<>();
        tracked = new HashMap<>();
        id = generateId();
        file = getObjectFile(id);
    }

    public String getTimestamp() {
        // Thu Jan 1 00:00:00 1970 +0000
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public Date getdate(){
       return date;
    }

    public String getMessage(){
        return message;
    }

    public String getId(){
        return id;
    }

    public List<String> getParents(){
        return parents;
    }

    public Map<String,String> getTracked(){
        return tracked;
    }

    public String generateId(){
        return sha1(getTimestamp(), message, parents.toString(), tracked.toString());
    }

    public void save(){
        saveObjectFile(file , this);
    }

    //从文件中读取指定 ID 对应的提交对象（Commit）
    //会读取指定文件中的对象，并将其转换成 Commit 类型的对象，并返回该对象。
    public static Commit fromFile(String id) {
        return readObject(getObjectFile(id), Commit.class);
    }

    public boolean restoretrack(String filePath){
        String blobid = tracked.get(filePath);
        if(blobid == null){
            return false;
        }
        Blob.fromFile(blobid).writeContentToSource();
        return true;
    }

    public void restoreAllTracked() {
        for (String blobId : tracked.values()) {
            Blob.fromFile(blobId).writeContentToSource();
        }
    }

    public String getlog(){
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("===").append("\n");
        logBuilder.append("commit").append(" ").append(id).append("\n");
        if (parents.size() > 1){
            logBuilder.append("Merge:");
            for(String parent : parents){
                logBuilder.append(" ").append(parent, 0 ,7);
            }
            logBuilder.append("\n");
        }
        logBuilder.append("Date:").append(" ").append(getTimestamp()).append("\n");
        logBuilder.append(message).append("\n");
        return logBuilder.toString();
    }

}
