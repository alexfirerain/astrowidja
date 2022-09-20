package ru.swetophor.РезонанснаяРешётка;

import java.util.ArrayList;
import static АстроВидья.НебеснаяМеханика.Механика.*;

public class ТестПереставлятора {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList();
        list.add(2);
        list.add(5);
        for (int x : list) System.out.println(x);
        list.set(0, list.set(1, list.get(0)));
        for (int x : list) System.out.println(x);

        System.out.println(51.4 - (int) 51.4);

        ArrayList<Double> роза = new ArrayList <>();
        for (int i = 1; i < 10; i++) {
            роза.add(привестиКоординату((double) (360 / i)));
        }
        for (Double х : роза) System.out.println(секундФормат(х, true));
        System.out.println(привестиКоординату((double) 360));
    }

}
