package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;
import static gitlet.MyUtils.*;

public class StagingArea implements Serializable {

    public final Map<String,String> added = new HashMap<>();
    public final Set<String> removed = new HashSet<>();
    private transient Map<String, String> tracked;
    //提交

    public boolean add(File file){
        // 获取文件路径
        String filepath = file.getPath();

        // 创建一个 Blob 对象来表示文件内容
        Blob blob = new Blob(file);
        // 获取文件内容的 ID
        String blobId = blob.getId();
        // 获取追踪列表中该文件的 ID
        //追踪该文件之前是否有过
        String trackedBlobId = tracked.get(filepath);

        // 如果文件已经在追踪列表中，并且文件内容没有发生变化
        if (trackedBlobId != null && trackedBlobId.equals(blobId)) {
            // 如果要添加的文件的 ID 与 Blob 中已经储存的 ID 一致，从暂存区域移除
            // 从已经添加文件列表中移除要添加的文件
            if (added.remove(filepath) != null) {
                //在added里面
                return true;
            }
            // 从已移除文件列表中移除要添加的文件
            //在remove里面
            return removed.remove(filepath);
        }

        // 如果文件不在追踪列表中，或者文件内容发生了变化
        // 将文件添加到已经添加文件列表中
        String prevBlobId = added.put(filepath, blobId);
        //返回之前与该键相关联的值
        // 如果文件已经添加到了已经添加的文件列表中，并且再次调用 add 方法时文件内容没有发生变化
        if (prevBlobId != null && prevBlobId.equals(blobId)) {
            return false;
        }

        if (!blob.getFile().exists()) {
            blob.save();
        }
        return true;
    }

    public boolean remove(File file){
        String filepath = file.getPath();
        String addedBlobId = added.remove(filepath);
        String trackedBlobId = tracked.get(filepath);
        if(trackedBlobId != null){
            if(file.exists()){
                rm(file);
            }
            return removed.add(filepath);

        }

        if(addedBlobId != null){
            //被暂存在added里面
            return true;
        }
        return false;
    }

    public void clear(){
        added.clear();
        removed.clear();
    }

    public Map<String, String> commit() {
        tracked.putAll(added);
        for (String filePath : removed) {
            tracked.remove(filePath);
        }
        clear();
        return tracked;
    }


    public static StagingArea fromFile() {
        return readObject(Repository.index, StagingArea.class);
    }

    public void setTracked(Map<String, String> filesMap) {
        tracked = filesMap;
    }
    public void save() {
        writeObject(Repository.index, this);
    }


    public boolean isClan(){
        return added.isEmpty() && removed.isEmpty();
    }



}