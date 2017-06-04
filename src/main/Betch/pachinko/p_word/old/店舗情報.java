package pachinko.p_word.old;

import com.avaje.ebean.Ebean;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.*;
import pachinko.library.JsoupHelper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.*;

/**
 * P-WORDの店舗情報解析クラス
 */
@Data
public class 店舗情報 {

    static Map<String,Integer> 台数変換取得元 = new HashMap();
    static Map<Integer, byte[]> 台数変換比較データ = new HashMap<Integer, byte[]>();

    static {
        台数変換取得元.put("/number/GYnmaq40/JBRysFMewaN2g.gif" , 0);
        台数変換取得元.put("/number/GYnmaq40/JBDvATU97HGRk.gif" , 1);
        台数変換取得元.put("/number/GYnmaq40/JBAq7X1214RI.gif" , 2);
        台数変換取得元.put("/number/GYnmaq40/JBDmmLIAhgqTc.gif" , 3);
        台数変換取得元.put("/number/GYnmaq40/JB3MTmMv0XcM.gif" , 4);
        台数変換取得元.put("/number/GYnmaq40/JBetTLDILm8ls.gif" , 5);
        台数変換取得元.put("/number/GYnmaq40/JBhT3sFQBiqBI.gif" , 6);
        台数変換取得元.put("/number/GYnmaq40/JB34sYPEGexe6.gif" , 7);
        台数変換取得元.put("/number/GYnmaq40/JBRN4uaDWNce6.gif" , 8);
        台数変換取得元.put("/number/m42sMJGu/S69Tj1kyPqRdw.gif" , 9);



        try {
            台数変換比較データ.put(99,Files.readAllBytes(Paths.get("img/dai.gif"))) ;
            台数変換比較データ.put(100,Files.readAllBytes(Paths.get("img/dedama.gif"))) ;
            台数変換比較データ.put(0,Files.readAllBytes(Paths.get("img/0.gif"))) ;
            台数変換比較データ.put(1,Files.readAllBytes(Paths.get("img/1.gif"))) ;
            台数変換比較データ.put(2,Files.readAllBytes(Paths.get("img/2.gif"))) ;
            台数変換比較データ.put(3,Files.readAllBytes(Paths.get("img/3.gif"))) ;
            台数変換比較データ.put(4,Files.readAllBytes(Paths.get("img/4.gif"))) ;
            台数変換比較データ.put(5,Files.readAllBytes(Paths.get("img/5.gif"))) ;
            台数変換比較データ.put(6,Files.readAllBytes(Paths.get("img/6.gif"))) ;
            台数変換比較データ.put(7,Files.readAllBytes(Paths.get("img/7.gif"))) ;
            台数変換比較データ.put(8,Files.readAllBytes(Paths.get("img/8.gif"))) ;
            台数変換比較データ.put(9,Files.readAllBytes(Paths.get("img/9.gif"))) ;


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    Map<String,Integer> 台数変換 = new HashMap<>();
    Map<String,List<Map<String,String>>> パチ機種リスト = new HashMap<>();
    String url;
    Mstore mstore;

    /**
     * コンストラクタDBに登録されていなければ登録をする。
     * @param url
     */
    public 店舗情報(String url) {
        this.url = url;
        Mstore mstore = new Mstore();

        if(Mstore.リンク存在チェック(url)) {
            mstore = Mstore.findByLink(url);
        }

        try {
            mstore = 詳細ページ店舗情報解析(mstore);
            if(mstore==null) {
                System.out.println("情報取得失敗" + url);
                return;
            }

            mstore.setLink(url);
            Ebean.save(mstore);
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.mstore = Mstore.findByLink(url);


    }

    public Mstore 詳細ページ店舗情報解析(Mstore mstore) throws IOException {
        Document doc = JsoupHelper.run(this.url);
        if(doc==null) {
            return null;
        }
        for(Element el:doc.select("center p b")) {
            if(0<=el.text().indexOf("最終更新日")) {
                System.out.println(el.text());
            }
        }


        if(doc.select("table[bordercolorlight=\"#6699FF\"] table").size()==0) {
            System.out.println(this.url+ "お店情報が存在しない");
            return mstore;
        }

        Element table = doc.select("table[bordercolorlight=\"#6699FF\"] table").get(0);
        Elements trs = table.select("tr");

        for(Element tr : trs) {

            if(tr.select("td").size()<2) {
                continue;
            }

            String title = tr.select("td").get(0).text();
            String data = tr.select("td").get(1).text();

            title = title.replace("　","").replace(" ","");


            switch(title) {
                case "住所":
                    mstore.setAddress(data);
                    break;
                case "営業時間":
                    mstore.setHours(data);
                    break;
                case "電話":
                    mstore.setTel(data);
                    break;
            }
        }

        return mstore;

    }

    public void イベント情報取得(Document doc1,Document doc2) {
        Elements imgEls1 = doc1.select("img");
        Elements imgEls2 = doc2.select("img");
        Map<String,String> imgMap = new HashMap<>();

        //最新のHTMLから画像取得
        for(Element imgEl:imgEls2) {
            imgMap.put(imgEl.attr("src"),imgEl.attr("src"));
        }

        //一致する画像削除
        for(Element imgEl:imgEls1) {
            imgMap.remove(imgEl.attr("src"));

        }
        //差分があったら画像を保存
        if( 1<=imgMap.size() ) {
            for(Map.Entry<String,String>e:imgMap.entrySet()) {
                DstoreImg d = new DstoreImg();
                d.setStoreId(this.getMstore().getId());
                d.setImgUrl(e.getValue());
                Ebean.save(d);
            }
        }

    }

    public Map<String,List<Map<String,String>>> パチンコ設置情報解析() throws IOException {

        //旧情報
        DhtmlCache oldHtmlCache = DhtmlCache.findByUrl(this.url);
        Document oldDoc = null;
        if(oldHtmlCache!=null) {
            oldDoc = Jsoup.parse(oldHtmlCache.getHtml());
        }

        Document doc = JsoupHelper.run(this.url);

        if(oldHtmlCache != null) {
            this.イベント情報取得(oldDoc,doc);
        }

        if(-1==doc.html().replace(" ","").replace("　","").indexOf("パチンコ")) {
            System.out.println(this.mstore.getLink() + ":パチンコ情報なし");
            return new HashMap<>();
        }

        String select = パチンコ情報存在チェック(doc);
        if(select==null || doc.select(select).size()==0) {
            System.out.println(this.mstore.getLink() + ":パチンコテーブル取得出来ない");
            return new HashMap<>();
        }

        Elements tableEls = doc.select(select);

        Element tabelEl = tableEls.get(0);
        Elements trEls = tabelEl.select("tr");

        Map<String,List<Map<String,String>>> 機種リスト = new HashMap<>();
        List<Map<String,String>> 玉別機種リスト = new ArrayList<>();
        String key = "";


        for(Element tr:trEls) {

            //タイトルを無視する。
            if(tr.select("td").size()==0) {
                continue;
            }
            String titleText = tr.select("td").get(0).text();

            //切り替えタイミング
            if(-1<titleText.indexOf("】 パチ")) {

                //登録処理
                if(1<=玉別機種リスト.size()) {
                    機種リスト.put(key,玉別機種リスト);
                    玉別機種リスト = new ArrayList<>();
                }
                key = titleText;


            } else {
                for(Element td:tr.select(
                        "td[background=\"../img/pachi_bk.gif\"]"
                        + ",td[bgcolor=\"#99CCFF\"]"
                        + ",td[bgcolor=\"#99CCCC\"]"
                        + ",td[bgcolor=\"#CCCC99\"]"

                )) {
                    Elements crTd = td.select("table td");
                    if(crTd.text().replace(" ","").replace("　","").equals("")) {
                        continue;
                    }

                    if(!crTd.get(0).text().replace("　","").isEmpty()) {
                        Integer 台数 = 台数変換(crTd.get(1));
                        Elements リンク = crTd.get(0).select("a");

                        Map<String,String> crData = new HashMap<>();
                        crData.put("名前",crTd.get(0).text());
                        crData.put("台数", 台数.toString());

                        if(1<=リンク.size()) {
                            crData.put("リンク", "http://www.p-world.co.jp" + crTd.get(0).select("a").get(0).attr("href"));
                        }
                        玉別機種リスト.add( crData );
                    }
                }
            }
        }
        機種リスト.put(key,玉別機種リスト);
        this.パチ機種リスト = 機種リスト;

        return 機種リスト;
    }

    /**
     * 店舗ページにパチンコ情報が存在するかどうかの確認
     * @return 存在時はselectを返す しない場合は null
     */
    public String パチンコ情報存在チェック(Document doc) {
        switch(this.mstore.getName()) {
            case "アジアセンター":
                return "[name=\"kisyu\"] table";

        }

        List<String> checkList = Arrays.asList(
                    "table[bordercolorlight=\"#FF0066\"] table"
                    ,"[name=\"kisyu\"] table"
                );

        for(String select:checkList) {
            int size = doc.select(select).size();
            if(0<size) {
                return select;
            }
        }


        if(-1<doc.html().indexOf("パ　チ　ン　コ")) {
            return "[name=\"kisyu\"] table";
        }
        return null;
    }

    private Integer 台数変換(Element tdEl) {

        StringBuilder 台数 = new StringBuilder();
        for( Element imgEl:tdEl.select("img") ) {
            String key = imgEl.attr("src");


            //DB存在チェック
            if(TNumber.url存在チェック("http://www.p-world.co.jp" + key)) {
                TNumber number = TNumber.findByUrl("http://www.p-world.co.jp" + key);
                台数変換.put(key,Integer.parseInt(number.getNum()));
            }

            //画像を比較して数字のファイル名を捜索。
            if(!台数変換.containsKey(key)) {
                try {
                    byte[] data = 画像取得("http://www.p-world.co.jp" + key);
                    for (Integer imgKey : 台数変換比較データ.keySet()) {
                        if(Arrays.equals(data,台数変換比較データ.get(imgKey))) {
                            台数変換.put(key,imgKey);

                            TNumber number = new TNumber();
                            number.setUrl("http://www.p-world.co.jp" + key);
                            number.setNum(imgKey.toString());
                            Ebean.save(number);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //台の画像は対象外なので
            if(台数変換.containsKey(key)) {
                if( 台数変換.get(key)!=99 && 台数変換.get(key)!=100) {
                    台数.append(台数変換.get(key));
                }
            }
        }
        try {
            return Integer.parseInt(台数.toString());
        }catch (NumberFormatException e) {
            return 0;
        }
    }

    //テスト用データ
    static List<String> TEST_DATA;
    static {
        TEST_DATA = Arrays.asList(
            "http://www.p-world.co.jp/shimane/marusan-daito.htm"
//            "http://www.p-world.co.jp/tokyo/p-port-tachikawa.htm",
//            "http://www.p-world.co.jp/tokyo/towars-oumeic.htm",
//            "http://www.p-world.co.jp/tokyo/megagaia-chofu-pt.htm"
        );
    }
    public static void main(String args[]) throws Exception {


//        List<Mstore> list = Ebean.createQuery(Mstore.class).findList();
        List<Mstore> list = Mstore.likeByAddress("東京");


//        for(Mstore m:list) {
        list.parallelStream().forEach(m->{
            String url = m.getLink();
            System.out.println(url + "開始");

            店舗情報 店舗 = new 店舗情報(url);

            if(店舗.getMstore()==null) {
//                continue;
                return;
            }

            //一旦削除
            Dstore.deleteBystoreId(店舗.getMstore().getId());

            try {
                店舗.パチンコ設置情報解析();
            } catch (IOException e) {
                e.printStackTrace();
            }

            店舗機種情報登録(店舗);

//        }
        });
        System.out.println("終了");

        JsoupHelper.end();

    }

    public static void 店舗機種情報登録(店舗情報 店舗) {

        Map<String,List<Map<String,String>>> パチ機種 = 店舗.getパチ機種リスト();

        パチ機種.entrySet().parallelStream().forEach(e->{
            e.getValue().parallelStream().forEach(cr->{
                if(!cr.containsKey("リンク")) {
                    System.out.println("----------");
                    System.out.println(店舗.getUrl());
                    System.out.println(cr.get("名前")+":機種情報取得失敗");
                    System.out.println("----------");
                    return;
                }

                //機種情報が存在しない場合登録する。
                if(!Mmodels.リンク存在チェック(cr.get("リンク"))) {
                    パチ機種情報登録(cr);
                }

                String text = e.getKey();
                text = text.replace(" ","").replace("　","").replace("【","").replace("】","").replace("パチ","").replace("玉","").replace("円","");
                text = text.replace("／","/");



                Dstore dstore = new Dstore();
                dstore.setModels(Mmodels.findByLink(cr.get("リンク")));
                dstore.setStoreId(店舗.getMstore().getId());
                dstore.setNumber(Integer.parseInt(cr.get("台数")));

                if(0<text.indexOf("/")) {
                    Float x = Float.parseFloat(text.split("/")[0]);
                    Float y = Float.parseFloat(text.split("/")[1]);
                    Float xy = x/y;
                    dstore.setUnitPrice(xy.floatValue());
                } else {
                    try {
                        Float.parseFloat(text);
                        dstore.setUnitPrice(Float.parseFloat(text));
                    } catch (NumberFormatException ne) {

                    }
                }
                dstore.setUnitPriceText(e.getKey());

                Ebean.save(dstore);
            });

        });
    }


    public static void パチ機種情報登録(Map<String,String>cr) {
        Mmodels 登録データ = new Mmodels();
        登録データ.setName(cr.get("名前"));
        登録データ.setLink(cr.get("リンク"));
        Ebean.save(登録データ);
    }


    public static void 比較用画像取得() throws Exception {

        for (Map.Entry<String, Integer> e : 台数変換取得元.entrySet()) {

            //URLオブジェクト作成
            URL url = new URL("http://www.p-world.co.jp" + e.getKey());

            InputStream in = url.openStream();

            OutputStream out = new FileOutputStream("img/" + e.getValue() + ".gif");

            try {
                byte[] buf = new byte[1024];
                int len = 0;

                while ((len = in.read(buf)) > 0) {  //終わるまで書き込み
                    out.write(buf, 0, len);
                }

                out.flush();
            } finally {
                out.close();
                in.close();
            }
        }
    }

    public static byte[] 画像取得(String urlStr) throws Exception {
        //URLオブジェクト作成
        URL url = new URL(urlStr.replace("../","/"));

        //URLからInputStreamオブジェクトを取得（入力）
        InputStream in = null;
        int index = 0;
        while (in==null) {
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



//        fileContentBytes = Files.readAllBytes(Paths.get("img/tmp.gif"));
        return fileContentBytes;
    }
}
