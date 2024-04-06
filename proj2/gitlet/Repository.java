package gitlet;


import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

public class Repository {

    public static final String init_breach_name = "master";
    private static final String head_branch_ref_prefix = "ref: refs/heads/";
    public static final File cwd = new File(System.getProperty("user.dir"));
    public static final File gitlet_dir = join(cwd, ".gitlet");
    public static final File head = join(gitlet_dir,"heads");
    public static final File object_dir = join(gitlet_dir,"object");
    private static final File refs_dir = join(gitlet_dir, "refs");
    private static final File breach_head_dir = join(refs_dir, "heads");
    public static final File index = join(gitlet_dir, "index");

    /**
     * 检查当前工作目录是否在一个初始化的 Gitlet 仓库内。
     */
    public static void checkWorkingDir() {
        if (!(gitlet_dir.exists() && gitlet_dir.isDirectory())) {
            exit("Not in an initialized Gitlet directory.");
        }
    }

    public final String currentbranch(){
        String headstring = readContentsAsString(head);
        return headstring.replace(head_branch_ref_prefix, "");
    }

    public final Commit HEADcommit = getBranchHeadCommit(currentbranch());

    /**
     * 初始化一个新的 Gitlet 仓库。
     */
    public static void init(){
        if(gitlet_dir.exists()){
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        mkdir(gitlet_dir);
        mkdir(refs_dir);
        mkdir(object_dir);
        mkdir(breach_head_dir);
        createInitialCommit();
        setCurrentBranch(init_breach_name);
    }

    /**
     * 设置当前分支为指定的分支名称。
     * @param branchName 要设置为当前分支的分支名称。
     */
    private static void setCurrentBranch(String branchName) {
        writeContents(head, head_branch_ref_prefix + branchName);
    }

    /**
     * 创建 Gitlet 仓库的初始提交。
     */
    private static void createInitialCommit() {
        Commit initialCommit = new Commit();
        initialCommit.save();
        setBranchHeadCommit(init_breach_name, initialCommit.getId());
    }

    /**
     * 获取指定分支头部的提交对象。
     */
    private static Commit getBranchHeadCommit(String branchName) {
        File branchHeadFile = getBranchHeadFile(branchName);
        return getBranchHeadCommit(branchHeadFile);
    }

    /**
     * 获取指定分支头部的提交对象。
     */
    private static Commit getBranchHeadCommit(File branchHeadFile) {
        String HEADCommitId = readContentsAsString(branchHeadFile);
        return Commit.fromFile(HEADCommitId);
    }

    /**
     * 将name转化为file
     */
    private static void setBranchHeadCommit(String branchName, String commitId) {
        File branchHeadFile = getBranchHeadFile(branchName);
        setBranchHeadCommit(branchHeadFile, commitId);
    }

    //将name写入；

    private static File getBranchHeadFile(String branchName) {
        return join(breach_head_dir, branchName);
    }

    /**
     * 提交head
     */
    private static void setBranchHeadCommit(File branchHeadFile, String commitId) {
        writeContents(branchHeadFile, commitId);
    }

    /**
     * 获取当前工作目录中指定文件名的文件对象。
     */
    private static File getFileFromCWD(String fileName) {
        return Paths.get(fileName).isAbsolute()
                ? new File(fileName)
                : join(cwd, fileName);
    }

    /**
     * 将指定文件添加到暂存区。
     */
    public void add(String filename){
        File file = getFileFromCWD(filename);
        if (!file.exists()) {
            exit("File does not exist.");
        }
        if (stagingArea().add(file)) {
            stagingArea().save();
        }
    }

    public void commit(String message){
        commit(message, null);
    }


    private void commit(String mes,String parent){
        if(stagingArea().isClan()){
            exit("No changes added to the commit");
        }
        Map<String,String> link = stagingArea().commit();
        List<String> parents = new ArrayList<>();
        parents.add(HEADcommit.getId());
        if(parent != null){
            parents.add(parent);
        }
        stagingArea().save();
        Commit com = new Commit(mes, parents, link);
        com.save();
        setBranchHeadCommit(currentbranch(),com.getId());
    }

    public void remove(String filename){
        File file = getFileFromCWD(filename);
        if (stagingArea().remove(file)) {
            stagingArea().save();
        }else {
            exit("No reason to remove the file.");
        }
    }

    public void log(){
        Commit currentcommit = HEADcommit;
        StringBuilder logprint = new StringBuilder();
        while (true) {
            logprint.append(currentcommit.getlog());
            List<String> parentcommitid = currentcommit.getParent();
            if(parentcommitid.size() == 0){
                break;
            }
            String firstParentCommitId = parentcommitid.get(0);
            currentcommit = Commit.fromFile(firstParentCommitId);
        }
        System.out.print(logprint);
    }


    public static void globalLog() {
        StringBuilder logBuilder = new StringBuilder();
        // 按照提交创建日期的逆序遍历所有提交，并将每个提交的日志信息添加到 logBuilder 中
        forEachCommitInOrder(commit -> logBuilder.append(commit.getlog()).append("\n"));
        //它接受一个 Commit 对象作为参数，并执行其中的操作
        System.out.print(logBuilder);
    }

    //Consumer<Commit> 是一个函数式接口，它定义了一个接受单个参数（在这种情况下是 Commit 对象）且不返回结果的操作。
    private static void forEachCommitInOrder(Consumer<Commit> cb) {
        // 创建一个按照提交创建日期逆序排序的比较器
        Comparator<Commit> commitComparator = Comparator.comparing(Commit::getdate).reversed();
        // 创建一个优先队列，用于存储提交对象，并按照比较器中定义的排序规则进行排序
        Queue<Commit> commitsPriorityQueue = new PriorityQueue<>(commitComparator);
        // 调用 forEachCommit 方法，传递 commitsPriorityQueue 作为参数，以便遍历所有提交
        forEachCommit(cb, commitsPriorityQueue);
    }
    //遍历整个树，从head的指向开始，到parents
    private static void forEachCommit(Consumer<Commit> cb) {
        Queue<Commit> commitsQueue = new ArrayDeque<>();
        forEachCommit(cb, commitsQueue);
    }

    private static void forEachCommit(Consumer<Commit> cb, Queue<Commit> queueToHoldCommits) {
        Set<String> checkedCommitIds = new HashSet<>();

        // 遍历所有分支头部文件，获取每个分支头部的提交对象，并将其添加到队列中
        File[] branchHeadFiles = breach_head_dir.listFiles();
        Arrays.sort(branchHeadFiles, Comparator.comparing(File::getName));

        for (File branchHeadFile : branchHeadFiles) {
            String branchHeadCommitId = readContentsAsString(branchHeadFile);
            if (checkedCommitIds.contains(branchHeadCommitId)) {
                continue;
            }
            checkedCommitIds.add(branchHeadCommitId);
            Commit branchHeadCommit = Commit.fromFile(branchHeadCommitId);
            queueToHoldCommits.add(branchHeadCommit);
        }

        // 遍历队列中的提交对象，并执行回调函数
        while (!queueToHoldCommits.isEmpty()) {
            Commit nextCommit = queueToHoldCommits.poll();
            //从队列中获取并移除队列的头部元素
            cb.accept(nextCommit);//接受单个参数并且不返回结果的操作。
            // 获取当前提交的所有父提交，并将未检查过的父提交添加到队列中
            List<String> parentCommitIds = nextCommit.getParent();
            for (String parentCommitId : parentCommitIds) {
                if (!checkedCommitIds.contains(parentCommitId)) {
                    checkedCommitIds.add(parentCommitId);
                    Commit parentCommit = Commit.fromFile(parentCommitId);
                    queueToHoldCommits.add(parentCommit);
                }
            }
        }
    }

    public static void find(String commes){
        StringBuilder resultBuilder = new StringBuilder();
        forEachCommit(commit -> {if(commit.getmessage().equals(commes)) {
            resultBuilder.append(commit.getId()).append("\n");
        }});
        if (resultBuilder.length() == 0) {
            exit("Found no commit with that message.");
        }
        System.out.print(resultBuilder);
    }

    public void status() {
        StringBuilder statusBuilder = new StringBuilder();

        statusBuilder.append("=== Branches ===").append("\n");
        statusBuilder.append("*").append(currentbranch()).append("\n");
        String[] branchNames = breach_head_dir.list((dir, name) -> !name.equals(currentbranch()));
        //排除当前分支
        Arrays.sort(branchNames);
        //排序 Arrays.sort(branchNames);
        for (String branchName : branchNames){
            statusBuilder.append(branchName).append("\n");
        }
        statusBuilder.append("\n");

        statusBuilder.append("=== Staged Files ===").append("\n");
        Map<String,String> addstage = stagingArea().added;
        Set<String> filenames = addstage.keySet();
        for(String filename : filenames){
            statusBuilder.append(filename).append("\n");
        }
        statusBuilder.append("\n");

        statusBuilder.append("=== Removed Files ===").append("\n");
        Set<String> removestage = stagingArea().removed;
        for(String fileName : removestage){
            statusBuilder.append(fileName).append("\n");
        }
        statusBuilder.append("\n");

        statusBuilder.append("=== Modifications Not Staged For Commit ===").append("\n");

        statusBuilder.append("=== Untracked Files ===").append("\n");

        System.out.print(statusBuilder);
    }



    private StagingArea stagingArea() {
        if (index.exists()) {
            return StagingArea.fromFile();
        } else {
            return new StagingArea();
        }
    }
}

