package gitlet;


import java.io.File;
import java.nio.file.Paths;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    //暂存？？？
    public static final File INDEX = join(GITLET_DIR, "index");
    private static final String DEFAULT_BRANCH_NAME = "master";
    //定义了 refs 目录，用于存放 Git 中的引用（reference）信息，比如分支、标签等。
    private static final File REFS_DIR = join(GITLET_DIR, "refs");
    //定义了 refs/heads 目录
    private static final File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");
    private static final String HEAD_BRANCH_REF_PREFIX = "ref: refs/heads/";
    //用于存放 Git 中的对象（object），包括提交、树、blob 等。
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    private static final StagingArea stagingArea() {
        StagingArea s;
        if(INDEX.exists()) {
            s = StagingArea.fromFile();
        }else {
            s = new StagingArea();
        }
        s.setTracked(HEADCommit().getTracked());
        return s;
    }


    private static String currentBranch() {
        String HEADFileContent = readContentsAsString(HEAD);
        //为了获取当前分支名称，我们可以从 HEAD 文件的内容中提取出分支名称部分，
        // 即将 ref: refs/heads/ 替换为空字符串，这样就得到了纯粹的分支名称。
        //109
        return HEADFileContent.replace(HEAD_BRANCH_REF_PREFIX, "");
    }

    private static File[] currentfiles() {
        //listFiles() 是 File 类的一个方法，用于获取目录中的所有文件和子目录。
        // 它返回一个 File[] 数组，其中包含目录中的所有文件和子目录的文件对象。
        // 可以通过传递一个 FileFilter 对象来过滤文件，或者传递一个文件名过滤器字符串来选择文件。
        // 如果目录不存在或者不是一个目录，它将返回 null。
        return CWD.listFiles(File::isFile);
    }

    //获取当前分支的最新提交
    private static Commit HEADCommit() {
        return getBranchHeadCommit(currentBranch());
    }

    private static Commit getBranchHeadCommit(String branchName) {
        File branchHeadFile = getBranchHeadFile(branchName);
        return getBranchHeadCommit(branchHeadFile);
    }

    private static Commit getBranchHeadCommit(File branchHeadFile) {
        //119,headfile 里存储id
        String HEADCommitId = readContentsAsString(branchHeadFile);
        return Commit.fromFile(HEADCommitId);
    }



    /* TODO: fill in the rest of this class. */

    public static void checkWorkDir() {
        if (!(GITLET_DIR.exists() && GITLET_DIR.isDirectory())) {
            exit("Not in an initialized Gitlet directory.");
        }
    }


    public static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        mkdir(GITLET_DIR);
        mkdir(OBJECTS_DIR);
        mkdir(REFS_DIR);
        mkdir(BRANCH_HEADS_DIR);
        setcurrentbranch(DEFAULT_BRANCH_NAME);
        initcommit();
    }

    private static void initcommit() {
        Commit initcommit = new Commit();
        initcommit.save();
        setBranchHeadCommit(DEFAULT_BRANCH_NAME, initcommit.getId());
    }

    private static void setcurrentbranch(String branchName) {
        writeContents(HEAD, HEAD_BRANCH_REF_PREFIX + branchName);
    }


    private static void setBranchHeadCommit(String branchName, String commitId) {
        File branchHeadFile = getBranchHeadFile(branchName);
        setBranchHeadCommit(branchHeadFile, commitId);
    }

    private static void setBranchHeadCommit(File branchHeadFile, String commitId) {
        writeContents(branchHeadFile, commitId);
    }


    //返回的是给定分支名称的分支头文件。在 Git 中，分支头文件通常位于 .git/refs/heads/ 目录下，
    // 文件名与分支名称相同，存储着该分支最新提交的哈希值。在 Gitlet 中，
    // getBranchHeadFile() 方法根据给定的分支名称构建分支头文件的路径，并返回对应的 File 对象。
    //
    //举个例子，如果传入的分支名称是 master，那么 getBranchHeadFile("master") 将返回 .git/refs/heads/master 这个文件。
    private static File getBranchHeadFile(String branchName) {
        return join(BRANCH_HEADS_DIR, branchName);
    }

    public void add(String filename) {
        File file = getFile(filename);
        StagingArea stagingAreais = stagingArea();
        if (!file.exists()) {
            exit("File does not exist.");
        }
        if (stagingAreais.add(file)) {
            stagingAreais.save();
        }
    }

    public void commit(String msg) {
        commit(msg, null);
    }

    private void commit(String mes, String parent) {
        StagingArea stagingAreais = stagingArea();
        if (stagingAreais.isClean()) {
            exit("No changes added to the commit.");
        }
        Map<String, String> newTrackedFilesMap = stagingAreais.commit();
        stagingAreais.save();
        List<String> parents = new ArrayList<>();
        //从head（head文件里面只储存id）里面找到id
        parents.add((HEADCommit().getId()));
        if (parent != null) {
            parents.add(parent);
        }
        Commit newcommit = new Commit(mes, parents, newTrackedFilesMap);
        newcommit.save();
        setBranchHeadCommit(currentBranch(), newcommit.getId());
    }

    // 如果传递给 getFile() 方法的文件名是绝对路径，那么直接使用 new File(fileName)
    // 创建 File 对象就足够了，因为绝对路径已经包含了完整的路径信息，不需要再与当前工作目录拼接。
    // 使用 join(CWD, fileName) 拼接当前工作目录可能会导致错误的结果，
    // 因为它会将绝对路径视为相对路径，并在其前面添加当前工作目录。
    private static File getFile(String fileName) {
        return Paths.get(fileName).isAbsolute()
                ? new File(fileName)
                : join(CWD, fileName);
    }

    //既未暂存，也未被头提交跟踪
    public void remove(String filename) {
        File file = getFile(filename);
        StagingArea stagingAreais = stagingArea();
        if (stagingAreais.remove(file)) {
            stagingAreais.save();
        } else {
            exit("No reason to remove the file.");
        }
    }

    public void log() {
        StringBuilder log = new StringBuilder();
        Commit currentcommit = HEADCommit();

        while (true) {
            log.append(currentcommit.getlog()).append("\n");
            List<String> parentCommitIds = currentcommit.getParents();
            if (parentCommitIds.size() == 0) {
                break;
            }
            String firstParentCommitId = parentCommitIds.get(0);
            currentcommit = Commit.fromFile(firstParentCommitId);
        }
        System.out.print(log);
    }

    private static List<String> forCommitInObject() {
        // 获取对象目录
        File objectsDir = Repository.OBJECTS_DIR;
        // 获取所有对象文件名
        return Utils.plainFilenamesIn(objectsDir);

    }

    public void globalLog() {
        List<String> objectFiles = forCommitInObject();
        // 遍历所有对象文件
        for (String fileName : objectFiles) {
            // 读取提交对象
            Commit commit = Commit.fromFile(fileName);
            // 打印提交信息
            System.out.println(commit.getlog());
        }
    }


    public static void find(String mes) {
        StringBuilder id = new StringBuilder();
        List<String> objectFiles = forCommitInObject();
        for (String fileName : objectFiles) {
            // 读取提交对象
            Commit commit = Commit.fromFile(fileName);

            if (commit.getMessage().equals(mes)) {
                id.append(commit.getId()).append("\n");
            }
        }
        System.out.print(id);
    }

    private static void appendFileNamesInOrder(StringBuilder stringBuilder, List<String> filePathsList) {
        filePathsList.sort(String::compareTo);
        for (String filePath : filePathsList) {
            String fileName = Paths.get(filePath).getFileName().toString();
            stringBuilder.append(fileName).append("\n");
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void status() {
        StringBuilder statusBuilder = new StringBuilder();

        // branches
        statusBuilder.append("=== Branches ===").append("\n");
        statusBuilder.append("*").append(currentBranch()).append("\n");
        String[] branchNames = BRANCH_HEADS_DIR.list((dir, name) -> !name.equals(currentBranch()));
        Arrays.sort(branchNames);
        for (String branchName : branchNames) {
            statusBuilder.append(branchName).append("\n");
        }
        statusBuilder.append("\n");
        // end

        Map<String, String> addedFilesMap = stagingArea().getAdded();
        Set<String> removedFilePathsSet = stagingArea().getRemoved();

        // staged files
        statusBuilder.append("=== Staged Files ===").append("\n");
        List<String> added = new ArrayList<>(addedFilesMap.keySet());
        appendFileNamesInOrder(statusBuilder, added);
        statusBuilder.append("\n");
        // end

        // removed files
        statusBuilder.append("=== Removed Files ===").append("\n");
        List<String> removed = new ArrayList<>(addedFilesMap.keySet());
        appendFileNamesInOrder(statusBuilder, removed);
        statusBuilder.append("\n");
        // end

        // modifications not staged for commit
        statusBuilder.append("=== Modifications Not Staged For Commit ===").append("\n");
        List<String> modifiedNotStageFilePaths = new ArrayList<>();
        Set<String> deletedNotStageFilePaths = new HashSet<>();

        Map<String, String> currentFilesMap = getCurrentFilesMap();
        Map<String, String> trackedFilesMap = HEADCommit().getTracked();

        trackedFilesMap.putAll(addedFilesMap);
        for (String filePath : removedFilePathsSet) {
            trackedFilesMap.remove(filePath);
        }

        for (Map.Entry<String, String> entry : trackedFilesMap.entrySet()) {
            String filePath = entry.getKey();
            String blobId = entry.getValue();

            String currentFileBlobId = currentFilesMap.get(filePath);

            if (currentFileBlobId != null) {
                if (!currentFileBlobId.equals(blobId)) {
                    // 1. Tracked in the current commit, changed in the working directory, but not staged; or
                    // 2. Staged for addition, but with different contents than in the working directory.
                    modifiedNotStageFilePaths.add(filePath);
                }
                currentFilesMap.remove(filePath);
            } else {
                // 3. Staged for addition, but deleted in the working directory; or
                // 4. Not staged for removal, but tracked in the current commit and deleted from the working directory.
                modifiedNotStageFilePaths.add(filePath);
                deletedNotStageFilePaths.add(filePath);
            }
        }

        modifiedNotStageFilePaths.sort(String::compareTo);

        for (String filePath : modifiedNotStageFilePaths) {
            String fileName = Paths.get(filePath).getFileName().toString();
            statusBuilder.append(fileName);
            if (deletedNotStageFilePaths.contains(filePath)) {
                statusBuilder.append(" ").append("(deleted)");
            } else {
                statusBuilder.append(" ").append("(modified)");
            }
            statusBuilder.append("\n");
        }
        statusBuilder.append("\n");
        // end

        // untracked files
        statusBuilder.append("=== Untracked Files ===").append("\n");
        List<String> current = new ArrayList<>(currentFilesMap.keySet());
        appendFileNamesInOrder(statusBuilder, current);
        statusBuilder.append("\n");
        // end

        System.out.print(statusBuilder);
    }

    private static Map<String, String> getCurrentFilesMap() {
        Map<String, String> filemap = new HashMap<>();
        for (File file : currentfiles()) {
            String filePath = file.getPath();
            String blobId = Blob.generateId(file);
            filemap.put(filePath, blobId);
        }
        return filemap;
    }

    public void checkout(String filename) {
        File file = getFile(filename);
        if (!HEADCommit().restoreTracked(file.getPath())) {
            exit("File does not exist in that commit.");
        }
    }

    public void checkout(String commitId, String filename) {
        commitId = getActualCommitId(commitId);
        String filePath = getFile(filename).getPath();
        if (!Commit.fromFile(commitId).restoreTracked(filePath)) {
            exit("File does not exist in that commit.");
        }

    }
    @SuppressWarnings("ConstantConditions")
    private static String getActualCommitId(String commitId) {
        if (commitId.length() < UID_LENGTH) {
            if (commitId.length() < 4) {
                exit("Commit id should contain at least 4 characters.");
            }
            String objectDirName = getObjectDirName(commitId);
            File objectDir = join(OBJECTS_DIR, objectDirName);
            if (!objectDir.exists()) {
                exit("No commit with that id exists.");
            }

            boolean isFound = false;
            String objectFileNamePrefix = getObjectFileName(commitId);

            for (File objectFile : objectDir.listFiles()) {
                String objectFileName = objectFile.getName();
                if (objectFileName.startsWith(objectFileNamePrefix) && isFileInstanceOf(objectFile, Commit.class)) {
                    if (isFound) {
                        exit("More than 1 commit has the same id prefix.");
                    }
                    commitId = objectDirName + objectFileName;
                    isFound = true;
                }
            }
            if (!isFound) {
                exit("No commit with that id exists.");
            }
        } else {
            if (!getObjectFile(commitId).exists()) {
                exit("No commit with that id exists.");
            }
        }
        return commitId;
    }


    public static void checkoutBranch(String branchname) {
        File targetBranchHeadFile = getBranchHeadFile(branchname);
        if (!targetBranchHeadFile.exists()) {
            exit("No such branch exists.");
        }
        if (targetBranchHeadFile.equals(currentBranch())) {
            exit("No need to checkout the current branch.");
        }
        Commit targetBranchHeadCommit = getBranchHeadCommit(targetBranchHeadFile);
        checkUntracked(targetBranchHeadCommit);
        checkoutCommit(targetBranchHeadCommit);
        setcurrentbranch(branchname);
    }

    private static void checkoutCommit(Commit targetCommit) {
        StagingArea stagingAreais = stagingArea();
        // 重装存储
        stagingAreais.clear();
        stagingAreais.save();
        for (File file : currentfiles()) {
            rm(file);
        }
        // 将目标提交的所有跟踪文件恢复到工作目录
        targetCommit.restoreAllTracked();
    }

    private static void checkUntracked(Commit targetCommit) {
        Map<String, String> currentFilesMap = getCurrentFilesMap();
        Map<String, String> trackedFilesMap = HEADCommit().getTracked();
        Map<String, String> addedFilesMap = stagingArea().getAdded();
        Set<String> removedFilePathsSet = stagingArea().getRemoved();

        List<String> untrackedFilePaths = new ArrayList<>();

        for (String filePath : currentFilesMap.keySet()) {
            if (trackedFilesMap.containsKey(filePath)) {
                if (removedFilePathsSet.contains(filePath)) {
                    untrackedFilePaths.add(filePath);
                }
            } else {
                if (!addedFilesMap.containsKey(filePath)) {
                    untrackedFilePaths.add(filePath);
                }
            }
        }

        Map<String, String> targetCommitTrackedFilesMap = targetCommit.getTracked();

        for (String filePath : untrackedFilePaths) {
            String blobId = currentFilesMap.get(filePath);
            String targetBlobId = targetCommitTrackedFilesMap.get(filePath);
            //要被覆盖
            if (!blobId.equals(targetBlobId)) {
                exit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
    }

    public static void branch(String newbranchname) {
        File newbranchfile = getBranchHeadFile(newbranchname);
        if (newbranchfile.exists()) {
            exit("A branch with that name already exists.");
        }
        setBranchHeadCommit(newbranchfile, newbranchname);
    }

    public static void rmBranch(String branchname) {
        File branchfile = getBranchHeadFile(branchname);
        if (!branchfile.exists()) {
            exit("A branch with that name does not exist.");
        }
        if (branchname.equals(currentBranch())) {
            exit("Cannot remove the current branch.");
        }
        rm(branchfile);
        //移除指针，指针就是文件
    }

    public static void reset(String id) {
        String commitid = getActualCommitId(id);
        Commit targetcommit = Commit.fromFile(commitid);
        checkUntracked(targetcommit);
        checkoutCommit(targetcommit);
        setBranchHeadCommit(currentBranch(), commitid);
    }

    public void merge(String targetBranchName) {
        File targetBranchHeadFile = getBranchHeadFile(targetBranchName);
        StagingArea stagingAreais = stagingArea();
        if (!targetBranchHeadFile.exists()) {
            exit("A branch with that name does not exist.");
        }
        if (targetBranchName.equals(currentBranch())) {
            exit("Cannot merge a branch with itself.");
        }
        if (!stagingAreais.isClean()) {
            exit("You have uncommitted changes.");
        }
        Commit targetBranchHeadCommit = getBranchHeadCommit(targetBranchHeadFile);
        checkUntracked(targetBranchHeadCommit);

        Commit lcaCommit = getLastCommonCommit(HEADCommit(), targetBranchHeadCommit);
        String lcaCommitId = lcaCommit.getId();

        if (lcaCommitId.equals(targetBranchHeadCommit.getId())) {
            exit("Given branch is an ancestor of the current branch.");
        }
        if (lcaCommitId.equals(HEADCommit().getId())) {
            checkoutCommit(targetBranchHeadCommit);
            setcurrentbranch(targetBranchName);
            exit("Current branch fast-forwarded.");
        }

        boolean hasConflict = false;

        Map<String, String> HEADCommitTrackedFilesMap = new HashMap<>(HEADCommit().getTracked());
        Map<String, String> targetBranchHeadCommitTrackedFilesMap = targetBranchHeadCommit.getTracked();
        Map<String, String> lcaCommitTrackedFilesMap = lcaCommit.getTracked();

        for (Map.Entry<String, String> entry : lcaCommitTrackedFilesMap.entrySet()) {
            String filePath = entry.getKey();
            File file = new File(filePath);
            String blobId = entry.getValue();

            String targetBranchHeadCommitBlobId = targetBranchHeadCommitTrackedFilesMap.get(filePath);
            String HEADCommitBlobId = HEADCommitTrackedFilesMap.get(filePath);

            if (targetBranchHeadCommitBlobId != null) { // exists in the target branch
                if (!targetBranchHeadCommitBlobId.equals(blobId)) { // modified in the target branch
                    if (HEADCommitBlobId != null) { // exists in the current branch
                        if (HEADCommitBlobId.equals(blobId)) { // not modified in the current branch
                            // case 1
                            Blob.fromFile(targetBranchHeadCommitBlobId).writeContentToSource();
                            stagingAreais.add(file);
                        } else { // modified in the current branch
                            if (!HEADCommitBlobId.equals(targetBranchHeadCommitBlobId)) { // modified in different ways
                                // case 8
                                hasConflict = true;
                                String conflictContent = getConflictContent(HEADCommitBlobId, targetBranchHeadCommitBlobId);
                                writeContents(file, conflictContent);
                                stagingAreais.add(file);
                            } // else modified in the same ways
                            // case 3
                        }
                    } else { // deleted in current branch
                        // case 8
                        hasConflict = true;
                        String conflictContent = getConflictContent(null, targetBranchHeadCommitBlobId);
                        writeContents(file, conflictContent);
                        stagingAreais.add(file);
                    }
                } // else not modified in the target branch
                // case 2, case 7
            } else { // deleted in the target branch
                if (HEADCommitBlobId != null) { // exists in the current branch
                    if (HEADCommitBlobId.equals(blobId)) { // not modified in the current branch
                        // case 6
                        stagingAreais.remove(file);
                    } else { // modified in the current branch
                        // case 8
                        hasConflict = true;
                        String conflictContent = getConflictContent(HEADCommitBlobId, null);
                        writeContents(file, conflictContent);
                        stagingAreais.add(file);
                    }
                } // else deleted in both branches
                // case 3
            }

            HEADCommitTrackedFilesMap.remove(filePath);
            targetBranchHeadCommitTrackedFilesMap.remove(filePath);
            //将拆分点上的全部从里面移除
        }

        for (Map.Entry<String, String> entry : targetBranchHeadCommitTrackedFilesMap.entrySet()) {
            String targetBranchHeadCommitFilePath = entry.getKey();
            File targetBranchHeadCommitFile = new File(targetBranchHeadCommitFilePath);
            String targetBranchHeadCommitBlobId = entry.getValue();

            String HEADCommitBlobId = HEADCommitTrackedFilesMap.get(targetBranchHeadCommitFilePath);

            if (HEADCommitBlobId != null) { // added in both branches
                if (!HEADCommitBlobId.equals(targetBranchHeadCommitBlobId)) { // modified in different ways
                    // case 8
                    hasConflict = true;
                    String conflictContent = getConflictContent(HEADCommitBlobId, targetBranchHeadCommitBlobId);
                    writeContents(targetBranchHeadCommitFile, conflictContent);
                    stagingAreais.add(targetBranchHeadCommitFile);
                } // else modified in the same ways
                // case 3
            } else { // only added in the target branch
                // case 5
                Blob.fromFile(targetBranchHeadCommitBlobId).writeContentToSource();
                stagingAreais.add(targetBranchHeadCommitFile);
            }
        }

        String newCommitMessage = "Merged" + " " + targetBranchName + " " + "into" + " " + currentBranch() + ".";
        commit(newCommitMessage, targetBranchHeadCommit.getId());

        if (hasConflict) {
            message("Encountered a merge conflict.");
        }
    }


    @SuppressWarnings("ConstantConditions")
    private Commit getLastCommonCommit (Commit a, Commit b){
            List<String> ancestorsA = new ArrayList<>();
            List<String> parentsid = new ArrayList<>();
            while (a != null) {
                ancestorsA.add(a.getId());
                String parent = a.getParents().get(0);
                parentsid.add(parent);
                a = Commit.fromFile(parent);
            }

            while (b != null) {
                if (ancestorsA.contains(b.getId())) {
                    return b; // 找到了最近的公共祖先
                }
                String parent = b.getParents().get(0);
                b = Commit.fromFile(parent);
            }

            return null; // 没有找到公共祖先
    }

    private static String getConflictContent (String currentBlobId, String targetBlobId){
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<<<<<<< HEAD").append("\n");
        if (currentBlobId != null) {
            Blob currentblob = Blob.fromFile(currentBlobId);
            contentBuilder.append(currentblob.getContentAsString());
        }
        contentBuilder.append("=======").append("\n");
        if (targetBlobId != null) {
            Blob targetBlob = Blob.fromFile(targetBlobId);
            contentBuilder.append(targetBlob.getContentAsString());
        }
        contentBuilder.append(">>>>>>>");
        return contentBuilder.toString();
    }


}
