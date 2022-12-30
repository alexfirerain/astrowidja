package ru.swetophor.resogrid;

import static java.lang.Math.floor;
import static ru.swetophor.celestialmechanics.Mechanics.findMultiplier;

/**
 * один из действующих для данной дуги резонансов
 */
class Aspect {

    /**
     * гармоника
     */
    int numeric;              //

    /**
     * дальность
     */
    int multiplier;               //

    /**
     * разность дуги резонанса с чистым аспектом вида дальность/гармоника
     */
    double clearance;           //

    /**
     * разность дуги резонанса с чистым аспектом % от наиточнейшего
     */
    double strength;            //

    /**
     * точность аспекта через количество последующих гармоник, через кои проходит
     */
    int depth;

    /**
     * Выводит характеристику, насколько точен резонанс.
     *
     * @return строковое представление ранга точности.
     */
    String strengthLevel() {
        if (depth <= 1) return "- приблизительный ";
        else if (depth == 2) return "- уверенный ";
        else if (depth <= 5) return "- глубокий ";
        else if (depth <= 12) return "- точный ";
        else if (depth <= 24) return "- глубоко точный ";
        else return "- крайне точный ";
    }

    String strengthRating() {
        if (depth <= 1) return "★";
        else if (depth == 2) return "★★";
        else if (depth <= 5) return "★★★";
        else if (depth <= 12) return "★★★★";
        else if (depth <= 24) return "★★★★★";
        else return "✰✰✰✰✰";
    }

    /**
     * Модель аспекта, одного из резонансов в дуге.
     * @param numeric      гармоника, т.е. кратность дуги Кругу.
     * @param clearance     с каким орбисом фиксируется соединение в этой гармонике.
     * @param fromArc   дуга, в которой распознан этот резонанс.
     * @param orb   первичный орб для соединений, используемый при определении резонансов.
     */
    Aspect(int numeric, double clearance, double fromArc, double orb) {
        this.numeric = numeric;
        this.multiplier = findMultiplier(numeric, fromArc, orb);
        this.clearance = clearance;
        this.strength = ((orb - clearance) / orb) * 100;
        this.depth = (int) floor(orb / clearance);
    }

    public double getStrength() {
        return strength;
    }
}
