package pachinko.library;

import com.avaje.ebean.Ebean;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pachinko.db.DhtmlCache;

import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by t-sakazume on 2015/01/28.
 */
public class JsoupHelper2 implements Callable<Document> {

    public static String ユーザーエージェント_PC = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.63 Safari/537.36";

    private static ExecutorService pool = Executors.newFixedThreadPool(1);
    private static String ユーザーエージェント = ユーザーエージェント_PC;

    public static Long SLEEP_TIME = 1500L;
    public static Map<String,DhtmlCache> dhtmlCacheMap = DhtmlCache.cacheKeyList();



    public static Document run(String url ){
        return run(url,ユーザーエージェント);
    }

    public static Document run(String url,String ua) {

        /**
         * キャッシュからのデータ読み込み
         */
        if(dhtmlCacheMap.containsKey(url + "_" + ua)) {
            System.out.println("キャッシュあり");
            return Jsoup.parse(DhtmlCache.findByUrlAndUa(url,ua).getHtml());
        }


        Future<Document> future = pool.submit(new JsoupHelper2(url,ua));
        try {

            Document doc = future.get();

            if(doc==null) {
                System.out.println("HTML取得失敗");
                return doc;
            }


            DhtmlCache dh = DhtmlCache.findByUrlAndUa(url, ua);
            if(dh==null) {
                dh = new DhtmlCache();
            }

            Ebean.execute(Ebean.createSqlUpdate("SET NAMES utf8mb4"));
            dh.setUa(ua);
            String ht = EmjUtil.cutEmj(doc.html());
            dh.setHtml(ht);
            dh.setUrl(url);
            Ebean.save(dh);

            //キャッシュデータ
            DhtmlCache dhc = new DhtmlCache();
            dhc.setUa(ua);
            dhc.setUrl(url);
            dhtmlCacheMap.put(dhc.getHashKey(),dhc);

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


    public JsoupHelper2(String url, String ua) {
        this.url = url;
        this.ua = ua;
    }
    String url;
    String ua;

    @Override
    public Document call() throws Exception {
        System.out.println("HTML取得開始");
        System.out.println(this.url);

        Document doc  = getDocument(this.url);
        if(doc == null) {
            return null;
        }

        Integer ret = 0;

        while (!pwordCheck(this.url,doc) && ret<3) {
            System.out.println("P-WORD制限");
            sleep(15 * 1000L);
            doc  = getDocument(this.url);
            ret++;
        }



        return doc;
    }

    public Document getDocument(String url) {
        return this.getDocument(0,url);
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
