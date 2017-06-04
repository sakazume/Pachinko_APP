package pachinko.p_word;

import com.avaje.ebean.Ebean;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.Mstore;
import pachinko.library.JsoupHelper2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gyutr20 on 2017/03/17.
 */
public class 店舗一覧解析2 {
    static String P_WORD_URL = "http://www.p-world.co.jp";

    public static void main(String... args) {
        Document doc = JsoupHelper2.run("http://www.p-world.co.jp/index.html");
        Elements 都道府県 = 都道府県取得(doc);
        List<Document> ホールURLList = new ArrayList<>();

        都道府県.forEach(s->{

            if(0<=s.attr("href").indexOf("cgi")){
                return;
            }

            String 都道府県URL = P_WORD_URL + s.attr("href");
            Elements 市町村elm = 市町村取得(都道府県URL);
            市町村elm.parallelStream().forEach(elm->{
                String link = elm.attr("href");
                String url = P_WORD_URL + link;
                List<Document> list = ホールURL取得(url);
                list.parallelStream().forEach(urlElm->{
                    ホールURLList.add(urlElm);
                });
            });

        });
        System.out.println(ホールURLList.size());
        ホールURLList.parallelStream().forEach(listDoc->{
            ホール一覧解析(listDoc);
        });


        JsoupHelper2.end();
    }

    public static Elements 都道府県取得(Document doc) {
        return doc.select("area");
    }

    public static Elements 市町村取得(String url) {
        Document doc = JsoupHelper2.run(url);
        return doc.select(".areaList a");
    }
    public static List<Document> ホールURL取得(String url) {
        List<Document> ret = new ArrayList<>();

        Document doc = JsoupHelper2.run(url);

        if(doc.select(".pageNavi span").size()==0) {
            System.out.println("ホールデータスキップ");
            System.out.println(url);
            return new ArrayList<>();
        }

        //次へリンク確認
        Element nextEls = doc.select(".pageNavi span").get(1);

        ret.add(doc);

        if(1<=nextEls.select("a").size()) {

            System.out.println("次ページあり");

            String nextUrl = "http://www.p-world.co.jp/_machine/kensaku.cgi" + nextEls.select("a").attr("href");
            List<Document> nextList = ホールURL取得(nextUrl);

            for(Document addDoc : nextList) {
                ret.add(addDoc);
            }
        }
        return ret;
    }

    public static void ホール一覧解析(Document doc) {
        Elements els = doc.select(".hallDetail");

        els.parallelStream().forEach(s->{
            String name = s.select(".detail-hallLink").text();
            String url = "http://www.p-world.co.jp" + s.select(".detail-hallLink").attr("href");
            String address = null;
            if(1<=s.select(".detail-address").size()) {

                address = s.select(".detail-address").get(0).text();
                if(1<=address.split("周辺").length) {
                    address = address.split("周辺")[0];
                }
            }

            Mstore mstore = new Mstore();
            if(Mstore.リンク存在チェック(url)) {
                mstore = Mstore.findByLink(url);
            }

            mstore.setName(name);
            mstore.setAddress(address);
            mstore.setLink(url);

            Ebean.save(mstore);
        });

    }
}
