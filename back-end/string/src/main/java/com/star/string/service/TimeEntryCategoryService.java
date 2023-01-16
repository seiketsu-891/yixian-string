package com.star.string.service;

import com.star.string.entity.TimeEntryCategory;
import com.star.string.exception.BusinessException;
import com.star.string.exception.BusinessExceptionCode;
import com.star.string.repository.TimeEntryCategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.star.string.util.BeanUtil.getNullPropertyNames;

@Service
@Transactional
public class TimeEntryCategoryService {

    private final String DEFAULT_CAT_COLOR = "#A4A4A4";
    private final String DEFAULT_CAT_NAME = "默认分类";
    @Resource
    private TimeEntryCategoryRepository repository;

    /**
     * 列出某用户所有的项目分类
     */
    public List<TimeEntryCategory> list(String userId) {
       return repository.findAllByUserIdOrderByCreateTimeAsc(userId);
    }

    /**
     * 新增项目分类
     */
    public void add(TimeEntryCategory cat,String userId) {
       if( repository.existsByUserIdAndName(userId,cat.getName())){
           throw new BusinessException(BusinessExceptionCode.PROJECTCAT_NAME_EXISTS);
       }
       cat.setCreateTime(new Date());
       cat.setUpdateTime(new Date());
       cat.setId(String.valueOf(UUID.randomUUID()));
       cat.setUserId(userId);
       this.save(cat);
    }

    /**
     * 更新项目分类
     */
    public void update(TimeEntryCategory cat, String userId) {
      TimeEntryCategory catDb = repository.findByIdAndUserId(cat.getId(), userId);
       if(catDb ==null){
           throw new BusinessException(BusinessExceptionCode.PROJECTCAT_DOES_NOT_EXISTS);
       }
        // 更新非空属性
        BeanUtils.copyProperties(cat,catDb, getNullPropertyNames(cat));
        catDb.setUpdateTime(new Date());
        this.save(catDb);
    }

    /**
     * 删除某个分类
     */
    public void del(String id, String userId){
        Integer deleted = repository.deleteByIdAndUserId(id, userId);
        if(deleted == null){
            throw new BusinessException(BusinessExceptionCode.PROJECTCAT_DOES_NOT_EXISTS);
        }
    }
    /**
     *
     */

    private void save(TimeEntryCategory cat){
        repository.save(cat);
    }

    /**
     *  创建并存储默认分类
     */
    public void defaultCat(String id) {
        TimeEntryCategory cat = new TimeEntryCategory();
        cat.setUserId(id);
        cat.setId(id);
        cat.setName(DEFAULT_CAT_NAME);
        cat.setColor(DEFAULT_CAT_COLOR);
        Date now = new Date();
        cat.setCreateTime(now);
        cat.setUpdateTime(now);

        this.save(cat);
    }


}
