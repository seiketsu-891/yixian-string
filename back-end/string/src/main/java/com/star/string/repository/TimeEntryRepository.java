package com.star.string.repository;

import com.star.string.entity.TimeEntry;
import com.star.string.entity.TimeEntryCategory;
import com.star.string.resp.TimeEntryCatDurResp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Time;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {
    Integer deleteByIdAndUserId(String id, String userId);

    TimeEntry findByIdAndUserId(String id, String userId);

    @Modifying
    @Query(value ="select t from TimeEntry t where t.userId = :userId ")
    List<TimeEntry.ProjectCategoryIdAndDuration> findCatAndDurByUserId(String userId);

    List<TimeEntry> findByUserIdAndStartBetweenAndEndBetweenAndDurationGreaterThanOrderByStartDesc(String userId, Long s1, Long s2, Long s3, Long s4, Long dur);
}
