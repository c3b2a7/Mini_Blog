package me.lolico.blog.service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author lolico
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    /**
     * Batch-update entities.
     *
     * @param entities The Collection of entity
     */
    void batchUpdate(Collection<? extends T> entities);

    /**
     * Dynamically update these entities in batch,if need.
     *
     * @param entities The Collection of entity
     * @param dynamic  if true,dynamically update,otherwise not
     */
    void batchUpdate(Collection<? extends T> entities, boolean dynamic);

    /**
     * Batch-insert entities.
     *
     * @param entities The Collection of entity
     */
    void batchInsert(Collection<? extends T> entities);

    /**
     * Dynamically update attributes after finding entities based on id.
     *
     * @param entity The given entity, must provide an ID
     * @return Updated entity
     */
    T dynamicUpdate(T entity);
}
