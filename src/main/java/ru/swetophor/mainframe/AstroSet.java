package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.AstraEntity;

public class AstroSet {

    private AstraEntity[] astras;
    public AstroSet(AstraEntity[] values) {
        astras = values;
    }

    public AstraEntity[] getAstras() {
        return astras;
    }

    public void setAstras(AstraEntity[] astras) {
        this.astras = astras;
    }
}
