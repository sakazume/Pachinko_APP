package pachinko.db;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import common.db.MappedSuper;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import pachinko.library.JsoupHelper2;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by t-sakazume on 2015/01/28.
 */
@Entity
@Data
public class DhtmlCache extends MappedSuper {

    @Column(columnDefinition = "TEXT")
    String url;

    @Column(columnDefinition = "MEDIUMTEXT")
    String html;

    @Column(columnDefinition = "MEDIUMTEXT")
    String ua;
    @Version
    Timestamp lastUpdate;



    public boolean isSave() {
        DateTime dateTime = new DateTime(this.getLastUpdate());
        DateTime nowTime = new DateTime();

        //24時間前より未来日か
        Duration d = new Duration(dateTime, nowTime);

        if(24 <= Math.abs(d.getStandardHours())) {
            return true;
        }

        return false;
    }

    public String getHashKey() {
        return this.getUrl() + "_" + this.getUa();
    }

    public static Query<DhtmlCache> createQuery() {
        return Ebean.createQuery(DhtmlCache.class);
//        return (Query<DhtmlCache>) new DhtmlCache().query();
    }

    public static ExpressionList<DhtmlCache> where1() {
        return createQuery().where();
    }

    /**
     * 更新する必要があるかどうか判定
     * @return
     */
    public static boolean isSave(String url) {
        DhtmlCache dhtml = where1().eq("url",url).findUnique();
        if(dhtml==null) {
            return true;
        }
        DateTime dateTime = new DateTime(dhtml.getLastUpdate());
        DateTime nowTime = new DateTime();

        //24時間前より未来日か
        Duration d = new Duration(dateTime, nowTime);

        if(24 <= Math.abs(d.getStandardHours())) {
            return true;
        }


        return false;

    }

    public static DhtmlCache findByUrl(String url) {
        return where1().eq("url",url).findUnique();
    }

    public static DhtmlCache findByUrlAndUa(String url,String ua) {
        return where1().eq("url",url).eq("ua",ua).findUnique();
    }


    public static void deleteByUrl(String url) {
        DhtmlCache dhtmlCache = findByUrl(url);
        Ebean.delete(dhtmlCache);
    }

    public static Map<String, DhtmlCache> cacheKeyList() {
        Date nowDate = new Date();
        Calendar cal = Calendar.getInstance();
        Map<String,DhtmlCache> map = new HashMap<>();

        // 前日
        cal.setTime(nowDate);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String strPreviousDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());

        RawSql rawSql = RawSqlBuilder.parse("SELECT id, url, ua FROM dhtml_cache")
                .columnMapping("id", "id")
                .columnMapping("url", "url")
                .columnMapping("ua", "ua")
                .create();

        Query<DhtmlCache> query = Ebean.find(DhtmlCache.class);

        query.setRawSql(rawSql);
        List<DhtmlCache> list = query.where()
                .ge("last_update", strPreviousDate)
//                .eq("ua", ua)
                .findList();

        for(DhtmlCache dh:list) {
            String key = dh.getHashKey() ;
            map.put(key,dh);
        }

        return map;
    }

    public static void main(String... args) {
//        cacheKeyList(JsoupHelper2.ユーザーエージェント_PC);
//        Ebean.createQuery(Dstore.class);
    }
}
