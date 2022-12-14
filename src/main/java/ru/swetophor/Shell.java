package ru.swetophor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Shell {
    static String autosaveName() {
        return "сохранение %s.awb"
                .formatted(new SimpleDateFormat("E d MMMM .yy HH-mm")
                        .format(new Date()));
    }

    static String frameText(String text,
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

    static String frameText(String text, int minWidth, char symbol) {
        return frameText(text, minWidth, 80, symbol, symbol, symbol, symbol, symbol, symbol);
    }

    private static String complementString(String string, int length) {
        return length - string.length() <= 0 ?
                string :
                string +
                        " ".repeat(length - string.length());

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

    /**
     * Выдаёт строку указанной длины, составленную как строка обрамлённого текста.
     *
     * @param border рамочный символ, помещаемый в начало и конец строки.
     * @param text   текст обрамляемой строки.
     * @param length желаемая полная длина строки.
     * @return сформированную оговорённым способом строку.
     */
    private static String buildMidleString(char border, String text, int length) {
        return "%s %s %s%n"
                .formatted(border,
                        complementString(text, length - 4),
                        border);
    }
}
