package ru.swetophor.celestialmechanics;

import ru.swetophor.resogrid.Aspect;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Прототип небесного те́ла — объект, имеющий:
 * <li> идентификатор
 * <li> положение
 * <li> физическия свойства
 * <li> астральныя свойства
 */
public class Astra {
    /**
     * Идентифицирующее имя астры.
     */
    private String name;                                        // чо за астра
    /**
     * Ссылка на карту, в которой находится астра.
     */
    private Chart heaven;                                        // чья астра
    /**
     * Зодиакальное положение астры от 0°♈
     */
    private double zodiacPosition;                        // положение в Зодиаке

    // конструкторы для задания координаты с/без минут и секунд
    public Astra(String name, double degree, double minute, double second) {
        this(name, degree + minute/60 + second/3600);
    }
    public Astra(String name, double degree, double minute) {
        this(name, degree + minute/60);
    }
    public Astra(String name, double degree) {
        this.name = name;
        this.zodiacPosition = CelestialMechanics.normalizeCoordinate(degree);
    }

    public static Astra fromData(String name, Double... coordinate) {
        switch (coordinate.length) {
            case 0 -> throw new IllegalArgumentException("координат нет");
            case 1 -> {
                return new Astra(name, coordinate[0]);
            }
            case 2 -> {
                return new Astra(name, coordinate[0], coordinate[1]);
            }
            case 3 -> {
                return new Astra(name, coordinate[0], coordinate[1], coordinate[2]);
            }
            default -> throw new IllegalArgumentException("слишком много координат");
        }
    }

    public static Astra readFromString(String input) {
        var elements = input.split(" ");
        if (elements.length == 0)
            throw new IllegalArgumentException("текст не содержит строк");

        var coors = IntStream.range(1, elements.length)
                .mapToObj(i -> Double.parseDouble(elements[i]))
                .collect(Collectors.toCollection(() -> new ArrayList<>(3)))
                .toArray(Double[]::new);

        return Astra.fromData(elements[0], coors);
    }

    /**
     * Выдаёт инфу об астре в виде "название (градус_зодиака)"
     *
     * @return строку с названием и градусом астры.
     */
    public String getNameWithZodiacDegree() {
        return "%s (%s)".formatted(name, getZodiacDegree());
    }

    public String getName() {
        return name;
    }

    public Chart getHeaven() {
        return this.heaven;
    }

    public double getZodiacPosition() {
        return this.zodiacPosition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeaven(Chart heaven) {
        this.heaven = heaven;
    }

    public void setZodiacPosition(double zodiacPosition) {
        this.zodiacPosition = zodiacPosition;
    }

    /**
     * Выдаёт зодиакальное положение астры, как предоставляется
     * функцией {@link CelestialMechanics#zodiacDegree(double)}
     *
     * @return строковое представление зодиакального градуса, в котором расположена астра.
     */
    public String getZodiacDegree() {
        return CelestialMechanics.zodiacDegree(zodiacPosition);
    }

    /**
     * Выдаёт астрологический символ для астры, если она распознана по названию
     * в библиотеке {@link AstraEntity}. Если имя не найдено среди псевдонимов,
     * возвращается '*'.
     *
     * @return астрологический символ дя известных астр, '*' для неизвестных.
     */
    public char getSymbol() {
        return AstraEntity.findSymbolFor(name);
    }

    public String getSymbolWithDegree() {
        return "%s (%s)".formatted(getSymbol(), getZodiacDegree());
    }

    public String getSymbolWithOwner() {
        return "%s (%s)".formatted(getSymbol(), heaven.getShortenedName(8));
    }

    /**
     * Возвращает строку того формата, который принят для хранения
     * данных о положении астры при сохранении.
     *
     * @return строку с инфой об астре вида "название градусы минуты секунды".
     */
    public String getString() {
        int[] coors = CelestialMechanics.degreesToCoors(zodiacPosition);
        return "%s %s %s %s%n"
                .formatted(name,
                        coors[0],
                        coors[1],
                        coors[2]);
    }

    public double getOrbInHarmonicWith(int harmonic, Astra counterpart) {
        return heaven
                .aspects
                .findResonance(this, counterpart)
                .getAspects().stream()
                .filter(a -> a.hasResonance(harmonic))
                .findFirst()
                .map(Aspect::getClearance)
                .orElseThrow();
    }

    /**
     * @return знак зодиака, в котором находится астра.
     */
    public ZodiacSign getZodiacSign() {
        return ZodiacSign.getZodiumOf(zodiacPosition);
    }
}
