package gitlet;


import static gitlet.MyUtils.exit;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if(args.length == 0){
            exit("Please enter a command.");
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init" -> {
                validateNumArgs(args, 1);
                Repository.init();
            }
            case "add" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                //为了调用 add 方法，需要首先创建一个 Repository 对象实例，
                // 然后通过该对象实例来调用 add 方法。
                // 使用 new Repository() 创建一个 Repository 对象实例，然后调用 add 方法，。
                new Repository().add(fileName);
            }
            case "commit" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 2);
                String message = args[1];
                if (message.length() == 0) {
                    exit("Please enter a commit message.");
                }
                new Repository().commit(message);
            }
            case "rm" -> {
                Repository.checkWorkDir();
                validateNumArgs(args,2);
                String filename = args[1];
                new Repository().remove(filename);
            }
            case "log" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 1);
                new Repository().log();
            }
            case "global-log" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 1);
                new Repository().globalLog();
            }
            case "find" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 2);
                String message = args[1];
                if (message.length() == 0) {
                    exit("Found no commit with that message.");
                }
                Repository.find(message);
            }
            case "status" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 1);
                new Repository().status();
            }
            case "checkout" -> {
                Repository.checkWorkDir();
                Repository repository = new Repository();
                switch (args.length) {
                    case 3 -> {
                        if (!args[1].equals("--")) {
                            exit("Incorrect operands.");
                        }
                        String fileName = args[2];
                        repository.checkout(fileName);
                    }
                    case 4 -> {
                        if (!args[2].equals("--")) {
                            exit("Incorrect operands.");
                        }
                        String commitId = args[1];
                        String fileName = args[3];
                        repository.checkout(commitId, fileName);
                    }
                    case 2 -> {
                        String branch = args[1];
                        repository.checkoutBranch(branch);
                    }
                }
            }
            case "branch" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 2);
                String branchName = args[1];
                new Repository().branch(branchName);
            }
            case "rm-branch" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 2);
                String branchName = args[1];
                new Repository().rmBranch(branchName);
            }
            case "reset" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 2);
                String commitId = args[1];
                new Repository().reset(commitId);
            }
            case "merge" -> {
                Repository.checkWorkDir();
                validateNumArgs(args, 2);
                String branchName = args[1];
                new Repository().merge(branchName);
            }
            default -> exit("No command with that name exists.");
        }
    }

    public static void  validateNumArgs(String[] args, int n){
        if(args.length != n){
            exit("Incorrect operands.");
        }
    }
}
