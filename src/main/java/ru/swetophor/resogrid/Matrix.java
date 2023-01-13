package ru.swetophor.resogrid;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.Settings;
import ru.swetophor.celestialmechanics.Astra;

import java.util.Arrays;
import java.util.List;

import static ru.swetophor.celestialmechanics.Mechanics.CIRCLE;
import static ru.swetophor.resogrid.MatrixType.COSMOGRAM;
import static ru.swetophor.resogrid.MatrixType.SYNASTRY;

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
    protected Astra[] datum1;
    /**
     * Второе множество астр, к которым вычисляются резонансы от астр первого множества.
     */
    protected Astra[] datum2;
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
     * Если тип "Синастрия", выдаются резонансы каждой точки с каждой точкой второй карты.
     * @return строковое представление всех резонансов между уникальными точками.
     */
    public String resultsOutput() {
        StringBuilder sb = new StringBuilder();

        switch (type) {
            case SYNASTRY -> {                                 // таблица всех астр одной на все астры другой
                for (int x = 0; x < datum1.length; x++)
                    for (int y = 0; y < datum2.length; y++)
                        sb.append(resonances[x][y].resonancesOutput()).append("\n");
            }
            case COSMOGRAM -> {                               // полутаблица астр карты между собой
                for (int i = 0; i < datum1.length; i++)
                    for (int j = i + 1; j < datum2.length; j++)
                        sb.append(resonances[i][j].resonancesOutput()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * вычисление матрицы для двух массивов (конструктор)
     *  с заданием крайней гармоники и делителя орбиса
     * @param astras_1    первый массив астр
     * @param astras_2    второй массив астр
     * @param edgeHarmonic  до какой максимальной гармоники проводится поиск
     * @param orbsDivider   делитель для определения начального орбиса.
     */
    public Matrix(List<Astra> astras_1, List<Astra> astras_2, int edgeHarmonic, int orbsDivider) {
        this.edgeHarmonic = edgeHarmonic;
        this.orbsDivider = orbsDivider;
        datum1 = astras_1.toArray(Astra[]::new);
        datum2 = astras_2.toArray(Astra[]::new);
        type = Arrays.equals(datum1, datum2) ? COSMOGRAM : SYNASTRY;
        resonances = new Resonance[astras_1.size()][astras_2.size()];
        for (int i = 0; i < datum1.length; i++)
            for (int j = 0; j < datum2.length; j++)
                resonances[i][j] = new Resonance(datum1[i],
                                                datum2[j],
                                                CIRCLE / this.orbsDivider,
                                                this.edgeHarmonic);
    }

    /**
     * вычисление матрицы из двух массивов б/доп-параметров
     * Крайняя гармоника и делитель первообраза берутся из Настроек
     * @param astras_1  первый массив астр
     * @param astras_2   второй массив астр
     */
    public Matrix(List<Astra> astras_1, List<Astra> astras_2) {
        this(astras_1, astras_2, Settings.getEdgeHarmonic(), Settings.getOrbsDivider());
    }

    /**
     * из одного массива (сам на себя) б/доп-параметров.
     * Крайняя гармоника и делитель орбиса берутся из Настроек.
     * @param astras     массив астр.
     */
    public Matrix(List<Astra> astras) {
        this(astras, astras);
    }

}
