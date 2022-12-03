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
    /**
     * Множество астр, от которых вычисляются резонансы.
     */
    protected ArrayList<Astra> datum1;
    /**
     * Второе множество астр, к которым вычисляются резонансы от астр первого множества.
     */
    protected ArrayList<Astra> datum2;
    /**
     * Двумерный массив резонансов между астрами.
     */
    protected static Resonance[][] resonances;
    /**
     * Крайняя гармоника, до которой рассматриваются резонансы.
     */
    protected int edgeHarmonic;
    /**
     * Какова кратность круга для орбиса соединения.
     */
    protected int orbsDivider;
    /**
     * Тип матрицы.
     */
    protected MatrixType type;

    /**
     * Выдаёт строку с перечислением всех резонансов между астрами Матрицы.
     * Если тип "Космограмма", выдаются резонансы каждой точки с каждой следующей по списку.
     * Если тип "Синастрия", выдаются резонансы каждой точки с каждой точки второй карты.
     * @return строковое представление всех резонансов между уникальными точками.
     */
    public String resultsOutput() {
        StringBuilder sb = new StringBuilder();

        switch (type) {
            case SYNASTRY -> {                                 // таблица всех астр одной на все астры другой
                for (int i = 0; i < datum1.size(); i++) {
                    for (int j = 0; j < datum2.size(); j++) {
                        sb.append(resonances[i][j].resonancesOutput());
                        sb.append("\n");
                    }
                }
            }
            case COSMOGRAM -> {                               // полутаблица астр карты между собой
                for (int i = 0; i < datum1.size(); i++) {
                    for (int j = i + 1; j < datum2.size(); j++) {
                        sb.append(resonances[i][j].resonancesOutput());
                        sb.append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * вычисление матрицы для двух массивов (конструктор)
     *  с заданием крайней гармоники и делителя орбиса
     * @param array1    первый массив астр
     * @param array2    второй массив астр
     * @param edgeHarmonic  до какой максимальной гармоники проводится поиск
     * @param orbsDivider   делитель для определения начального орбиса.
     */
    public Matrix(ArrayList<Astra> array1, ArrayList<Astra> array2, int edgeHarmonic, int orbsDivider) {
        this.edgeHarmonic = edgeHarmonic;
        this.orbsDivider = orbsDivider;
        type = SYNASTRY;
        resonances = new Resonance[array1.size()][array2.size()];
        datum1 = array1;
        datum2 = array2;
        fillTheMatrix();
    }

    /**
     * вычисление матрицы из двух массивов б/доп-параметров
     * Крайняя гармоника и делитель первообраза берутся из Настроек
     * @param array_1  первый массив астр
     * @param array_2   второй массив астр
     */
    public Matrix(ArrayList<Astra> array_1, ArrayList<Astra> array_2) {
        this(array_1, array_2, Settings.getEdgeHarmonic(), Settings.getOrbsDivider());
    }

    /**
     * из одного массива (сам на себя) б/доп-параметров.
     * Крайняя гармоника и делитель орбиса берутся из Настроек.
     * @param array     массив астр.
     */
    public Matrix(ArrayList<Astra> array) {
        this(array, array);
        type = COSMOGRAM;
    }

    /**
     * Заполняет матрицу рассчитанными объектами Резонанса
     * для каждой пары астр.
     */
    private void fillTheMatrix(){
        for (int i = 0; i < datum1.size(); i++)
            for (int j = 0; j < datum2.size(); j++)
                resonances[i][j] = new Resonance(datum1.get(i),
                                                datum2.get(j),
                                                CIRCLE / orbsDivider,
                                                edgeHarmonic);
    }
}
