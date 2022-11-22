package ru.swetophor.resogrid;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.Settings;
import ru.swetophor.celestialmechanics.Astra;

import java.util.ArrayList;
import static ru.swetophor.celestialmechanics.Mechanics.CIRCLE;
import static ru.swetophor.resogrid.MatrixType.*;

/**
 * Двумерная таблица, получающая два массива астр
 * и вычисляющая структуру резонансов
 * для каждой пары
 */
@Setter
@Getter
public class Matrix {
    protected ArrayList<Astra> datum1;
    protected ArrayList<Astra> datum2;
    protected static Resonance[][] resonances;
    protected int edgeHarmonic;
    protected int orbsDivider;
    protected MatrixType type;

    public String resultsOutput() {
        StringBuilder sb = new StringBuilder();

        switch (type) {
            case SYNASTRY:                                 // таблица всех астр одной на все астры другой
                for (int i = 0; i < datum1.size(); i++) {
                    for (int j = 0; j < datum2.size(); j++) {
                        sb.append(resonances[i][j].resonancesOutput());
                        sb.append("\n");
                    }
                }
                break;
            case COSMOGRAM:                               // полутаблица астр карты между собой
                for (int i = 0; i < datum1.size(); i++) {
                    for (int j = i + 1; j < datum2.size(); j++) {
                        sb.append(resonances[i][j].resonancesOutput());
                        sb.append("\n");
                    }
                }
        }
        return sb.toString();
    }

    /// вычисление матрицы для двух массивов (конструктор)
    // из двух массивов с заданием крайней гармоники и делителя орбиса
    public Matrix(ArrayList<Astra> array1, ArrayList<Astra> array2, int edgeHarmonic, int orbsDivider) {
        this.edgeHarmonic = edgeHarmonic;
        this.orbsDivider = orbsDivider;
        type = SYNASTRY;
        resonances = new Resonance[array1.size()][array2.size()];
        datum1 = array1;
        datum2 = array2;
        fillTheMatrix();
    }
    // из двух массивов б/доп-параметров
    public Matrix(ArrayList<Astra> array_1, ArrayList<Astra> array_2) {
        type = SYNASTRY;
        edgeHarmonic = Settings.getEdgeHarmonic();
        orbsDivider = Settings.getOrbsDivider();
        resonances = new Resonance[array_1.size()][array_2.size()];
        datum1 = array_1;
        datum2 = array_2;
        fillTheMatrix();
    }
    // из одного массива (сам на себя) б/доп-параметров
    public Matrix(ArrayList<Astra> array) {
        type = COSMOGRAM;
        edgeHarmonic = Settings.getEdgeHarmonic();
        orbsDivider = Settings.getOrbsDivider();
        resonances = new Resonance[array.size()][array.size()];
        datum1 = array;
        datum2 = array;
        fillTheMatrix();
    }

    private void fillTheMatrix(){
        for (int i = 0; i < datum1.size(); i++)
            for (int j = 0; j < datum2.size(); j++)
                resonances[i][j] = new Resonance(datum1.get(i), datum2.get(j), CIRCLE / orbsDivider, edgeHarmonic);
    }
}
