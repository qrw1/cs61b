package gitlet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static gitlet.MyUtils.exit;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                new Repository().add(fileName);
                break;
            case "commit":
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String message = args[1];
                if (message.length() == 0) {
                    exit("Please enter a commit message.");
                }
                new Repository().commit(message);
                break;
            case "rm":
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String filename = args[1];
                new Repository().remove(filename);
                break;
            case "log":
                Repository.checkWorkingDir();
                new Repository().log();
                break;
            case "global-log":
                Repository.checkWorkingDir();
                new Repository().globalLog();
                break;
            case "find":
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String commitmes = args[1];
                new Repository().find(commitmes);
                break;
            case "status":
                Repository.checkWorkingDir();
                new Repository().status();
                break;
        }
    }

    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            exit("Incorrect operands.");
        }
    }
}
