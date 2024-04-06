package gitlet;


import java.io.File;
import java.io.Serializable;


import static gitlet.Utils.*;

public class MyUtils {


    public static void mkdir(File dir){
        if(!dir.mkdir()){
            throw new IllegalArgumentException(String.format("mkdir: %s: Failed to create.", dir.getPath()));
        }
        //将file与dir区分；
    }

    public static void exit(String message, Object ... args){
        message(message,args);
        System.exit(0);
    }

    public static File getfile(String id){
        String fileName = getfilename(id);
        String dirName = getfiledirname(id);
        return join(Repository.object_dir, dirName, fileName);
    }

    public static String getfilename(String id){
        return id.substring(2);
    }

    public static String getfiledirname(String id){
        return id.substring(0,2);
    }

    public static void rm(File file) {
        if (!file.delete()) {
            //如果没删除成功
            throw new IllegalArgumentException(String.format("rm: %s: Failed to delete.", file.getPath()));
        }
    }


    public static void saveobjectfile(File file, Serializable commit){
        File dir = file.getParentFile();
        if(!dir.mkdir()){
            mkdir(dir);
        }
        writeObject(file,commit);
    }

}