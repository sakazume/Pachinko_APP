package pachinko.library;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pachinko.db.DhtmlCache;

/**
 * Created by gyutr20 on 2015/09/15.
 */
public class PwordJsoupHelper {

    public static Document run(String url) {
        Document doc = JsoupHelper.run(url);

        String html = doc.html();

        while(1<=html.indexOf("ココをクリック")) {
            System.out.println("リトライ処理");

            JsoupHelper.SLEEP_TIME = 120 * 1000L;
            DhtmlCache.deleteByUrl(url);
            doc = JsoupHelper.run(url);
        }

        JsoupHelper.SLEEP_TIME = 1500L;

        return doc;

    }

    public static void end() {
        JsoupHelper.end();
    }

    public static void main(String... args) {
        String url = "http://www.p-world.co.jp/_machine/kensaku.cgi?city=,%c3%e6%cc%ee%b6%e8,&dir=tokyo";
        run(url);
    }
}
