package com.star.string.service;

import com.star.string.entity.TimeEntry;
import com.star.string.entity.TimeEntryTmp;
import com.star.string.exception.BusinessException;
import com.star.string.exception.BusinessExceptionCode;
import com.star.string.repository.TimeEntryCategoryRepository;
import com.star.string.repository.TimeEntryRepository;
import com.star.string.repository.TimeEntryTmpRepository;
import com.star.string.req.TimeEntryReq;
import com.star.string.resp.TimeEntryCatDurResp;
import com.star.string.util.CopyUtil;
import com.star.string.util.TimeUtil;
import com.star.string.util.ValidatorUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.star.string.util.BeanUtil.getNullPropertyNames;
@Transactional
@Service
public class TimeEntryService {
    @Resource
    private TimeEntryRepository repository;
    @Resource
    private TimeEntryCategoryRepository timeEntryCatRepo;

    @Resource
    private TimeEntryTmpRepository timeEntryTmpRepo;

    /**
     * 新增时间条目
     */
    public TimeEntryTmp add(TimeEntryReq req, String userId, Boolean isManual) {
        // 如果有分类,检查分类是否存在于数据库,没有则设置为默认分类id
        if(req.getCategoryId() != null){
           if(!timeEntryCatRepo.existsByIdAndUserId(req.getCategoryId(), userId)){
               throw new BusinessException(BusinessExceptionCode.PROJECTCAT_DOES_NOT_EXISTS);
           }
        }else{
            req.setCategoryId(userId);
        }

        if(isManual){
          newManualTimeEntry(req, userId);
          return null;
        }else{
            return newTimeEntryTmp(req, userId);
        }
    }


    /**
     * 新建一条tmp时间条目(仅开始时间)
     */
    private TimeEntryTmp newTimeEntryTmp(TimeEntryReq req, String userId){
        TimeEntryTmp tmp = CopyUtil.copy(req, TimeEntryTmp.class);
        tmp.setUserId(userId);
        tmp.setCreateTime(new Date());
        tmp.setId(String.valueOf(UUID.randomUUID()));
        return timeEntryTmpRepo.save(tmp);
    }


    /**
     * 新建一条手动添加的时间条目
     */
    private void newManualTimeEntry(TimeEntryReq req,String userId){
        TimeEntry timeEntry = CopyUtil.copy(req, TimeEntry.class);
        timeEntry.setId(String.valueOf(UUID.randomUUID()));
        timeEntry.setCreateTime(new Date());
        timeEntry.setUpdateTime(new Date());
        timeEntry.setUserId(userId);
        timeEntry.setDuration(this.calcDur(req.getStart(), req.getEnd()));
        this.save(timeEntry);
    }
private  long calcDur(Long start, Long end){
        return end-start;
}

    /**
     * 保存时间条目
     */
    public TimeEntry save(TimeEntry t){
       return repository.save(t);
    }

    /**
     *删除时间条目
     */
    public void del(String id, String userId) {
        Integer deleted = repository.deleteByIdAndUserId(id, userId);
        if(deleted == 0){
            throw new BusinessException(BusinessExceptionCode.TIME_ENTRY_DOES_NOT_EXISTS);
        }
    }

    /**
     * 结束某时间条目的计时
     */
    public void endTimer(String userId, String id, Long end) {

        TimeEntryTmp tmp = timeEntryTmpRepo.findByIdAndUserId(id, userId);
        if(tmp == null){
            throw new BusinessException(BusinessExceptionCode.TIME_ENTRY_TIMER_NOT_EXISTS);
        }

        // 把这条entry刚开始计时时的的临时条目删除
        timeEntryTmpRepo.deleteById(tmp.getId());
        ValidatorUtil.validStartAndEnd(tmp.getStart(), end);
        TimeEntry entry = CopyUtil.copy(tmp, TimeEntry.class);
        entry.setId(String.valueOf(UUID.randomUUID()));
        entry.setUpdateTime(new Date());
        entry.setEnd(end);
        entry.setDuration(this.calcDur(tmp.getStart(), end));
        this.save(entry);


    }

    /**
     * 读取tmp表中的数据
     */
    public TimeEntryTmp readRunningEntry(String userId) {
         return timeEntryTmpRepo.findByUserId(userId);
    }

    /**
     * 更新时间条目
     */
    public void update(String userId, TimeEntryReq req) {
        TimeEntry entryDb = repository.findByIdAndUserId(req.getId(), userId);
        if(entryDb == null){
            throw new BusinessException(BusinessExceptionCode.TIME_ENTRY_DOES_NOT_EXISTS);
        }
        BeanUtils.copyProperties(req, entryDb,  getNullPropertyNames(req));
        this.save(entryDb);
    }

    /**
     * 查找分类及其对应的duration
     */
    public List<TimeEntryCatDurResp> listDur(String userId){
        List<TimeEntry.ProjectCategoryIdAndDuration> entries = repository.findCatAndDurByUserId(userId);
        List<TimeEntryCatDurResp> catDurResps = CopyUtil.copyList(entries, TimeEntryCatDurResp.class);
        return catDurResps;
    }

    /**
     * 列出某一周起始时间的所有时间条目
     */
    public List<TimeEntry> listWeeklyEntries(String userId, String dateStart, String dateEnd, String tz, Boolean showShortEntry) {
        // 把对应时区的时间转化为毫秒数，
        // week-start按照00：00：00来算，
        // week-end按照23：59：59来算
       Long start = TimeUtil.convertTimeTzToMills(dateStart + " 00:00:00", tz );
       Long end = TimeUtil.convertTimeTzToMills(dateEnd+ " 23:59:59",tz);

        // 寻找end和start在此时间范围内的time entry
        List<TimeEntry> entries;
        Long DURATION_ONE_MIN = 1000 * 60L;
        Long durMin = showShortEntry? -1L : DURATION_ONE_MIN;

        entries =  repository.findByUserIdAndStartBetweenAndEndBetweenAndDurationGreaterThanOrderByStartDesc(userId,start, end, start,end,durMin);

        return entries;
    }
}
