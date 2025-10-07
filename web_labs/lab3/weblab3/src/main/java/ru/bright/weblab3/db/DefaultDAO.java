package ru.bright.weblab3.db;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;


public class DefaultDAO<T, I extends Serializable> implements BaseDAO<T, I> {

    private final Class<T> entityClass;

    private EntityManager em;
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public DefaultDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.em = emf.createEntityManager();
    }

    @Override
    public void save(T entity) {
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    @Override
    public void delete(T entity) {
        try {
            em.getTransaction().begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    @Override
    public T findById(I id) {
        return em.find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getName() + " e", entityClass).getResultList();
    }

    @Override
    public T update(T entity) {
        try {
            em.getTransaction().begin();
            T o = em.merge(entity);
            em.getTransaction().commit();
            return o;
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }
}