package pachinko.db;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import common.db.MappedSuper;
import lombok.Data;

import javax.persistence.Entity;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by t-sakazume on 2015/01/27.
 */
@Entity
@Data
public class MModelsGroup extends MappedSuper {

    Integer groupId;
    Integer modelsId;


    public static Query<MModelsGroup> createQuery() {
        return (Query<MModelsGroup>) new MModelsGroup().query();
    }

    public static ExpressionList<MModelsGroup> where1() {
        return createQuery().where();
    }


    public static boolean 存在チェック(String url) {
        if(!Mmodels.リンク存在チェック_部分一致(url)) {
            System.out.println("機種情報が存在しない");
            return false;
        }

        Mmodels mmodels = Mmodels.likeByLink(url);
        List<Object> list = createQuery().where().eq("modelsId", mmodels.getId()).findIds();

        if(list.size()==0) {
            return false;
        } else {
            return true;
        }
    }

    public static MModelsGroup findByLink(String url) {
        Mmodels mmodels = Mmodels.likeByLink(url);
        where1().eq("modelsId",mmodels.getId()).findUnique();
        return where1().eq("modelsId",mmodels.getId()).findUnique();
    }

    /**
     * group_idを付与するために最大値を取得する
     * @return
     */
    public static Integer getMaxGroupId() {
        Integer max = Ebean.createSqlQuery("SELECT MAX(group_id) as max FROM mmodels_group")
                .findUnique()
                .getInteger("max");

        if(max==null) {
            max = 0;
        }
        return max ;
    }


    public static void main(String args[]) {
        String sortSQL = MappedSuper.SQL_MAP.get("MMODELS_SORT");


        List<SqlRow> rows = Ebean.createSqlQuery(sortSQL).findList();
        List<String> ids = new ArrayList<>();
        for(SqlRow row:rows) {
            ids.add(row.getString("group_id"));
        }
        String sort = String.join(",",ids);

        String selectSQL  = MappedSuper.SQL_MAP.get("MMODELS_SELECT");
        rows = Ebean.createSqlQuery(selectSQL).setParameter(1,sort).findList();




        try {
            for (SqlRow row : rows) {
                Mmodels mmodels = new Mmodels();
                for (Field field : Mmodels.class.getDeclaredFields()) {
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), Mmodels.class);
                    Method w = pd.getWriteMethod();
                    w.invoke(mmodels, new Object[]{row.get(field.getName())});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

}
