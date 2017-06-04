package pachinko.p_word.old;

import com.avaje.ebean.Ebean;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.Mstore;
import pachinko.library.JsoupHelper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by t-sakazume on 2015/01/26.
 */
public class 一覧ページ解析 {
    String url;


    public static void マスターデータ作成() {

        //都道府県リンクの取得
        Map<String,String> 都道府県Map = new HashMap<>();

        Document doc = JsoupHelper.run("http://www.p-world.co.jp/index.html");
        Elements els = doc.select("[name*=\"map\"]");
        els.forEach(s->{

            s.select("area").forEach(el->{
                if(el.attr("alt").equals("パチンコ店登録")) {
                    return;
                }

                String href = el.attr("href");
                都道府県Map.put(el.attr("alt"),"http://www.p-world.co.jp/" + href);
            });
        });


        //都道府県から市町村情報をCSVで抽出
        都道府県Map.entrySet().parallelStream().forEach(s-> {

            Document areaDoc = JsoupHelper.run(s.getValue());
            Elements areaEls = areaDoc.select(".areaList a");

            List<String> writerDataList = new ArrayList<String>();
            areaEls.forEach(el-> {
                String href = el.attr("href");

                String data = el.text() + ",,,";
                data = data + "http://www.p-world.co.jp" + href;
                writerDataList.add(data);
            });

            URL resource = 一覧ページ解析.class.getClassLoader().getResource("address");
            File file = new File(resource.getPath());
            try {
                file = new File(file.getAbsolutePath(),s.getKey() + ".text");
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Path dst = null;
            try {
                dst = file.toPath();
                Path r = Files.write(dst, writerDataList, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        JsoupHelper.end();
    }

    public static void 一覧ページ解析(String url) throws IOException {
        Document doc = JsoupHelper.run(url);

        if(doc==null || doc.toString().isEmpty()) {
            System.out.println("取得失敗");
        }

        Elements ホール情報まとめ = doc.select(".hallDetail");

        if(ホール情報まとめ.size()==0) {
            System.out.println("向こうで規制が入ったぽい");
            System.out.println(doc.html());
        }

        for(Element ホール情報:ホール情報まとめ) {
            Mstore store = new Mstore();

            Element table = ホール情報.select("table").get(0);

            String link = table.select(".hallLink").get(0).attr("href");
            link = "http://www.p-world.co.jp" + link;

            if(Mstore.リンク存在チェック(link)) {
                store = Mstore.findByLink(link);
            }


            String address = table.select(".address font").get(0).text();
            address = address.replace("周辺","").replace(" ","").replace("｜","");

            String name =  table.select(".hallLink").get(0).text();

            store.setLink(link);
            store.setName(name);
            store.setAddress(address);
            Ebean.save(store);

        }

        if(doc.select(".pageNavi span").size()<2) {
            return ;
        }
        Elements nextLink  = doc.select(".pageNavi span").get(1).select("a");
        if(1<=nextLink.size() ) {
            String nextUrl = "http://www.p-world.co.jp/_machine/kensaku.cgi" + nextLink.attr("href");
            一覧ページ解析(nextUrl);
        }
    }


    /**
     * 一覧ページの一覧ファイルを読込む
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Map<String,List<String>> 一覧ページURL一覧読込() throws IOException, URISyntaxException {
        URL url = 一覧ページ解析.class.getClassLoader().getResource("address/");
        Path dir = Paths.get(url.toURI());

        //address配下のファイルをfileName,中身arrayで読み込み
        Map<String,List<String>> addressMap = new HashMap<String,List<String>>();

        try (Stream<Path> stream = Files.list(dir)) {
            stream
                .filter(entry -> entry.getFileName().toString().endsWith(".text"))
                .forEach(file -> {
                    List<String> readData = null;
                    try {
                        readData = Files.readAllLines(file);
                        addressMap.put(file.getFileName().toString(),readData);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }

        return addressMap;
    }

    public static void main(String args[]) throws IOException, URISyntaxException {

//        マスターデータ作成();


        Map<String,List<String>> addressMap = 一覧ページURL一覧読込();
        addressMap.entrySet().parallelStream().forEach(data-> {
            System.out.println(data.getKey() + "開始");
            data.getValue().parallelStream().forEach(s -> {
                try {
                    String url = s.split(",,,")[1];
                    if (url == null) {
                        System.out.println("何故かURLがnull");
                        return;
                    }

                    一覧ページ解析(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println(data.getKey() + "終了");

        });
        JsoupHelper.end();

//            一覧ページ解析("http://www.p-world.co.jp/_machine/kensaku.cgi?city=,%c0%ee%ba%ea%bb%d4%c2%bf%cb%e0%b6%e8,&dir=kanagawa");

    }

}
