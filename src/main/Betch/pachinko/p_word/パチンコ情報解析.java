package pachinko.p_word;

/**
 * Created by gyutr20 on 2015/12/02.
 */

import com.avaje.ebean.Ebean;
import lombok.Data;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;
import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.Dstore;
import pachinko.db.Mmodels;
import pachinko.db.Mstore;
import pachinko.db.TNumber;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import pachinko.library.JsoupHelper;
import pachinko.p_word.店舗情報解析.*;
@Data
public class パチンコ情報解析 {
    public static String ユーザーエージェント = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4";
    private static String SP_URL = "http://www.p-world.co.jp/sp/";

    /** */
    static Map<Integer, byte[]> 比較用台数データ = new HashMap<Integer, byte[]>();

    static {
        try {
            比較用台数データ.put(99, Files.readAllBytes(Paths.get("img/dai.gif"))) ;
            比較用台数データ.put(100,Files.readAllBytes(Paths.get("img/dedama.gif"))) ;
            比較用台数データ.put(0,Files.readAllBytes(Paths.get("img/0.gif"))) ;
            比較用台数データ.put(1,Files.readAllBytes(Paths.get("img/1.gif"))) ;
            比較用台数データ.put(2,Files.readAllBytes(Paths.get("img/2.gif"))) ;
            比較用台数データ.put(3,Files.readAllBytes(Paths.get("img/3.gif"))) ;
            比較用台数データ.put(4,Files.readAllBytes(Paths.get("img/4.gif"))) ;
            比較用台数データ.put(5,Files.readAllBytes(Paths.get("img/5.gif"))) ;
            比較用台数データ.put(6,Files.readAllBytes(Paths.get("img/6.gif"))) ;
            比較用台数データ.put(7,Files.readAllBytes(Paths.get("img/7.gif"))) ;
            比較用台数データ.put(8,Files.readAllBytes(Paths.get("img/8.gif"))) ;
            比較用台数データ.put(9,Files.readAllBytes(Paths.get("img/9.gif"))) ;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * お店情報
     */
    Mstore mstore;

    /**
     * 台数データのハッシュ
     */
    Map<String, Integer> 台数Hash = new HashMap<>();

    public パチンコ情報解析(Mstore mstore) {
        this.mstore = mstore;
    }

    /**
     * 店舗ページにパチンコ情報が存在するかどうかの確認
     *
     * @return 存在時はselectを返す しない場合は null
     */
    public String 要素取得(Document doc) {
        switch (this.mstore.getName()) {
            case "アジアセンター":
                return "[name=\"kisyu\"] table";

        }

        List<String> checkList = Arrays.asList(
                "table[bordercolorlight=\"#FF0066\"] table"
                , "[name=\"kisyu\"] table"
        );

        for (String select : checkList) {
            int size = doc.select(select).size();
            if (0 < size) {
                return select;
            }
        }


        if (-1 < doc.html().indexOf("パ　チ　ン　コ")) {
            return "[name=\"kisyu\"] table";
        }
        return null;
    }

    public boolean 存在チェック(Document doc) {

        String select = this.要素取得(doc);

        if (select == null || doc.select(select).size() == 0) {
            return false;
        }
        return true;
    }

    public Integer 台数取得(Element el) {
        StringBuilder 台数 = new StringBuilder();
        for (Element imgEl : el.select("img")) {
            String imageName = imgEl.attr("src");
            //DB存在チェック
            if (TNumber.url存在チェック("http://www.p-world.co.jp" + imageName)) {
                TNumber number = TNumber.findByUrl("http://www.p-world.co.jp" + imageName);
                台数Hash.put(imageName, Integer.parseInt(number.getNum()));
            }


            //画像を比較して数字の取得
            if (!台数Hash.containsKey(imageName)) {
                try {
                    byte[] data = 画像取得("http://www.p-world.co.jp" + imageName);

                    if (data == null) {
                        System.out.println("画像が取得できなかったのでURLを確認" + this.getMstore().getLink());
                        continue;
                    }

                    for (Integer imgKey : 比較用台数データ.keySet()) {
                        if (Arrays.equals(data, 比較用台数データ.get(imgKey))) {
                            台数Hash.put(imageName, imgKey);

                            TNumber number = new TNumber();
                            number.setUrl("http://www.p-world.co.jp" + imageName);
                            number.setNum(imgKey.toString());
                            Ebean.save(number);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //ここに入ることはあるのか？
            if (!台数Hash.containsKey(imageName)) {
                continue;
            }
            //対象外のデータ
            if (台数Hash.get(imageName) == 99 || 台数Hash.get(imageName) == 100) {
                continue;
            }

            台数.append(台数Hash.get(imageName));

        }

        try {
            return Integer.parseInt(台数.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public static byte[] 画像取得(String urlStr) throws Exception {
        //URLオブジェクト作成
        URL url = new URL(urlStr.replace("../", "/"));

        //URLからInputStreamオブジェクトを取得（入力）
        InputStream in = null;
        int index = 0;
        while (in == null) {
            if (index == 5) {
                System.out.println("画像取得失敗:" + urlStr);
                return null;
            }

            try {
                Thread.sleep(2000);

                URLConnection uc = url.openConnection();
                uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.63 Safari/537.36");// ヘッダを設定
                uc.setRequestProperty("referer", "http://www.p-world.co.jp/");
                uc.setConnectTimeout(10000);
                uc.setReadTimeout(10000);
                in = uc.getInputStream();

            } catch (FileNotFoundException e) {
                System.out.println("ファイルが存在しない");
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                System.out.println("URL読込みでエラー");
                e.printStackTrace();
                Thread.sleep(60000);
                index++;
            }
        }
        byte[] fileContentBytes = null;
        try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
            fileContentBytes = IOUtils.toByteArray(bis);
        } catch (IOException ex) {

        }
        return fileContentBytes;
    }

    public Elements 機種カラム取得(Element el) {
        return el.select(
                "td[background=\"../img/pachi_bk.gif\"]"
                        + ",td[bgcolor=\"#99CCFF\"]"
                        + ",td[bgcolor=\"#99CCCC\"]"
                        + ",td[bgcolor=\"#CCCC99\"]"
        );
    }

    public List<設置機種情報> 解析処理1(Document doc) {
        if (!this.存在チェック(doc)) {
            System.out.println("パチンコ情報が存在しない" + this.mstore.getLink());
            return null;
        }

        String select = this.要素取得(doc);

        if (select == null || doc.select(select).size() == 0) {
            System.out.println("要素取得失敗" + this.mstore.getLink());
            return null;
        }

        Element パチンコテーブル = doc.select(select).get(0);
        Elements trEls = パチンコテーブル.select("tr");


        List<設置機種情報> 設置機種情報List = new ArrayList<>();

        String key = "";


        for (Element tr : trEls) {
            //タイトルを無視する。
            if (tr.select("td").size() == 0) {
                continue;
            }
            String titleText = tr.select("td").get(0).text();


            //切り替えタイミング
            if (-1 < titleText.indexOf("】 パチ")) {

                key = titleText;
                continue;
            }

            for (Element td : 機種カラム取得(tr)) {
                Elements crTd = td.select("table td");
                if (crTd.text().replace(" ", "").replace("　", "").equals("")) {
                    continue;
                }

                if (crTd.get(0).text().replace("　", "").isEmpty()) {
                    continue;
                }

                Integer 台数 = 台数取得(crTd.get(1));
                Elements リンク = crTd.get(0).select("a");

                設置機種情報 機種情報 = new 設置機種情報();
                機種情報.set台数(台数);
                機種情報.setName(crTd.get(0).text());
                if (1 <= リンク.size()) {
                    機種情報.setLink(crTd.get(0).select("a").get(0).attr("href"));
                }
                機種情報.set貸し玉料金(key);
                設置機種情報List.add(機種情報);

            }


        }
        return 設置機種情報List;
    }

    public List<店舗情報解析.設置機種情報> モバイル解析処理() {

        Document doc = JsoupHelper.run(this.getMstore().getSpLink(),ユーザーエージェント);
        return モバイル解析処理(doc);
    }

    public List<店舗情報解析.設置機種情報> モバイル解析処理(Document doc) {
        Map<String, Object> json;
        List<店舗情報解析.設置機種情報> 設置機種情報List = new ArrayList<>();
        try {
            json = getJson(doc.html());
            設置機種情報List = パチンコ情報解析(json);
        } catch (JSONException e) {
            System.out.println(this.getMstore().getSpLink() + ":JSON変換失敗" );
            e.printStackTrace();
            return 設置機種情報List;
        }

        return 設置機種情報List;
    }

    public void spLink() {
        Document doc = JsoupHelper.run(this.getMstore().getLink(), ユーザーエージェント);
        if (doc == null) {
            return;
        }
        if (doc.select(".hall-menu li a").size() == 0) {
            System.out.println(this.getMstore().getLink() + "ホール情報が存在しない");
            return;
        }

        String url = doc.select(".hall-menu li a").get(1).attr("href");
        System.out.println(SP_URL + url);

        if (this.getMstore().getSpLink() == null) {
            System.out.println("モバイルURL保存");
            this.getMstore().setSpLink(SP_URL + url);
            Ebean.update(this.getMstore());
        }
    }

    private Map<String,Object> getJson(String html) {
        int start = html.indexOf("HallView.hall_kisyu_view(") + "HallView.hall_kisyu_view(".length();
        int end = html.lastIndexOf("</script>");

        String jsonStr = html.substring(start,end);
        end = jsonStr.indexOf("});") + 1;
        jsonStr = jsonStr.substring(0, end);

        JSON json = new JSON();
        Map<String,Object> map = (Map)json.parse(jsonStr);
        return map;
    }

    private List<店舗情報解析.設置機種情報> パチンコ情報解析(Map<String,Object> jsonMap) {
        Map json = (Map)jsonMap.get("json");

        if(!json.containsKey("pachi")) {
            System.out.println("パチンコ情報が存在しない");
            return null;
        }


        List<Map> pachi = (List)json.get("pachi");
        List<店舗情報解析.設置機種情報> 設置機種情報List = new ArrayList<>();

        pachi.forEach(s-> {
            String yen = (String) s.get("yen");
            List<Map> kisyus = (List) s.get("kisyus");
            kisyus.forEach(kisyu->{
                店舗情報解析.設置機種情報 情報 = new 店舗情報解析.設置機種情報();

                if(!kisyu.containsKey("kisyu_code")) {
                    System.out.println("機種コードが存在しない");
                    return;
                }

                Mmodels model = Mmodels.likeByCode(kisyu.get("kisyu_code").toString());

                情報.set貸し玉料金(yen);
                if(model!=null) {
                    情報.setLink(model.getLink());
                    情報.setName(model.getName());
                } else {
                    String code = kisyu.get("kisyu_code").toString();
                    Mmodels mmodels = Mmodels.likeByCode(code);

                    if(mmodels==null) {
                        Ebean.beginTransaction();
                        Mmodels newMmodels = new Mmodels();
                        newMmodels.setName(kisyu.get("kisyu_name").toString());
                        newMmodels.setLink("http://www.p-world.co.jp/machine/database/" + code);
                        Ebean.save(newMmodels);
                        Ebean.commitTransaction();
                    }

                    情報.setName(kisyu.get("kisyu_name").toString());
                }

                if(kisyu.containsKey("daisuu")) {
                    情報.set台数( Integer.parseInt(kisyu.get("daisuu").toString()) );
                }
                設置機種情報List.add(情報);
            });
        });
        return 設置機種情報List;
    }
}