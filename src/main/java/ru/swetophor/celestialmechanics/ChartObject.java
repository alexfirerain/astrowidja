package ru.swetophor.celestialmechanics;

import lombok.Setter;
import ru.swetophor.Application;

@Setter
abstract public class ChartObject {
    protected int ID;
    protected ChartType type;
    protected String name;

    public ChartObject() {
        ID = ++Application.IDs;
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
}
