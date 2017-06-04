package pachinko.db;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import com.google.common.base.CaseFormat;
import common.db.MappedSuper;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.persistence.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by t-sakazume on 2015/01/22.
 */
@Entity
@Data
public class Mmodels extends MappedSuper {

    String name;

    /** 大当たり確率 */
    String probability;

    /** 出玉*/
    String outputSum;

    /** 賞球数 */
    String output;

    /** 継続 回数 */
    String round;

    /** 時短*/
    String shortNum;

    /** st回数 */
    String st;

    /** 備考 */
    @Column(columnDefinition = "TEXT")
    String remarks;

    @Column(columnDefinition = "TEXT")
    String link;


    @Version
    Timestamp lastUpdate;

    /** 導入開始 */
    Integer publishedAt;

    public static Query<Mmodels> createQuery() {
        return (Query<Mmodels>) new Mmodels().query();
    }

    public static ExpressionList<Mmodels> where1() {
        return createQuery().where();
    }

    public static boolean リンク存在チェック(String link) {
        Query<Mmodels> query = Ebean.createQuery(Mmodels.class);
        List<Object> list = query.where().eq("link", link).findIds();
        if(list.size()==0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean リンク存在チェック_部分一致(String link) {
        List<Object> list = where1().like("link","%" + link + "%").findIds();
        if(list.size()==0) {
            return false;
        } else {
            return true;
        }
    }
    public static Mmodels findById(Integer id) {
        return where1().eq("id",id).findUnique();
    }
    public static Mmodels findByLink(String link) {
        return where1().eq("link",link).findUnique();
    }

    public static Mmodels findByName(String name) {
        int size = where1().eq("name",name).findIds().size();

        if(size==0) {
            return null;
        } else {
            return where1().eq("name",name).findUnique();
        }

    }

    public static Mmodels likeByLink(String link) {

        List<Mmodels> list = where1().like("link","%" + link + "%").findList();
        if(1==list.size()) {
            return where1().like("link","%" + link + "%").findUnique();
        }

        for(Mmodels mmodels:list) {
            if(link.equals(mmodels.getLink())) {
                return mmodels;
            }
        }

        return null;
    }

    public static Mmodels likeByCode(String code) {
        List<Mmodels> list = where1().like("link","%/" + code ).findList();
        if(1==list.size()) {
            return where1().like("link","%/" + code ).findUnique();
        }

        return null;
    }

    public static List<Mmodels> findAll() {

        return createQuery().orderBy("id asc").findList();
    }

    public static List<Mmodels> findByUpdateData() {
        DateTime now = new DateTime();
        now = now.plusDays(-6);
        return where1().or(
                com.avaje.ebean.Expr.le("lastUpdate",now.toDate()) ,
                com.avaje.ebean.Expr.isNull("probability")
        ).findList();

    }


    public static List<Mmodels> findOfLimit(Integer page,Integer size) {
        String sortSQL = MappedSuper.SQL_MAP.get("MMODELS_SORT");
        //発売日順の並びを取得
        List<SqlRow> rows = Ebean.createSqlQuery(sortSQL).findList();
        List<String> ids = new ArrayList<>();
        for(SqlRow row:rows) {
            ids.add(row.getString("group_id"));
        }
        String sort = String.join(",",ids);
        return findOfLimit(page,size,sort);

    }

    public static List<Mmodels> findOfLimitLikeName(Integer page,Integer size,String name) {
        String sortSQL = MappedSuper.SQL_MAP.get("MMODELS_SORT_NAME_LIKE");

        //発売日順の並びを取得
        List<SqlRow> rows = Ebean.createSqlQuery(sortSQL).setParameter(1,"%" + name + "%").findList();
        List<String> ids = new ArrayList<>();
        for(SqlRow row:rows) {
            ids.add(row.getString("group_id"));
        }
        String sort = String.join(",",ids);
        return findOfLimit(page,size,sort);
    }

    /**
     * トップページに表示している。
     * 発売日順に関連機種を取得
     * @param page
     * @param size
     * @return
     */
    private static List<Mmodels> findOfLimit(Integer page,Integer size,String sortModelsIds) {
        List<Mmodels> retData = new ArrayList<>();

        Integer offset = (page-1) * size;
        //値の取得
        String selectSQL  = MappedSuper.SQL_MAP.get("MMODELS_SELECT");
        selectSQL = selectSQL.replace("__sort_parm",sortModelsIds);

        System.out.println(selectSQL);

        List<SqlRow> rows = Ebean.createSqlQuery(selectSQL)
                .setParameter(1, offset)
                .setParameter(2, size)
                .findList();

        try {
            for (SqlRow row : rows) {
                Mmodels mmodels = new Mmodels();

                //リフレクションを使用した値の設定
                for (Field field : Mmodels.class.getDeclaredFields()) {

                    String db変数名 = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,field.getName());
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), Mmodels.class);
                    Method w = pd.getWriteMethod();

                    w.invoke(mmodels, new Object[]{row.get(db変数名)});
                }
                //id等は親クラスに定義されているので
                for (Field field : Mmodels.class.getSuperclass().getDeclaredFields()) {
                    if( Modifier.isStatic(field.getModifiers()) ) {
                        continue;
                    }

                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), Mmodels.class);
                    Method w = pd.getWriteMethod();
                    w.invoke(mmodels, new Object[]{row.get(field.getName())});
                }


                retData.add(mmodels);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retData;
    }



    /**
     * 更新の必要があるかどうか判定
     * @return
     */
    @Transient
    public boolean isUpdate() {
        DateTime dateTime = new DateTime(this.getLastUpdate());
        DateTime nowTime = new DateTime();

        Duration d = new Duration(dateTime, nowTime);
        //前回の更新から7日以上経過しているか
        if(7<= Math.abs(d.getStandardDays())) {
            return true;
        }
        return false;
    }
    public static void main(String args[]) {
//        List<String> title = Arrays.asList("導入開始日","スペックタイプ","大当たり確率","確変時の確率","確変突入率","確変継続率","時短回数");
//        List<Mmodels> list = findOfLimit(1, 20);

        List<Mmodels> test = findOfLimitLikeName(1,48,"ハルヒ");

    }
}
