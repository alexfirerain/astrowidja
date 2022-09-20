package ru.swetophor;

public class Astra {
    String id;
    double position;
    public Astra(String name, double coordinate){
        id = name;
        position = coordinate;
        while (position < 0) position += 360;
        position %= 360;
    }
    public Astra(String name, double grades, double minutes){
        id = name;
        position = grades + minutes/60;
        position %= 360;
        if (position < 0) position += 360;
    }
    public Astra(String name, double grades, double minutes, double seconds){
        id = name;
        position = grades + minutes/60 + seconds/3600;
        position %= 360;
        if (position < 0) position += 360;
    }
}
