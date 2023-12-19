package ru.swetophor.celestialmechanics;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.harmonix.Matrix;
import ru.swetophor.harmonix.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Matrix getOwnMatrix(Astra a) {
        return a.getHeaven().getAspects();
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
        return "краткий анализ паттернов резонансов в синастрии";
    }

    /**
     * @param upToHarmonic до какой гармоники считать.
     * @return развёрнутый отчёт по паттернам.
     */
    @Override
    public String resonanceAnalysisVerbose(int upToHarmonic) {
        return "подробный анализ паттернов резонансов в синастрии";
    }

    /**
     * @param edgeHarmonic
     * @return
     */
    @Override
    public Map<Integer, List<Pattern>> buildPatternAnalysis(int edgeHarmonic) {
        return null;
    }

    /**
     * @return для сохранения
     */
    @Override
    public String getString() {
        return "представление карты";
    }

    /**
     * @param a 
     * @param b
     * @param harmonic
     * @return
     */
    @Override
    public boolean resonancePresent(Astra a, Astra b, int harmonic) {
        if (!hasChart(a.getHeaven()) || !hasChart(b.getHeaven()) || harmonic < 1)
            throw new IllegalArgumentException("астры не из тех карт или не положительная гармоника");
        if (Astra.ofSameHeaven(a, b))
            return a.getHeaven().resonancePresent(a, b, harmonic);
        else
            return aspects.resonancePresent(a, b, harmonic);
    }

    public boolean hasChart(Chart chart) {
        return chart != null && (chart == moments[0] || chart == moments[1]);
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
