package ru.swetophor.celestialmechanics;

import java.util.ArrayList;

import static ru.swetophor.celestialmechanics.Mechanics.secondFormat;
import static ru.swetophor.celestialmechanics.Mechanics.secondFormatTablewise;

public class FormatTest {
    public static void main(String[] args) {
        ArrayList<Astra> stars = new ArrayList<>();
        stars.add(new Astra("звезда", 50, 30, 5));
        stars.add(new Astra("звезда", 50, 0, 5));
        stars.add(new Astra("звезда", 50, 30, 0));
        stars.add(new Astra("звезда", 0, 30, 0));
        stars.add(new Astra("звезда", 0, 0, 0));

        System.out.println("\n* секундФормат без параметра *");
        for (Astra she : stars) System.out.println(secondFormat(she.getZodiacPosition()));

        System.out.println("\n* секундФормат без лишних нолей *");
        for (Astra she : stars) System.out.println(secondFormat(she.getZodiacPosition(), true));

        System.out.println("\n* секундФормат с лишними нолями *");
        for (Astra she : stars) System.out.println(secondFormat(she.getZodiacPosition(), false));

        System.out.println("\n* секундФорматТаблично без параметра *");
        for (Astra she : stars) System.out.println(secondFormatTablewise(she.getZodiacPosition()));

        System.out.println("\n* секундФорматТаблично без лишних нолей *");
        for (Astra she : stars) System.out.println(secondFormatTablewise(she.getZodiacPosition(), true));

        System.out.println("\n* секундФорматТаблично с лишними нолями *");
        for (Astra she : stars) System.out.println(secondFormatTablewise(she.getZodiacPosition(), false));
    }
}
