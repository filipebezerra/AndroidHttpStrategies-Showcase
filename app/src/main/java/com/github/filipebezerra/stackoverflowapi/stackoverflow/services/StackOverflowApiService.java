package com.github.filipebezerra.stackoverflowapi.stackoverflow.services;

import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.StackOverflowQuestions;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 04/11/2015
 * @since #
 */
public interface StackOverflowApiService {
    @GET("/search?order=desc&sort=activity&site=stackoverflow")
    Call<StackOverflowQuestions> searchQuestions(@Query("tagged") String tagged);
}
