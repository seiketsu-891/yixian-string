package com.star.string.repository;

import com.star.string.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {
    /**
     * 根据UserId、日期查找，按照创建时间升序排列，按照排序索引值升序排序
     */
    List<Todo>  findByUserIdAndDateOrderByOrderIndexAscCreateTimeAsc(String userId, String date);

    /**
     * 根据UserId、日期、优先区域查找未完成的todo。按照创建时间升序排列，按照排序索引值升序排序
     */
    List<Todo> findByUserIdAndDateAndPriorityAndDoneOrderByOrderIndexAscCreateTimeAsc(String userId, String date, String p, Boolean done);

    Todo findByIdAndUserIdAndDate(String id, String userId, String date);
    Todo findByIdAndUserId(String id, String userId);
    Integer deleteByIdAndUserId(String id, String userId);

    @Modifying
    @Query(value ="update Todo t set t.done=true, t.order_index = 0,t.update_time= :time where t.id=:id and t.user_id= :userId ", nativeQuery = true)
    Integer updateByIdAndUserId( String id,  String userId, Date time);


    /**
     * 根据用户id、日期、完成状况查找符合条件的数量
    */
    Integer countByUserIdAndDateAndDone(String userId, String date, boolean ifDone);
}
