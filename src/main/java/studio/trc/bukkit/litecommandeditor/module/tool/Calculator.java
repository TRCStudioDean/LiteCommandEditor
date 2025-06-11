package studio.trc.bukkit.litecommandeditor.module.tool;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

public class Calculator
{
    public static void splitOperators(List<String> elements) {
        String[] operators = new String[] {"\\+", "-", "\\*", "/"};
        Arrays.stream(operators).forEach(operator -> {
            List<String> solved = new LinkedList();
            elements.stream().forEach(textParagraphs -> {
                String[] splitedText = textParagraphs.split(operator, -1);
                for (int i = 0;i < splitedText.length;i++) {
                    if (!splitedText[i].isEmpty()) {
                        solved.add(splitedText[i]);
                    }
                    if (i != splitedText.length - 1) {
                        solved.add(operator.replace("\\", ""));
                    }
                }
            });
            elements.clear();
            elements.addAll(solved);
        });
    }
    
    public static double calculate(String expression) {
        List<String> elements = new LinkedList();
        elements.add(expression);
        splitOperators(elements);
        for (int index = 0;index < elements.size();index++) {
            String element = elements.get(index);
            if (element.equals("-") && index != elements.size() - 1 && isNumber(elements.get(index + 1))) {
                elements.set(index, element + elements.get(index + 1));
                elements.remove(index + 1);
            }
        }
        for (int index = 0;index < elements.size();index++) {
            String element = elements.get(index);
            switch (element) {
                case "/": {
                    if (index != 0 && index != elements.size() - 1 && isNumber(elements.get(index - 1)) && isNumber(elements.get(index + 1))) {
                        elements.set(index - 1, String.valueOf(Double.valueOf(elements.get(index - 1)) / Double.valueOf(elements.get(index + 1))));
                        elements.remove(index + 1);
                        elements.remove(index);
                        index--;
                    }
                    break;
                }
                case "*": {
                    if (index != 0 && index != elements.size() - 1 && isNumber(elements.get(index - 1)) && isNumber(elements.get(index + 1))) {
                        elements.set(index - 1, String.valueOf(Double.valueOf(elements.get(index - 1)) * Double.valueOf(elements.get(index + 1))));
                        elements.remove(index + 1);
                        elements.remove(index);
                        index--;
                    }
                    break;
                }
            }
        }
        splitOperators(elements);
        for (int index = 0;index < elements.size();index++) {
            String element = elements.get(index);
            if (index != 0 && isNumber(elements.get(index - 1)) && isNumber(element)) {
                elements.set(index - 1, String.valueOf(Double.valueOf(elements.get(index - 1)) + Double.valueOf(element)));
                elements.remove(index);
                index--;
                continue;
            }
            switch (element) {
                case "+": {
                    if (index != 0 && index != elements.size() - 1 && isNumber(elements.get(index - 1)) && isNumber(elements.get(index + 1))) {
                        elements.set(index - 1, String.valueOf(Double.valueOf(elements.get(index - 1)) + Double.valueOf(elements.get(index + 1))));
                        elements.remove(index + 1);
                        elements.remove(index);
                        index--;
                    } else if (index == 0 && index != elements.size() - 1 && isNumber(elements.get(index + 1))) {
                        elements.set(index, elements.get(index + 1));
                        elements.remove(index + 1);
                    } else if (index != elements.size() - 1 && elements.get(index + 1).equals("-")) {
                        elements.set(index, "-");
                        elements.remove(index + 1);
                        index--;
                    }
                    break;
                }
                case "-": {
                    if (index != 0 && index != elements.size() - 1 && isNumber(elements.get(index - 1)) && isNumber(elements.get(index + 1))) {
                        elements.set(index - 1, String.valueOf(Double.valueOf(elements.get(index - 1)) - Double.valueOf(elements.get(index + 1))));
                        elements.remove(index + 1);
                        elements.remove(index);
                        index--;
                    } else if (index == 0 && index != elements.size() - 1 && isNumber(elements.get(index + 1))) {
                        elements.set(index, "-" + elements.get(index + 1));
                        elements.remove(index + 1);
                    } else if (index != elements.size() - 1 && elements.get(index + 1).equals("-")) {
                        elements.set(index, "+");
                        elements.remove(index + 1);
                        index--;
                    }
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        elements.forEach(builder::append);
        return Double.valueOf(builder.toString());
    }
    
    
    public static double calculateAll(String expression) {
        expression = expression.replace(" ", "");
        ExpressionParagraph ep = getLowestParenthesis(expression);
        while (ep != null) {
            double result = calculate(ep.getText());
            expression = expression.substring(0, ep.getStartsWith() - 1) + result + expression.substring(ep.getEndsWith() + 1, expression.length());
            ep = getLowestParenthesis(expression);
        }
        return calculate(expression);
    }
    
    public static boolean isNumber(String number) {
        try {
            Double.valueOf(number);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    /**
     * Get lowest parenthesis of syntax.
     * @param expression
     * @return 
     */
    public static ExpressionParagraph getLowestParenthesis(String expression) {
        boolean foundStart = false;
        boolean foundEnd = false;
        int startsWith = -1;
        int endsWith = -1;
        int header = 0;
        int footer = 0;
        StringBuilder builder = new StringBuilder();
        String result = null;
        for (int index = 0;index < expression.toCharArray().length;index++) {
            char c = expression.toCharArray()[index];
            if (c == '(') {
                header++;
                if (result == null) {
                    builder = new StringBuilder();
                    foundStart = true;
                    startsWith = index + 1;
                }
                continue;
            }
            if (c == ')') {
                footer++;
                if (foundStart && result == null) {
                    foundEnd = true;
                    result = builder.toString();
                    if (endsWith == -1) {
                        endsWith = index;
                    }
                } else {
                    continue;
                }
            }
            if (foundStart) {
                builder.append(c);
            }
        }
        return header == footer && foundStart && foundEnd ? new ExpressionParagraph(result, startsWith, endsWith) : null;
    }
    
    public static class ExpressionParagraph {
        @Getter
        private final int startsWith;
        @Getter
        private final int endsWith;
        @Getter
        private final String text;

        public ExpressionParagraph(String text, int startsWith, int endsWith) {
            this.text = text;
            this.startsWith = startsWith;
            this.endsWith = endsWith;
        }
    }
}
