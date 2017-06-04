package pachinko.p_word;

import com.avaje.ebean.Ebean;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.Mstore;
import pachinko.library.JsoupHelper;

import java.util.*;

/**
 * Created by gyutr20 on 2015/09/14.
 */
public class 店舗一覧解析 {
    static String P_WORD_URL = "http://www.p-world.co.jp";
    public static void main(String... args) {

        Document doc = JsoupHelper.run("http://www.p-world.co.jp/index.html");
        Elements 都道府県 = 都道府県取得(doc);
        final HashMap<String,Elements> map = new HashMap<>();

        都道府県.forEach(s->{

            if(0<=s.attr("href").indexOf("cgi")){
                return;
            }

            String 都道府県URL = P_WORD_URL + s.attr("href");
            Elements 市町村elm = 市町村取得(都道府県URL);

            map.put(都道府県URL,市町村elm);
        });

        List<Document> ホール一覧ページList = new ArrayList<>();

        for(Map.Entry<String,Elements> s:map.entrySet()) {

            System.out.println(s.getKey() + ":開始");

            Elements 市町村els = s.getValue();


            市町村els.parallelStream().forEach(elm -> {
                String link = elm.attr("href");
                String url = P_WORD_URL + link;

                System.out.println(url);

                List<Document> list = ホールデータ取得(url);

                for(Document addDoc : list) {
                    ホール一覧ページList.add(addDoc);
                }
            });
        }


        JsoupHelper.end();

        for(Document 一覧doc : ホール一覧ページList) {
            ホール一覧解析(一覧doc);
        }

    }
    public static Elements 都道府県取得(Document doc) {
        return doc.select("area");
    }

    public static Elements 市町村取得(String url) {
        Document doc = JsoupHelper.run(url);
        return doc.select(".areaList a");
    }


    public static List<Document> ホールデータ取得(String url) {
        List<Document> ret = new ArrayList<>();

        Document doc = JsoupHelper.run(url);

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
            List<Document> nextList = ホールデータ取得(nextUrl);

            for(Document addDoc : nextList) {
                ret.add(addDoc);
            }
        }
        return ret;
    }

    public static void ホール一覧解析(Document doc) {
        Elements els = doc.select(".hallDetail");

        els.parallelStream().forEach(s->{
            String name = s.select(".hallLink").text();
            String url = "http://www.p-world.co.jp" + s.select(".hallLink").attr("href");
            String address = s.select(".address font").get(0).text();


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
