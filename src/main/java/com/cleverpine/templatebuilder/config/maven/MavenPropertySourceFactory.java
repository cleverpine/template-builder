package com.cleverpine.templatebuilder.config.maven;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

public class MavenPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties properties = this.getFactoryProperties(factory);
        return new PropertiesPropertySource(name, properties);
    }

    private Properties getFactoryProperties(YamlPropertiesFactoryBean factory) {
        Properties properties = factory.getObject();
        if (properties == null) {
            properties = new Properties();
        }
        return properties;
    }
}
