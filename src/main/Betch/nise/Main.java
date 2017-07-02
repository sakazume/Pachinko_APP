package nise;

import com.avaje.ebean.Ebean;
import lombok.Data;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.Papimo;
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

    public static void exec(Tenp tenp) {
//        String url = "http://papimo.jp/h/00031715/hit/index_sort/216060001/1-20-104488";
        Document doc = JsoupHelper2.run(tenp.getUrl());

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
            UnitData.取得位置算出(slotEls.get(0));

            for(int i=1;i<slotEls.size();i++) {
                Element el = slotEls.get(i);

                UnitData uni = new UnitData(el);
                uni.setDate(getDate(i * -1));
                Papimo papomo = uni.createPapimo();
                papomo.set台番号(ent.getKey());
                papomo.set店名(tenp.get店名());
                Ebean.save(papomo);
            }

        }
        JsoupHelper2.end();
    }

    public static void main(String... args) {
        List<Tenp> tenpList = Arrays.asList(
                new Tenp("アイランド秋葉原","http://papimo.jp/h/00031715/hit/index_sort/216060001/1-20-104488"),
                new Tenp("ビッグアップル秋葉原","http://papimo.jp/h/00031580/hit/index_sort/216060001/1-20-106178")
        );
        tenpList.stream().forEach(tenp->{
            exec(tenp);

        });
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
        Date date = Date.from(d.plusDays(i).atZone(ZoneId.systemDefault()).toInstant());
        return date;

    }

    public static Elements getSlotDataEls(Document doc) {
        return doc.select(".data").get(0).select("tr");
    }


    /**
     * 偽物語専用の店舗情報
     */
    @Data
    public static class Tenp {
        String 店名;
        String url;

        public Tenp(String 店名,String url) {
            this.店名 = 店名;
            this.url = url;
        }
    }
}
