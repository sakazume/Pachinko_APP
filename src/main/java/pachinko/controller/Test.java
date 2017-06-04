package pachinko.controller;

import common.controller.AbstractController;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pachinko.db.DhtmlCache;
import pachinko.db.Mstore;
import pachinko.library.JsoupHelper;
import sun.awt.image.ImageFormatException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created by gyutr20 on 2015/10/26.
 */
@Path("/test")
public class Test extends AbstractController {
    @Data
    static class StoreImg {
        Mstore mstore;
        List<String> imgList;
        List<Map<String,String>> imgListMap = new ArrayList<>();


        public Map<String,Object> createModel() {
            Map<String,Object> map = new HashMap<>();
            map.put("mstore", mstore);
            map.put("imglist",imgList);
            map.put("imglistmap",imgListMap);

            return map;
        }
    }
    public Elements getImageLink(String url) {
        Document doc = JsoupHelper.run(url);
        if(doc==null) {
            return null;
        }

        if(doc.select("img").size()==0) {
            return null;
        }
        doc.select("img").stream().forEach(img->{
            if(0<=img.attr("src").indexOf("number")){
                img.remove();
            }
            if(0 != img.attr("src").indexOf("http")) {
                img.remove();
            }

            if(-1 == img.attr("src").indexOf("p-world")) {
                img.remove();
            }
        });
        return doc.select("img");
    }
    @GET
    @Path("/")
    public String test(String ken) {
        List<Mstore> mstoreList = Mstore.likeByAddress(ken);

        List<Map<String,Object>> listMap = new ArrayList<>();

        Map<String,Integer> indexMap = new HashMap<>();

        mstoreList.parallelStream().forEach(s -> {

            indexMap.put(s.getName(),0);

            StoreImg storeImg = new StoreImg();

            storeImg.setMstore(s);
//            getImageLink(s.getLink());
            String url = s.getLink();
            Document doc = JsoupHelper.run(url);
            List<String> imgList = new ArrayList<>();
            List<Map<String,String>> imgListmap = new ArrayList<>();
            if(doc==null) {
                return;
            }

            if(doc.select("img").size()==0) {
                return;
            }

            doc.select("img").stream().forEach(img -> {
                Integer index = indexMap.get(s.getName());


                if(0<=img.attr("src").indexOf("number")){
                    return;
                }

                if(index==null) {
                    System.out.println("indexがなぜかnull" + s.getName());
                    return;
                }
                if(10<=index) {
                    return;
                }
                if(0 != img.attr("src").indexOf("http")) {
                    return;
                }

                if(-1 == img.attr("src").indexOf("p-world")) {
                    return;
                }

                try {
                    String urlEn = new URI(img.attr("src")).toASCIIString();
                    BufferedImage imgData = ImageIO.read( new URL(urlEn) );

                    int height = imgData.getHeight();
                    if(height < 200) {
                        return;
                    }


                    Integer width = imgData.getWidth();
                    double rate = 1.0F;
                    if(width < 300) {
                        rate = (double)width / 300;
                        height = (int) Math.floor(height * rate);
                    } else {
                        rate = (double)300 / width;
                        height = (int) Math.floor(height * rate);
                    }


                    imgList.add(img.attr("src"));


                    Map<String,String>imgMap = new HashMap<>();
                    imgMap.put("src",img.attr("src"));
                    imgMap.put("height",String.valueOf(height));
                    imgMap.put("width",String.valueOf(width * rate));
                    imgListmap.add(imgMap);

                    index++;
                    indexMap.put(s.getName(),index);
                } catch (IOException e) {
                    System.out.println( "IOException" + img.attr("src") );
                    return;
//                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    System.out.println( "URISyntaxException" + img.attr("src") );
//                    e.printStackTrace();
                    return;
                } catch(Exception e) {
                    System.out.println( "その他エラー" + e.getClass() + img.attr("src") );
                    return;
                }

            });
            storeImg.setImgList(imgList);
            storeImg.setImgListMap(imgListmap);

            listMap.add(storeImg.createModel());
        });
        HashMap<String,Object> model = new HashMap<>();
        model.put("data",listMap);
        String html = this.render("/event.jade", model);
        return html;
    }
    public static void main(String... args) {

        List<String> list = Arrays.asList("埼玉","千葉","東京");

        Test test = new Test();


        for(String s:list) {
            String html = test.test(s);
            java.nio.file.Path srcDir = Paths.get("/Users/gyutr20/u-note/test/" + s + ".html");
            try (OutputStream os = Files.newOutputStream(srcDir)) {
                os.write(html.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        JsoupHelper.end();
    }

}
