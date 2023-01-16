package com.star.string.service;

import com.star.string.entity.Todo;
import com.star.string.entity.TodoMemo;
import com.star.string.repository.TodoMemoRepository;
import com.star.string.repository.TodoRepository;
import com.star.string.util.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TodoMemoService {
    @Resource
    private TodoMemoRepository repository;

    @Resource
    private TodoRepository todoRepository;


    /**
     * 新增一条todo时memo的处理
     */
    public void updateMemoWhenAddTodo(String userId, Todo todo ){
        String[] ymd = TimeUtil.spitYMD(todo.getDate());
        //查找当天该用户有无memo记录
        TodoMemo memo  = repository.findByUserIdAndYearAndMonthAndDay(userId, ymd[0], ymd[1], ymd[2]);

        if(memo!=null){
            // 如果当天已有memo记录，则更新
            this.setAllDone(memo, false);
        }else{
            //没有当天memo记录，新建memo记录
            TodoMemo todoMemo = new TodoMemo();
            todoMemo.setId(String.valueOf(UUID.randomUUID()));
            todoMemo.setUserId(todo.getUserId());
            todoMemo.setAllDone(false);
            todoMemo.setYear(ymd[0]);
            todoMemo.setMonth(ymd[1]);
            todoMemo.setDay(ymd[2]);
            Date date = new Date();
            todoMemo.setCreateTime(date);
            todoMemo.setUpdateTime(date);
            repository.save(todoMemo);
        }
    }

    /**
     * 设置某个日期记录为全部已完成
     */
    private  void setAllDone(TodoMemo memo, boolean hasAllDone){
        memo.setAllDone(hasAllDone);
        memo.setUpdateTime(new Date());
        repository.save(memo);
    }

    /**
     * 查找某用户某天是否还有未完成todo
     */
    private boolean ifAllDone(String userId, String date) {
        Integer count =   todoRepository.countByUserIdAndDateAndDone(userId, date, false);
        System.out.println("count" + count);
        return  count == 0;
    }

    /**
     * 删除未完成状态todo/ 将一条todo标记为完成后的memo处理
     */
    public void updateMemoWhenDelOrMarkDoneTodo(String userId,  String y, String m, String d){
        TodoMemo memo = repository.findByUserIdAndYearAndMonthAndDay(userId, y,m,d);
        // 如果当天有memo并且memo不是全部完成
        if(memo !=null && !memo.getAllDone()){
            String date = y +"-"+  m+ "-" +  d;
            boolean notHasUndone = this.ifAllDone(userId, date);
            // 除了该项外没有其他未完成项目，那么就把allDone设置为true
            System.out.println("当天是否有未完成代办？" + notHasUndone);
            if(notHasUndone){
                this.setAllDone(memo, true);
            }
        }
    }

    /**
     * 获取某月还存在未完成代办的日期
     */
    public List<String> getMonthHasUndoneDates(String userId, String y, String m) {
        List<String> dates = new ArrayList<>();
        List<TodoMemo> memos =repository. findByUserIdAndYearAndMonthAndAllDone(userId, y, m ,false);
        for (TodoMemo memo : memos) {
            dates.add(memo.getDay());
        }
        return dates;
    }
}
