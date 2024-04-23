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
    public boolean add(File file){
        String  filePath = file.getPath();

        Blob blob = new Blob(file);
        String blobid = blob.getId();

        String trackedid = tracked.get(filePath);
        if(trackedid != null && blobid.equals(trackedid)){
            if(added.remove(filePath) != null){
                return true;
            }
            //Set 接口的 remove(Object obj) 方法的返回值是一个布尔值，
            // 用于表示移除操作是否成功。如果集合中存在指定的元素并成功移除，
            // 则返回 true；如果集合中不存在指定的元素，则返回 false。
            return removed.remove(filePath);
        }

        String prevBlobId = added.put(filePath,blobid);

        if (prevBlobId != null && prevBlobId.equals(blobid)) {
            //相等等于没变
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
        writeContents(Repository.INDEX,this);
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
