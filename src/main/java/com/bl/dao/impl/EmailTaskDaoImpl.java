package com.bl.dao.impl;

import com.bl.dao.EmailTaskDao;
import com.bl.model.BlEmailTask;
import com.bl.utils.Misc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class EmailTaskDaoImpl implements EmailTaskDao {

    @Resource
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<BlEmailTask> selectEmailTaskList() {
        String sql = "select * from bl_email_task where status = '0'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<BlEmailTask> emailTasks = new ArrayList<>();
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                emailTasks.add(mapRow(map));
            }
        }
        return emailTasks;
    }

    @Override
    public void updateEmailTaskStatus(Integer id) {
        String sql = "update bl_email_task set status='1' where id=?";
        jdbcTemplate.update(sql,new Object[]{id},new int[]{Types.INTEGER});
    }

    private BlEmailTask mapRow(Map<String, Object> map) {
        BlEmailTask emailTask = new BlEmailTask();
        emailTask.setId(Misc.parseInteger(map.get("id").toString()));
        emailTask.setCid(Misc.parseInteger(map.get("cid").toString()));
        emailTask.setAuthor((String) map.get("author"));
        emailTask.setMsg((String)map.get("msg"));
        emailTask.setEmail((String) map.get("email"));
        emailTask.setSendtime((Date) map.get("send_time"));
        emailTask.setIp((String) map.get("ip"));
        emailTask.setStatus((String)map.get("status"));
        return emailTask;
    }
}
