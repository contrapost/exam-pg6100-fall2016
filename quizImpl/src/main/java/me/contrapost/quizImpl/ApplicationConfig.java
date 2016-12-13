package me.contrapost.quizImpl;

import io.swagger.jaxrs.config.BeanConfig;
import me.contrapost.quizImpl.rest.CategoryRestImpl;
import me.contrapost.quizImpl.rest.QuizRestImpl;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    private final Set<Class<?>> classes;

    public ApplicationConfig() {

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("0.0.1");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/quiz/api");

        beanConfig.setResourcePackage("me.contrapost.quizAPI");

        beanConfig.setScan(true);

        HashSet<Class<?>> c = new HashSet<>();
        c.add(QuizRestImpl.class);
        c.add(CategoryRestImpl.class);

        c.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        c.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);


        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
