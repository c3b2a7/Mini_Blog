package me.lolicom.blog;

import me.lolicom.blog.service.repo.BaseRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lolicom
 */

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class RepoConfig {
    // nothing to do
}
