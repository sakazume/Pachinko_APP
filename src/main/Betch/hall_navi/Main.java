package hall_navi;

import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gyutr20 on 2016/11/11.
 */
public class Main {
    static String 東京 = "https://hall-navi.com/serch_hole_result?k=1&c=all&area=kanto";
    static String 埼玉 = "https://hall-navi.com/serch_hole_result?k=3&c=all&area=kanto";
    static String 神奈川 = "https://hall-navi.com/serch_hole_result?k=2&c=all&area=kanto";
    static String URL = 東京;

    public static void main(String... args) throws IOException {
        Document doc = Jsoup.connect(URL).get();
        Elements els = doc.select(".mgn_sche_list_base a");

        List<String> array = new ArrayList<>();
        els.parallelStream().forEach(s->{
            String linkUrl = s.attr("href").replace("./","https://hall-navi.com/");
            array.add(linkUrl);
            System.out.println(linkUrl);
        });

        List<HallData> hallList = new ArrayList<>();
        array.stream().forEach(s->{
            System.out.println(s + "開始");
            Document doc1;
            try {
                doc1 = Jsoup.connect(s).get();
            } catch (IOException e) {
                System.out.println(s + "失敗");
                return;
            }
            String hollName = doc1.select(".box_hole_view_hole_name").text();
            Element hallEl = doc1.select(".flat_inner_os").get(0);
            if(hallEl.text()=="情報はありません") {
                return;
            }
            String dayNum = hallEl.select(".samai_plus").text();
            HallData data = new HallData(hollName,dayNum);
            hallList.add(data);
            System.out.println(hollName + ":" + dayNum);
        });


    }

    @Data
    public static class HallData {
        String hollName;
        String dayNum;

        public HallData(String hollName, String dayNum) {
            this.hollName = hollName;
            this.dayNum = dayNum;
        }
    }
}
