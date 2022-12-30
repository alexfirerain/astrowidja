package ru.swetophor.celestialmechanics;

import java.util.ArrayList;
import java.util.List;

abstract class MultiChart extends ChartObject {
    List<Chart> moments = new ArrayList<>();

    public MultiChart(String name) {
        super(name);
    }
}
