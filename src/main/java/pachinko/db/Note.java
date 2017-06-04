package pachinko.db;

import com.avaje.ebean.Ebean;
import lombok.Data;
import net.arnx.jsonic.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gyutr20 on 2016/12/06.
 */

@Data
@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Lob
    private String text;

    private Float version;


    private static StringBuffer Log = new StringBuffer();

    @lombok.Data
    static class ResultData {
        Note note;
        List<Element> imgElsList;

        Document doc;
        List<Map<String,String>> jsons;

        public void remove() {
            for (int i=0;i<imgElsList.size();i++) {
                Element el = imgElsList.get(i);
                String urlStr = el.attr("src").replace("\\\"", "");

                if(-1==urlStr.indexOf("http")) {
                    urlStr = "http://" + urlStr;
                }

                try {
                    URL url = new URL(urlStr);
                    sourceRemove(url.getHost(),el);
                    el.remove();
                } catch (MalformedURLException e) {
                    System.out.println("URL不正" + urlStr);
                    return;
                }

            }
        }

        public void sourceRemove(String domain, Element imgEl) {
            Elements sourceEls = doc.select(".source-url");
            for(int i=0;i<sourceEls.size();i++) {
                String href = sourceEls.select("a").get(0).attr("href");
                if(0<=href.indexOf(domain)) {
                    System.out.println(sourceEls.text());
                }

            }
        }
    }
    public static void main(String... args) throws IOException {
        List<Note> list = Ebean.createQuery(Note.class).where().eq("published",1).eq("status",2).orderBy("published_at desc").findList();
        Map<String,List<String>> result = new HashMap<>();
        List<ResultData> removeList = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            Note note = list.get(i);
            System.out.println("------------------------------" + note.getId() + "チェック開始------------------------------" );
            Log.append("------------------------------" + note.getId() + "チェック開始------------------------------" );
            Log.append("\n");
            Log.append("|||" + note.title + "|||" );
            Log.append("\n");
            if( note.getVersion() < 3 ) {
                List<Map<String,String>> jsons = (List) JSON.decode(note.getText());
                v2ImgRemove(jsons);
                String josonData = JSON.encode(jsons);
                note.setText(josonData);
                if(note.getId()==47487807) {
                    continue;
                }
                Ebean.save(note);
            } else {
                Document doc = Jsoup.parse("<!DOCTYPE html><head></head><body>" + note.getText() + "</body></html>");
                Elements el = doc.select("img");
                List<Element> removeImgList =  getRemoveImags(el);
                v3ExecImgRemove(doc,removeImgList);
                note.setText(doc.select("body").html());

                Ebean.save(note);
            }
            System.out.println("---------------" + note.getId() + "チェック終了---------------" );
            Log.append("---------------" + note.getId() + "チェック終了---------------");
            Log.append("\n");
            Log.append("\n");
            Log.append("\n");


        };

        File file = new  File("/Users/gyutr20/log.text");
        if(!file.isFile()) {
            file.createNewFile();
        }
        Path path = new File("/Users/gyutr20/log.text").toPath();
        try {
            Files.write(path,Log.toString().getBytes(),StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Element> getRemoveImags(Elements el) {
        List<Element> result = new ArrayList<>();
        for(int elIndex=0;elIndex<el.size();elIndex++) {
            Element imgEl = el.get(elIndex);
            String url = imgEl.attr("src");
            if(-1==url.indexOf("u-note.me") && -1==url.indexOf("flickr") ) {
                result.add(imgEl);
            }
        }
        return result;
    }

    public static List<Map<String,String>> v2ImgRemove(List<Map<String,String>> json) {

        for( Map<String,String> value:json ) {
            if(!value.containsKey("post")) {
                continue;
            }
            String text = value.get("post");
            Document doc = Jsoup.parse("<!DOCTYPE html><head></head><body>" + text + "</body></html>");
            Elements imgEl = doc.select("img");
            List<Element> removeImags = getRemoveImags(imgEl);
            if(0<removeImags.size()) {
                v2ExecImgRemove(removeImags);
                value.put("post",doc.select("body").html());
            }
        }
        return json;
    }

    public static void v2ExecImgRemove(List<Element> removeImags) {
        for(Element el:removeImags) {

            System.out.println(el.attr("src") + "削除" );
            Log.append(el.attr("src") + "削除");
            Log.append("\n");

            if(1<=el.parents().select(".image").size()) {
                el.parents().select(".image").remove();
            } else if(1<=el.parents().select(".ResultImageBox").size()) {
                el.parents().select(".ResultImageBox").remove();
            } else if(1<=el.parent().select("a").size()) {
                el.parent().select("a").remove();
            } else {
                el.remove();
            }
        }
    }


    public static void v3SourceRemove(Document doc,String domain, Element imgEl) {
        Elements sourceEls = doc.select(".source-url");
        for(int i=0;i<sourceEls.size();i++) {
            String href = sourceEls.select("a").get(0).attr("href");
            if(0<=href.indexOf(domain)) {
                System.out.println(sourceEls.text() + "出典削除");
                Log.append(sourceEls.text() + "出典削除");
                Log.append("\n");
                sourceEls.remove();
            }

        }
    }

    public static void v3ExecImgRemove(Document doc, List<Element> imgElsList) {
        for (int i=0;i<imgElsList.size();i++) {
            Element el = imgElsList.get(i);
            String urlStr = el.attr("src").replace("\\\"", "");

            if(-1==urlStr.indexOf("http")) {
                urlStr = "http://" + urlStr;
            }

            try {
                URL url = new URL(urlStr);
                //外部サイト
                if(1<=el.parent().select(".ext-link-img").size()) {
                    System.out.println(urlStr + "外部リンク画像削除");
                    Log.append(urlStr + "外部リンク画像削除");
                    Log.append("\n");
                    el.parent().select(".ext-link-img").remove();
                } else {
                    System.out.println(urlStr + "画像削除");
                    Log.append(urlStr + "画像削除");
                    Log.append("\n");
                    el.remove();
                    v3SourceRemove(doc,url.getHost(),el);
                }

            } catch (MalformedURLException e) {
                System.out.println("URL不正" + urlStr);
                return;
            }

        }
    }

}
