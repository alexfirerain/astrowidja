package ru.swetophor.celestialmechanics;


import lombok.Getter;
import lombok.Setter;
import ru.swetophor.harmonix.Matrix;
import ru.swetophor.harmonix.Pattern;
import ru.swetophor.harmonix.PatternAnalysis;
import ru.swetophor.mainframe.Settings;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static ru.swetophor.celestialmechanics.Mechanics.zodiacFormat;

/**
 * Астрологическое описание момента времени,
 * включающее:<br/>
 *  ♦ уникальный в программе ИД<br/>
 *  ♦ астрологический момент, описанный через:<br/>
     *   • имя, название момента<br/>
     *   • массив астр<br/>
     *   • матрицу резонансов
*/
@Setter
public class Chart extends ChartObject {

    protected List<Astra> astras = new ArrayList<>();

    {
        type = ChartType.COSMOGRAM;
    }

    /**
     * Матрица резонансов между астрами карты.
     */
    @Getter
    protected Matrix aspects;


    // конструктор

    /**
     * Конструктор пустой карты.
     * @param name имя карты.
     */
    public Chart(String name) {
        super(name);
    }

    /**
     * Конструктор карты на основе строки ввода,
     * содержащей имя карты и, в следующих строках,
     * описание каждой астры в подобающем формате.
     * @param input входная строка.
     * @return  сформированную карту.
     */
    public static Chart readFromString(String input) {
        String[] lines = input.lines().toArray(String[]::new);
        if (lines.length == 0)
            throw new IllegalArgumentException("текст не содержит строк");

        Chart newChart = new Chart(lines[0]);
        var astras =
                Arrays.stream(lines, 1, lines.length)
                        .filter(line -> !line.isBlank()
                                && !line.startsWith("//"))
                        .map(Astra::readFromString)
                        .collect(Collectors.toList());
//                .forEach(newChart::addAstra);
//        newChart.calculateAspectTable();
        return new Chart(lines[0], astras);
    }

    @Override
    public boolean resonancePresent(Astra a, Astra b, int harmonic) {
        return aspects.resonancePresent(a, b, harmonic);
    }


    // функциональность

    public void attachAstra(Astra astra) {
        addAstra(astra);
        calculateAspectTable();
    }

    /**
     * Добавляет в карту астру. При этом если с таким именем астра
     * уже присутствует, она обновляется, заменяется.
     * Матрица резонансов при этом не пересчитывается!
     * @param astra добавляемая астра.
     */
    public void addAstra(Astra astra) {
        astra.setHeaven(this);
        for (int i = 0; i < astras.size(); i++)
            if (astras.get(i).getName().equals(astra.getName())) {
                astras.set(i, astra);
                return;
            }
        astras.add(astra);
    }

    public void addAstraFromString(String input) {
        addAstra(Astra.readFromString(input));
    }

    public Chart(String name, List<Astra> astras) {
        super(name);
        this.astras = astras;
        calculateAspectTable();
    }

    private void calculateAspectTable() {
        aspects = new Matrix(astras);
    }


    @Override
    public String getAspectTable() {
        calculateAspectTable();
        return getCaption() +
                aspects.resultsOutput();
    }

    @Override
    public String getCaption() {
        return super.getCaption("%s: %s (№%d)"
                .formatted(type, name, ID));
    }


    @Override
    public String getAstrasList() {
        StringBuilder list = new StringBuilder("%nЗодиакальные позиции (%s):%n".formatted(name));
        astras.forEach(next -> list.append(
                        "%s\t %s%n".formatted(
                                next.getNameWithZodiacDegree(),
                                zodiacFormat(next.getZodiacPosition())
                        )
                )
        );
        return list.toString();
    }

    /**
     * Текстовое соответствия карты (заголовок).
     *
     * @return строка "имя (тип, ИД)"
     */
    @Override
    public String toString() {
        return "%s (%s, №%d)".formatted(name, type, ID);
    }

    @Override
    public List<Astra> getAstras() {
        return this.astras;
    }


    public double getAstraPosition(String name) {
        Astra a = getAstra(name);
        if (a == null)
            throw new IllegalArgumentException("Astra " + name + " not found.");
        return a.getZodiacPosition();
    }


    /**
     * Отдаёт астру карты по ея имени.
     *
     * @param name имя астры, которую запрашивают.
     * @return астру с таким именем, если она есть в карте, иначе {@code пусто}.
     */
    public Astra getAstra(String name) {
        return astras.stream()
                .filter(a -> a.getName()
                        .equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Находит и возвращает список всех паттернов, образованных астрами
     * данной карты по указанной гармонике.
     * @param harmonic гармоника, по которой выделяются паттерны.
     * @return список паттернов из астр этой карты, резонирующих
     * по указанной гармонике. Если ни одного паттерна не обнаруживается,
     * то пустой список.
     */
    public List<Pattern> findPatterns(int harmonic) {
        List<Pattern> patterns = new ArrayList<>();
        boolean[] analyzed = new boolean[astras.size()];
        range(0, astras.size())
                .filter(i -> !analyzed[i])
                .mapToObj(i -> gatherResonants(i, harmonic, analyzed))
                .filter(Pattern::isValid)
                .forEach(patterns::add);
        return patterns;
    }

    /**
     * Выдаёт паттерн, состоящий из астр данной карты, связанных с указанной астрой
     * по указанной гармонике напрямую или посредством других астр.
     * Исходная астра помещается сразу в паттерн, её номер отмечается как
     * уже проверенный во вспомогательном массиве; затем функция рекурсивно
     * запускается для каждой из ещё не проверенных астр, имеющих указанный
     * резонанс с исходной, результат каждого вызова добавляется к паттерну.
     *
     * @param astraIndex индекс исходной астры в списке астр этой Карты.
     * @param harmonic номер гармоники, по которому надо проверить узор.
     * @param analyzed   вспомогательный массив, отмечающий, какие астры
     *                   из списка астр этой Карты уже проверены на этот резонанс.
     * @return  паттерн, содержащий исходную астру и все связанные с ней
     *      по указанной гармонике астры из списка астр этой карты; паттерн,
     *      содержащий одну исходную астру, если резонансов по этой гармонике нет.
     */
    private Pattern gatherResonants(int astraIndex, int harmonic, boolean[] analyzed) {
        Astra startingAstra = astras.get(astraIndex);
        analyzed[astraIndex] = true;
        Pattern currentPattern = new Pattern(harmonic, this);
        currentPattern.addAstra(startingAstra);
        this.aspects.getConnectedAstras(startingAstra, harmonic).stream()
                .filter(a -> !analyzed[astras.indexOf(a)])
                .map(a -> gatherResonants(astras.indexOf(a), harmonic, analyzed))
                .forEach(currentPattern::addAllAstras);
        return currentPattern;
    }


    public void printResonanceGroupsStupid(int harmonic) {
        List<Pattern> groups = findPatterns(harmonic);
        for (Pattern pattern : groups) {
            if (!pattern.getAstras().isEmpty()) {
                System.out.printf("Резонансные группы по числу %d для %s:%n", harmonic, name);
                groups.stream()
                        .filter(group -> !group.isEmpty())
                        .forEach(group -> {
                            group.getAstrasByConnectivity().stream()
                                    .map(Astra::getSymbol)
                                    .forEach(System.out::print);
                            System.out.println();
                        });
                return;
            }
        }
        System.out.printf("Резонансных групп по числу %d для %s не найдено при орбисе 1/%d%n",
                harmonic,
                name,
                Settings.getOrbDivisor());
    }

    private String patternReport(int harmonic) {
        StringBuilder output = new StringBuilder(harmonic + ": ");

        List<Pattern> groups = findPatterns(harmonic);

        if (groups.isEmpty())
            return output.append("-").toString();

        groups.stream()
                .filter(group -> !group.getAstras().isEmpty())
                .forEach(group -> {
                    group.getAstrasByConnectivity().stream()
                            .map(Astra::getSymbol)
                            .forEach(output::append);
                    output.append(" | ");
                });

        return output.toString();
    }

    //    @Override
//    public void printResonanceAnalysis(int upToHarmonic) {
//        System.out.printf("Резонансные группы для %s до гармоники %d с исходным орбисом 1/%d%n",
//                name,
//                upToHarmonic,
//                Settings.getOrbsDivider());
//        IntStream.rangeClosed(1, upToHarmonic)
//                .forEach(h -> System.out.println(patternReport(h)));
//    }
    @Override
    public String resonanceAnalysis(int upToHarmonic) {
        // TODO: переделать чтобы принимала vararg buildPatternAnalysis(upToHarmonic)
        StringBuilder output = new StringBuilder(
                "Резонансные группы для %s до гармоники %d с исходным орбисом 1/%d%n"
                        .formatted(name, upToHarmonic, Settings.getOrbDivisor()));
        buildPatternAnalysis(upToHarmonic)
                .forEach((key, list) -> {
                    output.append("%d: ".formatted(key));
                    if (list.isEmpty())
                        output.append("-\n");
                    else {
                        output.append(list.stream()
                                        .map(Pattern::getString)
                                        .collect(Collectors.joining(" | ")))
                                .append("\n");
                    }
                });
        return output.toString();
    }

    /**
     * Выполняет резонансный анализ этой карты для гармоник от первой до указанной
     * и генерирует подробный вывод, включающий паттерны, отсортированные по средней силе,
     * а также процентное соотношение паттернов и общее количество паттернов для каждой резонансной группы.
     * @param upToHarmonic  гармоника, до которой проводится анализ (включительно).
     * @return  представление результатов анализа в виде строки.
     */
    @Override
    public String resonanceAnalysisVerbose(int upToHarmonic) {
        StringBuilder output = new StringBuilder(
                "Резонансные группы для %s до гармоники %d с исходным орбисом 1/%d%n"
                        .formatted(name, upToHarmonic, Settings.getOrbDivisor()));
        Map<Integer, List<Pattern>> analysis = buildPatternAnalysis(upToHarmonic);

        for (Map.Entry<Integer, List<Pattern>> entry : analysis.entrySet()) {
            output.append("%d".formatted(entry.getKey()));

            List<Pattern> list = entry.getValue();
            list.sort(Comparator
                    .comparingDouble(Pattern::getAverageStrength)
                    .reversed());

            output.append(list.isEmpty() ?
                    ":\n" :
                    " %.0f%% (%d):%n"
                            .formatted(
                                    list.stream()
                                            .mapToDouble(Pattern::getAverageStrength)
                                            .sum() / list.size(),
                                    list.stream()
                                            .mapToInt(Pattern::size)
                                            .sum()));
            output.append(list.isEmpty() ?
                    "\t-\n" :
                    list.stream()
                            .map(Pattern::getConnectivityReport)
                            .collect(Collectors.joining()));
        }
        return output.toString();
    }

    public String reportAnalysis(PatternAnalysis analysis) {
        StringBuilder output = new StringBuilder(
                "Резонансные группы для %s до гармоники %d с исходным орбисом 1/%d%n"
                        .formatted(name, analysis.size(), Settings.getOrbDivisor()));
        for (int i = 0; i < analysis.size(); i++) {
            output.append("%d".formatted(i));
            List<Pattern> list = analysis.getPatternsForHarmonic(i);
            list.sort(Comparator.comparingDouble(Pattern::getAverageStrength).reversed());
            if (list.isEmpty())
                output.append("\n\t-\n");
            else {
                output.append(" %.0f%% (%d):%n".formatted());
            }


        }


        return output.toString();
    }

    /**
     * Простраивает и возвращает для данной карты список всех найденных паттернов
     * по гармоникам от первой до указанной.
     * @param edgeHarmonic до какой гармоники включительно проводить анализ.
     * @return мапу, где ключами номер гармоники, а значениями списки найденных по ней паттернов.
     */
    public Map<Integer, List<Pattern>> buildPatternAnalysis(int edgeHarmonic) {

        return rangeClosed(1, edgeHarmonic)
                .boxed()
                .collect(Collectors
                        .toMap(h -> h,
                                this::findPatterns,
                                (a, b) -> b));
    }

    public PatternAnalysis buildAnalysis(int edgeHarmonic) {

        Map<Integer, List<Pattern>> listMap = rangeClosed(1, edgeHarmonic)
                .boxed()
                .collect(Collectors
                        .toMap(h -> h,
                                this::findPatterns,
                                (a, b) -> b));
        return new PatternAnalysis(listMap);
    }

    /**
     * Возвращает строку того формата, который принят для хранения
     * данных карты астры при сохранении.
     * В первой строке содержит "#" + имя карты.
     * В каждой последующей строке – положение очередной астры, как оно
     * предоставляется функцией {@link Astra#getString()}.
     * В конце также добавляется пустая строка для разделения.
     *
     * @return многостроку с названием карты и положением астр.
     */
    @Override
    public String getString() {
        StringBuilder content = new StringBuilder();
        content.append("#%s%n".formatted(name));
        astras.stream()
                .map(Astra::getString)
                .forEach(content::append);
        content.append("\n");
        return content.toString();
    }

}


