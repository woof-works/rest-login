package woofworks.repository;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.internal.CriteriaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Tim on 11-Nov-16.
 */
@Repository(value = "baseRepository")
@Transactional
public class BaseRepository {
    protected transient Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Populates the parameter values into the query
     *
     * @param query
     * @param parameterValues contains the HQL keyword as the key, the paramter value as the map value
     */
    protected void populateParameterValues(Query query, Map<String, Object> parameterValues) {
        if (parameterValues.isEmpty() == false) {
            for (String key : parameterValues.keySet()) {
                Object value = parameterValues.get(key);

                if (Collection.class.isAssignableFrom(value.getClass())) {
                    Collection<?> collection = (Collection<?>) value;
                    query.setParameterList(key, collection.toArray());
                } else {
                    query.setParameter(key, value);
                }
            }
        }
    }

    /**
     * Creates a hibernate query based on the HQL string
     *
     * @param hql
     * @return
     */
    protected Query createQuery(String hql) {
        return getSession().createQuery(hql);
    }

    /**
     * Saves the specified entity
     *
     * @param t
     */
    public <T> void save(T t) {
        getSession().save(t);
    }

    /**
     * Saves the updates entity
     *
     * @param t
     */
    public <T> void update(T t) {
        getSession().update(t);
    }

    /**
     * Deletes the specified entity
     *
     * @param t
     */
    public <T> void delete(T t) {
        getSession().delete(t);
    }

    /**
     * Retrieves the object with the specified id
     *
     * @param cls
     * @param id
     * @return
     */
    public <T> T get(Class<T> cls, Serializable id) {
        return getSession().get(cls, id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> List<T> listByDetachedCriteria(DetachedCriteria detachedCriteria) {
        String test = ((CriteriaImpl) detachedCriteria.getExecutableCriteria(getSession()))
                .getEntityOrClassName();
        try {
            List<T> results = detachedCriteria.getExecutableCriteria(getSession()).list();

            logger.debug("List by detached criteria for " + test + ", result size: "
                    + results.size());
            return results;
        } catch (Exception re) {
            logger.error("List by detached criteria failed", re);
            throw new RuntimeException(re);
        }
    }

    public <T> Collection<T> getAll(Class<T> c) {
        DetachedCriteria criteria = DetachedCriteria.forClass(c);
        return listByDetachedCriteria(criteria);
    }
}

