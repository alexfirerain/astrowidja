package ru.swetophor.celestialmechanics;


import lombok.Setter;
import ru.swetophor.ChartType;
import ru.swetophor.resogrid.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static ru.swetophor.celestialmechanics.AstraEntity.MER;
import static ru.swetophor.celestialmechanics.AstraEntity.SOL;
import static ru.swetophor.celestialmechanics.Mechanics.*;

/**
 * Астрологическое описание момента времени,
 * включающее:<br/>
 *  ♦ уникальный в программе ИД<br/>
 *  ♦ астрологический момент, описанный через:<br/>
     *   • имя, название момента<br/>
     *   • массив астр<br/>
     *   • матрицу резонансов
*/
@Setter
public class Chart extends ChartObject {

    protected ArrayList<Astra> astras = new ArrayList<>();

    protected final ChartType type = ChartType.COSMOGRAM;
    protected Matrix aspects;

    public Chart(String name) {
        super(name);
    }

    // конструктор

    public static Chart readFromString(String input) {
        String[] lines = input.lines().toArray(String[]::new);
        Chart newChart;
        if (lines.length == 0)
            throw new IllegalArgumentException("текст не содержит строк");

        newChart = new Chart(lines[0]);
        IntStream.range(1, lines.length)
                .mapToObj(i -> Astra.readFromString(lines[i]))
                .forEach(newChart::addAstra);


        return newChart;
    }


    // функциональность

    public void attachAstra(Astra astra) {
        addAstra(astra);
        calculateAspectTable();
    }

    private void addAstra(Astra astra) {
        astra.setHeaven(this);
        IntStream.range(0, astras.size())
                .filter(i -> astras.get(i).getName().equals(astra.getName()))
                .forEach(i -> astras.set(i, astra));
        astras.add(astra);
    }

    private void calculateAspectTable() {
        aspects = new Matrix(astras);
    }
    public String getAspectTable() {
        calculateAspectTable();
        return """

                **************************************
                * %s: %s (№%d) *
                **************************************
                """.formatted(type, name, ID)
                + aspects.resultsOutput();
    }


    public String getAstrasList() {
        StringBuilder list = new StringBuilder("%nЗодиакальныя позиции (%s):%n".formatted(name));
        astras.forEach(next -> list.append(
                "%s\t %s%n".formatted(
                                next.getNameWithZodiacDegree(),
                                zodiacFormat(next.getZodiacPosition())
                        )
                )
        );
        return list.toString();
    }

    @Override  public String toString() {
        return "%s (%s №%d)".formatted(name, type, ID);
    }

    public ArrayList<Astra> getAstras() {
        return this.astras;
    }

    public ChartType getType() {
        return this.type;
    }

    public Matrix getAspects() {
        return this.aspects;
    }

    public double getAstraPosition(String name) {
        for (Astra a : astras)
            if (a.getName().equals(name))
                return a.getZodiacPosition();
        throw new IllegalArgumentException("Astra " + name + " not found.");
    }

    public static Chart composite(Chart chart_a, Chart chart_b) {
        if (chart_a == null || chart_b == null)
            throw new IllegalArgumentException("карта для композита не найдена");
        Chart composite = new Chart("Средняя карта %s и %s"
                .formatted(chart_a.getName(), chart_b.getName()));

        Astra sun = null, mercury = null, venus = null;
        for (Astra astra : chart_a.getAstras()) {
            Astra counterpart = chart_b.getAstra(astra.getName());
            if (counterpart != null) {
                composite.addAstra(new Astra(astra.getName(),
                                            findMedian(astra.getZodiacPosition(),
                                                    counterpart.getZodiacPosition())));
                AstraEntity innerBody = AstraEntity.getEntityByName(astra.getName());
                if (innerBody != null) {
                    switch (innerBody) {
                        case SOL -> sun = astra;
                        case MER -> mercury = astra;
                        case VEN -> venus = astra;
                    }
                }
            }
        }

        if (sun != null) {
            if (mercury != null &&
                    getArc(sun.getZodiacPosition(), mercury.getZodiacPosition()) > 30.0) {

                    mercury.setZodiacPosition(normalizeCoordinate(mercury.getZodiacPosition() + CIRCLE / 2));
                    composite.addAstra(mercury);
            }
            if (venus != null &&
                    getArc(sun.getZodiacPosition(), venus.getZodiacPosition()) > 60.0) {

                    venus.setZodiacPosition(normalizeCoordinate(venus.getZodiacPosition() + CIRCLE / 2));
                    composite.addAstra(venus);
            }
        }

        return composite;
    }

    /**
     * Отдаёт астру карты по ея имени.
     * @param name имя астры, которую запрашивают.
     * @return  астру с таким именем, если она есть в карте, иначе {@code пусто}.
     */
    private Astra getAstra(String name) {
        for (Astra a : astras)
            if (a.getName().equals(name))
                return a;
        return null;
    }
}
