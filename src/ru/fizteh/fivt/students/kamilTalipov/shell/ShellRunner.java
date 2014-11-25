package ru.fizteh.fivt.students.kamilTalipov.shell;

public class ShellRunner {
    public static void main(String[] args) {
        Command[] commands = {new ChangeDir(),
                            new MakeDir(),
                            new PrintWorkingDir(),
                            new Remove(),
                            new Copy(),
                            new Move(),
                            new PrintDirContain(),
                            new Exit()};
        try {
            Shell.run(commands, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
