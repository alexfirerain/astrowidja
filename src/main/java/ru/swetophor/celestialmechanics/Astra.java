package ru.swetophor.celestialmechanics;

import ru.swetophor.harmonix.Matrix;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.celestialmechanics.CelestialMechanics.getArcForHarmonic;
import static ru.swetophor.celestialmechanics.CelestialMechanics.normalizeCoordinate;

/**
 * Прототип небесного те́ла — объект, имеющий:
 * <li> идентификатор</li>
 * <li> положение</li>
 * <li> физические свойства</li>
 * <li> астральные свойства</li>
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

    /**
     * Конструктор на основе координаты в виде градусов, минут и секунд.
     *
     * @param name   имя астры.
     * @param degree градусы координаты.
     * @param minute минуты координаты.
     * @param second секунды координаты
     */
    public Astra(String name, double degree, double minute, double second) {
        this(name, degree + minute / 60 + second / 3600);
    }

    /**
     * Конструктор на основе координаты в виде градусов и минут.
     *
     * @param name   имя астры.
     * @param degree градусы координаты.
     * @param minute минуты координаты.
     */
    public Astra(String name, double degree, double minute) {
        this(name, degree + minute / 60);
    }

    /**
     * Конструктор на основе координаты в виде вещественного числа.
     *
     * @param name   имя астры.
     * @param degree координата астры в double.
     */
    public Astra(String name, double degree) {
        this.name = name;
        this.zodiacPosition = normalizeCoordinate(degree);
    }

    /**
     * Статический генератор астры из имени и координаты в произвольной форме.
     * В зависимости от количества аргументов, они трактуются как (1) градусы,
     * (2) градусы и минуты, (3) градусы, минуты и секунды или (4) номер знака
     * (1-12), градусы, минуты и секунды.
     *
     * @param name       астра, которая будет построена.
     * @param coordinate одна, две, три или четыре величины, задающие координату.
     * @return созданную на основе аргументов астру.
     * @throws IllegalArgumentException если количество аргументов, задающих
     *                                  координату, не равно одному, двум, трём или четырём.
     */
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
            case 4 -> {
                double signNumber = (coordinate[0] - 1) % 12;
                double degrees = signNumber * 30 + coordinate[1];
                return new Astra(name, degrees, coordinate[2], coordinate[3]);
            }
            default -> throw new IllegalArgumentException("слишком много координат");
        }
    }

    /**
     * Создаёт астру из строки специального формата.
     * Если чтение не удаётся, сообщает об этом.
     *
     * @param input строка вида "астра координаты", где 'координаты' может быть
     *              градусами, градусами и минутами или градусами,
     *              минутами и секундами - через пробел.
     * @return заполненный объект Астра.
     */
    public static Astra readFromString(String input) {
        var elements = input.trim().split(" ");
        Double[] coors = new Double[0];

        try {
            if (elements.length == 0)
                throw new IllegalArgumentException("текст не содержит строк");

            coors = IntStream.range(1, elements.length)
                    .mapToObj(i -> Double.parseDouble(elements[i]))
                    .collect(Collectors.toCollection(() -> new ArrayList<>(4)))
                    .toArray(Double[]::new);
        } catch (RuntimeException e) {
            System.out.println("Не удалось прочитать строку '" + input + "': " + e.getMessage());
        }

        return Astra.fromData(elements[0], coors);
    }

    public static boolean ofSameHeaven(Astra a, Astra b) {
        return a.getHeaven() == b.getHeaven();
    }

    /**
     * Выдаёт инфу об астре в виде "название (градус_зодиака)"
     *
     * @return строку с названием и градусом астры.
     */
    public String getNameWithZodiacDegree() {
        return "%s (%s)".formatted(name, getZodiacDegree());
    }

    /**
     * Сообщает название астры.
     *
     * @return название астры.
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает ссылку на карту, в которой находится астра.
     *
     * @return карту неба, в котором находится астра.
     */
    public Chart getHeaven() {
        return this.heaven;
    }

    /**
     * Сообщает зодиакальную позицию в виде вещественного числа.
     *
     * @return зодиакальное положение астры в небе от начала отсчёта.
     */
    public double getZodiacPosition() {
        return this.zodiacPosition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeaven(Chart heaven) {
        this.heaven = heaven;
    }

    /**
     * Устанавливает зодиакальную координату, предварительно нормализуя.
     *
     * @param zodiacPosition устанавливаемая зодиакальная координата в градусах.
     */
    public void setZodiacPosition(double zodiacPosition) {
        this.zodiacPosition = normalizeCoordinate(zodiacPosition);
    }

    /**
     * Прибавляет к зодиакальной позиции указанное число.
     *
     * @param change изменение зодиакальной координаты.
     * @return эту же астру с обновлённой координатой.
     */
    public Astra advanceCoordinateBy(double change) {
        setZodiacPosition(zodiacPosition + change);
        return this;
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
     * в библиотеке {@link AstraEntity АстроСущность}. Если имя не найдено среди псевдонимов,
     * библиотекой возвращается '*'.
     *
     * @return определяемый классом АстроСущность астрологический символ для известных астр, '*' для неизвестных.
     */
    public char getSymbol() {
        return AstraEntity.findSymbolFor(this);
    }

    /**
     * Выдаёт строку, представляющую символ астры и её зодиакально положение.
     *
     * @return строку вида "символ (положение)",
     * где 'положение' представлено как градус знака.
     */
    public String getSymbolWithDegree() {
        return "%s (%s)".formatted(getSymbol(), getZodiacDegree());
    }

    /**
     * Выдаёт строку, представляющую символ астры и обладателя неба с нею.
     *
     * @return строку вида "символ (обладатель)", где 'обладатель' -
     * название карты, в которой расположена астра.
     */
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

//    public double getOrbInHarmonicWith(int harmonic, Astra counterpart) {
//        return getResonanceMatrix()
//                .findResonance(this, counterpart)
//                .getAspects().stream()
//                .filter(a -> a.hasResonance(harmonic))
//                .findFirst()
//                .map(Aspect::getClearance)
//                .orElseThrow();
//    }

    public double getArcInHarmonicWith(int harmonic, Astra counterpart) {
        return getArcForHarmonic(this, counterpart, harmonic);
    }

    /**
     * @return знак зодиака, в котором находится астра.
     */
    public ZodiacSign getZodiacSign() {
        return ZodiacSign.getZodiumOf(zodiacPosition);
    }

    public Matrix getResonanceMatrix() {
        return heaven.aspects;
        // TODO: по поводу обращения к матрице синастрии вообще заново проархитектурить
    }

    public boolean isInDirectResonanceWith(Astra counterpart, int harmonic) {
        return getResonanceMatrix().astrasInResonance(this, counterpart, harmonic);
    }

//    public boolean isInDirectResonanceWith(Astra counterpart, int harmonic) {
//        double effectiveOrb = heaven != counterpart.getHeaven() && isHalfOrbsForDoubles() ?
//                getPrimalOrb() / 2 : getPrimalOrb();
//        return getArcInHarmonicWith(harmonic, counterpart) <= getPrimalOrb() && harmonic != 1
//                || getArc(getZodiacPosition(), counterpart.getZodiacPosition()) <= effectiveOrb;
//    }
}
