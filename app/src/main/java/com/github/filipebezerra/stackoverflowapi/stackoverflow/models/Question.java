package com.github.filipebezerra.stackoverflowapi.stackoverflow.models;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 04/11/2015
 * @since #
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {
    public String title;
    public String link;
}
