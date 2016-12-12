package me.contrapost.quizAPI.api;

import javax.ws.rs.core.MediaType;

public interface Formats {
    String JSON_V1 = MediaType.APPLICATION_JSON + "; charset=UTF-8; version=1";
    String JSON_MERGE_V1 = "application/merge-patch+json; charset=UTF-8; version=1";
    String HAL_V1 = "application/hal+json; charset=UTF-8; version=1";
}
