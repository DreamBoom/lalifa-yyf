package com.lalifa.yyf.weight;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @description html标签替换
 */
public class JsoupUtil {

    public static String getNewContent(String htmltext) {
        Document doc = Jsoup.parse(htmltext, "UTF-8");
        Elements head = doc.getElementsByTag("head");
        for (Element element : head) {
            element.attr("width", "100%").attr("height", "auto");
        }
        Elements body = doc.getElementsByTag("body");
        for (Element element : body) {
            element.attr("width", "100%")
                    .attr("height", "auto")
                    .attr("style", "margin:0;padding:0");
        }
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width", "100%").attr("height", "auto");
        }
        Elements div = doc.getElementsByTag("div");
        for (Element element : div) {
            element.attr("width", "100%").attr("height", "auto");
        }
        Elements tbody = doc.getElementsByTag("tbody");
        for (Element element : tbody) {
            element.attr("width", "100%").attr("height", "auto");
        }
        Elements table = doc.getElementsByTag("table");
        for (Element element : table) {
            element.attr("width", "100%").attr("height", "auto");
        }
        /*Elements esd = doc.select("[style]");
        Iterator<Element> iterator = esd.iterator();
        while (iterator.hasNext()) {
            Element etemp = iterator.next();
            String styleStr = etemp.attr("style");
            etemp.attr("style", cssStrWidth(styleStr));
            String styleStr1 = etemp.attr("style");
            etemp.attr("style", cssStrHeight(styleStr1));

        }*/
        //KLog.d("JsoupUtil", doc.toString());
        return doc.toString();
    }

    /**
     * 替换内联样式width
     *
     * @param str
     * @return
     */
    private static String cssStrWidth(String str) {
        if (!str.contains("width")) {
            return str;
        }
        String s1 = str.substring(0, str.indexOf("width"));
        String s2 = str.substring(str.indexOf("width"), str.length());
        String s3 = s2.substring(s2.indexOf(";"));
        return s1 + "width:100%" + s3;
    }

    /**
     * 替换内联样式height
     *
     * @param str
     * @return
     */
    private static String cssStrHeight(String str) {
        if (!str.contains("height")) {
            return str;
        }
        String s1 = str.substring(0, str.indexOf("height"));
        String s2 = str.substring(str.indexOf("height"), str.length());
        String s3 = s2.substring(s2.indexOf(";"));
        return s1 + "height:auto" + s3;
    }

}
