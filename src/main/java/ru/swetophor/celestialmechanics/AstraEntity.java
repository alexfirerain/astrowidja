package ru.swetophor.celestialmechanics;

import java.util.HashSet;
import java.util.Set;

public enum AstraEntity {
    /*
        Астрологические объекты
     */
    LUN("Луна", '☽', "Moon"),
    MER("Меркурий", '☿', "Mercury"),
    VEN("Венера", '♀', "Venus"),
    SOL("Солнце", '☉', "Sun", "Sol"),
    MAR("Марс", '♂', "Mars"),
    CER("Церера", '⚳', "Ceres"),
    LIL("Лилит", '⚸', "Lilith"),
    JUP("Юпитер", '♃', "Jupiter"),
    RAH("Раху", '☊', "Rahu"),
    SAT("Сатурн", '♄', "Saturn"),
    CHI("Хирон", '⚷', "Chiron"),
    URA("Уран", '♅', "Uranus"),
    NEP("Нептун", '♆', "Neptune"),
    PLU("Плутон", '♇', "Pluto");

    /*
        Структура объекта.
     */
    public final String name;
    public final char symbol;
    public final Set<String> also;

    AstraEntity(String name, char symbol, String... also) {
        this.name = name;
        this.symbol = symbol;
        this.also = new HashSet<>(Set.of(also));
    }

    /*
        Вспомогательные нужности.
     */
    public static AstraEntity getEntityByName(String name) {
        for (AstraEntity s : values()) {
            if (s.name.equalsIgnoreCase(name))
                return s;
            for (String alt : s.also)
                if (alt.equalsIgnoreCase(name))
                    return s;
        }
        return null;
    }

    public static char findSymbolFor(String name) {
        AstraEntity astra = getEntityByName(name);
        return astra != null ? astra.symbol : '*';
    }

    public static char findSymbolFor(Astra astra) {
        return findSymbolFor(astra.getName());
    }
}
