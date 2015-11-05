package com.github.filipebezerra.stackoverflowapi.stackoverflow.models;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 04/11/2015
 * @since #
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class StackOverflowQuestions {
    public List<Question> items;
}
