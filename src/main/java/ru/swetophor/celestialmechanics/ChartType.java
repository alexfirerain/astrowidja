package ru.swetophor.celestialmechanics;

/**
 * Какой может быть карта с точки зрения
 * отражённых в ней данных
 */
public enum ChartType {
    COSMOGRAM("Космограмма"), SYNASTRY("Синастрия"), TRANSIT("Транзит");

    public final String presentation;

    ChartType(String presentation) {
        this.presentation = presentation;

    }

    @Override
    public String toString() {
        return presentation;
    }
}
