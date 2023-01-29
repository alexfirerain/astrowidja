package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;
import ru.swetophor.celestialmechanics.ChartType;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    public ChartList() {
    }

    public ChartList(List<ChartObject> charts) {
        this();
        addAll(charts);
    }

    @Override
    public String toString() {
        return IntStream.range(0, size())
                .mapToObj(i -> "%d. %s%n"
                        .formatted(i + 1, get(i).toString()))
                .collect(Collectors.joining());
    }

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
                    return;
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
                    return;
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    return;
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
     * <li>переименовать – запрашивает новое имя для добавляемой карты и добавляет обновлённую;</li>
     * <li>обновить – ставит новую карту на место старой карты с этим именем;</li>
     * <li>заменить – удаляет из списка карту с конфликтным именем, добавляет новую;</li>
     * <li>отмена – карта не добавляется.</li>
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в этом списке.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     * @return
     */
    public boolean mergeResolving(ChartObject controversial, String listName) {
        while (true) {
            System.out.printf("""
                                                    
                            Карта с именем '%s' уже есть %s:
                            1. добавить под новым именем
                            2. заменить присутствующую в списке
                            3. удалить старую, добавить новую в конец списка
                            0. отмена
                            """, controversial.getName(),
                    listName.startsWith("на ") ?
                            listName :
                            "в " + listName);
            switch (keyboard.nextLine()) {
                case "1" -> {
                    String rename;
                    do {
                        System.out.print("Новое имя: ");
                        rename = keyboard.nextLine();         // TODO: допустимое имя
                        System.out.println();
                    } while (contains(rename));
                    controversial.setName(rename);
                    return addItem(controversial);
                }
                case "2" -> {
                    setItem(indexOf(controversial.getName()), controversial);
                    return true;
                }
                case "3" -> {
                    remove(controversial.getName());
                    return addItem(controversial);
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    return false;
                }
            }
        }
    }

    public boolean addResolving(ChartObject chart, String toListName) {
        return contains(chart.getName()) ?
                mergeResolving(chart, toListName) :
                addItem(chart);
    }

    public String getString() {
        return charts.stream()
                .filter(chart -> chart.getType() == ChartType.COSMOGRAM)
                .map(ChartObject::getString)
                .collect(Collectors.joining());
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
     * Применяет по конвейеру указанное действие последовательно ко всем картам,
     * контролируя, что список карт не изменился в его ходе.
     *
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
     * Добавляет в список указанную карту.
     * Если карта с таким именем уже в наличии, запускает интерактивную процедуру разрешения
     * конфликта {@link #mergeResolving(ChartObject, String)} (с названием списка "этот список").
     *
     * @param chart
     * @return
     */
    public boolean add(ChartObject chart) {
        int mod = this.modCount;
        if (contains(chart.getName()))
            mergeResolving(chart, "этом списке");
        else
            addItem(chart);
        return this.modCount != mod;
    }

    /**
     * @param collection
     * @return
     */
    public boolean addAll(Collection<ChartObject> collection) {
        int mod = this.modCount;
        collection.forEach(this::add);
        return this.modCount != mod;
    }

    /**
     * @param i
     * @param collection
     * @return
     */
    public boolean addAll(int i, Collection<ChartObject> collection) {
        ++this.modCount;
        names.addAll(i, collection.stream().map(ChartObject::getName).toList());
        return charts.addAll(i, collection);
    }

    /**
     * Сортирует карты сообразно полученному компаратору,
     * затем перезаполняет список имён в соответствие с обновлённым
     * списком карт.
     *
     * @param c компаратор для внесения порядка в список карт.
     */
    public void sort(Comparator<? super ChartObject> c) {
        charts.sort(c);
        names.clear();
        names.addAll(chartsToNames(charts));
        ++this.modCount;
    }

    /**
     * @return массив с содержащимися картами.
     */
    public ChartObject[] toArray() {
        return charts.toArray(ChartObject[]::new);
    }

    /**
     * Опустошает список карт (и имён).
     */
    public void clear() {
        charts.clear();
        names.clear();
        ++this.modCount;
    }

    /**
     * @param i
     * @param chartObject
     * @return
     */
    public ChartObject setItem(int i, ChartObject chartObject) {
//        if (contains(chartObject.getName()))
//            chartObject = resolveCollision(chartObject, charts, "этом списке'");
        names.set(i, chartObject.getName());
        ++this.modCount;
        return charts.set(i, chartObject);
    }

    /**
     * @param i
     * @param chartObject
     */
    public void insertItem(int i, ChartObject chartObject) {
        charts.add(i, chartObject);
        names.add(i, chartObject.getName());
    }

    /**
     * @param i
     * @return
     */
    public ChartObject remove(int i) {
        names.remove(i);
        ++this.modCount;
        return charts.remove(i);
    }

    /**
     * @param name
     * @return
     */
    public int indexOf(String name) {
        return names.indexOf(name);
    }
    /**
     * @param o
     * @return
     */
    public int indexOf(ChartObject o) {
        return charts.indexOf(o);
    }

    /**
     * @param name
     * @return
     */
    public int lastIndexOf(String name) {
        return names.lastIndexOf(name);
    }

    /**
     * @param o
     * @return
     */
    public int lastIndexOf(ChartObject o) {
        return charts.lastIndexOf(o);
    }

    /**
     * Возвращает новый список карт, содержащий карты этого списка
     * с номера i включительно до номера i1 исключительно.
     *
     * @param i  первый номер карты в списке, который
     *           попадёт в новый список.
     * @param i1 первый номер карты в списке, который не
     *           попадёт в новый список.
     * @return новый список карт, включающий карты этого списка с i до i1.
     */
    public ChartList subList(int i, int i1) {
        return new ChartList(charts.subList(i, i1));
    }

    /**
     * @return поток из карт-объектов.
     */
    public Stream<ChartObject> stream() {
        return charts.stream();
    }

    /**
     * @return параллельный поток из карт-объектов, как он определён в {@link ArrayList}.
     */
    public Stream<ChartObject> parallelStream() {
        return charts.parallelStream();
    }

    /**
     * Удаляет из списка карт все элементы, которые не присутствуют в указанном собрании.
     *
     * @param collection любой собрание карт-элементов.
     * @return {@code истинно}, если этот список изменился в результате вызова.
     */
    public boolean retainAll(Collection<ChartObject> collection) {
        names.retainAll(chartsToNames(collection));
        boolean changed = charts.retainAll(collection);
        if (changed) ++this.modCount;
        return changed;
    }

    private List<String> chartsToNames(Collection<ChartObject> collection) {
        return collection.stream().map(ChartObject::getName).toList();
    }

    /**
     * Удаляет из списка карт все элементы, которые присутствуют в указанном собрании.
     *
     * @param collection любое собрание карт-элементов.
     * @return {@code истинно}, если этот список изменился в результате вызова.
     */
    public boolean removeAll(Collection<ChartObject> collection) {
        names.removeAll(chartsToNames(collection));
        boolean changed = charts.removeAll(collection);
        if (changed)
            ++this.modCount;
        return changed;
    }

    /**
     * Присутствуют ли в этом списке все указанные карты.
     *
     * @param collection собрание карт.
     * @return да, если все суть, нет, если хотя бы какой-то нет.
     */
    public boolean containsAll(Collection<ChartObject> collection) {
        return charts.containsAll(collection);
    }

    /**
     * Добавляет карту и имя карты в соответствующие списки
     * без дополнительных проверок уникальности имени.
     *
     * @param chart добавляемая карта.
     */
    private boolean addItem(ChartObject chart) {
        boolean add = charts.add(chart);
        if (add) {
            ++this.modCount;
            names.add(chart.getName());
        }
        return add;
    }

    /**
     * Даёт карту по её номеру в списке.
     *
     * @param i номер карты в списке.
     * @return карту из списка с соответствующим номером,
     * или {@code пусто}, если указан номер за пределами списка.
     */
    public ChartObject get(int i) {
        return i >= 0 && i < charts.size() ?
                charts.get(i) :
                null;
    }

    /**
     * Даёт карту по её имени.
     *
     * @param name имя карты, которую хотим получить.
     * @return карту с соответствующим именем, или {@code пусто},
     * если карты с таким именем здесь нет.
     */
    public ChartObject get(String name) {
        return get(indexOf(name));
    }

    /**
     * Отдаёт список всех имён присутствующих карт.
     *
     * @return список имён карт в историческом порядке.
     */
    public List<String> getNames() {
        return names;
    }

    /**
     * Отдаёт список всех присутствующих карт.
     *
     * @return список карт в историческом порядке.
     */
    public List<ChartObject> getCharts() {
        return charts;
    }

    /**
     * Содержит ли этот список карту с таким именем.
     *
     * @param name имя, которое есть ли.
     * @return да, если есть, нет, если нет.
     */
    public boolean contains(String name) {
        return names.contains(name);
    }

    /**
     * Содержит ли этот список указанную карту.
     *
     * @param chart карта, которая есть ли.
     * @return да, если есть, нет, если нет.
     */
    public boolean contains(ChartObject chart) {
        return charts.contains(chart);
    }

    /**
     * Удаляет из списка карту с указанным именем.
     *
     * @param name имя карты, которую нужно удалить.
     * @throws IndexOutOfBoundsException если карта
     *                                   с указанным номером отсутствует.
     */
    public void remove(String name) {
        int index = names.indexOf(name);
        charts.remove(index);
        names.remove(index);
    }

    /**
     * Удаляет из списка указанную карту.
     *
     * @param item карта, которую нужно удалить.
     * @throws IndexOutOfBoundsException если указанная карта
     *                                   отсутствует.
     */
    public void remove(ChartObject item) {
        int index = charts.indexOf(item);
        names.remove(index);
        charts.remove(index);
    }

    public void addAll(ChartList adding) {
        addAll(adding.getCharts());
    }
}
