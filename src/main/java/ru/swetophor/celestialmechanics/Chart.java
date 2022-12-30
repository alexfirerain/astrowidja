package ru.swetophor.celestialmechanics;


import lombok.Setter;
import ru.swetophor.ChartType;
import ru.swetophor.resogrid.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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

    protected List<Astra> astras = new ArrayList<>();

    {
        type = ChartType.COSMOGRAM;
    }
    protected Matrix aspects;

    public Chart(String name) {
        super(name);
    }

    // конструктор

    public static Chart readFromString(String input) {
        String[] lines = input.lines().toArray(String[]::new);
        if (lines.length == 0)
            throw new IllegalArgumentException("текст не содержит строк");

        Chart newChart = new Chart(lines[0]);
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

    /**
     * Добавляет в карту астру. При этом если с таким именем астра
     * уже присутствует, она обновляется, заменяется.
     * @param astra добавляемая астра.
     */
    public void addAstra(Astra astra) {
        astra.setHeaven(this);
        for (int i = 0; i < astras.size(); i++)
            if (astras.get(i).getName().equals(astra.getName())) {
                astras.set(i, astra);
                return;
            }
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
        StringBuilder list = new StringBuilder("%nЗодиакальные позиции (%s):%n".formatted(name));
        astras.forEach(next -> list.append(
                "%s\t %s%n".formatted(
                                next.getNameWithZodiacDegree(),
                                zodiacFormat(next.getZodiacPosition())
                        )
                )
        );
        return list.toString();
    }

    @Override
    public String toString() {
        return "%s (%s №%d)".formatted(name, type, ID);
    }

    public List<Astra> getAstras() {
        return this.astras;
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
                Astra compositeAstra = new Astra(astra.getName(),
                        findMedian(astra.getZodiacPosition(),
                                counterpart.getZodiacPosition()));
                composite.addAstra(compositeAstra);
                AstraEntity innerBody = AstraEntity.getEntityByName(compositeAstra.getName());
                if (innerBody != null) {
                    switch (innerBody) {
                        case SOL -> sun = compositeAstra;
                        case MER -> mercury = compositeAstra;
                        case VEN -> venus = compositeAstra;
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
     *
     * @param name имя астры, которую запрашивают.
     * @return астру с таким именем, если она есть в карте, иначе {@code пусто}.
     */
    public Astra getAstra(String name) {
        for (Astra a : astras)
            if (a.getName().equals(name))
                return a;
        return null;
    }

}


