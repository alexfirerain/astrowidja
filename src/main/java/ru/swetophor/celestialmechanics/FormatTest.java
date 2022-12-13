package ru.swetophor.celestialmechanics;

import java.util.Arrays;

import static ru.swetophor.celestialmechanics.Mechanics.secondFormat;
import static ru.swetophor.celestialmechanics.Mechanics.secondFormatTablewise;

public class FormatTest {
    static Astra[] astras =
            Arrays.stream(new String[]{
                    "звезда 50 30 5",
                    "звезда 50 0 5",
                    "звезда 50 30 0",
                    "звезда 0 30 0",
                    "звезда 0 0 0" })
            .map(Astra::readFromString)
            .toList()
            .toArray(new Astra[0]);

    public static void main(String[] args) {

        System.out.println("\n* секундФормат без параметра *");
        Arrays.stream(astras)
                .map(she -> secondFormat(she.getZodiacPosition()))
                .forEach(System.out::println);

        System.out.println("\n* секундФормат без лишних нолей *");
        Arrays.stream(astras)
                .map(she -> secondFormat(she.getZodiacPosition(), true))
                .forEach(System.out::println);

        System.out.println("\n* секундФормат с лишними нолями *");
        Arrays.stream(astras)
                .map(she -> secondFormat(she.getZodiacPosition(), false))
                .forEach(System.out::println);

        System.out.println("\n* секундФорматТаблично без параметра *");
        Arrays.stream(astras)
                .map(she -> secondFormatTablewise(she.getZodiacPosition()))
                .forEach(System.out::println);

        System.out.println("\n* секундФорматТаблично без лишних нолей *");
        Arrays.stream(astras)
                .map(she -> secondFormatTablewise(she.getZodiacPosition(), true))
                .forEach(System.out::println);

        System.out.println("\n* секундФорматТаблично с лишними нолями *");
        Arrays.stream(astras)
                .map(she -> secondFormatTablewise(she.getZodiacPosition(), false))
                .forEach(System.out::println);
    }
}
