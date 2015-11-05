package com.github.filipebezerra.stackoverflowapi.network.http;

import android.content.Context;
import android.util.Log;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
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
public class AQueryAsyncCall implements AsyncCall<List<Question>> {
    private static final String TAG = AQueryAsyncCall.class.getSimpleName();
    private final AQuery mQuery;

    public AQueryAsyncCall(Context context) {
        mQuery = new AQuery(context);
    }

    @Override
    public void execute(String tagged, final AsyncLifecycle<List<Question>> lifecycle) {
        mQuery.ajax(NetworkUtil.buildUrl(tagged), String.class,
                new AjaxCallback<String>() {
                    @Override
                    protected void showProgress(boolean show) {
                        Log.d(TAG, "showProgress()");
                        lifecycle.onBeforeExecute();
                    }

                    @Override
                    public void callback(String url, String json, AjaxStatus status) {
                        Log.d(TAG, "url -> ("+url);
                        Log.d(TAG, "json -> ("+json);
                        Log.d(TAG, "status -> "+status);

                        if (json != null) {
                            final StackOverflowQuestions stackOverflowQuestions = new Gson().fromJson(json,
                                    StackOverflowQuestions.class);

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
                            if (status.getError() != null) {
                                lifecycle.onFailure(new Exception(status.getError()));
                            } else {
                                lifecycle.onFailure(new Exception(status.getMessage()));
                            }
                        }
                    }
                });
    }
}
