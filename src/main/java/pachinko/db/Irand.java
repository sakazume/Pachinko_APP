package pachinko.db;

import common.db.MappedSuper;
import lombok.Data;
import org.jsoup.select.Elements;

import javax.persistence.Entity;

/**
 * Created by gyutr20 on 2017/01/12.
 */
@Entity
@Data
public class Irand extends MappedSuper {
    String 台番号;
    String 回数;
    String 時間;
    String スタート;
    String 出メダル;
    String ステータス;

    public void create(Elements el) {
        回数 = el.get(0).text();
        時間 = el.get(1).text();
        スタート = el.get(2).text();
        出メダル = el.get(3).text();
        ステータス = el.get(4).text();
    }
}
