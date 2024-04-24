package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.MyUtils.rm;
import static gitlet.Utils.*;

public class StagingArea implements Serializable {
    private final Map<String,String> added = new HashMap<>();
    private final Set<String> removed = new HashSet<>();

    //跟踪commit中的内容
    //key = 文件路径， id = 值
    private transient Map<String,String> tracked;
   //如果文件的当前工作版本与当前提交中的版本相同，请不要暂存要添加的版本，
   // 如果文件已存在，则将其从暂存区域中删除（当文件被更改、添加，
   // 然后更改回其原始版本时可能会发生这种情况）。
   // 如果该文件是在执行命令时暂存以进行删除的（请参阅 gitlet rm ）。
   public boolean add(File file) {
       String filePath = file.getPath();

       Blob blob = new Blob(file);
       String blobId = blob.getId();

       String trackedBlobId = tracked.get(filePath);
       if (trackedBlobId != null) {
           if (trackedBlobId.equals(blobId)) {
               if (added.remove(filePath) != null) {
                   return true;
               }
               return removed.remove(filePath);
           }
       }


       String prevBlobId = added.put(filePath, blobId);
       if (prevBlobId != null && prevBlobId.equals(blobId)) {
           return false;
       }

       if (!blob.getFile().exists()) {
           blob.save();
       }

       return true;
   }


    public boolean remove(File file){
        String  filePath = file.getPath();

        String addedBlobId = added.remove(filePath);
        if (addedBlobId != null) {
            return true;
        }

        String trackedid = tracked.get(filePath);
        if(trackedid != null){
            if(file.exists()){
                rm(file);
            }
            return removed.add(filePath);
        }
        return false;
    }

    public void save(){
        writeObject(Repository.INDEX,this);
    }

    public void setTracked(Map<String,String> map){
        tracked = map;
    }

    public void clear(){
       added.clear();
       removed.clear();
    }

    public boolean isClean() {
        return added.isEmpty() && removed.isEmpty();
    }

    public Map<String, String> commit(){
        tracked.putAll(added);
        for (String filePath : removed) {
            tracked.remove(filePath);
        }
        clear();
        return tracked;
    }

    public static StagingArea fromFile(){
        return readObject(Repository.INDEX, StagingArea.class);
    }

    public Set<String> getRemoved(){
        return removed;
    }

    public Map<String,String> getAdded(){
        return added;
    }

}
