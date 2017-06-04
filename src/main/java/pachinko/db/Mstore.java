package pachinko.db;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import common.db.MappedSuper;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by t-sakazume on 2015/01/23.
 */
@Data
@Entity
public class Mstore extends MappedSuper {

    @Column(columnDefinition = "TEXT")
    String name;

    Integer mAddressId;

    @Column(columnDefinition = "TEXT")
    String address;

    @Column(columnDefinition = "TEXT")
    String hours;

    @Column(columnDefinition = "TEXT")
    String link;

    @Column(columnDefinition = "TEXT")
    String spLink;

    /** 電話番号*/
    String tel;

    boolean type;

    Timestamp updatedAt;

    public static void main(String args[])  {
//        List<Mstore> test = (List<Mstore>) new Mstore().where().eq("id",1).findList();
//        try {
//            findByAddressCode(new String[]{"1","2"});
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IntrospectionException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        更新データ取得();
    }


    public static boolean リンク存在チェック(String link) {
        Query<Mstore> query = Ebean.createQuery(Mstore.class);
        List<Object> list = query.where().eq("link", link).findIds();
        if(list.size()==0) {
            return false;
        } else {
            return true;
        }
    }

    public static List<Mstore> 更新データ取得() {
        String sql = MappedSuper.SQL_MAP.get("DSTORE_NOT_24");
        List<SqlRow> list = Ebean.createSqlQuery(sql).findList();

        sql = MappedSuper.SQL_MAP.get("DSTORE_NULL");
        List<SqlRow> list1 = Ebean.createSqlQuery(sql).findList();

        List<SqlRow> retSqls  = new ArrayList<>();
        retSqls.addAll(list);
        retSqls.addAll(list1);
        List<Mstore> ret = new ArrayList<>();
        for(SqlRow s:retSqls) {
            Mstore model = new Mstore();
            model.setName(s.getString("name"));
            model.setMAddressId(s.getInteger("m_address_id"));
            model.setAddress(s.getString("m_address_id"));
            model.setHours(s.getString("hours"));
            model.setLink(s.getString("link"));
            model.setSpLink(s.getString("sp_link"));
            model.setTel(s.getString("tel"));
            model.setType(s.getBoolean("type"));
            model.setUpdatedAt(s.getTimestamp("updated_at"));
            ret.add(model);
        }
        return ret;
//         retSqls.stream().map(s->{
//            Mstore model = new Mstore();
//            model.setName(s.getString("name"));
//            model.setMAddressId(s.getInteger("m_address_id"));
//            model.setAddress(s.getString("m_address_id"));
//            model.setHours(s.getString("hours"));
//            model.setLink(s.getString("link"));
//            model.setSpLink(s.getString("sp_link"));
//            model.setTel(s.getString("tel"));
//            model.setType(s.getBoolean("type"));
//            model.setUpdatedAt(s.getTimestamp("updated_at"));
//            return model;
//        }).collect(Collectors.toList());
    }


    public static Mstore findByLink(String link) {
        return where1().eq("link",link).findUnique();

    }

    public static List<Mstore> findByAddressCode(String ids[]) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        String sql = MappedSuper.SQL_MAP.get("MSTORE_FIND_ADDRESS_CODES");
        String addressIn = String.join(",",ids);
        sql = sql.replace("__address_in",addressIn);
        List<SqlRow> list = Ebean.createSqlQuery(sql).findList();
        List<Mstore> retLisr = new ArrayList<>();
        for(SqlRow row:list) {
            Mstore mstore = beanMaping(Mstore.class,row);
            mstore.setId(row.getInteger("id"));
            retLisr.add(mstore);
        }
        return retLisr;


    }

    public static List<Mstore> likeByLink(String link) {
        return where1().like("link", "%" + link + "%").findList();
    }

    public static List<Mstore> likeByAddress(String address) {
        return where1().like("address", "%" + address + "%").findList();
    }
    public static List<Mstore> likeByName(String name) {
        return where1().like("name", "%" + name + "%").findList();
    }
    public static Query<Mstore> createQuery() {
        return (Query<Mstore>) new Mstore().query();
    }

    public static ExpressionList<Mstore> where1() {
        return createQuery().where();
    }

}
