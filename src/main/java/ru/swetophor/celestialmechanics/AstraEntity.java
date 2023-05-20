package ru.swetophor.celestialmechanics;

import java.util.HashSet;
import java.util.Set;

/**
 * Используемые в Астровидье астросущности, которые
 * имеют ряд ассоциированных названий и символ для отображения.
 * Астросущность соответствует какому-то телу, рассматриваемому
 * в астрологии. В то время как объект типа Астра соответствует
 * некому реальному телу с реальной координатой в момент времени.
 * По идее, каждый объект Астра в программе должен быть ассоциирован
 * с какой-то Астросущностью.
 */
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

    /**
     * Идентифицирует и выдаёт астросущность по имени.
     * В качестве имени может быть использовано основное
     * или любое из альтернативных имён, а также символ астры.
     * @param name обозначение астры, по которому идентифицируется сущность.
     * @return  идентифицированная астросущность или {@code null},
     * если астросущность не идентифицирована
     */
    public static AstraEntity getEntityByName(String name) {
        name = name.trim();
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

    /**
     * Находит и отдаёт символ, связанный с астросущностью, какое-то
     * имя которой передано в аргументе и распознано {@link #getEntityByName(String)}.
     * @param name имя, псевдоним или символ астры.
     * @return символ астросущности, распознанной в астре, или '*', если не распознано.
     */
    public static char findSymbolFor(String name) {
        AstraEntity astra = getEntityByName(name);
        return astra != null ? astra.symbol : '*';
    }

    /**
     * Находит и отдаёт символ, связанный с астросущностью,
     * найденной {@link #findSymbolFor(String)} по имени астры,
     * переданной в аргументе.
     * @param astra астра, символ для которой определяется.
     * @return  {@code char}, соответствующий распознанной астре,
     * или '*', если не распознано.
     */
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
