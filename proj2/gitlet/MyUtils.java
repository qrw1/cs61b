package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import static gitlet.Utils.*;

public class MyUtils {

    public static void exit(String mes, Object... args){
        message(mes,args);
        System.exit(0);
    }

    public static boolean isFileInstanceOf(File file, Class<?> c) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return c.isInstance(in.readObject());
        } catch (Exception ignored) {
            return false;
        }
    }

    public static File getObjectFile(String id) {
        String dirName = getObjectDirName(id);
        String fileName = getObjectFileName(id);
        return join(Repository.OBJECTS_DIR, dirName, fileName);
    }
    /**
     * Get directory name from SHA1 id in the objects folder.
     在Git中，为了防止在单个目录下存储过多文件导致性能下降
     ，对象文件会被分散存储在256个子目录（00至ff）中，每
     个子目录下存储具有相同前两个字符的对象文件。因此，该方法从
     SHA-1哈希值中获取前两个字符，用作对象文件所在的子目录名。
     */
    public static String getObjectDirName(String id) {
        return id.substring(0, 2);
    }

    /**
     * Get file name from SHA1 id.
     在Git中，对象文件的名称是其SHA-1哈希值去掉前两个字符后的剩余部分。
     */
    public static String getObjectFileName(String id) {
        return id.substring(2);
    }

    public static void mkdir(File dir){
        if(!dir.mkdir()){
            throw new IllegalArgumentException(String.format("mkdir: %s: Failed to create.", dir.getPath()));
        }
    }

    public static void saveObjectFile(File file, Serializable obj){
        File dir = file.getParentFile();
        if(!dir.exists()){
            mkdir(dir);
        }
        writeObject(file,obj);
    }

    public static void rm(File file) {
        if (!file.delete()) {
            throw new IllegalArgumentException(String.format("rm: %s: Failed to delete.", file.getPath()));
        }
    }

}
