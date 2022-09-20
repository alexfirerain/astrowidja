package ru.swetophor.celestialmechanics;


import static ru.swetophor.celestialmechanics.Mechanics.зодиакФормат;
import static ru.swetophor.Application.ИДы;

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
    private ArrayList<Astra> астрыКарты;
    Matrix chartsAspects;

    // конструктор
    Chart() {
        ИДы++;
        ID = ИДы;
        type = ChartType.COSMOGRAMME;
        установкаАстрКарты(new ArrayList<>());
    }
    public Chart(String name) {
        this();
        setName(name);
    }

    // функциональность
    public void addAstra(Astra astra) {
        astra.heaven = this;
        chartsAstrasOutput().add(astra);
    }
    private void calculateAspectTable() {
        chartsAspects = new Matrix(chartsAstrasOutput());
    }
    public void plotAspectTable() {
        calculateAspectTable();
        System.out.println("\n**************************************");
        System.out.printf("* %s: %s (№%d) *", type, name, ID);
        System.out.println("\n**************************************");
        chartsAspects.resultsOutput();
    }

    public ArrayList<Astra> chartsAstrasOutput() {
        return астрыКарты;
    }

    public void списокАстрКарты() {
        System.out.println("\nЗодиакальныя позиции (" + name + "):");
        for (Astra очердная : астрыКарты) {
            System.out.println(очердная.name + "\t " + зодиакФормат(очердная.zodiacPosition));
        }
    }

    public void установкаАстрКарты(ArrayList<Astra> наборАстр) {
        астрыКарты = наборАстр;
    }
}
