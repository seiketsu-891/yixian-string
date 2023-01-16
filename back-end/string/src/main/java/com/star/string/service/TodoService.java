package com.star.string.service;

import com.star.string.entity.Todo;
import com.star.string.exception.BusinessException;
import com.star.string.exception.BusinessExceptionCode;
import com.star.string.repository.TodoRepository;
import com.star.string.req.TodoReq;
import com.star.string.util.CopyUtil;
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
public class TodoService {
    private  final Long TODO_ORDER_INDEX_GAP  = 65536L;
    @Resource
    private TodoRepository todoRepository;

    @Resource
    private  TodoMemoService todoMemoService;

    /**
     * 列出制定日期和用户的代办
     */
    public List<Todo> list(String userId, String date){
       List<Todo>  todoList= todoRepository.findByUserIdAndDateOrderByOrderIndexAscCreateTimeAsc(userId, date);
       return todoList;
    }

    /**
     * 增加todo
     */
    public void add(TodoReq req, String userId) {
         Todo newTodo = CopyUtil.copy(req, Todo.class);

         newTodo.setId(String.valueOf(UUID.randomUUID()));
         newTodo.setUserId(userId);
         newTodo.setDone(false);

         // 设置OrderIndex
        // 查找当天同优先区域的todo项目
         List<Todo> todos =  todoRepository.findByUserIdAndDateAndPriorityAndDoneOrderByOrderIndexAscCreateTimeAsc(userId,req.getDate(),req.getPriority(),false);
         if(todos == null ||todos.size() == 0){
             //
             newTodo.setOrderIndex(TODO_ORDER_INDEX_GAP);
         }else{
             // 将OrderIndex设置为比最后一项大65536
             Todo prev = todos.get(todos.size()-1);
              newTodo.setOrderIndex(prev.getOrderIndex() + TODO_ORDER_INDEX_GAP);
         }

         Date now = new Date();
         newTodo.setCreateTime(now);
         newTodo.setUpdateTime(now);

         this.save(newTodo);
         todoMemoService.updateMemoWhenAddTodo(userId, newTodo);
    }

    /**
     * 保存todo
     */
    public void save(Todo todo){
        todoRepository.save(todo);
    }


    /**
     * 删除todo
     */

    public void del(String id, String userId,String y, String m, String d) {
        Todo todoDb = todoRepository.findByIdAndUserId(id, userId);
        System.out.println("tododb" + todoDb);
      if(todoDb == null){
          throw new BusinessException(BusinessExceptionCode.TODO_NOT_EXISTS);
      }
     int deleted =  todoRepository.deleteByIdAndUserId(id, userId);
        System.out.println(deleted);
      // 被删除的todo不是已完成状态时，需要确认是否需要更新memo
      if(!todoDb.getDone()){
         todoMemoService.updateMemoWhenDelOrMarkDoneTodo(userId,y,m,d);
      }
    }

    /**
     * 将todo变为完成状态
     */
    public void markDone(String id, String userId, String y, String m, String d) {
        Integer marked = todoRepository.updateByIdAndUserId(id , userId, new Date());
        if(marked == 0){
            throw new BusinessException(BusinessExceptionCode.TODO_NOT_EXISTS);
        }
        todoMemoService.updateMemoWhenDelOrMarkDoneTodo(userId, y, m, d);
    }

    /**
     * 更新todo
     */
    public void update(TodoReq req, String userId) {
        Todo todoDb = todoRepository.findByIdAndUserId(req.getId(), userId);
        if( todoDb == null){
            throw new BusinessException(BusinessExceptionCode.TODO_NOT_EXISTS);
        }

        // 更新非空属性
        Todo todo = CopyUtil.copy(req, Todo.class);
        BeanUtils.copyProperties(todo, todoDb,  getNullPropertyNames(todo));

        todoDb.setUpdateTime(new Date());
        this.save(todoDb);
    }

    /**
     * 调整todo的排序索引值
     */
    public void reorder(String userId,int destIndex, TodoReq req) {
        Todo todoDb = todoRepository.findByIdAndUserIdAndDate(req.getId(), userId, req.getDate());
        if(todoDb == null){
            throw new BusinessException(BusinessExceptionCode.TODO_NOT_EXISTS);
        }
        // 如果优先区域不一样，说明优先区域发生了改变

        // 查找当天同区域的所有未完成todo
        //  (1)如果当天目的size为0（或者size发生数据更新，size不为0，但是前端拖拽时为0） 把index设置为gap即可。
        // （1）把数据按照destIndex排序，假设destIndex是0， 那么需要查询第一条数据的index
        // （2）假设destIndex与数据库的size相等，那么需要获取最后一条数据的index
        // （3）destIndex在0和size之间时候，假设为3，那么需要获取第二条和第三条数据的index
        List<Todo> todos = todoRepository.findByUserIdAndDateAndPriorityAndDoneOrderByOrderIndexAscCreateTimeAsc(userId,req.getDate(),req.getPriority(),false);

        if(!todoDb.getPriority().equals(req.getPriority())){
            todoDb.setPriority(req.getPriority());
        }
        Long prevIndex;
        Long nextIndex;
        Long newIndex;
        System.out.println(todos);
        if(todos == null || todos.size() == 0  || destIndex == -1){
            newIndex = TODO_ORDER_INDEX_GAP;
        }else if(destIndex == 0){
            prevIndex = 0L;
            nextIndex = todos.get(0).getOrderIndex();
            newIndex = calcMid(prevIndex, nextIndex);
        }else if(destIndex == todos.size()){
            prevIndex = todos.get(todos.size()-1).getOrderIndex();
            newIndex = prevIndex + TODO_ORDER_INDEX_GAP;
        }else{
            prevIndex = todos.get(destIndex-1).getOrderIndex();
            nextIndex = todos.get(destIndex).getOrderIndex();
            newIndex = calcMid(prevIndex, nextIndex);
        }
        todoDb.setOrderIndex(newIndex);
        todoDb.setUpdateTime(new Date());
        todoRepository.save(todoDb);
    }

    /**
     * 计算中间位置
     */
    private Long calcMid(Long left, Long right){
        return (left + right) /2;
    }



}
