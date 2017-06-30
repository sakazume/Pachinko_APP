package pachinko.db;

import common.db.MappedSuper;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by gyutr20 on 2017/06/30.
 */
@Data
@Entity
public class Papimo extends MappedSuper {
    Integer BB回数;
    Integer RB回数;
    Integer BB確率;
    Integer RB確率;

    Integer 合成確率;
    Integer 合計回数;

    Integer 総スタート;
    Integer 最終スタート;
    Integer 最大メダル;
    Date date;
//    public Papimo(Main.UnitData data) {
//
//        this.BB回数 = Integer.valueOf(data.getBB回数());
//        this.RB回数 = Integer.valueOf(data.getRB回数());
//        this.総スタート = Integer.valueOf(data.get総スタート().replace(",",""));
//
//        this.BB回数 = this.総スタート/this.BB回数;
//        this.RB回数 = this.総スタート/this.RB回数;
//        this.合成確率 = this.総スタート/(this.BB回数+this.RB回数);
//        this.合計回数 = this.BB回数+this.RB回数;
//
//        date = data.getDate();
//    }
}
