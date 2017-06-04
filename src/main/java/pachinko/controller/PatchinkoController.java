package pachinko.controller;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlRow;
import common.controller.AbstractController;
import pachinko.db.Dstore;
import pachinko.db.MModelsGroup;
import pachinko.db.Mmodels;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by t-sakazume on 2015/02/02.
 */
@Path("/")

public class PatchinkoController extends AbstractController {
    public static void main(String args[]) throws IOException, URISyntaxException {
//        String html = new PatchinkoController().機種("ＣＲ銀河乙女　９９．９ｖｅｒ．");
//        String html = new PatchinkoController().index(1,"");
//        System.out.println(html);

        String test = new PatchinkoController().機種("ＣＲデビルマン　覚醒　Ｌ６‐ＶＥ", "1,2,3");
        System.out.println(test);

    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index(
            @DefaultValue("1")@QueryParam("page") Integer page,
            @DefaultValue("")@QueryParam("name") String name

    ) throws IOException {

        List<List<Map<String,Object>>> viewDataList = new ArrayList<>();
        List<Mmodels> mmodelsList;

        //検索かどうか
        if(name.equals("")) {
            mmodelsList = Mmodels.findOfLimit(page,48);
        } else {
            mmodelsList = Mmodels.findOfLimitLikeName(page,48,name);
        }


        List<Map<String,Object>> rowList = new ArrayList<>();
        for(int i=0;i<mmodelsList.size();i++) {
            Mmodels s = mmodelsList.get(i);
            Map<String,Object> viewData = new HashMap<String, Object>();
            List<Float> priceList = Dstore.findByUnitPrice(s.getId());
            viewData.put("mmodels",s);
            viewData.put("priceList",priceList);


            //リンクに使うURLエンコード
            String encodeStr = URLEncoder.encode(s.getName(), "UTF-8");
            encodeStr = encodeStr.replace("*", "%2a");
            encodeStr = encodeStr.replace("-", "%2d");
            encodeStr = encodeStr.replace("+", "%20");
            viewData.put("linkStr",encodeStr);

            rowList.add(viewData);
            //行データに追加
            if(rowList.size()==4) {
                viewDataList.add(rowList);
                rowList = new ArrayList<>();
            }
        }

        if(rowList.size()!=0) {
            viewDataList.add(rowList);
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("viewDataList",viewDataList);
        model.put("page",page);
        String html = render("/home/index2.jade", model);
        return html;
    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{name}")
    public String spec(@PathParam("name") String name) throws IOException, URISyntaxException {
        Mmodels mmodels = Mmodels.findByName(name);
        String html = render("/spec.jade");
        return html;
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{name}/rate")
//    @Path("/test/{name}")
    public String 機種(@PathParam("name") String name,@QueryParam("t") String addressCodes) throws IOException, URISyntaxException {

        Mmodels mmodels = Mmodels.findByName(name);

        List<Float> priceList = Dstore.findByUnitPrice(mmodels.getId());
        //最終的なレートリスト。
        List<Float> priceList2 = new ArrayList<>();

        if(addressCodes.equals("null")) {
            addressCodes = "";
            for(int i=0;i<=47;i++) {
                if(i!=0) {
                    addressCodes = addressCodes + ",";
                }
                addressCodes = addressCodes + i;
            }
        }
        String sql = getSql("MMODELS_AND_MSTORE");
        sql = sql.replace("__address_in",addressCodes);


        //レート毎に情報を取得 1円とか4円とか
        Map<String,List<Map<String,Object>>> priceInfoMap = new HashMap<>();
        final String finalSql = sql;
        priceList.stream().forEach(s->{
            List<SqlRow> list = Ebean.createSqlQuery(finalSql)
                    .setParameter(1, mmodels.getId())
                    .setParameter(2,s)
                    .findList();
            List<Map<String,Object>> infoList = new ArrayList<>();
            for(SqlRow row:list) {
                Map<String, Object> data = new HashMap<>();
                data.put("name",row.getString("name"));
                data.put("address",row.getString("address"));
                data.put("number",row.getString("number"));
                data.put("hours",row.getString("hours"));
                infoList.add(data);
            }

            if(infoList.size()!=0) {
                priceInfoMap.put(s.toString(), infoList);
                priceList2.add(s);
            }
        });


        Map<String, Object> model = new HashMap<String, Object>();
        model.put("priceInfoMap",priceInfoMap);
        model.put("priceList",priceList2);
        model.put("mmodels",mmodels);


        String html = render("/tenpo.jade", model);
        return html;
    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("map")
    public String map() {
        String html = render("/japan-map.jade");
        return html;
    }

}
