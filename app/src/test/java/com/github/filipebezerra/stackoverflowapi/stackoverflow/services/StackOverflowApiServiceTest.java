package com.github.filipebezerra.stackoverflowapi.stackoverflow.services;

import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.Question;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.StackOverflowQuestions;
import com.jayway.restassured.config.HttpClientConfig;
import com.jayway.restassured.path.json.JsonPath;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.baseURI;
import static com.jayway.restassured.RestAssured.config;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.HttpClientConfig.httpClientConfig;
import static com.jayway.restassured.config.RestAssuredConfig.newConfig;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Integration test for StackOverflow Search API.
 *
 * @author Filipe Bezerra
 * @version #, 05/11/2015
 * @since #
 */
public class StackOverflowApiServiceTest {
    /**
     * Setups the base API url and a custom {@link HttpClientConfig} for reuse instances and
     * change properly the socket timeout.
     */
    public StackOverflowApiServiceTest() {
        baseURI = "http://api.stackexchange.com/2.2/search";
        config = newConfig().httpClient(httpClientConfig().reuseHttpClientInstance().setParam(
                CoreConnectionPNames.SO_TIMEOUT, (int)TimeUnit.SECONDS.toMillis(30)));
    }

    /**
     * Tests and validates the json document received against the json schema rules.
     */
    @Test
    public void shouldMatchesJsonSchema() {
        given().
                queryParam("order", "desc").
                queryParam("sort", "activity").
                queryParam("site", "stackoverflow").
                queryParam("tagged", "android").
        when().
                get().
        then().
                assertThat().
                    body(matchesJsonSchemaInClasspath("json-schema.json"));
    }

    /**
     * Tests the http status code and the content type of the response.
     */
    @Test
    public void shouldChecksStatusCodeAndContentType() {
        given().
                queryParam("order", "desc").
                queryParam("sort", "activity").
                queryParam("site", "stackoverflow").
                queryParam("tagged", "android").
        when().
                get().
        then().
                statusCode(200).
                header("Content-Type", "application/json; charset=utf-8");
    }

    /**
     * Tests the json document body as expected.
     */
    @Test
    public void shouldValidatesBody() {
        given().
                queryParam("order", "desc").
                queryParam("sort", "activity").
                queryParam("site", "stackoverflow").
                queryParam("tagged", "android").
        when().
                get().
        then().
                body("items", not(emptyArray())).
                body("items[0..-1].title", notNullValue()).
                body("items[0..-1].link", notNullValue());
    }

    /**
     * Tests the json document converted to the expected typed objects and  their expected structure.
     */
    @Test
    public void shouldValidatesJsonToObject() {
        final JsonPath jsonPath = given().
                queryParam("order", "desc").
                queryParam("sort", "activity").
                queryParam("site", "stackoverflow").
                queryParam("tagged", "android").
        when().
                get().
        thenReturn().
                jsonPath();

        final StackOverflowQuestions stackOverflowQuestions =
                jsonPath.getObject("", StackOverflowQuestions.class);

        assertThat(stackOverflowQuestions, notNullValue());
        assertThat(stackOverflowQuestions.items, notNullValue());

        final List<Question> questions = stackOverflowQuestions.items;

        assertThat(questions, not(emptyIterable()));

        for (Question single : questions) {
            assertThat(single.title, not(isEmptyOrNullString()));
            assertThat(single.link, not(isEmptyOrNullString()));
        }
    }
}
