package ru.swetophor.celestialmechanics;

import java.util.ArrayList;

class MultiChart extends Chart {
    ArrayList<Chart> moments;
    private MultiChart() {
        super();
        moments = new ArrayList<>();
    }

    MultiChart(String chartTitle) {
        this();
        setName(chartTitle);
    }
}
