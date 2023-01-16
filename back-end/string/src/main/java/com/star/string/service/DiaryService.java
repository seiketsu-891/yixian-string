package com.star.string.service;

import com.star.string.entity.Diary;
import com.star.string.exception.BusinessException;
import com.star.string.exception.BusinessExceptionCode;
import com.star.string.repository.DiaryRepository;
import com.star.string.req.DiaryContentReq;
import com.star.string.req.DiaryReq;
import com.star.string.util.CopyUtil;
import com.star.string.util.TimeUtil;
import org.apache.catalina.startup.Tomcat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class DiaryService {
    @Resource
    private DiaryRepository repository;

    @Resource
    private DiaryDialogService dialogService;

    /**
     * 增加新的日记内容
     */
    public void add(List<DiaryContentReq> contents, String dialogs, String userId, String date) {
        System.out.println("test" + date);
        for(DiaryContentReq c : contents) {
            // 储存日记内容
            Diary diary = new Diary();
            diary.setId(String.valueOf(UUID.randomUUID()));
            diary.setQuestion(c.getQuestion());
            diary.setAnswer(c.getAnswer());
            diary.setDate(date);
            diary.setUserId(userId);
            diary.setContentOrder(c.getOrder());
            Date d  =new Date();
            diary.setCreateTime(d);
            diary.setUpdateTime(d);
            this.save(diary);
        }
        System.out.println("发出添加dialog的请求");
        // 储存日记对话信息
        dialogService.add(dialogs, userId, date);
    }


    /**
     * 将条目保存于数据库
     */
    private Diary save(Diary d){
        return repository.save(d);
    }
    /**
     * 查找某用户指定日期的日记
     */
    public List<Diary> list(String userId, String date) {
        return repository.findByUserIdAndDateOrderByContentOrderAsc(userId,date);
    }


    /**
     * 删除某条日记
     */
    public void del(String userId, String id) {
        Integer deleted = repository.deleteByIdAndUserId(id, userId);
        if(deleted == 0){
            throw new BusinessException(BusinessExceptionCode.DIARY_NOT_EXISTS);
        }
    }

    /**
     * 更新日记答案信息
     */
    public void updateAnswer(String userId, DiaryContentReq req) {
        Diary diary = repository.findByIdAndUserId(req.getId(), userId);
        if(diary == null){
            throw new BusinessException(BusinessExceptionCode.DIARY_NOT_EXISTS);
        }
        diary.setAnswer(req.getAnswer());
        diary.setUpdateTime(new Date());
        this.save(diary);
    }

    /**
     * 删除日记
     */
    public void delEntry(String userId, String id, String date) {
        Diary diaryDb = repository.findByIdAndUserId(id, userId);
        if(diaryDb == null){
            throw  new BusinessException(BusinessExceptionCode.DIARY_NOT_EXISTS);
        }

        repository.deleteById(id);

        // 查看改天是否还有日记记录，如果没有了，则需要删除dialog表中的数据。
        // 避免当天日记已没有，但是dialog依然存在，导致前端还是能读取出dialog，而无法写当天的新日记
        Integer count = repository.countByUserIdAndDate(userId, date);
        if(count ==  0){
            dialogService.delByUserIdAndDate(userId, date);
        }
    }
}
