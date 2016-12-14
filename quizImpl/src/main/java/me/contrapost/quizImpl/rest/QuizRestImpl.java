package me.contrapost.quizImpl.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import io.swagger.annotations.ApiParam;
import me.contrapost.quizAPI.api.QuizRest;
import me.contrapost.quizAPI.dto.QuizDTO;
import me.contrapost.quizAPI.dto.QuizWithCorrectAnswerDTO;
import me.contrapost.quizAPI.dto.collection.ListDTO;
import me.contrapost.quizAPI.dto.hal.HalLink;
import me.contrapost.quizImpl.ejb.QuizEJB;
import me.contrapost.quizImpl.entities.Quiz;
import me.contrapost.quizImpl.rest.converters.QuizConverter;
import me.contrapost.quizImpl.rest.converters.QuizWithCorrectAnswerConverter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class QuizRestImpl implements QuizRest {

    @Context
    UriInfo uriInfo;

    @EJB
    private QuizEJB quizEJB;

    @Override
    public ListDTO<QuizDTO> getAllQuizzes(@ApiParam("The numeric id of the subcategory all quizzes belongs to")
                                                  Long id,
                                          @ApiParam("Offset in the list of news")
                                          @DefaultValue("0")
                                                  Integer offset,
                                          @ApiParam("Limit of news in a single retrieved page")
                                          @DefaultValue("10")
                                                  Integer limit) {

        if(offset < 0){
            throw new WebApplicationException("Negative offset: "+offset, 400);
        }

        if(limit < 1){
            throw new WebApplicationException("Limit should be at least 1: "+limit, 400);
        }

        int maxFromDb = 50;

        List<Quiz> list;

        if(id != null) {
            list = quizEJB.getAllQuizForSubcategory(maxFromDb, id);
        } else {
            list = quizEJB.getAllQuizzes(maxFromDb);
        }

        if(offset != 0 && offset >=  list.size()){
            throw new WebApplicationException("Offset "+ offset + " out of bound "+ list.size(), 400);
        }

        ListDTO<QuizDTO> listDTO = QuizConverter.transform(list, offset, limit);

        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path("/quizzes")
                .queryParam("limit", limit);

        if(id != null){
            builder = builder.queryParam("filter", id);
        }

        listDTO._links.self = new HalLink(builder.clone()
                .queryParam("offset", offset)
                .build().toString()
        );

        if (!list.isEmpty() && offset > 0) {
            listDTO._links.previous = new HalLink(builder.clone()
                    .queryParam("offset", Math.max(offset - limit, 0))
                    .build().toString()
            );
        }
        if (offset + limit < list.size()) {
            listDTO._links.next = new HalLink(builder.clone()
                    .queryParam("offset", offset + limit)
                    .build().toString()
            );
        }

        return listDTO;
    }

    @Override
    public Long createQuiz(@ApiParam("Question, set of answers as a List<String> and " +
            "id of subcategory the quiz belongs to. Should not specify quiz id.")
                                   QuizWithCorrectAnswerDTO dto) {

        if (dto.id != null) {
            throw new WebApplicationException("Cannot specify id for a newly generated quiz", 400);
        }

        Long subcategoryId;
        try {
            subcategoryId = Long.parseLong(dto.subcategoryId);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Id of the subcategory is not numeric", 400);
        }

        Long id;

        try {
            id = quizEJB.createQuiz(dto.question, dto.answerList, dto.indexOfCorrectAnswer, subcategoryId);
        } catch (Exception e) {
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public QuizDTO getQuizById(@ApiParam("The numeric id of the quiz") Long id) {

        if (quizEJB.getQuiz(id) == null) {
            throw new WebApplicationException("Cannot find category with id: " + id, 404);
        }

        return QuizConverter.transform(quizEJB.getQuiz(id));
    }

    @Override
    public QuizWithCorrectAnswerDTO checkQuizAnswer(@ApiParam("The numeric id of the quiz") Long quizId) {

        if (quizEJB.getQuiz(quizId) == null) {
            throw new WebApplicationException("Cannot find category with id: " + quizId, 404);
        }

        return QuizWithCorrectAnswerConverter.transform(quizEJB.getQuiz(quizId));
    }

    @Override
    public void deleteQuiz(@ApiParam("The numeric id of the quiz") Long id) {
        if (quizEJB.getQuiz(id) == null) {
            throw new WebApplicationException("Cannot find category with id: " + id, 404);
        }

        quizEJB.deleteQuiz(id);
    }

    @Override
    public void mergePatchQuiz(@ApiParam("The numeric id of the quiz") Long id,
                               @ApiParam("The partial patch") String jsonPatch) {

        if (quizEJB.getQuiz(id) == null) {
            throw new WebApplicationException("Cannot find category with id: " + id, 404);
        }

        QuizWithCorrectAnswerDTO dto = QuizWithCorrectAnswerConverter.transform(quizEJB.getQuiz(id));

        ObjectMapper jackson = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = jackson.readValue(jsonPatch, JsonNode.class);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid JSON data as input: " + e.getMessage(), 400);
        }

        if (jsonNode.has("id")) {
            throw new WebApplicationException(
                    "Cannot modify the quiz id from " + id + " to " + jsonNode.get("id"), 409);
        }

        if (jsonNode.has("subcategoryId")) {
            throw new WebApplicationException(
                    "Cannot modify the id of quiz's subcategory", 400);
        }

        String newQuestion = dto.question;
        List<String> newAnswerList = dto.answerList;
        int newIndexOfCorrectAnswer = dto.indexOfCorrectAnswer;

        if (jsonNode.has("question")) {
            JsonNode questionNode = jsonNode.get("question");
            if (questionNode.isNull()) {
                newQuestion = dto.question;
            } else if (questionNode.isTextual()) {
                newQuestion = questionNode.asText();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string title", 400);
            }
        }

        if (jsonNode.has("indexOfCorrectAnswer")) {
            JsonNode correctAnswerNode = jsonNode.get("indexOfCorrectAnswer");
            if (correctAnswerNode.isNull()) {
                newIndexOfCorrectAnswer = dto.indexOfCorrectAnswer;
            } else if (correctAnswerNode.isNumber()) {
                newIndexOfCorrectAnswer = correctAnswerNode.asInt();
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string title", 400);
            }
        }

        if (jsonNode.has("answerList")) {
            JsonNode answerMapNode = jsonNode.get("answerMap");
            if (answerMapNode.isNull()) {
                newAnswerList = dto.answerList;
            } else if (answerMapNode.isObject()) {
                //noinspection unchecked
                newAnswerList = jackson.convertValue(answerMapNode, List.class);
            } else {
                throw new WebApplicationException("Invalid JSON. Non-string root category id", 400);
            }
        }

        quizEJB.updateQuiz(id, newQuestion, newAnswerList, newIndexOfCorrectAnswer);

    }

    @Override
    public Response getRandomQuiz() {

        if(quizEJB.getAllQuizzes().isEmpty()) {
            return Response.status(404).build();
        }

        return Response.status(307)
                .location(URI.create("quizzes/" + quizEJB.getRandomQuiz()))
                .build();
    }

    private WebApplicationException wrapException(Exception e) throws WebApplicationException {
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        Throwable cause = Throwables.getRootCause(e);
        if (cause instanceof ConstraintViolationException || cause instanceof SQLException) {
            return new WebApplicationException("Invalid constraints on input: " + cause.getMessage(), 400);
        } else {
            return new WebApplicationException("Internal error", 500);
        }
    }
}