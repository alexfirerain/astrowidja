package ru.swetophor;

public class Interpreter {
    public static String ResonanceDescription(int гармоника, int крат) {
        switch (гармоника) {
            case 1 : return "<1> Соединение: ";
            case 2 : return "<2> Противоположение: ";
            case 3 : return "<3> Трин: ";
            case 4 : return "<2x2> Четратура: ";

            case 5 : if (крат == 1) return "<5> Пятерик: ";
                else if (крат == 2) return "<5>[2] Двупятерик: ";

            case 6 : return "<2x3> Шестерик: ";

            case 7 : if (крат == 1) return "<7> Семерик: ";
                else if (крат == 2) return "<7>[2] Двусемерик: ";
                else if (крат == 3) return "<7>[3] Трисемерик: ";

            case 8 : if (крат == 1) return "<2x2x2> Получетрат: ";
                else if (крат == 3) return "<2x2x2>[3] Полуторачетрат ";

            case 9 : if (крат == 1) return "<3x3> Девятерик: ";
                else if (крат == 2) return "<3x3x3>[2] Двудевятерик: ";
                else if (крат == 4) return "<3x3x3>[4] Четрадевятерик: ";

            case 10 : if (крат == 1) return "<2x5> Полупятерик: ";
                else if (крат == 3) return "<2x5> Полуторапятерик: ";

            case 11 : if (крат == 1)return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";

            case 12 : if (крат == 1) return "<4х3> Полушестерик: ";
                else if (крат == 5) return "<4x3>[5] Квиконс";

            case 13 : if (крат == 1) return "<13> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";

            case 14 : if (крат == 1) return "<7x2> Полусемирик: ";
                else if (крат == 3) return "<7x2>[3] Полуторасемерик: ";
                else if (крат == 5) return "<7x2>[5] Двусполовин-семерик: ";

            case 15 : if (крат == 1) return "<5x3> : ";
                else if (крат == 2) return "<5x3>[2] : ";
                else if (крат == 4) return "<5x3>[4] : ";
                else if (крат == 7) return "<5x3>[7] : ";

            case 16 : if (крат == 1) return "<4x4> : ";
                else if (крат == 3) return "<4x4>[3] : ";
                else if (крат == 5) return "<4x4>[5] : ";
                else if (крат == 7) return "<4x4>[7] : ";

            case 17 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";

            case 18 : if (крат == 1) return "<2x3x3> Полудевятерик: ";
                else if (крат == 5) return "<2x3x3>[5] : ";
                else if (крат == 7) return "<2x3x3>[7] : ";

            case 19 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";

            case 20 : if (крат == 1) return "<2x2x5> : ";
                else if (крат == 7) return "<2x2x5>[7] : ";

            case 21 : if (крат == 1) return "<3x7> : ";
                else return "<3x7>[" + крат + "] : ";

            case 22 : if (крат == 1) return "<2x11> : ";
                else return "<2x11>[" + крат + "] : ";

            case 23 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";

            case 24 : if (крат == 1) return "<2x3x4> : ";
                else return "<2x3x4>[" + крат + "] : ";

            case 25 : if (крат == 1) return "<5x5> : ";
                else return "<5x5>[" + крат + "] : ";

            case 26 : if (крат == 1) return "<2x13> : ";
                else return "<2x13>[" + крат + "] : ";

            case 27 : if (крат == 1) return "<3x3x3> : ";
                else return "<3x3x3>[" + крат + "] : ";

            case 28 : if (крат == 1) return "<2x2x7> : ";
                else return "<2x2x7>[" + крат + "] : ";

            case 29 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";

            case 30 : if (крат == 1) return "<2x3x5> : ";
                else return "<2x3x5>[" + крат + "] : ";

            case 31 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";

            case 37 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";
            case 41 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";
            case 43 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";
            case 47 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";
            case 53 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";
            case 59 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";
            case 61 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";
            case 67 : if (крат == 1) return "<" + гармоника + "> : ";
                else return "<" + гармоника + ">[" + крат + "] : ";
            default: return "<" + гармоника + ">[" + крат + "] : ";
        }
    }

}
