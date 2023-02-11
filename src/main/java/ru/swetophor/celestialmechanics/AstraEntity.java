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
    RAH("Раху", '☊', "Rahu"),
    LIL("Лилит", '⚸', "Lilith");

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
            if (String.valueOf(s.symbol).equals(name))
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

    /**
     * Находит для данной астры, под каким номером значится
     * соответствующая астросущность, т.е. какой ординал имеет в перечислении.
     * Это зависит от конкретной реализации класса AstraEntity.
     * Сущность ищется по совпадению имени астры.
     * Если имя не опознано, выдаётся следующий номер за наибольшим,
     * т.е. равный размеру известных астросущностей.
     *
     * @param astra астра, чей номер в реестре смотрится.
     * @return ординальный номер соответствующей астре сущности,
     * если же такой не найдено, то количество сущностей в реестре.
     */
    public static int getAstraEntityNumber(Astra astra) {
        AstraEntity entity = getEntityByName(astra.getName());
        return entity != null ?
                entity.ordinal() :
                values().length;
    }

}
