package com.star.string.repository;

import com.star.string.entity.Sms;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface SmsRepository extends JpaRepository<Sms, String> {
    /**
     *  根据手机号、用途、是否已使用、有效期查询
     *  按照创建时间倒序排序（这样用index0可以拿到最新的）
     */
    List<Sms> findByMobileAndForWhatAndStatusAndAtIsAfterOrderByAtDesc(String mobile, String use, String status, Date date);
}
