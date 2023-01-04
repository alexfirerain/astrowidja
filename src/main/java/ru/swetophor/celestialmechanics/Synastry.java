package ru.swetophor.celestialmechanics;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.resogrid.Matrix;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Synastry extends MultiChart {

    @Override
    public String toString() { return "%s (карта №%d)".formatted(name, ID); }

    {
        type = ChartType.SYNASTRY;
        moments = new ArrayList<>(2);
    }

    protected Matrix aspects;

    /**
     * конструктор синастрии из двух карт
     */
    public Synastry(Chart chart1, Chart chart2) {
        super("Синастрия: %s и %s".formatted(chart1.name, chart2.name));
        moments.add(chart1);
        moments.add(chart2);
        aspects = new Matrix(chart1.getAstras(), chart2.getAstras());
    }

    public void plotAspectTable() {
        System.out.printf("""
                        **************************************
                        * %s: %s и %s (№%d) *
                        **************************************
                        """,
                type,
                moments.get(0).name,
                moments.get(1).name,
                ID);
        System.out.println(aspects.resultsOutput());
    }

    public Chart getChartA() {
        return moments.get(0);
    }

    public Chart getChartB() {
        return moments.get(1);
    }

    @Override
    public List<Astra> getAstras() {
        List<Astra> r = new ArrayList<>();
        getChartA().getAstras().forEach(a -> {
            a.setName("%s (%s)"
                    .formatted(a.getName(), a.getHeaven()));
            r.add(a);
        });
        getChartB().getAstras().forEach(a -> {
            a.setName("%s (%s)"
                    .formatted(a.getName(), a.getHeaven()));
            r.add(a);
        });

        return r;
    }
}
