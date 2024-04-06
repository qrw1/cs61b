package gitlet;


import static gitlet.MyUtils.exit;



public class Main {

    public static void main(String[] args){
        if (args.length == 0) {
            exit("Please enter a command.");
        }

        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> {
                validateNumArgs(args, 1);
                Repository.init();
            }
            case "add" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                new Repository().add(fileName);
            }
            case "commit" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String message = args[1];
                if (message.length() == 0) {
                    exit("Please enter a commit message.");
                }
                new Repository().commit(message);
            }
            case "rm" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                new Repository().remove(fileName);
            }
            case "log" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                new Repository().log();
            }
            case "global-log" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                Repository.globalLog();
            }
            case "find" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String message = args[1];
                if (message.length() == 0) {
                    exit("Found no commit with that message.");
                }
                Repository.find(message);
            }
            case "status" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                new Repository().status();
            }
        }
    }

    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            exit("Incorrect operands.");
        }
    }
}
