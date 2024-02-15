package cz.cuni.mff.pijalekj.ui;
public class ColorConsole {
    private static final String ESC = "\033[";
    private static final String FORE = "38;5;";
    private static final String BACK = "48;5;";
    private static final String CLEAR = "J";

    public TerminalColor FGDefault = TerminalColor.White;
    public TerminalColor BGDefault = TerminalColor.Black;

    public void setForeground(TerminalColor color) {
        String s = ESC + FORE + color.ordinal() + "m";
        System.out.print(s);
    }

    public void setBackground(TerminalColor color) {
        String s = ESC + BACK + color.ordinal() + "m";
        System.out.print(s);
    }

    public void reset(int level) {
        String s = ESC + level + CLEAR;
        System.out.print(s);
    }
    public void reset() {
        reset(3);
    }

    public void printFG(TerminalColor fgColor, String message) {
        setForeground(fgColor);
        System.out.print(message);
        setForeground(FGDefault);
    }
    public void printFGBG(TerminalColor fgColor, TerminalColor bgColor, String message) {
        setForeground(fgColor); setBackground(bgColor);
        System.out.print(message);
        setForeground(FGDefault); setBackground(BGDefault);
    }
    public void printBG(TerminalColor bgColor, String message) {
        setBackground(bgColor);
        System.out.print(message);
        setBackground(FGDefault);
    }
    public void print(String message) {
        System.out.print(message);
    }
    public void println(String message) {
        System.out.println(message);
    }
    public enum TerminalColor {
        Black, DarkRed, DarkGreen, DarkYellow, DarkBlue,
        DarkViolet, Turquoise, LightGray, DarkGray,
        Red, Green, Yellow, Blue, Violet, LightBlue, White;
    }
}
