package ru.fizteh.fivt.students.kamilTalipov.calculator;


public class InputValidator {
    public static boolean isCorrectInput(String[] input) {
        for (String currentPart : input) {
            if (!isOnlyMathSymbols(currentPart)) {
                return false;
            }
        }
        return true;
    }

    public static boolean is19BaseDigit(char c) {
        return Character.isDigit(c)
                || (c >= 'A' && c <= 'I')
                || (c >= 'a' && c <= 'i');
    }

    public static boolean isMathOperator(char c) {
        return c == '+' || c == '*' || c == '-' || c == '/';
    }

    public static boolean isBrackets(char c) {
        return c == '(' || c == ')';
    }

    private static boolean isOnlyMathSymbols(String string) {
        for (int pos = 0; pos < string.length(); ++pos) {
            if (!is19BaseDigit(string.charAt(pos))
                    && !isMathOperator(string.charAt(pos))
                    && !isBrackets(string.charAt(pos))
                    && !Character.isWhitespace(string.charAt(pos))) {
                return false;
            }
        }

        return true;
    }
}
