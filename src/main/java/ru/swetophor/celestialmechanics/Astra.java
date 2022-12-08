package ru.swetophor.celestialmechanics;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.celestialmechanics.Mechanics.normalizeCoordinate;

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
        this.zodiacPosition = normalizeCoordinate(degree);
    }

    public static Astra fromData(String name, Double[] coordinate) {
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
        var e = input.split(" ");
        if (e.length == 0)
            throw new IllegalArgumentException("текст не содержит строк");

        var coors = IntStream.range(1, e.length)
                .mapToObj(i -> Double.parseDouble(e[i]))
                .collect(Collectors.toCollection(() -> new ArrayList<>(3)));

        return Astra.fromData(e[0], coors.toArray(Double[]::new));
    }


    public String getName() {
        return "%s (%s)".formatted(name, getZodiacDegree());
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

    public String getZodiacDegree() {
        return (int) Math.ceil(zodiacPosition % 30) + "°" + ZodiacSign.pointsZodium(zodiacPosition);
    }

}
