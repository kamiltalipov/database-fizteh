package ru.fizteh.fivt.students.kamilTalipov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;

public class Shell {
    public static final String GREETING = "$ ";

    public Shell(Command[] commands) {
        this.currentPath = PathUtils.normalizePath(new File(".").getAbsolutePath());
        this.commands = commands;
    }

    public static void run(Command[] commands, String[] args) throws IOException, IllegalArgumentException {
        Shell shell = new Shell(commands);

        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            shell.packageMode(args);
        }
    }

    public void interactiveMode() throws IOException, IllegalArgumentException {
        wasExit = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (!wasExit) {
            System.out.print(GREETING);
            String cmd = input.readLine();
            if (cmd == null) {
                wasExit = true;
                break;
            }
            String[] cmds = cmd.trim().split(";");
            for (String currentCommand : cmds) {
                try {
                    processCommand(currentCommand);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    System.err.flush();
                }
            }
        }
    }

    public void packageMode(String[] args) throws IllegalArgumentException {
        wasExit = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : args) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }

        String[] inputCommands = stringBuilder.toString().trim().split(";");
        for (String command : inputCommands) {
            processCommand(command);
            if (wasExit) {
                break;
            }
        }
    }

    private void processCommand(String cmd) throws IllegalArgumentException {
        if (cmd == null) {
            wasExit = true;
            return;
        }
        String[] parsedCommand = cmd.trim().split("\\s+");
        if (parsedCommand == null) {
            return;
        }
        if (parsedCommand.length == 0) {
            return;
        }

        String commandName = null;
        ArrayList<String> arguments = new ArrayList<String>();
        for (String string : parsedCommand) {
            if (commandName == null) {
                commandName = string;
            } else {
                arguments.add(string);
            }
        }
        if (commandName.equals("")) {
            return;
        }
        for (Command command : commands) {
            if (command.equalName(commandName)) {
                command.run(this, arguments.toArray(new String[arguments.size()]));
                return;
            }
        }

        throw new IllegalArgumentException(commandName + ": command not found");
    }

    String getCurrentPath() {
        return currentPath;
    }

    void setCurrentPath(String path) {
        currentPath = path;
    }

    void exit() {
        wasExit = true;
    }

    private String currentPath;
    private Command[] commands;
    private boolean wasExit = false;
}
