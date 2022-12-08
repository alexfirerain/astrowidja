package ru.swetophor.celestialmechanics;


import lombok.Setter;
import ru.swetophor.ChartType;
import ru.swetophor.resogrid.Matrix;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static ru.swetophor.celestialmechanics.Mechanics.zodiacFormat;

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
                                next.getName(),
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
}
