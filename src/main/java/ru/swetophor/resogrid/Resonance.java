package ru.swetophor.resogrid;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.celestialmechanics.*;
import java.util.ArrayList;
import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static ru.swetophor.celestialmechanics.Mechanics.*;
import static ru.swetophor.Interpreter.ResonanceDescription;
import static ru.swetophor.resogrid.ResonanceType.*;

/**
 * Структурированный массив, описывающий
 * взаимодействие (резонансы) между некоторыми двумя точками
 */
@Setter
@Getter
public class Resonance {
    /**
     * Ссылка на карту, в которой находится первая астра.
     */
    private final Chart whoseFirst;
    /**
     * Ссылка на карту, в которой находится вторая астра.
     */
    private Chart whoseSecond;
    /**
     * Тип резонанса:
     */
    private final ResonanceType type;
    /**
     * Название первой астры.
     */
    private String first;
    /**
     * Название второй астры.
     */
    private String second;
    /**
     * Угловое расстояние меж точками.
     */
    private double arc;
    /**
     * Орбис, т.е. допуск, с которым аспект считается действующим.
     */
    private double orb;
    /**
     * Наибольший проверяемый целочисленный резонанс
     */
    private int edgeHarmonic;
    private ArrayList<Resound> resounds;     // найденные в пределах орбиса резонансы по росту гармоники
    private ArrayList<Resound> resoundsByStrength;     // найденные в пределах орбиса резонансы убыванию силы

    // один из действующих для данной дуги резонансов
    class Resound {
        int number;              // гармоника
        int multiplier;               // дальность
        double gap;           // орбис для соединения в этой гармонике
        double strength;            // тот же орбис в % от наиточнаго
        int depth;            // он же через количество последующих гармоник, через кои проходит
        String strengthLevel() {
            if (depth <= 1) return "- приблизительный ";
            else if (depth <= 2) return "- уверенный ";
            else if (depth <= 5) return "- глубокий ";
            else if (depth <= 12) return "- точный ";
            else if (depth <= 24) return "- глубоко точный ";
            else return "- крайне точный ";
        }
        Resound(int number, double gap, double fromArc) {
            this.number = number;
            this.multiplier = findMultiplier(number, fromArc);
            this.gap = gap;
            this.strength = ((orb - gap) / orb) * 100;
            this.depth = (int) floor(orb / gap);
        }
    }

    // получение массива резонансов для двух астр (конструктор)
    Resonance(Astra a, Astra b, double orb, int edgeHarmonic) {
        first = a.getName(); whoseFirst = a.getHeaven();
        if (a.equals(b)) {
            type = SELF;
        } else {
            second = b.getName(); whoseSecond = b.getHeaven();
            type = whoseFirst.getName().equals(whoseSecond.getName()) ?
                    CHART : SYNASTRY;
            first = a.getName(); second = b.getName();
            arc = Mechanics.getArc(a, b);
            this.orb = orb; this.edgeHarmonic = edgeHarmonic;
            resounds = new ArrayList<>(); resoundsByStrength = new ArrayList<>();
            double harmonicArc;
            for (int i = 1; i <= edgeHarmonic; i++) {
                harmonicArc = normalizeArc(arc * i);
                if (harmonicArc < orb && !isNotSimple(i))
                    resounds.add(new Resound(i, harmonicArc, arc));
            }
            sort();
        }
    }

    // вспомогательный метод нахождения крата аспекта
    private int findMultiplier(int resonance, double arc) {
//        double единичник = 360 / резонанс;
        int multiplier = 1;
        while (multiplier < resonance / 2) {
            if (abs(multiplier * CIRCLE / resonance - arc) < orb / resonance) break;
            else multiplier++;
        }
        return multiplier;
    }

    /**
     * Вспомогательный метод отсечения кратных гармоник.
     * @param which число, которое проверяется на кратность уже найденным отзвукам.
     * @return истинно, если проверяемое число кратно какому-то из уже найденных.
     */
    private boolean isNotSimple(int which) {
        for (Resound next : resounds) {
            if (next.number == 1) continue;
            if(which % next.number == 0) return true;
        }
        return false;
    }

    private String resoundsReport() {
        StringBuilder sb = new StringBuilder();

        if (resounds.size() == 0) {
            sb.append("Ни однаго резонанса до %d при орбисе %s%n".formatted(edgeHarmonic, orb));
        }
        for (Resound aspect : resoundsByStrength) {
            sb.append(ResonanceDescription(aspect.number, aspect.multiplier));
            sb.append("Резонанс %d (x%d) %s (%d) (%.2f%%, %s)%n".formatted(
                    aspect.number,
                    aspect.multiplier,
                    aspect.strengthLevel(),
                    aspect.depth,
                    aspect.strength,
                    secondFormat(aspect.gap, true)));
        }
        return sb.toString();
    }

    // метод получения упорядоченнаго по силе
    private void sort() {
        while (resoundsByStrength.size() < resounds.size()) {
            int strongest = 0;
            while (resoundsByStrength.contains(resounds.get(strongest)))
                strongest++;
            if (resoundsByStrength.size() < resounds.size() - 1) {
                int strong = strongest + 1;

                while (resoundsByStrength.contains(resounds.get(strong)))
                    strong++;

                while (strong < resounds.size()) {
                    if (resounds.get(strong).gap < resounds.get(strongest).gap)
                        strongest = strong;

                    strong++;

                    while (strong < resounds.size() && resoundsByStrength.contains(resounds.get(strong)))
                        strong++;
                }
            }
            resoundsByStrength.add(resounds.get(strongest));
        }
    }

    public String resonancesOutput() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case SELF -> sb.append("%n%s (%s)%n".formatted(first, whoseFirst.getName()));
            case CHART -> {
                sb.append("%n* Дуга между %s и %s (%s) = %s%n".formatted(
                        first,
                        second,
                        whoseFirst.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
            case SYNASTRY -> {
                sb.append("%n* Дуга между %s (%s) и %s (%s) = %s%n".formatted(
                        first,
                        whoseFirst.getName(),
                        second,
                        whoseSecond.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
        }
        return sb.toString();

    }
}
