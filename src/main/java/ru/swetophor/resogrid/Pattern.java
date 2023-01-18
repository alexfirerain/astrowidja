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
        PatternElement added = new PatternElement(astra);
        for (PatternElement a : entries) {
            Resonance r = astra.getHeaven().getAspects().findResonance(astra, a.element);
            if (r.hasHarmonicPattern(harmonic)) {

            }
        }
        entries.add(added);
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
