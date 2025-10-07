package ru.bright.weblab3.db;

import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;

/**
 * @param <T> - тип сущности
 * @param <I> - тип ключа
 */
public interface BaseDAO<T, I extends Serializable> {

    void save(T entity);
    void delete(T entity);
    T findById(I id);
    List<T> findAll();
    T update(T entity);

}
