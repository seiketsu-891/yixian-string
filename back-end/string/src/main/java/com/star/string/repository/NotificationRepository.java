package com.star.string.repository;

import com.star.string.entity.Notification;
import com.star.string.enums.NotificationTypeEnum;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
      List<Notification> findByUserId(String userId);

      @Transactional
      @Modifying
      @Query(value ="update notification n set n.status=true where n.id=:id and n.user_id= :userId ", nativeQuery = true)
      Integer updateByIdAndUserId(@Param("id") String id, @Param("userId") String userId);
      Notification findByUserIdAndId(String userId, String id);

      Integer deleteByIdAndUserId(String id, String userId);

      void deleteAllByUserId(String userId);

      boolean existsByUserIdAndStatus(String userId, Boolean status);

      List<Notification> findByUserIdOrderByCreateTimeDesc(String user);

      @Transactional
      @Modifying
      @Query(value = "select * from notification where user_id =:userId order by create_time limit 3",  nativeQuery = true)
      List<Notification> findByUserIdRecentThree(String userId);

}
