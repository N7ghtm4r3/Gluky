package com.tecknobit.gluky.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * The {@code ResourceConfigs} class is useful to set the configuration of the resources to correctly serve the
 * images by the server
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see WebMvcConfigurer
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    /**
     * Add handlers to serve static resources such as images, js, and, css
     * files from specific locations under web application root, the classpath,
     * and others.
     *
     * @see ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:resources/")
                .setCachePeriod(0)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

}