package me.lolico.blog.config;

import me.lolico.blog.service.repo.BaseRepository;
import me.lolico.blog.service.repo.BaseRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lolico
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = BaseRepository.class, repositoryBaseClass = BaseRepositoryImpl.class)
public class RepositoryConfig {
    // ...
}
