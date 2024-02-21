package cz.cuni.mff.pijalekj.utils;
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
        this.reset(3);
    }

    public void printFG(TerminalColor fgcolor, String message) {
        this.setForeground(fgcolor);
        System.out.print(message);
        this.setForeground(this.FGDefault);
    }
    public void printFGBG(TerminalColor fgcolor, TerminalColor bgcolor, String message) {
        this.setForeground(fgcolor);
        this.setBackground(bgcolor);
        System.out.print(message);
        this.setForeground(this.FGDefault);
        this.setBackground(this.BGDefault);
    }
    public void printBG(TerminalColor bgcolor, String message) {
        this.setBackground(bgcolor);
        System.out.print(message);
        this.setBackground(this.FGDefault);
    }
    public void print(String message) {
        System.out.print(message);
    }
    public void println(String message) {
        System.out.println(message);
    }
}
