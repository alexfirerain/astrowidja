package ru.swetophor.resogrid;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.celestialmechanics.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private int ultimateHarmonic;
    /**
     * найденные в пределах орбиса резонансы по росту гармоники
     */
    private ArrayList<Resound> resounds;     //

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

        public double getStrength() {
            return strength;
        }
    }

    /**
     * получение массива резонансов для двух астр (конструктор)
     * @param a первая астра резонанса.
     * @param b вторая астра резонанса.
     * @param orb первичный орбис резонанса.
     * @param ultimateHarmonic  до какой гармоники продолжать анализ.
     */
    Resonance(Astra a, Astra b, double orb, int ultimateHarmonic) {
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
            this.orb = orb;
            this.ultimateHarmonic = ultimateHarmonic;

            resounds = new ArrayList<>();
            for (int h = 1; h <= ultimateHarmonic; h++) {
                double arcInHarmonic = normalizeArc(arc * h);
                if (arcInHarmonic < orb && isNotRepeating(h))
                    resounds.add(new Resound(h, arcInHarmonic, arc));
            }

//            IntStream.range(1, ultimateHarmonic + 1).forEach(h -> {
//                double arcInHarmonic = normalizeArc(arc * h);
//                if (arcInHarmonic < orb && isNotRepeating(h))
//                    resounds.add(new Resound(h, arcInHarmonic, arc));
//            });
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
            if (abs(multiplier * single - arc) < orbHere)
                break;
            else
                multiplier++;
        return multiplier;
    }

    /**
     * Вспомогательный метод отсечения кратных гармоник при заполнении списка отзвуков.
     * @param which число, которое проверяется на кратность уже найденным отзвукам.
     * @return истинно, если проверяемое число не кратно никакому из уже найденных (кроме 1).
     */
    private boolean isNotRepeating(int which) {
        for (Resound next : resounds) {
            if (next.numeric == 1) continue;
            if(which % next.numeric == 0) return false;
        }
        return true;
    }

    private String resoundsReport() {
        StringBuilder sb = new StringBuilder();

        if (resounds.size() == 0) {
            sb.append("Ни однаго резонанса до %d при орбисе %s%n".formatted(ultimateHarmonic, orb));
        }
        for (Resound aspect : getResoundsByStrength()) {
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

    public String resonancesOutput() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case SELF -> sb.append("%n%s (%s)%n".formatted(astra_1, whose_a1.getName()));
            case CHART -> {
                sb.append("%n* Дуга между %c %s и %c %s (%s) = %s%n".formatted(
                        AstraEntity.findSymbolByName(astra_1), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_1)),
                        AstraEntity.findSymbolByName(astra_2), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_2)),
                        whose_a1.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
            case SYNASTRY -> {
                sb.append("%n* Дуга между %c %s (%s) и %c %s (%s) = %s%n".formatted(
                        AstraEntity.findSymbolByName(astra_1), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_1)),
                        whose_a1.getName(),
                        AstraEntity.findSymbolByName(astra_2), Mechanics.zodiacDegree(whose_a2.getAstraPosition(astra_2)),
                        whose_a2.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
        }
        return sb.toString();

    }

    private List<Resound> getResoundsByStrength() {
        return resounds.stream()
                .sorted(Comparator.comparing(Resound::getStrength).reversed())
                .collect(Collectors.toList());
    }

}
