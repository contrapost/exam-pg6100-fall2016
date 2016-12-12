package me.contrapost.quizImpl.Validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@SuppressWarnings("unused")
@Constraint(validatedBy = AnswerIndexValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.ANNOTATION_TYPE}
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnswerIndex {

    String message() default "Index of correct answer is out of the answer list";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
