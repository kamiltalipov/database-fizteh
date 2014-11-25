package ru.fizteh.fivt.students.kamilTalipov.calculator;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Calculator {
    public static long calculate(String expression) throws ParseException, ArithmeticException {
        expression = RpnConverter.convert(expression);
        LinkedList<Long> numberStack = new LinkedList<Long>();
        for (int pos = 0; pos < expression.length(); ) {
            while (pos < expression.length() && Character.isWhitespace(expression.charAt(pos))) {
                ++pos;
            }
            if (pos == expression.length()) {
                break;
            }

            Lexeme nextLexeme = getNextLexeme(expression, pos);
            try {
                switch (nextLexeme.type) {
                    case NUMBER:
                        numberStack.push(Long.parseLong(nextLexeme.lexeme, BASE));
                        break;

                    case OPERATOR:
                        long num1 = numberStack.pop();
                        if (nextLexeme.lexeme.equals("+")) {
                            long num2 = numberStack.pop();
                            numberStack.push(safeAdd(num2, num1));
                        } else if (nextLexeme.lexeme.equals("-")) {
                            long num2 = numberStack.pop();
                            numberStack.push(safeSubtract(num2, num1));
                        } else if (nextLexeme.lexeme.equals("*")) {
                            long num2 = numberStack.pop();
                            numberStack.push(safeMultiply(num2, num1));
                        } else if (nextLexeme.lexeme.equals("/")) {
                            long num2 = numberStack.pop();
                            numberStack.push(safeDivide(num2, num1));
                        } else if (nextLexeme.lexeme.equals("#")) {
                            numberStack.push(safeNegate(num1));
                        } else {
                            throw new ParseException("Unknown operator");
                        }
                        break;

                    default:
                        throw new ParseException("Unknown lexeme");
                }
            } catch (NoSuchElementException e) {
                throw new ParseException("Incorrect expression");
            }
            pos += nextLexeme.lexeme.length();
        }

        if (numberStack.size() != 1) {
            throw new ParseException("Incorrect expression");
        }
        return numberStack.peek();
    }

    private static long safeAdd(long num1, long num2) throws ArithmeticException {
        if ((num2 > 0 && num1 > Long.MAX_VALUE - num2)
                || (num2 <= 0 && num1 < Long.MIN_VALUE - num2)) {
            throw new ArithmeticException("Calculation overflow");
        }

        return num1 + num2;
    }

    private static long safeSubtract(long num1, long num2) throws ArithmeticException {
        if ((num2 > 0 && num1 < Long.MIN_VALUE + num2)
                || (num2 <= 0 && num1 > Long.MAX_VALUE + num2)) {
            throw new ArithmeticException("Calculation overflow");
        }

        return num1 - num2;
    }

    private static long safeMultiply(long num1, long num2) throws ArithmeticException {
        if (((num2 > 0) && (num1 > Long.MAX_VALUE / num2 || num1 < Long.MIN_VALUE / num2))
                || ((num2 < -1) && (num1 > Long.MIN_VALUE / num2 || num1 < Long.MAX_VALUE / num2))
                || (num2 == -1 && num1 == Long.MIN_VALUE)) {
            throw new ArithmeticException("Calculation overflow");
        }

        return num1 * num2;
    }

    private static long safeDivide(long num1, long num2) throws ArithmeticException {
        if (num2 == 0) {
            throw new ArithmeticException("Division by zero");
        }
        if (num1 == Long.MIN_VALUE && num2 == -1) {
            throw new ArithmeticException("Calculation overflow");
        }

        return num1 / num2;
    }

    private static long safeNegate(long num) throws ArithmeticException {
        if (num == Long.MIN_VALUE) {
            throw new ArithmeticException("Calculation overflow");
        }

        return -num;
    }

    private static Lexeme getNextLexeme(String expression, int pos) throws ParseException {
        Lexeme nextLexeme = new Lexeme(LexemeType.OPERATOR, "");

        if (InputValidator.isMathOperator(expression.charAt(pos))
                || expression.charAt(pos) == '#') {
            nextLexeme.type = LexemeType.OPERATOR;
            nextLexeme.lexeme = expression.substring(pos, pos + 1);
        } else if (InputValidator.is19BaseDigit(expression.charAt(pos))) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = pos; i < expression.length() && InputValidator.is19BaseDigit(expression.charAt(i)); ++i) {
                stringBuilder.append(expression.charAt(i));
            }

            nextLexeme.type = LexemeType.NUMBER;
            nextLexeme.lexeme = stringBuilder.toString();
        }

        return nextLexeme;
    }

    private enum LexemeType {
        NUMBER,
        OPERATOR
    }

    public static final int BASE = 19;

    private static class Lexeme {
        public String lexeme;
        public LexemeType type;

        public Lexeme(LexemeType type, String lexeme) {
            this.type = type;
            this.lexeme = lexeme;
        }

        public String toString() {
            return lexeme + " " + type.toString();
        }
    }
}
