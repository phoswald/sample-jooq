package com.github.phoswald.sample.task;

import static com.github.phoswald.sample.jooq.Tables.TASK_;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class TaskRepository implements AutoCloseable {

    private final Connection conn;
    private final DSLContext dsl;

    public TaskRepository(Connection conn) {
        this.conn = conn;
        this.dsl = DSL.using(conn, SQLDialect.H2);
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<TaskEntity> selectAllTasks() {
        Result<Record> result = dsl.select().from(TASK_).orderBy(TASK_.TIMESTAMP_.desc()).fetch();
        List<TaskEntity> entities = new ArrayList<>();
        for(Record record : result) {
            TaskEntity entity = new TaskEntity();
            entity.setTaskId(record.get(TASK_.TASK_ID_));
            entity.setUserId(record.get(TASK_.USER_ID_));
            entity.setTimestamp(convertTimestamp(record.get(TASK_.TIMESTAMP_)));
            entity.setTitle(record.get(TASK_.TITLE_));
            entity.setDescription(record.get(TASK_.DESCRIPTION_));
            entity.setDone(record.get(TASK_.DONE_));
            entities.add(entity);
        }
        return entities;
    }

    public TaskEntity selectTaskById(String taskId) {
        Record record = dsl.select().from(TASK_).where(TASK_.TASK_ID_.eq(taskId)).fetchOne();
        if(record == null)  {
            return null;
        } else {
            TaskEntity entity = new TaskEntity();
            entity.setTaskId(record.get(TASK_.TASK_ID_));
            entity.setUserId(record.get(TASK_.USER_ID_));
            entity.setTimestamp(convertTimestamp(record.get(TASK_.TIMESTAMP_)));
            entity.setTitle(record.get(TASK_.TITLE_));
            entity.setDescription(record.get(TASK_.DESCRIPTION_));
            entity.setDone(record.get(TASK_.DONE_));
            return entity;
        }
    }

    public void createTask(TaskEntity entity) {
        dsl.insertInto(TASK_, TASK_.TASK_ID_, TASK_.USER_ID_, TASK_.TIMESTAMP_, TASK_.TITLE_, TASK_.DESCRIPTION_, TASK_.DONE_)
            .values(entity.getTaskId(), entity.getUserId(), convertTimestamp(entity.getTimestamp()), entity.getTitle(), entity.getDescription(), entity.isDone())
            .execute();
    }

    public void deleteTask(TaskEntity entity) {
        dsl.deleteFrom(TASK_).where(TASK_.TASK_ID_.eq(entity.getTaskId())).execute();
    }

    public void updateTask(TaskEntity entity) {
        dsl.update(TASK_)
                .set(TASK_.TIMESTAMP_, convertTimestamp(entity.getTimestamp()))
                .set(TASK_.TITLE_, entity.getTitle())
                .set(TASK_.DESCRIPTION_, entity.getDescription())
                .set(TASK_.DONE_, entity.isDone())
                .where(TASK_.TASK_ID_.eq(entity.getTaskId()))
                .execute();
    }

    private Instant convertTimestamp(LocalDateTime t) {
        return t == null ? null : t.toInstant(ZoneOffset.UTC);
    }

    private LocalDateTime convertTimestamp(Instant t) {
        return t == null ? null : t.atOffset(ZoneOffset.UTC).toLocalDateTime();
    }
}
