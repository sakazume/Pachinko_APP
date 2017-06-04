package pachinko.db;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import common.db.MappedSuper;
import lombok.Data;

import javax.persistence.Entity;
import java.util.List;

/**
 * Created by t-sakazume on 2015/01/31.
 */
@Entity
@Data
public class TNumber extends MappedSuper {
    String url;
    String num;

    public static Query<TNumber> createQuery() {
        return (Query<TNumber>) new TNumber().query();
    }

    public static ExpressionList<TNumber> where1() {
        return createQuery().where();
    }

    public static boolean url存在チェック(String url) {
        List<Object> list = where1().like("url", url ).findIds();
        if(list.size()==0) {
            return false;
        } else {
            return true;
        }
    }
    public static TNumber findByUrl(String url) {
        List<TNumber> list = where1().like("url", url ).findList();
        if(2<=list.size()) {
            System.out.println("数字データが何故か複数存在");
            for(int i=1;i<list.size();i++) {
                Ebean.delete(list.get(i));
            }
        }

        return list.get(0);
    }
}
