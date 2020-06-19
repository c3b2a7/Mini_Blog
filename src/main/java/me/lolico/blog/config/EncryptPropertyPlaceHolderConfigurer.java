package me.lolico.blog.config;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author lolico
 */
public class EncryptPropertyPlaceHolderConfigurer extends PropertySourcesPlaceholderConfigurer {

    private final String[] encryptPropertyNames = {"spring.datasource.url", "spring.datasource.username", "spring.datasource.password"};

    @Override
    public void setLocation(Resource location) {
        super.setLocation(new ClassPathResource("application.properties"));
    }

    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        return super.convertProperty(propertyName, propertyValue);
        // return isEncryptProperty(propertyName) ? DesUtils.getDecryptString(propertyName) : propertyValue;
    }

    private boolean isEncryptProperty(String propertyName) {
        for (String encryptPropertyName : encryptPropertyNames) {
            if (propertyName.equals(encryptPropertyName)) {
                return true;
            }
        }
        return false;
    }
}
