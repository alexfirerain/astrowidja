package ru.swetophor.celestialmechanics;

import java.util.ArrayList;

abstract class MultiChart extends ChartObject {
    ArrayList<Chart> moments = new ArrayList<>();

    public MultiChart(String name) {
        super(name);
    }
}
