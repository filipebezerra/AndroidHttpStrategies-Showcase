package com.github.filipebezerra.stackoverflowapi.network.http;

import android.content.Context;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.filipebezerra.stackoverflowapi.network.utils.NetworkUtil;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.Question;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.StackOverflowQuestions;
import com.google.gson.Gson;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 05/11/2015
 * @since #
 */
public class VolleyAsyncTask implements AsyncCall<List<Question>> {
    private static final String TAG = VolleyAsyncTask.class.getSimpleName();
    private final RequestQueue mRequestQueue;

    public VolleyAsyncTask(Context applicationContext) {
        mRequestQueue = Volley.newRequestQueue(applicationContext);
    }

    @Override
    public void execute(String tagged, final AsyncLifecycle<List<Question>> lifecycle) {
        Log.d(TAG, "onBeforeExecute()");
        lifecycle.onBeforeExecute();

        mRequestQueue.add(new StringRequest(NetworkUtil.buildUrl(tagged),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse("+response+")");
                        if (response != null) {
                            final StackOverflowQuestions stackOverflowQuestions = new Gson().
                                    fromJson(response, StackOverflowQuestions.class);

                            if (stackOverflowQuestions != null) {
                                if (stackOverflowQuestions.items.isEmpty()) {
                                    lifecycle.onResultNothing();
                                } else {
                                    lifecycle.onSuccess(stackOverflowQuestions.items);
                                }
                            } else {
                                lifecycle.onResultNothing();
                            }
                        } else {
                            lifecycle.onResultNothing();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse()", error);
                        lifecycle.onFailure(error);
                    }
                }
        ));
    }
}
