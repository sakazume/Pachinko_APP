package nise;

import lombok.Data;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pachinko.db.Papimo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gyutr20 on 2017/06/30.
 */
@Data
public class UnitData {
    static Map<String,Integer> 位置Map = new HashMap<>();


    String BB回数;
    String RB回数;
    String BB確率;
    String ART回数;
    String 合成回数;
    String 総スタート;
    String 最終スタート;
    String 最大メダル;
    String 日付;
    Date date;

    public UnitData(Element el) {
            Elements els = el.select("td");
            BB回数 = 項目取得("BB回数",els);
            RB回数 = 項目取得("RB回数",els);
            ART回数 = 項目取得("ART回数",els);
            総スタート = 項目取得("総スタート",els);
            最終スタート = 項目取得("最終スタート",els);
            最大メダル = 項目取得("最大出   メダル",els);
            日付 = els.get(0).text();
    }

    public Papimo createPapimo() {
        Papimo papimo = new Papimo();
        papimo.setBB回数(Integer.parseInt(this.getBB回数()));
        papimo.setRB回数(Integer.parseInt(this.getRB回数()));
        papimo.set総スタート(Integer.parseInt(this.get総スタート().replace(",", "")));

        if(0<papimo.getBB回数()) {
        papimo.setBB確率(papimo.get総スタート() / papimo.getBB回数());
        } else {
        papimo.setBB確率(papimo.get総スタート() );
        }
        if(0<papimo.getRB回数()) {
        papimo.setRB確率(papimo.get総スタート() / papimo.getRB回数());
        } else {
        papimo.setBB確率(papimo.get総スタート() );
        }

        if(0<papimo.getBB回数() + papimo.getRB回数()) {
        papimo.set合成確率(papimo.get総スタート() / (papimo.getBB回数() + papimo.getRB回数()));
        } else {
        papimo.set合成確率(papimo.get総スタート() );
        }

        papimo.set合計回数(papimo.getBB回数() + papimo.getRB回数());

        papimo.setDate(this.getDate());
        return papimo;

    }

    public static void 取得位置算出(Element el) {
        Elements els = el.select("th");
        for(int i=0;i<els.size();i++) {
            Element tdEl = els.get(i);
            位置Map.put(tdEl.text().replace(" ",""),i);
        }
    }

    public static String 項目取得(String key, Elements els ) {
        if(!位置Map.containsKey(key)){
            return "";
        }
        return els.get(位置Map.get(key)).text();
    }

}