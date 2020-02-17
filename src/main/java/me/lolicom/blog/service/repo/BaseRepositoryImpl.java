package me.lolicom.blog.service.repo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lolicom
 */
@Transactional(readOnly = true)
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;
    private final Class<T> entityClass;
    private final int batchSize = 100;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
        this.entityClass = entityInformation.getJavaType();
    }

    /**
     * Batch update entities.
     *
     * @param entities The Collection of entity
     */
    @Transactional
    @Override
    public void batchUpdate(Collection<? extends T> entities) {
        Iterator<? extends T> iterator = entities.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            T entity = iterator.next();
            entityManager.merge(entity);
            num++;
            if (num % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
                num = 0;
            }
        }
    }

    /**
     * Dynamically update these entities in batch, if need.
     *
     * @param entities The Collection of entity
     * @param dynamic  if true,dynamically update,otherwise not
     */
    @Transactional
    @Override
    public void batchUpdate(Collection<? extends T> entities, boolean dynamic) {
        Iterator<? extends T> iterator = entities.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            T entity = iterator.next();
            dynamicUpdate(entity);
            num++;
            if (num % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
                num = 0;
            }
        }
    }

    /**
     * Batch insert entities.
     *
     * @param entities The Collection of entity
     */
    @Transactional
    @Override
    public void batchInsert(Collection<? extends T> entities) {
        Iterator<? extends T> iterator = entities.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            T entity = iterator.next();
            entityManager.persist(entity);
            num++;
            if (num % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
                num = 0;
            }
        }
    }

    /**
     * Dynamically update attributes based on id.
     *
     * @param entity The given entity, id is required
     * @return Updated entity
     */
    @Transactional
    @Override
    public T dynamicUpdate(T entity) {
        BeanWrapper wrapper = new BeanWrapperImpl(entity);
        //get id value
        Object id = entityInformation.getRequiredId(entity);
        //find the entity base on id
        T old = entityManager.find(entityClass, id);
        Set<String> nullPropertyNames = new HashSet<>();
        for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
            String propertyName = descriptor.getName();
            Object propertyValue = wrapper.getPropertyValue(propertyName);
            //if property value is null,add to set
            if (propertyValue == null) {
                nullPropertyNames.add(propertyName);
            }
        }
        //Copy the property values of the given entity into the old entity,ignoring the property in "nullPropertyNames"
        BeanUtils.copyProperties(entity, old, nullPropertyNames.toArray(new String[0]));
        //do merge
        return entityManager.merge(old);
    }
}
