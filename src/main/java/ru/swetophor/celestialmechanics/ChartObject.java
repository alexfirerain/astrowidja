package ru.swetophor.celestialmechanics;

import ru.swetophor.Application;
import ru.swetophor.Decorator;

import java.util.List;

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

    public int getID() {
        return this.ID;
    }

    public ChartType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setType(ChartType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract List<Astra> getAstras();

    /**
     * @return строку с перечислением зодиакальных положений астр.
     */
    public abstract String getAstrasList();
    public abstract String getAspectTable();

    protected String getCaption(String title) {
        return Decorator.frameText(title, 30, '*');
    }
}
