package com.bl.dao;


import com.bl.model.BlEmailTask;
import java.util.List;


public interface EmailTaskDao {

    List<BlEmailTask> selectEmailTaskList();

    void updateEmailTaskStatus(Integer id);

}
