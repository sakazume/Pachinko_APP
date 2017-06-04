package common.db;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.google.common.base.CaseFormat;
import lombok.Data;

import javax.persistence.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by t-sakazume on 2015/01/23.
 */
@Data
@MappedSuperclass
public class MappedSuper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;


    public Query<? extends MappedSuper> query() {
        return  Ebean.createQuery(this.getClass());
    }

    public ExpressionList<? extends MappedSuper> where() {
        return this.query().where();
    }

    /** 簡易外だしSQLマップ */
    public static Map<String,String> SQL_MAP;
    static {
        //外部ファイルの読込み
        SQL_MAP = LoadSQL();
    }

    /**
     * 外だしSQLの読み込み
     * @return
     */
    private static Map<String,String> LoadSQL() {
        //クラスパスからファイルパスの取得
        URL resource =   MappedSuper.class.getClassLoader().getResource( "SQL.sql" );
        File rcFile = new File(resource.getFile().replace("!",""));
        Path src = rcFile.toPath();

        //ファイル読込み
        List<String> list = new ArrayList<>();
        try {
            list = Files.readAllLines(src);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //解析開始
        Map<String,String> sqlMap = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        String sqlKey = null;
        for(String s:list) {
            //SQLをハッシュマップに登録
            if(0<=s.indexOf("SQL:")) {
                if(sqlKey!=null) {
                    sqlMap.put(sqlKey,sql.toString());
                    sql = new StringBuilder();
                }
                //次のSQLの名称
                sqlKey = s.split(":")[1];
            } else {
                sql.append(" " + s);
            }
        };
        sqlMap.put(sqlKey,sql.toString());
        return sqlMap;
    }


    /**
     * SELECT * した結果をClassへマッピング
     * @param cls 設定クラス
     * @param result sql結果
     * @param <T>
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public static <T> T beanMaping(Class<T> cls ,SqlRow result) throws IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {

            T beanObj = cls.newInstance();

            //リフレクションを使用した値の設定
            //本当は存在チェックしないといけない
            for (Field field : cls.getDeclaredFields()) {

                //@Column("")は考慮してない
                String dbName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), cls);
                Method w = pd.getWriteMethod();

                w.invoke(beanObj, new Object[]{result.get(dbName)});
            }

            //親クラスの処理(本当はもう一回呼び出しだがとりあえずコピー)
//            for (Field field : cls.getSuperclass().getDeclaredFields()) {
//                if (Modifier.isStatic(field.getModifiers())) {
//                    continue;
//                }
//
//                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), cls);
//                Method w = pd.getWriteMethod();
//                w.invoke(beanObj, new Object[]{result.get(field.getName())});
//            }

        return beanObj;
    }

    public static void main(String args[]) {

    }


}
