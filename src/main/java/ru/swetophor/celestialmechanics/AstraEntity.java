package ru.swetophor.celestialmechanics;

import java.util.HashSet;
import java.util.Set;

public enum AstraEntity {
    /*
        Астрологические объекты
     */
    SOL("Солнце", '☉', "Sun", "Sol"),
    LUN("Луна", '☽', "Moon"),
    MER("Меркурий", '☿', "Mercury"),
    VEN("Венера", '♀', "Venus"),
    MAR("Марс", '♂', "Mars"),
    CER("Церера", '⚳', "Ceres"),
    JUP("Юпитер", '♃', "Jupiter"),
    SAT("Сатурн", '♄', "Saturn"),
    CHI("Хирон", '⚷', "Chiron"),
    URA("Уран", '♅', "Uranus"),
    NEP("Нептун", '♆', "Neptune"),
    PLU("Плутон", '♇', "Pluto"),
    LIL("Лилит", '⚸', "Lilith"),
    RAH("Раху", '☊', "Rahu");

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

    public static char findSymbolByName(String name) {
        AstraEntity astra = getEntityByName(name);
        return astra != null ? astra.symbol : '*';
    }
}
