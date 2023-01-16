package com.star.string.service;

import com.star.string.entity.DiaryQuestion;
import com.star.string.exception.BusinessException;
import com.star.string.exception.BusinessExceptionCode;
import com.star.string.repository.DiaryQuestionRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Transactional
@Service
public class DiaryQuestionService {
    @Resource
    private DiaryQuestionRepository repository;

    /**
     *  添加日记问题
     */
    public void add(DiaryQuestion diaryQues, String userId) {
        DiaryQuestion q = new DiaryQuestion();
        q.setId(String.valueOf(UUID.randomUUID()));
        q.setDescription(diaryQues.getDescription());
        q.setUserId(userId);
        q.setInUse(true);

        Date now = new Date();
        q.setUpdateTime(now);
        q.setCreateTime(now);

        repository.save(q);
    }

    /**
     * 查找在使用中的日记问题
     */
    public List<DiaryQuestion> listInUseQues(String userId) {
       return repository.findByUserIdAndAndInUseOrderByCreateTimeAsc(userId, true);
    }

    /**
     * 设置日记问题是否在使用（删除时用）
     */
    public void setInUse(String userId, String id, Boolean ifInUse) {
       Integer updated = repository.updateInUseByIdAndUserId(id, userId, ifInUse);
       if(updated == 0){
           throw new BusinessException(BusinessExceptionCode.DIARY_QUESTION_IN_USE_SET_FAILED);
       }
    }
}
