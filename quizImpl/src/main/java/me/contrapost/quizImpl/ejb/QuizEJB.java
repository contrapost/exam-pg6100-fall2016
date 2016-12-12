package me.contrapost.quizImpl.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class QuizEJB {

    @PersistenceContext
    protected EntityManager em;


}
