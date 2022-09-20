package ru.swetophor.celestialmechanics;

import static ru.swetophor.celestialmechanics.Mechanics.normalizeCoordinate;

/**
 * Прототип небеснаго те́ла —
 * объект, имеющий:
 *      ♦ идентификатор
 *      ♦ положение
 *      ♦ физическия свойства
 *      ♦ астральныя свойства
 */
public class Astra {
    public String name;                                        // чо за астра
    public Chart heaven;                                        // чья астра
    public double zodiacPosition;                        // положение в Зодиаке

    // конструкторы для задания координаты с/без минут и секунд
    public Astra(String name, double degree, double minute, double second) {
        this.name = name;
        this.zodiacPosition = normalizeCoordinate(degree + minute/60 + second/3600);
    }
    public Astra(String name, double degree, double minute) {
        this.name = name;
        this.zodiacPosition = normalizeCoordinate(degree + minute/60);
    }
    public Astra(String name, double degree) {
        this.name = name;
        this.zodiacPosition = normalizeCoordinate(degree);
    }


}
