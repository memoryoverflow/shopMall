SET @r_result= -9;

SELECT @r_result;

CALL execute_seckill('罗',159.9,'红色','impg/asas',159.9,40.5,1,'orderId2',NOW(),13.9,1,1,1,NOW(),@r_result);

DROP PROCEDURE execute_seckill


DELIMITER $$
CREATE PROCEDURE 
    shopmall.execute_seckill 
    ( IN up_name VARCHAR(64) ,IN up_price DOUBLE,IN up_color VARCHAR(64),IN up_img VARCHAR(64) ,IN up_totalprice DOUBLE,IN up_size DOUBLE,IN up_num INT,IN up_orderId VARCHAR(64),IN createTime TIMESTAMP,IN totalPrice DOUBLE, IN address_Id VARCHAR(64) ,IN up_userId VARCHAR(64),IN up_productId VARCHAR(64),IN kill_Time TIMESTAMP,OUT r_result INT)
    
    BEGIN 
    
      DECLARE insert_count INT DEFAULT 0;

      START TRANSACTION;
      INSERT IGNORE INTO tb_order (orderId,createTime,totalPrice,address_Id,userId,isPayfor) VALUES (up_orderId,createTime,totalPrice, address_Id,up_userId,1);

      SELECT ROW_COUNT() INTO insert_count;

      IF (insert_count = 0 ) THEN
         SET r_result = -100;
         ROLLBACK;
      ELSEIF (insert_count < 0) THEN
         ROLLBACK ;
         SET r_result = -2;
      ELSE
         UPDATE seckill SET VERSION=VERSION+1 WHERE kill_productId = up_productId AND  kill_endTime > kill_Time AND kill_Time > kill_startTime;
      
         SELECT ROW_COUNT() INTO insert_count;
         
         IF (insert_count = 0 ) THEN          
            SET r_result = -10;
            ROLLBACK;
         ELSEIF (insert_count < 0 ) THEN          
           SET r_result= -3;
           ROLLBACK;
         ELSE         
        
           UPDATE tb_product  SET num = num-1 WHERE productId = up_productId AND num>0;
                
           SELECT ROW_COUNT() INTO insert_count;

           IF (insert_count = 0 ) THEN           
              SET r_result = -11;
              ROLLBACK ;
           ELSEIF (insert_count < 0 ) THEN            
             SET r_result= -4;
             ROLLBACK;
           ELSE
 
             INSERT INTO tb_userproduct VALUES(up_orderId,up_userId,up_productId,up_name,up_price,up_size,up_num,up_totalprice,up_color,up_img,0);

             SELECT ROW_COUNT() INTO insert_count;

             IF (insert_count = 0 ) THEN
               SET r_result = -12;
               ROLLBACK;
             ELSEIF (insert_count < 0 ) THEN
               SET r_result= -5;
               ROLLBACK;
             ELSE

               COMMIT;
               SET r_result = 1;
             END IF;
           END IF;
         END IF;
       END IF;
    END;
$$
DELIMITER ;


