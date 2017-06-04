package pachinko.p_word;

import com.avaje.ebean.Ebean;
import net.arnx.jsonic.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pachinko.db.Mstore;
import pachinko.library.JsoupHelper2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gyutr20 on 2017/03/19.
 */
public class 店舗情報解析2 {
    private static String SP_ユーザーエージェント = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4";
    private static String SP_URL = "http://www.p-world.co.jp/sp/";

    private static String PACHI_PARAMS = "&type=pachi";
    private static String SLOT_PARAMS = "&type=slot";


    public static void main(String... args) {
        final Long[] count = {0L};
        List<Mstore> allList = Ebean.createQuery(Mstore.class).findList();
        allList.parallelStream().map(s -> {

            //モバイルURLが存在しない場合の前処理
            if (s.getSpLink() == null) {
                String url = getSpLink(s);

                if (url == null) {

                } else {
                    s.setSpLink(url);
                    Ebean.save(s);
                }
            }
            System.out.println(s.getName() + ":解析URL取得");
            String url = s.getSpLink();
            JsoupHelper2.run(url + PACHI_PARAMS,SP_ユーザーエージェント);
            JsoupHelper2.run(url + SLOT_PARAMS,SP_ユーザーエージェント);
            return s;
        }).forEach(s -> {
            System.out.println(s.getName());
            Document doc = JsoupHelper2.run(s.getSpLink() + PACHI_PARAMS, SP_ユーザーエージェント);

            Map<String,Object> json = getJson(doc.html());
            パチンコ情報解析(json);

        });
        JsoupHelper2.end();
    }

    public static String getSpLink(Mstore mstore) {
        Document doc = JsoupHelper2.run(mstore.getLink(), SP_ユーザーエージェント);
        if (doc == null) {
            return null;
        }
        if (doc.select(".hall-menu li a").size() == 0) {
            System.out.println(mstore.getLink() + "ホール情報が存在しない");
            return null;
        }

        String hallUrl = doc.select(".hall-menu li a").get(1).attr("href");
        System.out.println(SP_URL + hallUrl);
        return SP_URL + hallUrl;
    }

    /**
     * モバイルのhtmlかあjavascriptのjsonを取得
     * @param html
     * @return
     */
    private static Map<String,Object> getJson(String html) {
        int start = html.indexOf("HallView.hall_kisyu_view(") + "HallView.hall_kisyu_view(".length();
        int end = html.lastIndexOf("</script>");

        String jsonStr = html.substring(start,end);
        end = jsonStr.indexOf("});") + 1;
        jsonStr = jsonStr.substring(0, end);

        JSON json = new JSON();
        Map<String,Object> map = (Map)json.parse(jsonStr);
        return map;
    }

    private static void パチンコ情報解析(Map<String,Object> jsonMap) {
        Map json = (Map) jsonMap.get("json");

        if (!json.containsKey("pachi")) {
            System.out.println("パチンコ情報が存在しない");
            return ;
        }


        List<Map> pachi = (List) json.get("pachi");
        pachi.forEach(s-> {
            String yen = (String) s.get("yen");
            List<Map> kisyus = (List) s.get("kisyus");
            kisyus.forEach(kisyu->{
                String 台数 = (String) kisyu.get("daisuu");
                String kisyuCode = (String) kisyu.get("kisyu_code");
                String name = (String) kisyu.get("kisyu_name");
            });
        });
    }
}
