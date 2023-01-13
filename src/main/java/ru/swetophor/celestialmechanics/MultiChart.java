package ru.swetophor.celestialmechanics;

import java.util.ArrayList;
import java.util.List;

abstract class MultiChart extends ChartObject {
    Chart[] moments;

    public MultiChart(String name) {
        super(name);
    }
}
