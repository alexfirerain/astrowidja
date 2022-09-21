package ru.swetophor;

import java.util.LinkedList;

public class ResonanceOld {
    final private AstraOld a, b;
    private double arc;

    public void printArc() {
        System.out.printf("The arc between %s and %s is %.2f° %n", a.id, b.id, arc);
    }

    private static class Resound {
        int cifer;
        double clearance;
    }
    private LinkedList<Resound> resounds;

    public ResonanceOld(AstraOld a, AstraOld b){
        this.a = a;
        this.b = b;
        arc = Math.abs(a.position - b.position);
        if (arc > 180) arc = 360 - arc;
    }
}
