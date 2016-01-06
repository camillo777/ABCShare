package com.millo.abcshare;

/**
 * Created by camillo on 04/01/16.
 */
public class HtmlFormatter {

    public final static String NL = "\n";

    public static String html_start() {
        return "<html>" + NL;
    }

    public static String html_end() {
        return "</html>" + NL;
    }

    public static String head(String title) {
        return "<head>" + NL +
                "<title>" + title + "</title>" + NL +
                "<style>" + NL +
                "#header {" + NL +
                "background-color:blue;" + NL +
                "color:white;" + NL +
                "text-align:center;" + NL +
                "padding:5px;" + NL +
                "font-family: verdana;" + NL +
                "font-size: 1.5em;" + NL +
                "}" + NL +
                "#footer {" + NL +
                "background-color:blue;" + NL +
                "color:white;" + NL +
                "clear:both;" + NL +
                "text-align:center;" + NL +
                "padding:5px;" + NL +
                "font-family: verdana;" + NL +
                "font-size: 1.5em;" + NL +
                "}" + NL +
                "#nav {" + NL +
                "background-color:#eeeeee;" + NL +
                "padding:5px;" + NL +
                "font-family: verdana;" + NL +
                "font-size: 2em;" + NL +
                "}" + NL +
                "#section {" + NL +
                "padding:5px;" + NL +
                "font-family: verdana;" + NL +
                "font-size: 2em;" + NL +
                "}" + NL +
                "h1 {" + NL +
                "color: blue;" + NL +
                "font-size: 1.5em;" + NL +
                "}" + NL +
                "p  {" + NL +
                "color: red;" + NL +
                "}" + NL +
                "a  {" + NL +
                "color: blue;" + NL +
                "text-decoration: none;" + NL +
                "}" + NL +
                "</style>" + NL +
                "</head>" + NL;
    }

    public static String body_start() {
        return "<body>" + NL;
    }

    public static String body_end() {
        return "</body>" + NL;
    }

    public static String a(String uri, String name) {
        return "<a href=\"" + uri + "\">" + name + "</a>" + NL;
    }

    public static String header(String s) {
        return "<div id=\"header\">" + NL +
                s + NL +
                "</div>" + NL;
    }
    public static String footer(String s) {
        return "<div id=\"footer\">" + NL +
                s + NL +
                "</div>" + NL;
    }

    //    public static String nav(String s){
//        return "<body>"+s+"</body>";
//    }
    public static String section_start() {
        return "<div id=\"section\">" + NL;
    }

    public static String section_end() {
        return "</div>" + NL;
    }

    //    public static String footer(String s){
//        return "<body>"+s+"</body>";
//    }

    public static String item(String s) {
        return "<p>" + s + "</p>" + NL;
    }
//    public static String item(String s) { return s + "<br/>" + NL; }

    public static String title(String s) {
        return "<h1>" + s + "</h1>" + NL;
    }

    public static String nav_start() {
        return "<div id=\"nav\">" + NL;
    }

    public static String nav_end() {
        return "</div>" + NL;
    }
}
