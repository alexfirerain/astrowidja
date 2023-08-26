package ru.swetophor.mainframe;

public interface AstroSource {



    /**
     * Прочитывает карты из файла в папке базы данных на {@link Application#DESK стол} {@link Application АстроВидьи}.
     *
     * @param filename имя файла в папке базы данных.
     */
    void loadFromFile(String filename);
}
