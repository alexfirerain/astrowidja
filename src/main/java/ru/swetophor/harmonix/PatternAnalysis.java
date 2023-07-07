package ru.swetophor.harmonix;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PatternAnalysis {
    private final Map<Integer, List<Pattern>> listMap;

    public PatternAnalysis(Map<Integer, List<Pattern>> listMap) {
        this.listMap = listMap;
    }

    public int size() {
        return listMap.size();
    }

    public List<Pattern> getPatternsForHarmonic(int id) {
        List<Pattern> patterns = listMap.get(id);
        patterns.sort(Comparator.comparingDouble(Pattern::getAverageStrength).reversed());
        return patterns;
    }

}
