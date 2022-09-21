package ru.swetophor.celestialmechanics;


import static ru.swetophor.celestialmechanics.Mechanics.зодиакФормат;
import static ru.swetophor.Application.IDs;

import ru.swetophor.resogrid.Matrix;
import ru.swetophor.ChartType;
import java.util.ArrayList;

/** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
 * Астрологическое описание момента времени,         *
 * включающее:                                       *
 *  ♦ уникальный в программе ИД                      *
 *  ♦ астрологический момент, описанный через:       *
 *   • имя, название момента                          *
 *   • массив астр                                    *
 *   • матрицу резонансов                             *
 ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **/
public class Chart {
    int ID;
    ChartType type;
    String name;
    public void setName(String имя) { this.name = имя; }
    public String getName() { return name; }
    @Override  public String toString() {
        return name + " (" + type + " №" + ID + ")";
    }
    private ArrayList<Astra> chartsAstras;
    Matrix chartsAspects;

    // конструктор
    Chart() {
        IDs++;
        ID = IDs;
        type = ChartType.COSMOGRAM;
        chartsAstrasSetting(new ArrayList<>());
    }
    public Chart(String name) {
        this();
        setName(name);
    }

    // функциональность
    public void addAstra(Astra astra) {
        astra.setHeaven(this);
        chartsAstras.add(astra);
    }
    private void calculateAspectTable() {
        chartsAspects = new Matrix(chartsAstras);
    }
    public void plotAspectTable() {
        calculateAspectTable();
        System.out.println("\n**************************************");
        System.out.printf("* %s: %s (№%d) *", type, name, ID);
        System.out.println("\n**************************************");
        chartsAspects.resultsOutput();
    }

    public ArrayList<Astra> chartsAstrasOutput() {
        return chartsAstras;
    }

    public void списокАстрКарты() {
        System.out.println("\nЗодиакальныя позиции (" + name + "):");
        for (Astra очердная : chartsAstras) {
            System.out.println(очердная.getName() + "\t " + зодиакФормат(очердная.getZodiacPosition()));
        }
    }

    public void chartsAstrasSetting(ArrayList<Astra> наборАстр) {
        chartsAstras = наборАстр;
    }
}
