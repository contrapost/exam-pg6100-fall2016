package me.contrapost.quizImpl.Validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AnswerIndexValidator implements ConstraintValidator<AnswerIndex, Integer> {
   public void initialize(AnswerIndex constraint) {
   }

   public boolean isValid(Integer index, ConstraintValidatorContext context) {
      return index < 5 && index >= 0;
   }
}
