package ru.swetophor.resogrid;

import ru.swetophor.celestialmechanics.Astra;

import java.util.ArrayList;
import java.util.List;

public class Pattern {
    List<Astra> astras = new ArrayList<>();
    List<PatternElement> entries = new ArrayList<>();
    int harmonic;

    public Pattern(int harmonic) {
        this.harmonic = harmonic;
    }

    public Pattern(int harmonic, List<Astra> astras) {
        this(harmonic);
        astras.forEach(this::addAstra);
    }

    public void addAstra(Astra astra) {
        astras.add(astra);
        entries.add(new PatternElement(astra));
        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = 1; j < entries.size(); j++) {
//                double clearance =
            }
        }
    }

    public List<Astra> getAstras() {
        return astras;
    }

    public int size() {
        return astras.size();
    }


    class PatternElement {
        Astra element;
        double totalClearance;

        public PatternElement(Astra astra) {
            element = astra;
        }
    }

}
