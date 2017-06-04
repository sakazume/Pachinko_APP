package pachinko.p_word;

import com.avaje.ebean.Ebean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.DhtmlCache;
import pachinko.library.JsoupHelper;
import pachinko.db.MModelsGroup;
import pachinko.db.Mmodels;

import javax.persistence.OptimisticLockException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by t-sakazume on 2015/01/26.
 */
public class 機種情報解析 {
    String url = "";
    public 機種情報解析(String url) {
        this.url = url;
    }
    public void 関連機種解析() throws IOException {
        Document doc = JsoupHelper.run(this.url);
        Mmodels models;
        MModelsGroup modelsGroup;
        List<MModelsGroup> mModelsGroups = new ArrayList<>();

        Integer groupId = MModelsGroup.getMaxGroupId() + 1;
        //自分の関連機種情報が存在したらそれから取得する。
        if(MModelsGroup.存在チェック(this.url)) {
            groupId = MModelsGroup.findByLink(this.url).getGroupId();
        } else {
            //自分自身を保存
            models = Mmodels.likeByLink(this.url);
            modelsGroup = new MModelsGroup();
            modelsGroup.setGroupId(groupId);
            modelsGroup.setModelsId(models.getId());
            mModelsGroups.add(modelsGroup);
        }

        //関連機種リンク
        for(Element el : doc.select(".related td a") ){
            String href = el.attr("href");

            if(!MModelsGroup.存在チェック(href)) {

                //機種情報が存在しないので書き込み
                if(!Mmodels.リンク存在チェック_部分一致(href)){
                    Mmodels mmodels  = new Mmodels();
                    mmodels.setLink("http://www.p-world.co.jp" + href);
                    Ebean.save(mmodels);
                }


                models = Mmodels.likeByLink(href);
                modelsGroup = new MModelsGroup();
                modelsGroup.setGroupId(groupId);
                modelsGroup.setModelsId(models.getId());
                mModelsGroups.add(modelsGroup);
            }


        }
        if(1<=mModelsGroups.size()) {
            Ebean.save(mModelsGroups);
        }

    }


    public Mmodels 機種詳細解析() {
        Document doc = JsoupHelper.run(this.url);


        if(doc==null || doc.select(".outer .detail").size()==0) {
            System.out.println("機種情報取得失敗" + url);
            return null;
        }

        Element table = doc.select(".outer .detail").get(0);
        Elements trs = table.select("tr");

        Mmodels mmodels = new Mmodels();
        if( Mmodels.リンク存在チェック_部分一致(this.url) ) {
            mmodels = Mmodels.likeByLink(this.url);
        }
        mmodels.setName(doc.select("h1").text());
        for(Element el:trs) {
            if(el.select("th").size()==0) {
                continue;
            }
            String title = el.select("th").get(0).text();
            String value = el.select("td").get(0).text();

            switch (title) {
                case "継続 回数":
                    mmodels.setRound(value);
                    break;
                case "賞球数":
                    mmodels.setOutput(value);
                    break;
                case "大当り確率":
                    mmodels.setProbability(value);
                    break;
                case "時短":
                    mmodels.setShortNum(value);
                    break;
                case "備考":
                    mmodels.setRemarks(value);
                    break;
                case "導入開始":
                    String pub = value.replace("月","").replace("年","");
                    try {
                        mmodels.setPublishedAt(Integer.parseInt(pub));
                    } catch (NumberFormatException e) {
                        mmodels.setPublishedAt(0);
                    }
                    break;
            }
        }
        //        Ebean.save(mmodels);
        return mmodels;
    }
    public static void 関連機種情報更新() {
        Mmodels.findAll().forEach(mmodels->{
            if(mmodels.getLink()==null) {
                return;
            }
            機種情報解析 機種情報解析 = new 機種情報解析(mmodels.getLink());
            try {
                機種情報解析.関連機種解析();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.print(mmodels.getLink());
                e.printStackTrace();

            }
        });
    }
    public static void main(String args[]) {
        List<Mmodels> list = Mmodels.findByUpdateData();
        List<Mmodels> ret = new ArrayList<>();

        関連機種情報更新();

        list.parallelStream().forEach(mmodels -> {
            System.out.println(mmodels.getName() + "解析開始");
            機種情報解析 機種情報解析 = new 機種情報解析(mmodels.getLink());
            Mmodels upMmodels = 機種情報解析.機種詳細解析();
            if(upMmodels!=null) {
                ret.add(upMmodels);
            }
        });
        try {
            Ebean.save(ret);
        }catch (OptimisticLockException e) {
            System.out.println("なぜかエラー");

            ret.stream().forEach(s -> {
                try {
                    Ebean.save(s);
                } catch (OptimisticLockException e1) {
                    System.out.println("エラー" + s.getLink());
                }

            });
        }
        JsoupHelper.end();

    }

}
