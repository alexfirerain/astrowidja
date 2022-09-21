package ru.swetophor.celestialmechanics;

import ru.swetophor.resogrid.Matrix;
import ru.swetophor.ChartType;

public class Synastry extends MultiChart {

    @Override public String toString() { return "%s (карта №%d)".formatted(name, ID); }

    // конструктор синастрии из двух карт
    public Synastry(Chart chart1, Chart chart2) {
        super("Синастрия: %s и %s".formatted(chart1.name, chart2.name));
        moments.add(chart1);
        moments.add(chart2);
        type = ChartType.SYNASTRY;
        calculateAspectTable();
    }

    private void calculateAspectTable() {
        chartsAspects = new Matrix(moments.get(0).chartsAstrasOutput(),
                                        moments.get(1).chartsAstrasOutput());
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
        calculateAspectTable();
        chartsAspects.resultsOutput();
    }


}
