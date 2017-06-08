package nise;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.library.JsoupHelper2;

/**
 * Created by gyutr20 on 2017/06/05.
 */
public class Main {

    static String domain = "http://papimo.jp/";

    public static void main(String... args) {
        String url = "http://papimo.jp/h/00031715/hit/index_sort/216060001/1-20-104488";
        Document doc = JsoupHelper2.run(url);

        Elements els = getListLink(doc);

        els.forEach(s->{
            String href = s.attr("href");
            String text = s.text();
            System.out.println(href);
            System.out.println(text);
        });
    }

    /**
     * お店の機種一覧から台番号を取得する
     * @param doc
     * @return
     */
    public static Elements getListLink(Document doc) {
        return doc.select(".sort a");
    }

}
