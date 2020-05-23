import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class creates a shell using java and creates several built in commands and provides support for using external
 * commands. The internal commands include ptime, ^ <number>, history, list, |, cd, and exit. The external capabilities
 * allow the user to send give an external command and it will verify and run the external command.
 */
public class Assign3 {
    // Commands are the history and childTime is the cumulative time spent in child processes
    private static ArrayList<String> commands = new ArrayList<>();
    private static long childTime = 0;

    /**
     * The main method continually prints the working directory and then waits for input from the user. Once input is
     * received it calls a method to verify and run the command.
     * @param args
     */
    public static void main(String[] args) {
        while(true) {
            System.out.printf("[%s]:", System.getProperty("user.dir"));
            String command;
            try {
                Scanner input = new Scanner(System.in);
                command = input.nextLine();
            }
            catch (Exception ex) {
                System.out.println("Something bad happened before anything began!");
                return;
            }
            // if the command is "exit" it will return out of the shell program
            if (!verifyCommand(command)) {
                return;
            }
        }
    }

    /**
     * This adds the command to the history, calls the split to separate the command into an array, checks if the initial
     * command is exit, if not try the built in commands, and if not those attempt to do an external command.
     * @param command The line of command from the user
     * @return
     */
    private static boolean verifyCommand(String command) {
        //add to history
        commands.add(command);
        // split into an array of strings
        String[] commandLine = splitCommand(command);
        //end the program if exit
        if (commandLine[0].equals("exit")) {
            return false;
        }
        // check if it's a built in command
        else if (isBuiltInCommand(commandLine)) {
            return true;
        }
        // if neither of the before try it as an external command
        else {
            runExternalCommand(commandLine);
            return true;
        }
    }

    /**
     * Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from: https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
     */
    public static String[] splitCommand(String command) {
        java.util.List<String> matchList = new java.util.ArrayList<>();

        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }

        return matchList.toArray(new String[matchList.size()]);
    }

    /**
     * Checks if it is any of the built in commands, calls their function if so, if not return false.
     * @param commandLine commands from the user
     * @return true if it's a built in command and false if not
     */
    private static boolean isBuiltInCommand(String[] commandLine) {
        if (commandLine[0].equals("ptime")) {
            System.out.printf("Total time in child processes: %.4f seconds\n", (double)childTime / 1000);
            return true;
        }
        else if (commandLine[0].equals("history")) {
            System.out.println("-- Command History --");
            for (int i = 1; i <= commands.size(); i++) {
                System.out.printf("%d : %s\n", i, commands.get(i - 1));
            }
            return true;
        }
        else if (commandLine[0].equals("^")) {
            return wedgeCommand(commandLine);
        }
        else if (commandLine[0].equals("list")) {
            return listCommand();
        }
        else if (commandLine[0].equals("cd")) {
            return cdCommand(commandLine);
        }
        else {
            return false;
        }
    }

    /**
     * This looks at the current directory, finds all the sub directories, prints information to the user about the
     * directory, whether the user can read, write, or execute, the size in bytes of the file, last date modified
     * and the name of the file for each sub file or directory
     * @return true because it was already verified in the previously called method that it was a built in command
     */
    private static boolean listCommand() {
        File currentDirectory = new File(System.getProperty("user.dir"));
        //get all sub directories or lists and print each one to the console
        String[] subFiles = currentDirectory.list();
        for (int i = 0; i < subFiles.length; i++) {
            // Create a file for each sub directory/file, show if it's a directory, if the user can read, write, or execute
            File subFile = new File(System.getProperty("user.dir") + "/" + subFiles[i]);
            System.out.print(subFile.isDirectory() ? "d" : "-");
            System.out.print(subFile.canRead() ? "r" : "-");
            System.out.print(subFile.canWrite() ? "w" : "-");
            System.out.print(subFile.canExecute() ? "e" : "-");
            // size of the file in bytes
            System.out.printf("%10d ", subFile.length());
            // last modified date
            java.text.SimpleDateFormat fileDate = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm");
            String formattedDate = fileDate.format(subFile.lastModified());
            System.out.print(formattedDate);
            // name of file or directory
            System.out.print(" " + subFiles[i] + "\n");
        }
        return true;
    }

    /**
     * Checks if the number is within range and if it isn't a number the catch statement will print illegal command.
     * It runs teh number command given that is within the command history
     *
     * @param commandLine The command given by the user split on spaces
     * @return true because the prvious method already verified it as an internal command
     */
    private static boolean wedgeCommand(String[] commandLine) {
        // in case the number isn't an int or is a char or string
        try {
            int numberCommand = Integer.parseInt(commandLine[1]);
            if (numberCommand > commands.size() || numberCommand <= 0 || commandLine.length > 2) {
                System.out.println("Illegal command");
            }
            else {
                verifyCommand(commands.get(numberCommand - 1));
            }
        }
        // if not a correct int given then tell user
        catch (Exception e) {
            System.out.println("Illegal command");
        }
        return true;
    }

    /**
     * If the user only sent "cd" go to the user's home directory. If they did cd .. go up to the parent directory.
     * If they specified "cd <file name>" check if it is a proper directory and if so go into it
     *
     * @param commandLine user given commands
     * @return true because the previous method already verified it's an internal command
     */
    private static boolean cdCommand(String[] commandLine) {
        // if just cd then go to user home directory
        if (commandLine.length == 1) {
            java.nio.file.Path proposed = java.nio.file.Paths.get(System.getProperty("user.home"));
            System.setProperty("user.dir", proposed.toString());
            return true;
        }
        // Check between cd .. and cd <file/directory>
        else {
            try {
                String currentDirectory = System.getProperty("user.dir");
//                System.out.printf("Current Directory: %s\n", currentDirectory); personal help line
                java.nio.file.Path proposed;
                // if cd .. go up to the parent
                if (commandLine[1].equals("..")) {
                    File current = new File(currentDirectory);
                    proposed = java.nio.file.Paths.get(current.getParent());
                }
                // otherwise create teh proposed path
                else {
                    proposed = java.nio.file.Paths.get(currentDirectory, commandLine[1]);
                }
                //Check if the proposed path is a directory and go to it if so
                File newFilePath = new File(proposed.toString());
                if (newFilePath.isDirectory()) {
                    System.setProperty("user.dir", proposed.toString());
//                    System.out.printf("Updated Directory: %s\n", System.getProperty("user.dir")); personal help
                }
                else {
                    System.out.println("The directory does not exist");
                }
            } catch (Exception ex) {
                System.out.println("Can not go into such a directory");
            }
        }
        return true;
    }

    /**
     * Runs an external command, but calls a separate function to call if given a | as part of the commands
     *
     * @param commandLine user given commands
     */
    private static void runExternalCommand(String[] commandLine) {
        boolean shouldPipe = false;
        int pipeIndex = -1;
        // Check if there is a pipe among the user given commands
        for (int i = 0; i < commandLine.length; i++) {
            if (commandLine[i].equals("|")) {
                pipeIndex = i;
                shouldPipe = true;
                break;
            }
        }
        // If there is a pipe then split the commands up into separate arrays and call that method instead
        if (shouldPipe) {
            String[] pb1Commands = new String[pipeIndex];
            for (int i = 0; i < pipeIndex; i++) {
                pb1Commands[i] = commandLine[i];
            }
            String[] pb2Commands = new String[commandLine.length - pipeIndex - 1];
            for (int i = (pipeIndex + 1), j = 0; i < commandLine.length; i++, j++) {
                pb2Commands[j] = commandLine[i];
            }
            runPipeExternalCommand(pb1Commands, pb2Commands);
            return;
        }

        // Check if there is an & at the end of the user commands to know if we should wait, if so create a new array without the &
        ProcessBuilder pb;
        boolean shouldWaitFor = true;
        if (commandLine[commandLine.length - 1].equals("&")) {
            String[] commandLineWithoutAnd = Arrays.copyOf(commandLine, commandLine.length - 1);
            pb = new ProcessBuilder(commandLineWithoutAnd);
            shouldWaitFor = false;
        }
        else {
            pb = new ProcessBuilder(commandLine);
        }
        //establish the directory for the process to run from
        pb.directory(new File(System.getProperty("user.dir")));
        //redirect the input and output
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        try {
            // Start the process and track how much time it takes
            long start = System.currentTimeMillis();
            Process p = pb.start();
            // if the process should not be waited on because of &
            long end = System.currentTimeMillis();
            if (shouldWaitFor) {
//                System.out.println("Starting to wait"); personal help
                p.waitFor();//this happens when notepad is closed
                end = System.currentTimeMillis();
            }
            System.out.printf("Waited for %.4f seconds\n", (double)(end - start) / 1000);
            // update the cumulative time for child processes
            childTime = childTime + (end - start);
        }
        catch (IOException ex) {
            System.out.println("Illegal command");
        }
        catch (Exception ex) {
            System.out.println("Something else bad happened");
        }
    }

    /**
     * Accepts 2 arrays of commands to pipe between as external commands
     *
     * @param pb1Commands the first process who's output will be piped to the second
     * @param pb2Commands the second process to run who will accept input from the first's output
     */
    private static void runPipeExternalCommand(String[] pb1Commands, String[] pb2Commands) {
        ProcessBuilder pb1 = new ProcessBuilder(pb1Commands);
        ProcessBuilder pb2 = new ProcessBuilder(pb2Commands);
        // change the processes to the current java directories
        pb1.directory(new File(System.getProperty("user.dir")));
        pb2.directory(new File(System.getProperty("user.dir")));

        pb1.redirectInput(ProcessBuilder.Redirect.INHERIT);
        pb2.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        try {
            // start the processes and do the piping, code is compliment Dr. Mathias
            long start = System.currentTimeMillis();
            Process p1 = pb1.start();
            Process p2 = pb2.start();

            java.io.InputStream in = p1.getInputStream();
            java.io.OutputStream out = p2.getOutputStream();

            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }

            out.flush();
            out.close();

            p1.waitFor();
            p2.waitFor();

            long end = System.currentTimeMillis();
            //update cumulative child processses time
            childTime = childTime + (end - start);
        }
        catch (Exception ex) {
            System.out.println("Something went wrong with the piping");
        }
    }
}
