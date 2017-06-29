package nise;

import lombok.Data;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.library.JsoupHelper2;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Map.Entry;

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

        Map<String,Document> map = new HashMap<>();

        els.forEach(s -> {
            String href = s.attr("href");
            String text = s.text();
            String niseUrl = domain + href;
            Document dataDoc = JsoupHelper2.run(niseUrl);
            map.put(text, dataDoc);
            System.out.println(text + "を取得");
        });
        for(Entry<String,Document> ent:map.entrySet()) {
            Document slotDoc = ent.getValue();
            Elements slotEls = getSlotDataEls(slotDoc);

            for(int i=1;i<slotEls.size();i++) {
                Element el = slotEls.get(i);
                UnitData uni = new UnitData(el);
                uni.setDate(getDate(i * -1));
                System.out.println(uni.getDate());
            }

//            slotEls.stream().forEach(s->{
//                new UnitData(s);
//            });

            System.out.println(slotEls.size());
        }


        JsoupHelper2.end();


    }

    /**
     * お店の機種個別一覧から台リンクのエレメントを取得する
     * @param doc
     * @return
     */
    public static Elements getListLink(Document doc) {
        return doc.select(".sort a");
    }

    public static Date getDate(Integer i) {
        LocalDateTime d = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String s = formatter.format(d.plusDays(i));
        System.out.println(s);
        Date date = Date.from(d.plusDays(i).atZone(ZoneId.systemDefault()).toInstant());
        return date;

    }

    public static Elements getSlotDataEls(Document doc) {
        return doc.select(".data tbody tr");
    }

    @Data
    public static class UnitData {
        String BB回数;
        String RB回数;
        String BB確率;
        String 合成回数;
        String 総スタート;
        String 最終スタート;
        String 最大メダル;
        String 日付;
        Date date;

        public UnitData(Element el) {
            Elements els = el.select("td");
            BB回数 = els.get(1).text();
            RB回数 = els.get(2).text();
            BB確率 = els.get(3).text();
            合成回数 = els.get(4).text();
            総スタート = els.get(5).text();
            最終スタート = els.get(6).text();
            最大メダル = els.get(7).text();
            日付 = els.get(0).text();

        }
    }
}
