package pachinko.db;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import common.db.MappedSuper;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by t-sakazume on 2015/01/22.
 */
@Entity
@Data
public class Dstore extends MappedSuper {


    /** 店舗ID*/
    Integer storeId;

    /** 機種ID */
    @ManyToOne
    @JoinColumn(name = "models_id")
    Mmodels models;

    /** 出玉の価格 */
    Float unitPrice;

    /** 出玉の価格 */
    String unitPriceText;

    /** 台数 */
    Integer number;

    //作成日
    Date createdDate = new Date();

    public static Query<Dstore> createQuery() {
        return (Query<Dstore>) new Dstore().query();
    }

    public static ExpressionList<Dstore> where1() {
        return createQuery().where();
    }

    public static void deleteBystoreId(Integer storeId) {
        List<Object> list = where1().eq("storeId",storeId).findIds();
        Ebean.delete(Dstore.class,list);
    }

    public static List<Dstore> findByStoreId(Integer storeId) {
        return where1().eq("storeId",storeId).findList();
    }

    public static List<Float> findByUnitPrice(Integer modelsId) {
        String sql = MappedSuper.SQL_MAP.get("DSTORE_UNIT_PRICE");
        List<SqlRow> list = Ebean.createSqlQuery(sql).setParameter(1, modelsId).findList();
        List<Float> retData = new ArrayList<>();
        for(SqlRow row:list) {
            Float price = row.getFloat("unit_price");
            retData.add(price);
        }

        return retData;
    }



    public static void main(String args[]) {
        findByUnitPrice(1452);
    }

}
