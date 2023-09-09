package ru.swetophor.harmonix;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Удобная обёртка для представления результатов гармонического анализа карты.
 */
public class PatternAnalysis implements Iterable<Map.Entry<Integer, List<Pattern>>> {
    private final Map<Integer, List<Pattern>> listMap;

    public PatternAnalysis(Map<Integer, List<Pattern>> listMap) {
        this.listMap = listMap;
    }

    public PatternAnalysis(ChartObject chart, int ultimateHarmonic) {
        listMap = chart.buildPatternAnalysis(ultimateHarmonic);
    }

    public int size() {
        return listMap.size();
    }

    public List<Pattern> getPatternsForHarmonic(int id) {
        return listMap.get(id);
    }

    public Double getAverageStrengthForHarmonic(int id) {
        List<Pattern> patterns = listMap.get(id);
        return patterns == null || patterns.isEmpty() ?
                null :
                patterns.stream()
                    .mapToDouble(Pattern::getAverageStrength)
                    .sum() / patterns.size();
    }
    public Double getAverageStrengthForPattern(List<Pattern> patterns) {
        return patterns == null || patterns.isEmpty() ?
                null :
                patterns.stream()
                    .mapToDouble(Pattern::getAverageStrength)
                    .sum() / patterns.size();
    }

    public String getPatternRepresentation(List<Pattern> patterns) {
        return patterns == null || patterns.isEmpty() ?
                null :
                patterns.stream()
                    .map(Pattern::getConnectivityReport)
                    .collect(Collectors.joining());
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Map.Entry<Integer, List<Pattern>>> iterator() {
        return listMap.entrySet().iterator();
    }

    public String[] getReportFor(int i) {
        String[] data = new String[5];
        data[0] = String.valueOf(i);

        return data;
    }

}
