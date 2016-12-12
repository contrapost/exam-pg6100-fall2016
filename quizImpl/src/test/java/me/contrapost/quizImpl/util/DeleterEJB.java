package me.contrapost.quizImpl.util;


import me.contrapost.quizImpl.entities.Quiz;

import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DeleterEJB {

    @PersistenceContext
    private EntityManager em;

    public void deleteEntityById(Class<?> entity, Object id){
        Object obj = em.find(entity, id);
        em.remove(obj);
    }

    public void deleteEntities(Class<?> entity){

        if(entity == null || entity.getAnnotation(Entity.class) == null){
            throw new IllegalArgumentException("Invalid non-entity class");
        }

        String name = entity.getSimpleName();

        // SQL injection prevented by passing a class as a method argument,
        // i.e. name was retrieved by calling getSimpleName method

        Query query = em.createQuery("delete from " + name);
        query.executeUpdate();
    }

    public void deleteQuizzes() {
        List<Quiz> quizzes = em.createNamedQuery(Quiz.GET_ALL_QUIZZES).getResultList();
        for(Quiz quiz : quizzes) {
            deleteEntityById(Quiz.class, quiz.getId());
        }
    }
}
