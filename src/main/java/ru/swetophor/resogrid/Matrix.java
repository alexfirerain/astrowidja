package ru.swetophor.resogrid;

import ru.swetophor.Settings;
import ru.swetophor.celestialmechanics.Astra;

import java.util.ArrayList;
import static ru.swetophor.celestialmechanics.Mechanics.CIRCLE;
import static ru.swetophor.resogrid.MatrixType.*;

/** * * * * * * * * * * * * * * * * * * * * * * * * *
 * Двумерная таблица, получающая два массива астр   *
 * и вычисляющая структуру резонансов               *
 * для каждой пары                                  *
 *  * * * * * * * * * * * * * * * * * * * * * * * * */
public class Matrix {
    protected ArrayList<Astra> datum1;
    protected ArrayList<Astra> datum2;
    protected static Resonance[][] resonances;
    protected int edgeHarmonic;
    protected int orbisDivider;
    protected MatrixType type;

    // методы открытаго доступа к внутренним полям
    public int getEdgeHarmonic() { return edgeHarmonic; }
    public void setEdgeHarmonic(int edgeHarmonic) { this.edgeHarmonic = edgeHarmonic; }
    public int getOrbisDivider() { return orbisDivider; }
    public void setOrbisDivider(int orbisDivider) { this.orbisDivider = orbisDivider; }

    // текстовый вывод матрицы результатов
    public void resultsOutput() {
        switch (type) {
            case SYNASTRY:                                 // таблица всех астр одной на все астры другой
                for (int i = 0; i < datum1.size(); i++)
                    for (int j = 0; j < datum2.size(); j++)
                        resonances[i][j].resonancesOutput();
                break;
            case COSMOGRAM:                               // полутаблица астр карты между собой
                for (int i = 0; i < datum1.size(); i++)
                    for (int j = i + 1; j < datum2.size(); j++)
                        resonances[i][j].resonancesOutput();
        }
    }

    /// вычисление матрицы для двух массивов (конструктор)
    // из двух массивов с заданием крайней гармоники и делителя орбиса
    public Matrix(ArrayList <Astra> array1, ArrayList<Astra> array2, int edgeHarmonic, int orbisDivider) {
        this.edgeHarmonic = edgeHarmonic;
        this.orbisDivider = orbisDivider;
        type = SYNASTRY;
        resonances = new Resonance[array1.size()][array2.size()];
        datum1 = array1;
        datum2 = array2;
        fillTheMatrix();
    }
    // из двух массивов б/доп-параметров
    public Matrix(ArrayList<Astra> массив1, ArrayList<Astra> массив2) {
        type = SYNASTRY;
        edgeHarmonic = Settings.getEdgeHarmonic();
        orbisDivider = Settings.getOrbisDivider();
        resonances = new Resonance[массив1.size()][массив2.size()];
        datum1 = массив1;
        datum2 = массив2;
        fillTheMatrix();
    }
    // из одного массива (сам на себя) б/доп-параметров
    public Matrix(ArrayList<Astra> array) {
        type = COSMOGRAM;
        edgeHarmonic = Settings.getEdgeHarmonic();
        orbisDivider = Settings.getOrbisDivider();
        resonances = new Resonance[array.size()][array.size()];
        datum1 = array;
        datum2 = array;
        fillTheMatrix();
    }

    private void fillTheMatrix(){
        for (int i = 0; i < datum1.size(); i++)
            for (int j = 0; j < datum2.size(); j++)
                resonances[i][j] = new Resonance(datum1.get(i), datum2.get(j), CIRCLE / orbisDivider, edgeHarmonic);
    }
}
