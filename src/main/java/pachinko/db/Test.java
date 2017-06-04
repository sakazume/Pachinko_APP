package pachinko.db;

import com.avaje.ebean.Ebean;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by gyutr20 on 2015/06/24.
 */
public class Test {



    public static void main(String args[]) throws IOException {

        // ベーシック認証のIDとパスワードを設定
        String basicID = "u-note";
        String basicPass = "yut0k01de";
        // IDとパスワードを送信用に結合
        String basicIdPass = basicID + ":" + basicPass;
        // IDとパスワードをベーシック認証用にbase64でエンコード
        String basicIdPass64 = Base64.getEncoder().encodeToString(basicIdPass.getBytes());


        // ヘッダへ設定する情報を用意
        String headKey = "Authorization";
        String headValue = "Basic " + basicIdPass64;

        System.out.println(new File("").getAbsolutePath());
        Path path = Paths.get("src/main/resources/test.txt");
        Files.lines(path);
        try (Stream<String> stream = Files.lines(path)) {
            stream.parallel().forEach(s -> {
                s = s.replace(",", "");
                String url = "http://54.199.247.230/note/" + s;
                System.out.println(s);

                try {
                    Jsoup.connect(url).header(headKey, headValue).timeout(20000).get();
                } catch (HttpStatusException e) {
                    System.out.println("404");
                    return;
                } catch (IOException e) {
                    System.out.println("IOException");
                    return;
                }
            });
        }



//        ids.parallelStream().forEach(s->{
//            System.out.println(s);
//            String url = "http://54.199.247.230/note/" + s;
//            try {
//                Jsoup.connect(url).header(headKey, headValue).timeout(20000).get();
//            } catch (HttpStatusException e) {
//                System.out.println("404");
//                return;
//            } catch (IOException e) {
//                System.out.println("IOException");
//                return;
//            }
//        });
    }
}
