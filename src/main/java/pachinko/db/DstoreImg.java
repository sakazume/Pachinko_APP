package pachinko.db;

import common.db.MappedSuper;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Version;
import java.sql.Timestamp;

/**
 * Created by t-sakazume on 2015/02/04.
 */
@Entity
@Data
public class DstoreImg extends MappedSuper {
    /** 店舗ID*/
    Integer storeId;

    String imgUrl;

    @Version
    Timestamp createdDate;

}
