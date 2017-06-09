package nise;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.library.JsoupHelper2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gyutr20 on 2017/06/05.
 */
public class Main {

    static String domain = "http://papimo.jp";

    public static void main(String... args) {
        String url = "http://papimo.jp/h/00031715/hit/index_sort/216060001/1-20-104488";
        Document doc = JsoupHelper2.run(url);

        Elements els = getListLink(doc);
        List<Document> docList = new ArrayList<>();

        els.forEach(s->{
            String href = s.attr("href");
            String text = s.text();
            String niseUrl = domain + href;
            Document dataDoc = JsoupHelper2.run(niseUrl);
            docList.add(dataDoc);
        });

        JsoupHelper2.end();
        getDate(-1);
    }

    /**
     * お店の機種一覧から台リンクのエレメントを取得する
     * @param doc
     * @return
     */
    public static Elements getListLink(Document doc) {
        return doc.select(".sort a");
    }

    public static void getDate(Integer i) {
        LocalDateTime d = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String s = formatter.format(d.plusDays(i));
        System.out.println(s);
        //        d.plusDays(i);
    }

    public static Elements getSlotData(Document doc) {
        return doc.select(".data tbody tr");
    }


}
