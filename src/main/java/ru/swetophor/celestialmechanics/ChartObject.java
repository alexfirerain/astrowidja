package ru.swetophor.celestialmechanics;

import ru.swetophor.harmonix.Pattern;
import ru.swetophor.mainframe.Application;
import ru.swetophor.mainframe.Decorator;

import java.util.List;
import java.util.Map;

abstract public class ChartObject {
    protected int ID;
    protected ChartType type;
    protected String name;

    public ChartObject() {
        ID = ++Application.id;
    }

    public ChartObject(String name) {
        this();
        this.name = name;
    }

    /**
     * Выдаёт имя каты. Если оно длиннее указанного предела,
     * выдаёт его первые буквы и символ "…" в конце, так чтобы
     * общая длина строки была равна указанному пределу.
     *
     * @param limit максимальная длина возвращаемой строки.
     * @return имя карты, сокращённое до указанной длины.
     */
    public String getShortenedName(int limit) {
        return name.length() <= limit ?
                name :
                name.substring(0, limit - 1) + "…";
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract List<Astra> getAstras();

    public abstract String getCaption();

    /**
     * @return строку с перечислением зодиакальных положений астр.
     */
    public abstract String getAstrasList();

    public abstract String getAspectTable();

    public String getCaption(String title) {
        return Decorator.frameText(title, 30, '*');
    }

    public abstract String resonanceAnalysis(int upToHarmonic);

    public abstract String resonanceAnalysisVerbose(int upToHarmonic);

    public abstract Map<Integer, List<Pattern>> buildPatternAnalysis(int edgeHarmonic);

    public abstract String getString();

    public static ChartObject readFromString(String s) {
        return Chart.readFromString(s);
    }

    public abstract boolean resonancePresent(Astra a, Astra b, int harmonic);

    public int getID() {
        return this.ID;
    }

    public ChartType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
