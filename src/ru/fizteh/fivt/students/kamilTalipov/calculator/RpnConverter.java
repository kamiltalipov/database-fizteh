package ru.fizteh.fivt.students.kamilTalipov.calculator;

import java.util.LinkedList;

public class RpnConverter {
    public static String convert(String expression) throws ParseException {
        StringBuilder result = new StringBuilder();
        LinkedList<Character> operatorStack = new LinkedList<Character>();
        boolean isCanBeUnaryOperator = true;
        for (int pos = 0; pos < expression.length(); ) {
            if (InputValidator.is19BaseDigit(expression.charAt(pos))) {
                StringBuilder stringBuilder = new StringBuilder();
                while (pos < expression.length()
                        && InputValidator.is19BaseDigit(expression.charAt(pos))) {
                    stringBuilder.append(expression.charAt(pos));
                    ++pos;
                }
                stringBuilder.append(" ");
                result.append(stringBuilder.toString());
                isCanBeUnaryOperator = false;
            } else if (InputValidator.isMathOperator(expression.charAt(pos))) {
                if (!operatorStack.isEmpty()) {
                    if (getPriority(expression.charAt(pos)) <= getPriority(operatorStack.peek())) {
                        result.append(operatorStack.pop());
                        result.append(" ");
                    }
                }
                if (!isCanBeUnaryOperator) {
                    operatorStack.push(expression.charAt(pos));
                } else if (expression.charAt(pos) == '-') {
                    operatorStack.push('#');
                } else if (expression.charAt(pos) != '+') {
                    throw new ParseException("Incorrect expression");
                }
                ++pos;
                isCanBeUnaryOperator = true;
            } else if (InputValidator.isBrackets(expression.charAt(pos))) {
                switch (expression.charAt(pos)) {
                    case '(':
                        operatorStack.push('(');
                        isCanBeUnaryOperator = true;
                        break;

                    case ')':
                        char topOperator = operatorStack.pop();
                        while (topOperator != '(') {
                            result.append(topOperator);
                            result.append(" ");
                            topOperator = operatorStack.pop();
                        }
                        isCanBeUnaryOperator = false;
                        break;

                    default:
                }
                ++pos;
            } else if (Character.isWhitespace(expression.charAt(pos))) {
                ++pos;
            }
        }

        while (!operatorStack.isEmpty()) {
            result.append(operatorStack.pop());
        }

        return result.toString();
    }

    private static int getPriority(char c) {
        switch (c) {
            case '(':
                return 0;
            case ')':
                return 1;
            case '+':
                return 2;
            case '-':
                return 3;
            case '*':
                return 4;
            case '/':
                return 4;
            default:
                return 5;
        }
    }
}
