package gitlet;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *
 * @author Exuanbo
 */
public class Repository {

    /**
     * Default branch name.
     */
    private static final String DEFAULT_BRANCH_NAME = "master";

    /**
     * HEAD ref prefix.
     */
    private static final String HEAD_BRANCH_REF_PREFIX = "ref: refs/heads/";

    /**
     * The current working directory.
     */
    private static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    private static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * The index file.
     */
    public static final File INDEX = join(GITLET_DIR, "index");

    /**
     * The objects directory.
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    /**
     * The HEAD file.
     */
    private static final File HEAD = join(GITLET_DIR, "HEAD");

    /**
     * The refs directory.
     */
    private static final File REFS_DIR = join(GITLET_DIR, "refs");

    /**
     * The heads directory.
     */
    private static final File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");

    /**
     * Files in the current working directory.
     */
    private static final File[] currentFiles = CWD.listFiles(File::isFile);

    /**
     * The current branch name.
     */
    public final String currentBranch(){
        String headstring = readContentsAsString(HEAD);
        return headstring.replace(HEAD_BRANCH_REF_PREFIX, "");
    }
    /**
     * The commit that HEAD points to.
     */


    public final Commit HEADCommit = getBranchHeadCommit(currentBranch());
    /**
     * The staging area instance. Initialized in the constructor.
     */

    private final StagingArea stagingArea() {
        if (INDEX.exists()) {
            return StagingArea.fromFile();
        } else {
            return new StagingArea();
        }
    }

    /**
     * Exit if the repository at the current working directory is not initialized.
     */
    public static void checkWorkingDir() {
        if (!(GITLET_DIR.exists() && GITLET_DIR.isDirectory())) {
            exit("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * Initialize a repository at the current working directory.
     *
     * <pre>
     * .gitlet
     * ├── HEAD
     * ├── objects
     * └── refs
     *     └── heads
     * </pre>
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        mkdir(GITLET_DIR);
        mkdir(REFS_DIR);
        mkdir(BRANCH_HEADS_DIR);
        mkdir(OBJECTS_DIR);
        setCurrentBranch(DEFAULT_BRANCH_NAME);
        createInitialCommit();
    }

    /**
     * Print all commit logs ever made.
     */
    public static void globalLog() {
        StringBuilder logBuilder = new StringBuilder();
        // As the project spec goes, the runtime should be O(N) where N is the number of commits ever made.
        // But here I choose to log the commits in the order of created date, which has a runtime of O(NlogN).
        forEachCommitInOrder(commit -> logBuilder.append(commit.getLog()).append("\n"));
        System.out.print(logBuilder);
    }

    /**
     * Print all commits that have the exact message.
     *
     * @param msg Content of the message
     */
    public static void find(String msg) {
        StringBuilder resultBuilder = new StringBuilder();
        forEachCommit(commit -> {
            if (commit.getMessage().equals(msg)) {
                resultBuilder.append(commit.getId()).append("\n");
            }
        });
        if (resultBuilder.length() == 0) {
            exit("Found no commit with that message.");
        }
        System.out.print(resultBuilder);
    }

    /**
     * Set current branch.
     *
     * @param branchName Name of the branch
     */
    private static void setCurrentBranch(String branchName) {
        writeContents(HEAD, HEAD_BRANCH_REF_PREFIX + branchName);
    }

    /**
     * Get head commit of the branch.
     *
     * @param branchName Name of the branch
     * @return Commit instance
     */
    private static Commit getBranchHeadCommit(String branchName) {
        File branchHeadFile = getBranchHeadFile(branchName);
        return getBranchHeadCommit(branchHeadFile);
    }

    /**
     * Get head commit of the branch.
     *
     * @param branchHeadFile File instance
     * @return Commit instance
     */
    private static Commit getBranchHeadCommit(File branchHeadFile) {
        String HEADCommitId = readContentsAsString(branchHeadFile);
        return Commit.fromFile(HEADCommitId);
    }

    /**
     * Set branch head.
     *
     * @param branchName Name of the branch
     * @param commitId   Commit SHA1 id
     */
    private static void setBranchHeadCommit(String branchName, String commitId) {
        File branchHeadFile = getBranchHeadFile(branchName);
        setBranchHeadCommit(branchHeadFile, commitId);
    }

    /**
     * Set branch head.
     *
     * @param branchHeadFile File instance
     * @param commitId       Commit SHA1 id
     */
    private static void setBranchHeadCommit(File branchHeadFile, String commitId) {
        writeContents(branchHeadFile, commitId);
    }

    /**
     * Get branch head ref file in refs/heads folder.
     *
     * @param branchName Name of the branch
     * @return File instance
     */
    private static File getBranchHeadFile(String branchName) {
        return join(BRANCH_HEADS_DIR, branchName);
    }

    /**
     * Create an initial commit.
     */
    private static void createInitialCommit() {
        Commit initialCommit = new Commit();
        initialCommit.save();
        setBranchHeadCommit(DEFAULT_BRANCH_NAME, initialCommit.getId());
    }

    /**
     * Iterate all commits in the order of created date
     * and execute callback function on each of them.
     *
     * @param cb Function that accepts Commit as a single argument
     */
    private static void forEachCommitInOrder(Consumer<Commit> cb) {
        Comparator<Commit> commitComparator = Comparator.comparing(Commit::getDate).reversed();
        Queue<Commit> commitsPriorityQueue = new PriorityQueue<>(commitComparator);
        forEachCommit(cb, commitsPriorityQueue);
    }

    /**
     * Iterate all commits and execute callback function on each of them.
     *
     * @param cb Function that accepts Commit as a single argument
     */
    private static void forEachCommit(Consumer<Commit> cb) {
        Queue<Commit> commitsQueue = new ArrayDeque<>();
        forEachCommit(cb, commitsQueue);
    }

    /**
     * Helper method to iterate all commits.
     *
     * @param cb                 Callback function executed on the current commit
     * @param queueToHoldCommits New Queue instance to hold the commits while iterating
     */
    @SuppressWarnings("ConstantConditions")
    private static void forEachCommit(Consumer<Commit> cb, Queue<Commit> queueToHoldCommits) {
        Set<String> checkedCommitIds = new HashSet<>();

        File[] branchHeadFiles = BRANCH_HEADS_DIR.listFiles();
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

        while (true) {
            Commit nextCommit = queueToHoldCommits.poll();
            cb.accept(nextCommit);
            List<String> parentCommitIds = nextCommit.getParents();
            if (parentCommitIds.size() == 0) {
                break;
            }
            for (String parentCommitId : parentCommitIds) {
                if (checkedCommitIds.contains(parentCommitId)) {
                    continue;
                }
                checkedCommitIds.add(parentCommitId);
                Commit parentCommit = Commit.fromFile(parentCommitId);
                queueToHoldCommits.add(parentCommit);
            }
        }
    }

    /**
     * Get a File instance from CWD by the name.
     *
     * @param fileName Name of the file
     * @return File instance
     */
    private static File getFileFromCWD(String fileName) {
        return Paths.get(fileName).isAbsolute()
                ? new File(fileName)
                : join(CWD, fileName);
    }



    /**
     * Add file to the staging area.
     *
     * @param fileName Name of the file
     */
    public void add(String fileName) {
        File file = getFileFromCWD(fileName);
        if (!file.exists()) {
            exit("File does not exist.");
        }
        if (stagingArea().add(file)) {
            stagingArea().save();
        }
    }

    /**
     * Perform a commit with message.
     *
     * @param msg Commit message
     */
    public void commit(String msg) {
        commit(msg, null);
    }

    /**
     * Perform a commit with message and two parents.
     *
     * @param msg          Commit message
     * @param secondParent Second parent Commit SHA1 id
     */
    private void commit(String msg, String secondParent) {
        if (stagingArea().isClean()) {
            exit("No changes added to the commit.");
        }
        Map<String, String> newTrackedFilesMap = stagingArea().commit();
        stagingArea().save();
        List<String> parents = new ArrayList<>();
        parents.add(HEADCommit.getId());
        if (secondParent != null) {
            parents.add(secondParent);
        }
        Commit newCommit = new Commit(msg, parents, newTrackedFilesMap);
        newCommit.save();
        setBranchHeadCommit(currentBranch(), newCommit.getId());
    }

    /**
     * Remove file.
     *
     * @param fileName Name of the file
     */
    public void remove(String fileName) {
        File file = getFileFromCWD(fileName);
        if (stagingArea().remove(file)) {
            stagingArea().save();
        } else {
            exit("No reason to remove the file.");
        }
    }

    /**
     * Print log of the current branch.
     */
    public void log() {
        StringBuilder logBuilder = new StringBuilder();
        Commit currentCommit = HEADCommit;
        while (true) {
            logBuilder.append(currentCommit.getLog()).append("\n");
            List<String> parentCommitIds = currentCommit.getParents();
            if (parentCommitIds.size() == 0) {
                break;
            }
            String firstParentCommitId = parentCommitIds.get(0);
            currentCommit = Commit.fromFile(firstParentCommitId);
        }
        System.out.print(logBuilder);
    }

    /**
     * Print the status.
     */
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


        // staged files
        statusBuilder.append("=== Staged Files ===").append("\n");
        Map<String,String> addstage = stagingArea().getAdded();
        Set<String> filenames = addstage.keySet();
        for(String filename : filenames){
            statusBuilder.append(filename).append("\n");
        }
        statusBuilder.append("\n");

        // end

        // removed files
        statusBuilder.append("=== Removed Files ===").append("\n");
        Set<String> removestage = stagingArea().getRemoved();
        for(String fileName : removestage){
            statusBuilder.append(fileName).append("\n");
        }
        statusBuilder.append("\n");
        // end

        // modifications not staged for commit
        statusBuilder.append("=== Modifications Not Staged For Commit ===").append("\n");


        // end

        // untracked files
        statusBuilder.append("=== Untracked Files ===").append("\n");


        //end

        System.out.print(statusBuilder);

    }

    public void checkout(String filename){
        File file = getFileFromCWD(filename);
        String commitid = readContentsAsString(file);
        checkout(commitid,filename);
    }
    public void checkout(String commitid, String filename){
        File file = getFileFromCWD(filename);
    }

    public void checkoutBranch(String filename){

    }

}
