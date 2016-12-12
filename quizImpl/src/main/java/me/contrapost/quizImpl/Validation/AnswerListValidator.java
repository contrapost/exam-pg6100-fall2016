package me.contrapost.quizImpl.Validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class AnswerListValidator implements ConstraintValidator<AnswerList, List<String>> {
   public void initialize(AnswerList constraint) {
   }

   public boolean isValid(List<String> answersList, ConstraintValidatorContext context) {

       return answersList.size() == 4;

   }
}
