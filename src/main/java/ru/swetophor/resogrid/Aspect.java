package ru.swetophor.resogrid;

import ru.swetophor.celestialmechanics.Mechanics;

import java.util.List;

import static java.lang.Math.floor;
import static ru.swetophor.celestialmechanics.Mechanics.findMultiplier;

/**
 * Некоторая установленная для реальной дуги гармоническая кратность.
 * Аспект характеризуется математическим определением резонанса
 * (основное гармоническое число и множитель повторения кратности)
 * и степенью точности (добротности) резонанса, определяемой как близость
 * реального небесного расстояния к дуге точного аспекта.
 */
public class Aspect {
    /**
     * Гармоника, в которой аспект предстаёт соединением.
     * Т.е. число, на которое делится Круг, чтобы получить
     * дугу единичного резонанса для данной гармоники.
     */
    private final int numeric;              //
    /**
     * Множитель дальности, или повторитель кратности. Т.е. то число, на которое
     * нужно умножить дугу единичного резонанса этой гармоники, чтоб получить
     * дугу чистого неединичного аспекта.
     */
    private final int multiplicity;               //

    public int getNumeric() {
        return numeric;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public double getClearance() {
        return clearance;
    }

    public int getDepth() {
        return depth;
    }

    /**
     * Выдаёт список множителей, в произведении дающих
     * число резонанса данного аспекта.
     * @return  список множителей гармоники.
     */
    public List<Integer> getMultipliers() {
        return Mechanics.multipliersExplicate(numeric);
    }

    /**
     * Разность дуги резонанса с дугой чистого аспекта, полученной как
     * (360° / гармоника) * множитель. Т.е. эффективный орбис аспекта.
     */
    private final double clearance;           //
    /**
     * Эффективный орбис аспекта, выраженный в %-ах, где 100% означает полное
     * совпадение реальной дуги с математическим аспектом (экзакт, эффективный орбис 0°),
     * а 0% означает совпадение эффективного орбиса с предельным для данной гармоники.
     * Иначе говоря, точность аспекта, выраженная в %-ах.
     */
    private final double strength;            //
    /**
     * точность аспекта через количество последующих гармоник, через кои проходит
     */
    private final int depth;
    /**
     * Выводит характеристику, насколько точен резонанс.
     *
     * @return строковое представление ранга точности.
     */
    public String strengthLevel() {
        if (depth <= 1) return "- приблизительный ";
        else if (depth == 2) return "- уверенный ";
        else if (depth <= 5) return "- глубокий ";
        else if (depth <= 12) return "- точный ";
        else if (depth <= 24) return "- глубоко точный ";
        else return "- крайне точный ";
    }

    /**
     * Рейтинг силы в звёздах:
     * <ul>★    < 50% - присутствует только в данной гармонике</ul>
     * <ul>★★   50-66% - присутствует в данной и в следующей х2</ul>
     * <ul>★★★  67-83% - сохраняется ещё в гармониках х3, х4 и х5</ul>
     * <ul>★★★★ 84-92% - сохраняется до гармоник х12</ul>
     * <ul>★★★★★  93-96% - сохраняется до х24 гармоник  </ul>
     * <ul>✰✰✰✰✰  > 96 % - присутствует в нескольких десятках кратных гармоник  </ul>
     * @return
     */
    public String strengthRating() {
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
    public Aspect(int numeric, double clearance, double fromArc, double orb) {
        this.numeric = numeric;
        this.multiplicity = findMultiplier(numeric, fromArc, orb);
        this.clearance = clearance;
        this.strength = ((orb - clearance) / orb) * 100;
        this.depth = (int) floor(orb / clearance);
    }

    public double getStrength() {
        return strength;
    }



}
