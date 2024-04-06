package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.MyUtils.getfile;
import static gitlet.MyUtils.saveobjectfile;
import static gitlet.Utils.readObject;
import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable{
    private String message;
    private String id;
    private List<String> parent;
    private Map<String, String> tracked;
    private Date timepointer;
    private File file;

    public Commit(){
        timepointer = new Date(0);
        message = "initcommit";
        id = getId();
        parent = new ArrayList<>();
        tracked = new HashMap<>();
        file = getfile(id);
    }

    public Commit(String message, List<String> parent, Map<String,String> link){
        timepointer = new Date();
        this.message = message;
        this.parent = parent;
        this.tracked = link;
        id = getId();
        file = getfile(id);
    }

    public String gettimepointer(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (dateFormat.format(this.timepointer));
    }

    public String getId(){
        return sha1(gettimepointer(), message,tracked.toString(),parent.toString());
    }

    public List<String> getParent(){
        return parent;
    }

    public Date getdate(){
        return timepointer;
    }

    public String getmessage(){
        return message;
    }

    public void save(){
        saveobjectfile(file,this);
    }

    public Map<String, String> getTracked() {
        return tracked;
    }



    public String getlog(){
        StringBuilder print = new StringBuilder();
        print.append("===").append("\n");
        print.append("commit").append(" ").append(id).append("\n");
        if(parent.size() > 1){
            print.append("Merge:");
            for (String parent : parent) {
                print.append(" ").append(parent, 0, 7);
            }
            print.append("\n");
        }
        print.append("Date:").append(" ").append(gettimepointer()).append("\n");
        print.append(message).append("\n");
        return print.toString();
    }


    public static Commit fromFile(String id) {
        return readObject(getfile(id), Commit.class);
    }
    //，它采用了 getfile(id) 方法获取存储特定提交的文件，然后调用 readObject()
    // 方法从该文件中读取提交对象。这个提交对象被转换成 Commit 类型，并返回给调用者。

}
