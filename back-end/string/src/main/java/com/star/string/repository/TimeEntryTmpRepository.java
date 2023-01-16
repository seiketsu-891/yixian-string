package com.star.string.repository;

import com.star.string.entity.TimeEntryTmp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeEntryTmpRepository extends JpaRepository<TimeEntryTmp, String> {
    TimeEntryTmp findByIdAndUserId(String id, String userId);

    TimeEntryTmp findByUserId(String userId);
}

