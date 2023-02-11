package ru.swetophor.mainframe;

import java.util.Arrays;

public class Decorator {
    public static final char[] ASTERISK_FRAME = {'*'};
    public static final char[] SINGULAR_FRAME = {'┌', '─', '┐', '│', '└', '┘'};
    public static final char[] DOUBLE_FRAME = {'╔', '═', '╗', '║', '╚', '╝'};
    public static final char[] HALF_DOUBLE_FRAME = {'┌', '─', '╖', '│', '║', '╘', '═', '╝'};


    public static String frameText(String text,
                                   int minWidth,
                                   int maxWidth,
                            char leftTop,
                            char horizontal,
                            char rightTop,
                            char vertical,
                            char leftBottom,
                            char rightBottom) {
        String[] lines = text.lines().toArray(String[]::new);
        StringBuilder output = new StringBuilder();

        int width = Math.min(maxWidth,
                Math.max(minWidth,
                        Arrays.stream(lines)
                                .mapToInt(String::length)
                                .max()
                                .orElse(0)));

        output.append(buildBorderString(leftTop, horizontal, rightTop, width + 4));

        for (String line : lines) {
            while (line.length() > width) {
                output.append(buildMidleString(vertical, line.substring(0, width), width + 4));
                line = line.substring(width);
            }
            output.append(buildMidleString(vertical, line, width + 4));
        }

        output.append(buildBorderString(leftBottom, horizontal, rightBottom, width + 4));

        return output.toString();
    }

    public static String frameText(String text, int minWidth, char symbol) {
        return frameText(text, minWidth, 80, symbol, symbol, symbol, symbol, symbol, symbol);
    }

    public static String frameText(String text, int minWidth, int maxWidth, char... pattern) {
        return switch (pattern.length) {
            case 1 -> frameText(text, minWidth, pattern[0]);
            case 6 -> frameText(text, minWidth, maxWidth,
                    pattern[0], pattern[1], pattern[2],
                    pattern[3], pattern[4], pattern[5]);
            case 8 -> frameText(text, minWidth, maxWidth,
                    pattern[0], pattern[1], pattern[2],
                    pattern[3], pattern[4], pattern[5],
                    pattern[6], pattern[7]);
            default -> throw new IllegalArgumentException("Некорректная длинна паттерна рамки.");
        };
    }

    private static String complementString(String string, int length) {
        int lack = length - string.length();
        return lack <= 0 ?
                string :
                string + " ".repeat(lack);
    }

    /**
     * Выдаёт строку указанной длины, составленную как верхняя или нижняя
     * граница обрамлённого текста. Особо указываются первый и последний
     * символы и символы заполнения.
     *
     * @param beginning первый символ строки.
     * @param body      символ, из повторов которого составлена основная часть строки.
     * @param ending    заключительный символ строки.
     * @param length    желаемая полная длина строки.
     * @return сформированную оговорённым способом строку.
     */
    private static String buildBorderString(char beginning,
                                            char body,
                                            char ending,
                                            int length) {
        return "%s%s%s%n"
                .formatted(beginning,
                        String.valueOf(body).repeat(length - 2),
                        ending);
    }

    private static String frameText(String text, int minWidth, int maxWidth,
                                    char leftTop, char upperHorizontal, char rightTop,
                                    char leftVertical, char rightVertical,
                                    char leftBottom, char lowerHorizontal, char rightBottom) {
        String[] lines = text.lines().toArray(String[]::new);
        StringBuilder output = new StringBuilder();

        int width = Math.min(maxWidth,
                Math.max(minWidth,
                        Arrays.stream(lines)
                                .mapToInt(String::length)
                                .max()
                                .orElse(0)));

        output.append(buildBorderString(leftTop, upperHorizontal, rightTop, width + 4));

        for (String line : lines) {
            while (line.length() > width) {
                output.append(buildMidleString(leftVertical, rightVertical, line.substring(0, width), width + 4));
                line = line.substring(width);
            }
            output.append(buildMidleString(leftVertical, rightVertical, line, width + 4));
        }

        output.append(buildBorderString(leftBottom, lowerHorizontal, rightBottom, width + 4));

        return output.toString();
    }

    /**
     * Выдаёт строку указанной длины, составленную как строка обрамлённого текста
     * с одинаковыми символами для левой и правой декоративной границы.
     *
     * @param border рамочный символ, помещаемый в начало и конец строки.
     * @param text   текст обрамляемой строки.
     * @param length желаемая полная длина строки.
     * @return сформированную оговорённым способом строку.
     */
    private static String buildMidleString(char border, String text, int length) {
        return buildMidleString(border, border, text, length);
    }

    /**
     * Выдаёт строку указанной длины, составленную как строка обрамлённого текста
     * с различным символом для левой и правой декоративной границы.
     *
     * @param leftBorder  рамочный символ, помещаемый в начало строки.
     * @param rightBorder рамочный символ, помещаемый в конец строки.
     * @param text        текст обрамляемой строки.
     * @param length      желаемая полная длина строки.
     * @return сформированную оговорённым способом строку.
     */
    private static String buildMidleString(char leftBorder, char rightBorder, String text, int length) {
        return "%s %s %s%n"
                .formatted(leftBorder,
                        complementString(text, length - 4),
                        rightBorder);
    }

    /*
        Фасадные функции.
     */
    public static String singularFrame(String text) {
        return frameText(text, 30, 90, SINGULAR_FRAME);
    }

    public static String halfDoubleFrame(String text) {
        return frameText(text, 30, 90, HALF_DOUBLE_FRAME);
    }

    public static String doubleFrame(String text) {
        return frameText(text, 30, 90, DOUBLE_FRAME);
    }

    public static String asteriskFrame(String text) {
        return frameText(text, 30, 90, ASTERISK_FRAME);
    }


    static void print(String text) {
        System.out.println(text);
    }

    static void print() {
        System.out.println();
    }

    static void printInAsterisk(String text) {
        System.out.println(asteriskFrame(text));
    }

    static void printInFrame(String text) {
        System.out.println(singularFrame(text));
    }

    static void printInDoubleFrame(String text) {
        System.out.println(doubleFrame(text));
    }

    static void printInSemiDouble(String text) {
        System.out.println(halfDoubleFrame(text));
    }
}
