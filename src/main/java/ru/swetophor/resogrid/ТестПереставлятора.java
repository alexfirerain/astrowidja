package ru.swetophor.resogrid;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.celestialmechanics.Mechanics.normalizeCoordinate;
import static ru.swetophor.celestialmechanics.Mechanics.secondFormat;

public class ТестПереставлятора {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(5);

        list.forEach(System.out::println);

        list.set(0, list.set(1, list.get(0)));

        list.forEach(System.out::println);

        System.out.println(51.4 - (int) 51.4);

        ArrayList<Double> rose = IntStream.range(1, 10)
                .mapToObj(i -> normalizeCoordinate(360. / i))
                .collect(Collectors.toCollection(ArrayList::new));

        rose.stream()
                .map(x -> secondFormat(x, true))
                .forEach(System.out::println);

        System.out.println(normalizeCoordinate(360));



    }

}
