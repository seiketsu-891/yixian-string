package com.star.string.repository;

import com.star.string.entity.DiaryQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface DiaryQuestionRepository extends JpaRepository<DiaryQuestion, String> {
    /**
     * 根据用户id和是否在使用中查找
     */
   List<DiaryQuestion>  findByUserIdAndAndInUseOrderByCreateTimeAsc(String userId, Boolean inUse);


    /**
     * 根据id和用户uid更新in_use值
     */
    @Modifying
    @Query(value ="update diary_question q set q.in_use=:inUse where q.id=:id and q.user_id= :userId ", nativeQuery = true)
    Integer updateInUseByIdAndUserId( String id,  String userId, Boolean inUse);
}
