package ru.fizteh.fivt.students.kamilTalipov.calculator;

public class CalculatorRunner {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("You should enter expression to calculate");
            System.exit(1);
        }
        if (!InputValidator.isCorrectInput(args)) {
            System.err.println("Incorrect expression");
            System.exit(1);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String inputPart : args) {
            stringBuilder.append(inputPart);
            stringBuilder.append(" ");
        }
        String expression = stringBuilder.toString();

        try {
            System.out.println(Long.toString(Calculator.calculate(expression), Calculator.BASE));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
