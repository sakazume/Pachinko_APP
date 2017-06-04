package pachinko.p_word;

import com.avaje.ebean.Ebean;
import lombok.Data;
import net.arnx.jsonic.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pachinko.db.DhtmlCache;
import pachinko.db.Dstore;
import pachinko.db.Mmodels;
import pachinko.db.Mstore;
import pachinko.library.JsoupHelper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by gyutr20 on 2015/10/26.
 */
public class Test {
    @Data
    static class StoreImg {
        Mstore mstore;
        List<String> imgList;
    }

    public static void main(String... args) {
//        List<Mstore> list = Mstore.likeByAddress("埼玉");

//        List<Mstore> list = Mstore.likeByName("マルハン");
//
//        List<StoreImg> listMap = new ArrayList<>();
//
//        list.parallelStream().forEach(s -> {
//            StoreImg storeImg = new StoreImg();
//
//            storeImg.setMstore(s);
//
//            String url = s.getLink();
//            DhtmlCache dHtmlCache = DhtmlCache.findByUrlAndUa(url,JsoupHelper.ユーザーエージェント_PC);
//            Document doc = Jsoup.parse(dHtmlCache.getHtml());
//            List<String> imgList = new ArrayList<>();
//            doc.select("img").forEach(img -> {
//                imgList.add(s.getLink() + img.attr("src"));
//            });
//            storeImg.setImgList(imgList);
//            listMap.add(storeImg);
//        });
//        System.out.println("終了");

        Jedis jedis = new Jedis("localhost");
        jedis.set("foo", "bar");
        
        String value = jedis.get("foo");
        System.out.println(value);
    }


}
