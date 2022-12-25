package ru.swetophor;

public class Interpreter {
    public static String ResonanceDescription(int harmonic, int multiplier) {
        switch (harmonic) {
            case 1 : return "<1> Соединение: ";
            case 2 : return "<2> Противоположение: ";
            case 3 : return "<3> Трин: ";
            case 4 : return "<2x2> Четратура: ";

            case 5 : if (multiplier == 1) return "<5> Пятерик: ";
                else if (multiplier == 2) return "<5>[2] Двупятерик: ";

            case 6 : return "<2x3> Шестерик: ";

            case 7 : if (multiplier == 1) return "<7> Семерик: ";
                else if (multiplier == 2) return "<7>[2] Двусемерик: ";
                else if (multiplier == 3) return "<7>[3] Трисемерик: ";

            case 8 : if (multiplier == 1) return "<2x2x2> Получетрат: ";
                else if (multiplier == 3) return "<2x2x2>[3] Полуторачетрат ";

            case 9 : if (multiplier == 1) return "<3x3> Девятерик: ";
                else if (multiplier == 2) return "<3x3>[2] Двудевятерик: ";
                else if (multiplier == 4) return "<3x3>[4] Четрадевятерик: ";

            case 10 : if (multiplier == 1) return "<2x5> Полупятерик: ";
                else if (multiplier == 3) return "<2x5>[3] Полуторапятерик: ";

            case 11 : if (multiplier == 1) return "<" + harmonic + "> : ";
                else return "<" + harmonic + ">[" + multiplier + "] : ";

            case 12 : if (multiplier == 1) return "<4х3> Полушестерик: ";
                else if (multiplier == 5) return "<4x3>[5] Квиконс";

            case 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67 :
                return standard(harmonic, multiplier);

            case 14 : if (multiplier == 1) return "<7x2> Полусемирик: ";
                else if (multiplier == 3) return "<7x2>[3] Полуторасемерик: ";
                else if (multiplier == 5) return "<7x2>[5] Двусполовин-семерик: ";

            case 15 : if (multiplier == 1) return "<5x3> : ";
                else if (multiplier == 2) return "<5x3>[2] : ";
                else if (multiplier == 4) return "<5x3>[4] : ";
                else if (multiplier == 7) return "<5x3>[7] : ";

            case 16 : if (multiplier == 1) return "<4x4> : ";
                else if (multiplier == 3) return "<4x4>[3] : ";
                else if (multiplier == 5) return "<4x4>[5] : ";
                else if (multiplier == 7) return "<4x4>[7] : ";

            case 18 : if (multiplier == 1) return "<2x3x3> Полудевятерик: ";
                else if (multiplier == 5) return "<2x3x3>[5] Двусполовин-девятерик: ";
                else if (multiplier == 7) return "<2x3x3>[7] Трисполовин-девятерик: ";

            case 20 : if (multiplier == 1) return "<2x2x5> : ";
                else if (multiplier == 7) return "<2x2x5>[7] : ";

            case 21 : return standard("<3x7>", multiplier);

            case 22 : return standard("<2x11>", multiplier);

            case 24 : return standard("<2x3x4>", multiplier);

            case 25 : return standard("<5x5>", multiplier);

            case 26 : return standard("<2x13>", multiplier);

            case 27 : return standard("<3x3x3>", multiplier);

            case 28 : return standard("<2x2x7>", multiplier);

            case 30 : return standard("<2x3x5>", multiplier);

            case 40 : return standard("<2x4x5>", multiplier);

            default: return "<" + harmonic + ">[" + multiplier + "] : ";
        }
    }

    private static String standard(String pat, int mult) {
        return "%s%s : ".formatted(pat, mult == 1 ? "" : "[" + mult + "]");
    }

    private static String standard(int harm, int mult) {
        return "<%d>%s : ".formatted(harm, mult == 1 ? "" : "[" + mult + "]");
    }

}
