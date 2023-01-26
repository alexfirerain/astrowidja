package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static ru.swetophor.mainframe.Application.keyboard;

/**
 * Список карт-объектов, воспроизводящий многие функции обычного списка.
 * Список поддерживает уникальность имён содержащихся карт и обращение
 * к картам по имени карты.
 */
public class ChartList {
    /**
     * Карты, хранимые в списке.
     */
    private final List<ChartObject> charts = new ArrayList<>();
    /**
     * Имена хранимых в списке карт.
     */
    private final List<String> names = new ArrayList<>();
    protected transient int modCount = 0;

    /**
     * Содержит ли указанный список указанное имя карты.
     *
     * @param content   список, в котором проверяем.
     * @param chartName проверяемое имя.
     * @return есть ли карта с таким именем в этом списке.
     */
    public static boolean containsName(List<? extends ChartObject> content, String chartName) {
        return content.stream()
                .anyMatch(c -> c.getName()
                        .equals(chartName));
    }

    /**
     * Содержит ли указанный список карт указанное имя карты.
     *
     * @param content   список карт, в котором проверяем.
     * @param chartName проверяемое имя.
     * @return есть ли карта с таким именем в этом списке.
     */
    public static boolean containsName(ChartList content, String chartName) {
        return content.getNames().contains(chartName);
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>заменить – удаляет из списка карту с конфликтным именем, возвращает добавляемую;</li>
     * <li>переименовать – запрашивает новое имя для добавляемой карты, обновляет её и возвращает;</li>
     * <li>отмена – возвращает {@code null}</li>
     * <p>
     * Таким образом, возращаемое функцией значение соответствует той карте, которую
     * следует слудующим шагом добавить в целевой список.
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в целевом списке.
     * @param list          список, куда должны добавляться карты с уникальными именами.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     * @return ту же карту, если выбрано "заменить старую карту",
     * ту же карту с новым именем, если выбрано "переименовать новую карту",
     * или {@code пусто}, если выбрано "отменить операцию".
     */
    public static ChartObject resolveCollision(ChartObject controversial, List<ChartObject> list, String listName) {
        boolean fixed = false;
        while (!fixed) {
            System.out.printf("""
                                            
                    Карта с именем %s уже есть:
                    1. заменить присутствующую в %s
                    2. добавить под новым именем
                    0. отмена
                    """, controversial.getName(), listName);
            switch (keyboard.nextLine()) {
                case "1" -> {
                    list.stream()
                            .filter(c -> c.getName()
                                    .equals(controversial.getName()))
                            .findFirst()
                            .ifPresent(list::remove);
                    fixed = true;
                }
                case "2" -> {
                    String name;
                    do {
                        System.out.print("Новое имя: ");
                        name = keyboard.nextLine();         // TODO: допустимое имя
                        System.out.println();
                    } while (containsName(list, name));
                    controversial.setName(name);
                    fixed = true;
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    return null;
                }
            }
        }
        return controversial;
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>заменить – удаляет из списка карту с конфликтным именем, добавляет новую;</li>
     * <li>переименовать – запрашивает новое имя для добавляемой карты и добавляет обновлённую;</li>
     * <li>отмена – карта не добавляется.</li>
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в целевом списке.
     * @param list          список, куда должны добавляться карты с уникальными именами.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     * @return {@code true}, если карта (с оригинальным либо обновлённым именем) добавлена в список.
     */
    public static boolean mergeResolving(ChartObject controversial, List<ChartObject> list, String listName) {
        while (true) {
            System.out.printf("""
                                                    
                            Карта с именем '%s' уже есть:
                            1. заменить присутствующую %s
                            2. добавить под новым именем
                            0. отмена
                            """, controversial.getName(),
                    listName.startsWith("на ") ?
                            listName :
                            "в " + listName);
            switch (keyboard.nextLine()) {
                case "1" -> {
                    list.stream()
                            .filter(c -> c.getName()
                                    .equals(controversial.getName()))
                            .findFirst()
                            .ifPresent(list::remove);
                    return list.add(controversial);
                }
                case "2" -> {
                    String name;
                    do {
                        System.out.print("Новое имя: ");
                        name = keyboard.nextLine();         // TODO: допустимое имя
                        System.out.println();
                    } while (containsName(list, name));
                    controversial.setName(name);
                    return list.add(controversial);
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    return false;
                }
            }
        }
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>заменить – удаляет из списка карту с конфликтным именем, добавляет новую;</li>
     * <li>переименовать – запрашивает новое имя для добавляемой карты и добавляет обновлённую;</li>
     * <li>отмена – карта не добавляется.</li>
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в целевом списке.
     * @param list          список, куда должны добавляться карты с уникальными именами.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     */
    public static void mergeResolving(ChartObject controversial, ChartList list, String listName) {
        while (true) {
            System.out.printf("""
                                                    
                            Карта с именем '%s' уже есть:
                            1. заменить присутствующую %s
                            2. добавить под новым именем
                            0. отмена
                            """, controversial.getName(),
                    listName.startsWith("на ") ?
                            listName :
                            "в " + listName);
            switch (keyboard.nextLine()) {
                case "1" -> {
                    list.remove(controversial.getName());
                    list.addItem(controversial);
                }
                case "2" -> {
                    String rename;
                    do {
                        System.out.print("Новое имя: ");
                        rename = keyboard.nextLine();         // TODO: допустимое имя
                        System.out.println();
                    } while (list.contains(rename));
                    controversial.setName(rename);
                    list.addItem(controversial);
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                }
            }
        }
    }

    public static List<ChartObject> mergeList(List<ChartObject> addingCharts, List<ChartObject> mergingList, String listName) {
        if (addingCharts == null || addingCharts.isEmpty())
            return mergingList;
        if (mergingList == null || mergingList.isEmpty())
            return addingCharts;
        for (ChartObject adding : addingCharts)
            if (containsName(mergingList, adding.getName()))
                mergeResolving(adding, mergingList, listName);
            else
                mergingList.add(adding);
        return mergingList;
    }

    /**
     * Вливает в старый список содержимое нового списка.
     * Если какая-то из добавляемых карт имеет имя, которое уже содержится в списке,
     * запускается интерактивная процедура разрешения коллизии.
     *
     * @param addingCharts список добавляемых карт.
     * @param mergingList  список карт, в который добавляется.
     * @param listName     имя списка, в который добавляется, в предложном падеже
     *                     (с предлогом "на", если он предпочтительнее предлога "в").
     * @return старый список, в который добавлены карты из нового списка и из которого,
     * если пользователем выбирался такой вариант, удалены старые карты с совпавшими именами.
     */
    public static ChartList mergeList(ChartList addingCharts, ChartList mergingList, String listName) {
        if (addingCharts == null || addingCharts.isEmpty())
            return mergingList;
        if (mergingList == null || mergingList.isEmpty())
            return addingCharts;
        for (ChartObject adding : addingCharts.getCharts())
            if (mergingList.contains(adding.getName()))
                mergingList.mergeResolving(adding, listName);
            else
                mergingList.addItem(adding);
        return mergingList;
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>заменить – удаляет из списка карту с конфликтным именем, добавляет новую;</li>
     * <li>переименовать – запрашивает новое имя для добавляемой карты и добавляет обновлённую;</li>
     * <li>отмена – карта не добавляется.</li>
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в этом списке.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     */
    public void mergeResolving(ChartObject controversial, String listName) {
        while (true) {
            System.out.printf("""
                                                    
                            Карта с именем '%s' уже есть:
                            1. заменить присутствующую %s
                            2. добавить под новым именем
                            0. отмена
                            """, controversial.getName(),
                    listName.startsWith("на ") ?
                            listName :
                            "в " + listName);
            switch (keyboard.nextLine()) {
                case "1" -> {
                    remove(controversial.getName());
                    addItem(controversial);
                    return;
                }
                case "2" -> {
                    String rename;
                    do {
                        System.out.print("Новое имя: ");
                        rename = keyboard.nextLine();         // TODO: допустимое имя
                        System.out.println();
                    } while (contains(rename));
                    controversial.setName(rename);
                    addItem(controversial);
                    return;
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    return;
                }
            }
        }
    }

    /**
     * @return количество карт-объектов в этом списке.
     */
    public int size() {
        return charts.size();
    }

    /**
     * @return является ли этот список пустым.
     */
    public boolean isEmpty() {
        return charts.isEmpty();
    }


    /**
     * @return
     */
    public Iterator iterator() {
        return null;
    }

    /**
     * @param action действие, которое будет последовательно применено ко всем картам.
     */
    public void forEach(Consumer<? super ChartObject> action) {
        Objects.requireNonNull(action);
        int expectedModCount = this.modCount;
        int size = size();

        for (int i = 0; this.modCount == expectedModCount && i < size; ++i)
            action.accept(charts.get(i));

        if (this.modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    /**
     * @return
     */
    public ChartObject[] toArray() {
        return charts.toArray(ChartObject[]::new);
    }


    /**
     * @param c
     */
    @Override
    public void sort(Comparator<? super ChartObject> c) {
        List.super.sort(c);
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean addAll(Collection collection) {
        return false;
    }

    /**
     * @param i
     * @param collection
     * @return
     */
    @Override
    public boolean addAll(int i, Collection collection) {
        return false;
    }

    /**
     *
     */
    @Override
    public void clear() {

    }

    /**
     * @param i
     * @return
     */
    @Override
    public Object get(int i) {
        return null;
    }

    /**
     * @param i
     * @param chartObject
     * @return
     */
    public ChartObject set(int i, ChartObject chartObject) {
        return null;
    }

    /**
     * @param i
     * @param chartObject
     */
    @Override
    public void add(int i, ChartObject chartObject) {

    }

    /**
     * @param i
     * @param o
     * @return
     */
    @Override
    public Object set(int i, Object o) {
        return null;
    }

    /**
     * @param i
     * @param o
     */
    @Override
    public void add(int i, Object o) {

    }

    /**
     * @param i
     * @return
     */
    @Override
    public Object remove(int i) {
        return null;
    }

    /**
     * @param o
     * @return
     */
    @Override
    public int indexOf(Object o) {
        return 0;
    }

    /**
     * @param o
     * @return
     */
    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public ListIterator listIterator() {
        return null;
    }

    /**
     * @param i
     * @return
     */
    @Override
    public ListIterator listIterator(int i) {
        return null;
    }

    /**
     * @param i
     * @param i1
     * @return
     */
    @Override
    public List subList(int i, int i1) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public Spliterator<ChartObject> spliterator() {
        return List.super.spliterator();
    }

    /**
     * @return
     */
    @Override
    public Stream<ChartObject> stream() {
        return List.super.stream();
    }

    /**
     * @return
     */
    @Override
    public Stream<ChartObject> parallelStream() {
        return List.super.parallelStream();
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean retainAll(Collection collection) {
        return false;
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean removeAll(Collection collection) {
        return false;
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean containsAll(Collection collection) {
        return false;
    }

    /**
     * @param objects
     * @return
     */
    @Override
    public Object[] toArray(Object[] objects) {
        return new Object[0];
    }

    public boolean add(ChartObject chart) {
        return false;
    }

    public void addItem(ChartObject chart) {
        charts.add(chart);
        names.add(chart.getName());
    }

    public ChartObject getChart(int i) {
        return i >= 0 && i < charts.size() ?
                charts.get(i) :
                null;
    }

    public ChartObject getChart(String name) {
        return getChart(names.indexOf(name));
    }

    public List<String> getNames() {
        return names;
    }

    public List<ChartObject> getCharts() {
        return charts;
    }

    public boolean contains(String name) {
        return names.contains(name);
    }

    public boolean contains(ChartObject chart) {
        return charts.contains(chart);
    }

    public void remove(String name) {
        int index = names.indexOf(name);
        charts.remove(index);
        names.remove(index);
    }

    public void remove(ChartObject item) {
        int index = charts.indexOf(item);
        names.remove(index);
        charts.remove(index);
    }

}
