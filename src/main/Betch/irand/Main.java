package irand;

import com.avaje.ebean.Ebean;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.Irand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gyutr20 on 2016/10/31.
 */
public class Main {
//    public static void main(String args[]) throws IOException {
//        Document document = Jsoup.connect("http://papimo.jp/h/00031715/hit/index_sort/213110004/1-20-81240").get();
//        Elements els = document.select(".sort a");
//        List<String> array = new ArrayList<>();
//        els.stream().forEach(s->{
//            array.add(s.attr("href"));
//        });
//    }

    public static void main(String... args) throws IOException {
        test();
    }

    public static void test() throws IOException {
        String url = "http://papimo.jp/h/00031715/hit/view/1062/20170112";
        Document document = Jsoup.connect(url).get();
        Element table = document.select("#tab-history-index table").get(0);
        List<Result> results = new ArrayList<>();

        String 台番号 = document.select(".ttl-unit .unit_no").text();

        table.select("tr").stream().forEach(trEl->{

            if(trEl.select("td").size()==0) {
                return;
            }

            Elements td = trEl.select("td");
            Result ret = new Result(td);
            Irand data = new Irand();
            data.create(td);
            data.set台番号(台番号);
            Ebean.save(data);
            results.add(ret);
        });

        System.out.println(table.html());
    }

    @Data
    static class Result {
        String 回数;
        String 時間;
        String スタート;
        String 出メダル;
        String ステータス;

        public Result(Elements el) {
            回数 = el.get(0).text();
            時間 = el.get(1).text();
            スタート = el.get(2).text();
            出メダル = el.get(3).text();
            ステータス = el.get(4).text();
        }
    }
}
