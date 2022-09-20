package ru.swetophor.НебеснаяМеханика;

import java.util.ArrayList;

class МногоКарта extends Карта {
    ArrayList<Карта> моменты;
    private МногоКарта() {
        super();
        моменты = new ArrayList<>();
    }

    МногоКарта(String названиеКарты) {
        this();
        установкаИмени(названиеКарты);
    }
}
