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
    private final Chart whose_a1;
    /**
     * Ссылка на карту, в которой находится вторая астра.
     */
    private Chart whose_a2;
    /**
     * Тип резонанса:
     */
    private final ResonanceType type;
    /**
     * Название первой астры.
     */
    private String astra_1;
    /**
     * Название второй астры.
     */
    private String astra_2;
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
    /**
     * найденные в пределах орбиса резонансы по росту гармоники
     */
    private ArrayList<Resound> resounds;     //
    /**
     * найденные в пределах орбиса резонансы по убыванию силы
     */
    private ArrayList<Resound> resoundsByStrength;     //

    /**
     * один из действующих для данной дуги резонансов
     */
    class Resound {

        /**
        гармоника
         */
        int numeric;              //

        /**
        дальность
         */
        int multiplier;               //

        /**
        орбис для соединения в этой гармонике
         */
        double clearance;           //

        /**
        тот же орбис в % от наиточнаго
         */
        double strength;            //

        /**
        он же через количество последующих гармоник, через кои проходит
         */
        int depth;

        /**
         * Выводит характеристику, насколько точен резонанс.
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
        Resound(int numeric, double clearance, double fromArc) {
            this.numeric = numeric;
            this.multiplier = findMultiplier(numeric, fromArc);
            this.clearance = clearance;
            this.strength = ((orb - clearance) / orb) * 100;
            this.depth = (int) floor(orb / clearance);
        }
    }

    /**
     * получение массива резонансов для двух астр (конструктор)
     */
    Resonance(Astra a, Astra b, double orb, int edgeHarmonic) {
        astra_1 = a.getName();
        whose_a1 = a.getHeaven();
        if (a.equals(b)) {
            type = SELF;
        } else {
            astra_2 = b.getName();
            whose_a2 = b.getHeaven();
            type = whose_a1.getID() == whose_a2.getID() ?
                    CHART : SYNASTRY;
            astra_1 = a.getName();
            astra_2 = b.getName();
            arc = Mechanics.getArc(a, b);
            this.orb = orb; this.edgeHarmonic = edgeHarmonic;
            resounds = new ArrayList<>();
            resoundsByStrength = new ArrayList<>();
            double harmonicArc;
            for (int i = 1; i <= edgeHarmonic; i++) {
                harmonicArc = normalizeArc(arc * i);
                if (harmonicArc < orb && !isNotSimple(i))
                    resounds.add(new Resound(i, harmonicArc, arc));
            }
            sort();
        }
    }

    /**
     * вспомогательный метод нахождения крата аспекта
     */
    private int findMultiplier(int resonance, double arc) {
        double single = CIRCLE / resonance;
        int multiplier = 1;
        double orbHere = orb / resonance;

        while (multiplier < resonance / 2)
            if (abs(single * multiplier - arc) < orbHere)
                return multiplier;
            else
                multiplier++;

        throw new IllegalArgumentException("Предложенная дуга %f.0°не является кратом для %d"
                .formatted(arc, resonance));
    }

    /**
     * Вспомогательный метод отсечения кратных гармоник.
     * @param which число, которое проверяется на кратность уже найденным отзвукам.
     * @return истинно, если проверяемое число кратно какому-то из уже найденных.
     */
    private boolean isNotSimple(int which) {
        for (Resound next : resounds) {
            if (next.numeric == 1) continue;
            if(which % next.numeric == 0) return true;
        }
        return false;
    }

    private String resoundsReport() {
        StringBuilder sb = new StringBuilder();

        if (resounds.size() == 0) {
            sb.append("Ни однаго резонанса до %d при орбисе %s%n".formatted(edgeHarmonic, orb));
        }
        for (Resound aspect : resoundsByStrength) {
            sb.append(ResonanceDescription(aspect.numeric, aspect.multiplier));
            sb.append("Резонанс %d (x%d) %s (%d) (%.2f%%, %s)%n".formatted(
                    aspect.numeric,
                    aspect.multiplier,
                    aspect.strengthLevel(),
                    aspect.depth,
                    aspect.strength,
                    secondFormat(aspect.clearance, true)));
        }
        return sb.toString();
    }

    /**
     * метод получения упорядоченнаго по силе
     */
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
                    if (resounds.get(strong).clearance < resounds.get(strongest).clearance)
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
            case SELF -> sb.append("%n%s (%s)%n".formatted(astra_1, whose_a1.getName()));
            case CHART -> {
                sb.append("%n* Дуга между %s и %s (%s) = %s%n".formatted(
                        astra_1,
                        astra_2,
                        whose_a1.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
            case SYNASTRY -> {
                sb.append("%n* Дуга между %s (%s) и %s (%s) = %s%n".formatted(
                        astra_1,
                        whose_a1.getName(),
                        astra_2,
                        whose_a2.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
        }
        return sb.toString();

    }
}
