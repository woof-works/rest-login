package woofworks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import woofworks.repository.BaseRepository;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by Tim on 11-Nov-16.
 */
public abstract class BaseService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("baseRepository")
    protected BaseRepository baseRepo;

    /**
     * Saves the specified entity
     *
     * @param t
     */
    public <T> void save(T t) {
        baseRepo.save(t);
    }

    /**
     * Saves the updates entity
     *
     * @param t
     */
    public <T> void update(T t) {
        baseRepo.update(t);
    }

    /**
     * Deletes the specified entity
     *
     * @param t
     */
    public <T> void delete(T t) {
        baseRepo.delete(t);
    }

    /**
     * Saves the specified entity
     *
     * @param t
     */
    public <T> void save(Collection<T> t) {
        for (T item : t) {
            baseRepo.save(item);
        }
    }

    /**
     * Retrieves the object with the specified id
     *
     * @param cls
     * @param id
     * @return
     */
    public <T> T get(Class<T> cls, Serializable id) {
        return baseRepo.get(cls, id);
    }
}