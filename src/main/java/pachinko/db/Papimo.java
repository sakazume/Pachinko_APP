package pachinko.db;

import common.db.MappedSuper;
import lombok.Data;
import nise.Main;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by gyutr20 on 2017/06/30.
 */
@Data
@Entity
public class Papimo extends MappedSuper {
    String 店名;
    String 台番号;
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

}
