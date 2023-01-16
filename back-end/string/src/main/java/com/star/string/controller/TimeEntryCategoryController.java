package com.star.string.controller;


import com.star.string.entity.TimeEntryCategory;
import com.star.string.resp.CommonResp;
import com.star.string.resp.TimeEntryCategoryResp;
import com.star.string.service.TimeEntryCategoryService;
import com.star.string.util.CopyUtil;
import com.star.string.util.ValidatorUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/user/entrycat")
@RestController
public class TimeEntryCategoryController {
    @Resource
    private TimeEntryCategoryService timeEntryCategoryService;

    /**
     * 列出用户所有项目分类
     */
    @GetMapping("/list/{userId}")
    public CommonResp send(@PathVariable String userId) {
        CommonResp<List<TimeEntryCategoryResp>> resp = new CommonResp();
        List<TimeEntryCategory> cats = timeEntryCategoryService.list(userId);
        List<TimeEntryCategoryResp> catsResp = CopyUtil.copyList(cats, TimeEntryCategoryResp.class);
        resp.setContent(catsResp);
        return resp;
    }

    /**
     * 新增项目分类
     */
    @PostMapping("/add/{userId}")
    public CommonResp add(@PathVariable String userId, @RequestBody TimeEntryCategory cat) {
        ValidatorUtil.require(cat.getColor(), "项目颜色");
        ValidatorUtil.require(cat.getName(), "项目名称");

        timeEntryCategoryService.add(cat, userId);
        CommonResp resp = new CommonResp();

        return resp;
    }

    /**
     * 更新项目分类
     */
    @PostMapping("/update/{userId}")
    public CommonResp update(@RequestBody TimeEntryCategory cat, @PathVariable String userId) {
        ValidatorUtil.require(cat.getId(), "项目id");
        ValidatorUtil.require(cat.getName(), "项目名称");

        CommonResp resp = new CommonResp();
        timeEntryCategoryService.update(cat, userId);

        return resp;
    }

    /**
     * 删除某个项目分类
     */
    @DeleteMapping("/del/{userId}/{id}")
    public CommonResp del(@PathVariable String userId, @PathVariable String id) {
        CommonResp resp = new CommonResp();
        timeEntryCategoryService.del(id, userId);
        return resp;
    }


}
