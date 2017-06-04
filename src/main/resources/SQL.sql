SQL:MMODELS_SORT
SELECT mmodels.name,mmodels_group.group_id,max(mmodels.published_at) as pub FROM mmodels_group
INNER JOIN mmodels ON mmodels.id = mmodels_group.models_id
WHERE mmodels.name is not null
GROUP BY mmodels_group.group_id
ORDER BY pub DESC

SQL:MMODELS_SORT_NAME_LIKE
SELECT mmodels.name,mmodels_group.group_id,MAX( mmodels.published_at ) AS pub
    FROM
        mmodels_group INNER JOIN mmodels
            ON mmodels.id = mmodels_group.models_id
    WHERE
        mmodels.name IS NOT NULL
        AND models_id IN (
            SELECT id FROM mmodels
                WHERE
                    mmodels.name COLLATE utf8_unicode_ci LIKE ?
        )
    GROUP BY
        mmodels_group.group_id
    ORDER BY
        pub DESC

SQL:MMODELS_NAME_LIKE
SELECT id FROM mmodels
    WHERE
        mmodels.name COLLATE utf8_unicode_ci LIKE ?

SQL:MMODELS_SELECT
SELECT mmodels.*,mmodels_group.group_id FROM mmodels
INNER JOIN mmodels_group ON mmodels.id = mmodels_group.`models_id`
WHERE
  mmodels.name IS NOT NULL
  AND mmodels_group.group_id IN (__sort_parm)

ORDER BY FIELD(mmodels_group.group_id , __sort_parm ) , mmodels.published_at desc
LIMIT ? , ?

SQL:DSTORE_UNIT_PRICE
SELECT unit_price from (SELECT unit_price FROM dstore WHERE models_id = ?
GROUP BY unit_price) AS t
WHERE unit_price IS NOT NULL
ORDER BY unit_price DESC

SQL:MMODELS_AND_MSTORE
SELECT mstore.`name`,mstore.`address`,mstore.`hours`,mmodels.probability,mmodels.remarks,unit_price_text , unit_price,number from mmodels
INNER JOIN dstore ON dstore.models_id = mmodels.id
INNER JOIN mstore ON mstore.id = dstore.store_id
INNER JOIN maddress_group ON mstore.address LIKE CONCAT(maddress_group.address1,'%')
WHERE mmodels.id = ?
AND unit_price = ?
AND maddress_group.id in(__address_in)
ORDER BY maddress_group.id ASC

SQL:MSTORE_FIND_ADDRESS_CODES
SELECT * FROM maddress_group
INNER JOIN mstore ON mstore.address LIKE CONCAT(maddress_group.address1,'%')
WHERE maddress_group.id in(__address_in)

SQL:DSTORE_NOT_24
SELECT mstore.* FROM mstore WHERE id IN(
SELECT store_id FROM dstore WHERE NOT DATE_ADD(created_date, INTERVAL 1 DAY) > NOW() group by store_id
)

SQL:DSTORE_NULL
SELECT mstore.* from mstore
LEFT JOIN dstore ON dstore.store_id = mstore.id
WHERE dstore.store_id IS NULL
