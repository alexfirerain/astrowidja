package ru.swetophor.harmonix;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Удобная обёртка для представления результатов гармонического анализа карты.
 */
public class PatternAnalysis {
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
        List<Pattern> patterns = listMap.get(id);
        if (patterns != null)
            patterns.sort(Comparator.comparingDouble(Pattern::getAverageStrength).reversed()); // ? надо ли здесь
        return patterns;
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


}
