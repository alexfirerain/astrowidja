package ru.swetophor.celestialmechanics;


import lombok.Setter;
import ru.swetophor.mainframe.Settings;
import ru.swetophor.resogrid.Matrix;
import ru.swetophor.resogrid.Pattern;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static ru.swetophor.celestialmechanics.CelestialMechanics.*;
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

    protected Matrix aspects;


    // конструктор
    public Chart(String name) {
        super(name);
    }

    public static Chart readFromString(String input) {
        String[] lines = input.lines().toArray(String[]::new);
        if (lines.length == 0)
            throw new IllegalArgumentException("текст не содержит строк");

        Chart newChart = new Chart(lines[0]);
        Arrays.stream(lines, 1, lines.length)
                .filter(line -> !line.isBlank()
                        && !line.startsWith("//"))
                .map(Astra::readFromString)
                .forEach(newChart::addAstra);

        return newChart;
    }


    // функциональность

    public void attachAstra(Astra astra) {
        addAstra(astra);
        calculateAspectTable();
    }

    /**
     * Добавляет в карту астру. При этом если с таким именем астра
     * уже присутствует, она обновляется, заменяется.
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

    private void calculateAspectTable() {
        aspects = new Matrix(astras);
    }

    @Override
    public String getAspectTable() {
        calculateAspectTable();
        return getCaption() +
                aspects.resultsOutput();
    }

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

    @Override
    public String toString() {
        return "%s (%s, №%d)".formatted(name, type, ID);
    }

    @Override
    public List<Astra> getAstras() {
        return this.astras;
    }


    public Matrix getAspects() {
        return this.aspects;
    }

    public double getAstraPosition(String name) {
        for (Astra a : astras)
            if (a.getName().equals(name))
                return a.getZodiacPosition();
        throw new IllegalArgumentException("Astra " + name + " not found.");
    }

    public static Chart composite(Chart chart_a, Chart chart_b) {
        if (chart_a == null || chart_b == null)
            throw new IllegalArgumentException("карта для композита не найдена");
        Chart composite = new Chart("Средняя карта %s и %s"
                .formatted(chart_a.getName(), chart_b.getName()));

        Astra sun = null, mercury = null, venus = null;
        for (Astra astra : chart_a.getAstras()) {
            Astra counterpart = chart_b.getAstra(astra.getName());
            if (counterpart != null) {
                Astra compositeAstra = new Astra(astra.getName(),
                        findMedian(astra.getZodiacPosition(),
                                counterpart.getZodiacPosition()));
                composite.addAstra(compositeAstra);
                AstraEntity innerBody = AstraEntity.getEntityByName(compositeAstra.getName());
                if (innerBody != null) {
                    switch (innerBody) {
                        case SOL -> sun = compositeAstra;
                        case MER -> mercury = compositeAstra;
                        case VEN -> venus = compositeAstra;
                    }
                }
            }
        }

        if (sun != null) {

            if (mercury != null &&
                    getArc(sun, mercury) > 30.0) {

                mercury.setZodiacPosition(normalizeCoordinate(mercury.getZodiacPosition() + HALF_CIRCLE));
                composite.addAstra(mercury);
            }
            if (venus != null &&
                    getArc(sun, venus) > 60.0) {

                venus.setZodiacPosition(normalizeCoordinate(venus.getZodiacPosition() + HALF_CIRCLE));
                composite.addAstra(venus);
            }
        }

        return composite;
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

    private Pattern gatherResonants(int astraIndex, int harmonic, boolean[] analyzed) {
        analyzed[astraIndex] = true;
        Pattern currentPattern = new Pattern(harmonic);
        currentPattern.addAstra(astras.get(astraIndex));
        aspects.getConnectedAstras(astras.get(astraIndex), harmonic).stream()
                .filter(a -> !analyzed[astras.indexOf(a)])
                .map(a -> gatherResonants(astras.indexOf(a), harmonic, analyzed))
                .forEach(currentPattern::addAllAstras);
        return currentPattern;
    }

    /**
     * Вспомогательный предикат, удостоверяющий, что в группе астр наличествует
     * указанный аспект в явном виде для хотя бы одной пары.
     *
     * @param pattern  анализируемый астропаттерн.
     * @param harmonic число резонанса, который надо удостоверить.
     * @return {@code false}, если паттерна нет, если он пуст или содержит только
     * одну астру, или если ни в одной из пар элементов нет указанного резонанса
     * в явном виде. {@code true}, если хотя бы в одной паре номинальный резонанс
     * наличествует.
     */
    private boolean isValidPattern(List<Astra> pattern, int harmonic) {
        if (pattern == null || pattern.size() < 2)
            return false;
        for (int i = 0; i < pattern.size() - 1; i++)
            for (int j = i + 1; j < pattern.size(); j++)
                if (aspects.astrasInResonance(pattern.get(i), pattern.get(j), harmonic))
                    return true;
        return false;
    }


    public void printResonanceGroupsStupid(int harmonic) {
        List<Pattern> groups = findPatterns(harmonic);
        for (Pattern pattern : groups) {
            if (!pattern.getAstras().isEmpty()) {
                System.out.printf("Резонансные группы по числу %d для %s:%n", harmonic, name);
                groups.stream()
                        .filter(group -> !group.getAstras().isEmpty())
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
        StringBuilder string = new StringBuilder(harmonic + ": ");

        List<Pattern> groups = findPatterns(harmonic);

        if (groups.isEmpty())
            return string.append("-").toString();

        groups.stream()
                .filter(group -> !group.getAstras().isEmpty())
                .forEach(group -> {
                    group.getAstrasByConnectivity().stream()
                            .map(Astra::getSymbol)
                            .forEach(string::append);
                    string.append(" | ");
                });

        return string.toString();
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
        StringBuilder output = new StringBuilder(
                "Резонансные группы для %s до гармоники %d с исходным орбисом 1/%d%n"
                        .formatted(name, upToHarmonic, Settings.getOrbDivisor()));
        buildPatternAnalysis(upToHarmonic)
                .forEach((key, list) -> {
                    output.append("%d: ".formatted(key));
                    if (list.isEmpty())
                        output.append("-\n");
                    else
                        range(0, list.size())
                            .forEach(i -> {
                                list.get(i)
                                        .getAstrasByConnectivity().stream()
                                        .map(Astra::getSymbol)
                                        .forEach(output::append);
                                output.append(i < list.size() - 1 ?
                                        " | " : "\n");
                            });
                });
        return output.toString();
    }

    @Override
    public String resonanceAnalysisVerbose(int upToHarmonic) {
        StringBuilder output = new StringBuilder(
                "Резонансные группы для %s до гармоники %d с исходным орбисом 1/%d%n"
                        .formatted(name, upToHarmonic, Settings.getOrbDivisor()));
        for (Map.Entry<Integer, List<Pattern>> entry :
                buildPatternAnalysis(upToHarmonic).entrySet()) {
            Integer key = entry.getKey();
            List<Pattern> list = entry.getValue();

            list.sort(Comparator
                    .comparingDouble(Pattern::getAverageStrength)
                    .reversed());

            output.append("%d".formatted(key));
            output.append(list.isEmpty() ?
                    ":\n" :
                    " (%.0f%% @%d):%n"
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

    public Map<Integer, List<Pattern>> buildPatternAnalysis(int edgeHarmonic) {

        return rangeClosed(1, edgeHarmonic)
                .boxed()
                .collect(Collectors
                        .toMap(h -> h,
                                this::findPatterns,
                                (a, b) -> b));
    }
}


