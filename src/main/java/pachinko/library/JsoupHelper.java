package pachinko.library;

import com.avaje.ebean.Ebean;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pachinko.db.DhtmlCache;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by t-sakazume on 2015/01/28.
 */
public class JsoupHelper implements Callable<Document> {
    private Boolean CHECK_FLG = false;


    public static String ユーザーエージェント_PC = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.63 Safari/537.36";

    private static ExecutorService pool = Executors.newFixedThreadPool(1);
    private static String ユーザーエージェント = ユーザーエージェント_PC;

    public static Long SLEEP_TIME = 1500L;
    public static Map<String,DhtmlCache> dhtmlCacheMap = new HashMap<>();



    public static Document run(String url ){
        return run(url,ユーザーエージェント);
    }

    public static Document run(String url,String ua) {


        Future<Document> future = pool.submit(new JsoupHelper(url,ua));
        try {

            Document doc = future.get();

            if(doc==null) {
                System.out.println("HTML取得失敗");
                return doc;
            }

            return doc;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void end() {
        pool.shutdown();
    }


    public JsoupHelper(String url,String ua) {
        this.url = url;
        this.ua = ua;
    }
    String url;
    String ua;

    @Override
    public Document call() throws Exception {
        Document doc = cacheCheck();
        if(doc != null) {
            return doc;
        }

        doc =  getDocument(this.url);

        if(doc == null) {
            return null;
        }

        if(!CHECK_FLG) {
            return doc;
        }

        DhtmlCache dHtmlCache = DhtmlCache.findByUrlAndUa(this.url, this.ua);
        if(dHtmlCache==null) {
            dHtmlCache = new DhtmlCache();
        }
        dHtmlCache.setUrl(url);
        dHtmlCache.setUa(this.ua);
        dHtmlCache.setHtml(doc.html());

        try {
            Ebean.save(dHtmlCache);
        } catch(PersistenceException e){
            System.out.println("DB書き込み失敗" + url);

        }
        //ハッシュマップにデータ登録
        DhtmlCache newModel= new DhtmlCache();
        newModel.setHtml(dHtmlCache.getHtml());
        newModel.setUrl(dHtmlCache.getUrl());
        newModel.setUa(dHtmlCache.getUa());
        String key = dHtmlCache.getUrl() + "_" + dHtmlCache.getUa();

        dhtmlCacheMap.put(key , newModel);

        return doc;
    }

    public Document getDocument(String url) {
        return this.getDocument(0,url);
    }

    public Document cacheCheck() {

        if(!CHECK_FLG) {
            return null;
        }


        Document doc;
        DhtmlCache dHtmlCache;

        if(dhtmlCacheMap.containsKey(this.url + "_" + this.ua)) {
            dHtmlCache = dhtmlCacheMap.get(this.url + "_" + this.ua);
        } else {
            dHtmlCache = DhtmlCache.findByUrlAndUa(this.url , this.ua);
        }


        if (dHtmlCache != null && !dHtmlCache.isSave()) {
            System.out.println("キャッシュあり");
            String key = dHtmlCache.getUrl() + "_" + dHtmlCache.getUa();
            dhtmlCacheMap.put(key,dHtmlCache);

            doc = Jsoup.parse(dHtmlCache.getHtml());
            if(pwordCheck(url,doc)) {
                return Jsoup.parse(dHtmlCache.getHtml());
            }
        }

        return null;
    }

    private static Long t1 = System.nanoTime();
    private static final int DIVISOR = 1000000;
    private Document getDocument(Integer ret,String url) {
        //処理時間
        Long time = (System.nanoTime() - this.t1) / DIVISOR;

        if(0<=time - SLEEP_TIME) {
            Long sleepTIme = Math.abs(time - SLEEP_TIME);
            if(SLEEP_TIME<=sleepTIme) {
                sleepTIme = SLEEP_TIME;
            } else {
                System.out.println("sleep=" + sleepTIme);
                sleep(sleepTIme);
            }
        }


        if(3<ret) {
            return null;
        }
        try {

            Document doc = Jsoup.connect(url)
                    .referrer(url)
                    .userAgent(this.ua)
                    .timeout(6000).get();

            this.t1 = System.nanoTime();
            if(!pwordCheck(url,doc)) {
                System.out.println("リトライ処理");

                sleep(15 * 1000L);
                return getDocument(ret+1,url);
            }


            return doc;
        } catch (HttpStatusException httperr) {
            System.out.println("ページエラー" + httperr.getStatusCode());
            return null;
        } catch (Exception e) {
            this.t1 = System.nanoTime();
            return getDocument(ret+1,url);
        }
    }

    private void sleep(Long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * PWORDの情報が取得できているか確認
     * @param doc
     * @return
     */
    public boolean pwordCheck(String url, Document doc) {

//        if(-1==url.indexOf("p-word")) {
//            System.out.println("p-word以外");
//            return true;
//        }

        // 機種情報の場合
        if(0<=url.indexOf("database")) {
            if(doc.select(".outer .detail").size()==0) {
                return false;
            }
        }


        Elements el = doc.select("p font");

        if(el.size()<2) {
            return true;
        }

        String s = el.get(1).text();
        if(1<=s.indexOf("ホール情報は、ココをクリック")) {
            return false;
        }
        return true;


    }



}
