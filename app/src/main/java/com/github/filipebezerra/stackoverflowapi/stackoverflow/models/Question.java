package com.github.filipebezerra.stackoverflowapi.stackoverflow.models;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 04/11/2015
 * @since #
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class Question {
    public String title;
    public String link;
}
