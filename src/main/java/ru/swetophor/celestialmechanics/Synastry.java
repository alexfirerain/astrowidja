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
        moments = new Chart[2];
    }

    protected Matrix aspects;

    /**
     * конструктор синастрии из двух карт
     */
    public Synastry(Chart chart1, Chart chart2) {
        super("Синастрия: %s и %s".formatted(chart1.name, chart2.name));
        moments[0] = chart1;
        moments[1] = chart2;
        aspects = new Matrix(chart1.getAstras(), chart2.getAstras());
    }

    public void plotAspectTable() {
        System.out.printf("""
                        **************************************
                        * %s: %s и %s (№%d) *
                        **************************************
                        """,
                type,
                moments[0].name,
                moments[1].name,
                ID);
        System.out.println(aspects.resultsOutput());
    }

    public Chart getChartA() {
        return moments[0];
    }

    public Chart getChartB() {
        return moments[1];
    }

    @Override
    public List<Astra> getAstras() {
        List<Astra> r = new ArrayList<>();
        moments[0].getAstras().forEach(a -> {
            a.setName("%s (%s)"
                    .formatted(a.getName(), a.getHeaven()));
            r.add(a);
        });
        moments[1].getAstras().forEach(a -> {
            a.setName("%s (%s)"
                    .formatted(a.getName(), a.getHeaven()));
            r.add(a);
        });

        return r;
    }

    @Override
    public String getAstrasList() {
        // TODO: сделать рядом две колонки
        return getChartA().getAstrasList() + getChartB().getAstrasList();
    }

    @Override
    public String getAspectTable() {
        String title = "%s: %s и %s (№%d)".formatted(
                type,
                moments[0].name,
                moments[1].name,
                ID);
        return getCaption() +
                "\n" +
                aspects.resultsOutput();
    }

    /**
     * @param upToHarmonic до какой
     */
    @Override
    public String resonanceAnalysis(int upToHarmonic) {
        return null;
    }

    public String getCaption() {
        return super.getCaption("%s: %s и %s (№%d)"
                .formatted(
                        type,
                        moments[0].name,
                        moments[1].name,
                        ID));
    }
}
