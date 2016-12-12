package me.contrapost.quizImpl.Validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@SuppressWarnings("unused")
@Constraint(validatedBy = AnswerListValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.ANNOTATION_TYPE}
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnswerList {

    String message() default "Answer list with wrong number of answers";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
