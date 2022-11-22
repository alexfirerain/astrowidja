package ru.swetophor.celestialmechanics;


import lombok.Getter;
import lombok.Setter;
import ru.swetophor.ChartType;
import ru.swetophor.resogrid.Matrix;

import java.util.ArrayList;

import static ru.swetophor.Application.IDs;
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
@Getter
public class Chart {
    protected int ID;
    protected ChartType type;
    protected String name;
    protected ArrayList<Astra> astras;
    protected Matrix aspects;

    // конструктор

    Chart() {
        ID = ++IDs;
        type = ChartType.COSMOGRAM;
        setAstras(new ArrayList<>());
    }
    public Chart(String name) {
        this();
        setName(name);
    }
    // функциональность

    public void addAstra(Astra astra) {
        astra.setHeaven(this);
        astras.add(astra);
    }
    private void calculateAspectTable() {
        aspects = new Matrix(astras);
    }
    public void plotAspectTable() {
        calculateAspectTable();
        String CAPTION = """

                **************************************
                * %s: %s (№%d) *
                **************************************
                """;
        System.out.printf(CAPTION, type, name, ID);
        System.out.println(aspects.resultsOutput());
    }

    public void printAstrasList() {
        System.out.printf("%nЗодиакальныя позиции (%s):%n", name);
        astras.forEach(next -> System.out.printf(
                "%s\t %s%n", next.getName(), zodiacFormat(next.getZodiacPosition()))
        );
    }

    @Override  public String toString() {
        return "%s (%s №%d)".formatted(name, type, ID);
    }

}
