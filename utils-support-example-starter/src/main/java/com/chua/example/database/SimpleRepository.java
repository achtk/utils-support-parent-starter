package com.chua.example.database;

import com.chua.common.support.database.repository.Repository;
import com.chua.common.support.mapping.annotations.MappingRequest;

import java.io.Serializable;
import java.util.List;

/**
 * @author CH
 */
public interface SimpleRepository<T> extends Repository<T> {


    @MappingRequest("SELECT * FROM Test_Entity20230315 WHERE ID > #{id} and ID < #{id2}")
    public List<T> list(Serializable id, Serializable id2);
}
