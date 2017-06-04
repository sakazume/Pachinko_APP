package pachinko.p_word.old;

import com.avaje.ebean.Ebean;
import lombok.Data;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.Dstore;
import pachinko.db.Mmodels;
import pachinko.db.Mstore;
import pachinko.library.JsoupHelper;
import pachinko.p_word.パチンコ情報解析;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gyutr20 on 2015/10/02.
 */
public class 店舗情報解析 {


//    public static void main(String... args) {
//
//        DateTime a = new DateTime().withTime(0,0,0,0).plusHours(-24);
//
////        List<Mstore> allList = Ebean.createQuery(Mstore.class).where().lt("updatedAt",a.toDate()).findList();
////        List<Mstore> allList = Mstore.likeByAddress("埼玉");
//        List<Mstore> allList = Ebean.createQuery(Mstore.class).where().findList();
//
//        allList.parallelStream().forEach(s -> {
//
//            String url = s.getLink();
//
//            パチンコ設置台解析(s);
//            お店情報解析(s);
//
//            JsoupHelper.dhtmlCacheMap.remove(s.getLink() + "_" + パチンコ情報解析.ユーザーエージェント);
//            JsoupHelper.dhtmlCacheMap.remove(s.getLink() + "_" + JsoupHelper.ユーザーエージェント_PC);
//
//            JsoupHelper.dhtmlCacheMap.remove(s.getSpLink() + "_" + パチンコ情報解析.ユーザーエージェント);
//            JsoupHelper.dhtmlCacheMap.remove(s.getSpLink() + "_" + JsoupHelper.ユーザーエージェント_PC);
//
////            スロット設置台解析(s);
//
//        });
//        JsoupHelper.end();
//    }

    @Data
    static class Dto{
        Document doc;
        パチンコ情報解析 パチンコ;
    }
    public static void main(String... args) {
        List<Mstore> allList = Ebean.createQuery(Mstore.class).where().findList();
        allList.parallelStream().map(s->{
            Dto dto = new Dto();
            パチンコ情報解析 パチンコ = new パチンコ情報解析(s);
            if(s.getSpLink()==null) {
                パチンコ.spLink();
            }
            dto.setパチンコ(パチンコ);
            return dto;
        }).map(dto->{
            System.out.println(dto.getパチンコ().getMstore().getName() + "URL取得");
            Document doc = JsoupHelper.run(dto.getパチンコ().getMstore().getSpLink(),パチンコ情報解析.ユーザーエージェント);
            dto.setDoc(doc);
            return dto;
        }).forEach(dto->{

//            List<設置機種情報> 設置機種情報List = dto.getパチンコ().モバイル解析処理(dto.getDoc());
            List<設置機種情報> 設置機種情報List = null;
            if (設置機種情報List == null) {
                return;
            }
            savePachi(設置機種情報List,dto.getパチンコ().getMstore());
        });
        JsoupHelper.end();
    }


    public static Mstore お店情報解析(Mstore mstore) {

        DateTime dtime =  new DateTime(mstore.getUpdatedAt());
        dtime = dtime.plusHours(24);

        DateTime now =  new DateTime();
        if(dtime.isAfter(now)) {
            return mstore;
        }


        Document doc = JsoupHelper.run(mstore.getLink());
        if(doc==null) {
            return null;
        }
        for(Element el:doc.select("center p b")) {
            if(0<=el.text().indexOf("最終更新日")) {
                System.out.println(el.text());
            }
        }


        if(doc.select("table[bordercolorlight=\"#6699FF\"] table").size()==0) {
            System.out.println(mstore.getLink()+ "お店情報が存在しない");
            return mstore;
        }

        Element table = doc.select("table[bordercolorlight=\"#6699FF\"] table").get(0);
        Elements trs = table.select("tr");

        for(Element tr : trs) {

            if(tr.select("td").size()<2) {
                continue;
            }

            String title = tr.select("td").get(0).text();
            String data = tr.select("td").get(1).text();

            title = title.replace("　","").replace(" ","");


            switch(title) {
                case "住所":
                    mstore.setAddress(data);
                    break;
                case "営業時間":
                    mstore.setHours(data);
                    break;
                case "電話":
                    mstore.setTel(data);
                    break;
            }
        }
        mstore.setUpdatedAt(new Timestamp(new Date().getTime()));
        Ebean.save(mstore);
        return mstore;

    }

    public static void パチンコ設置台解析(Mstore mstore) {

        パチンコ情報解析 パチンコ = new パチンコ情報解析(mstore);

        if(mstore.getSpLink()==null) {
            パチンコ.spLink();
        }

//        List<設置機種情報> 設置機種情報List = パチンコ.モバイル解析処理();
            List<設置機種情報> 設置機種情報List = null;

        if (設置機種情報List == null) {
            return;
        }
        savePachi(設置機種情報List,mstore);

    }

    public static void savePachi(List<設置機種情報> 設置機種情報List,Mstore mstore) {
        List<Dstore> saveDstores = new ArrayList<>();

        設置機種情報List.parallelStream().forEach(s -> {
            //機種情報が存在しない場合登録する。
            if (!Mmodels.リンク存在チェック(s.getLink())) {
                Mmodels 登録データ = new Mmodels();
                登録データ.setName(s.getName());
                登録データ.setLink(s.getLink());
                Ebean.save(登録データ);
            }

            Dstore dstore = new Dstore();
            dstore.setModels(Mmodels.findByLink(s.getLink()));
            dstore.setStoreId(mstore.getId());
            dstore.setNumber(s.get台数());
            dstore.setUnitPriceText(s.get貸し玉料金());
            dstore.setUnitPrice(s.貸し玉料金解析());
            saveDstores.add(dstore);
        });

        //delete&insert
        try {
            Dstore.deleteBystoreId(mstore.getId());
            Ebean.save(saveDstores);
        } catch (NullPointerException e) {
            System.out.println("NULL");
            for (Dstore s : saveDstores) {
                try {
                    Ebean.save(s);
                } catch (NullPointerException ex) {
                    System.out.println("登録できない情報あり" + mstore.getLink());
                }
            }

        }
    }

    public static void スロット設置台解析(Mstore mstore) {
        Document doc = JsoupHelper.run(mstore.getLink());

    }




    @Data
    static class 設置機種情報 {
        Integer 台数;
        String 貸し玉料金;
        String name;
        String link;


        public Float 貸し玉料金解析() {

            Float ret = 0F;

            String text = this.貸し玉料金;
            text = text.replace(" ","").replace("　","").replace("【","").replace("】","").replace("パチ","").replace("玉","").replace("円","");
            text = text.replace("／","/");

            if(0<text.indexOf("/")) {
                Float x = Float.parseFloat(text.split("/")[0]);
                Float y = Float.parseFloat(text.split("/")[1]);
                Float xy = x/y;
                ret =  xy.floatValue();
            } else {
                try {
                    ret = Float.parseFloat(text);
                } catch (NumberFormatException ne) {

                }
            }
            return ret;
        }
    }
}
