package ru.swetophor.celestialmechanics;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.Application;
import ru.swetophor.ChartType;

@Setter
@Getter
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
}
