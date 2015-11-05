package com.github.filipebezerra.stackoverflowapi.network.http;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 05/11/2015
 * @since #
 */
public interface AsyncCall<R> {
    interface AsyncLifecycle<R> {
        void onBeforeExecute();
        void onSuccess(R result);
        void onResultNothing();
        void onFailure(Throwable error);
    }

    void execute(String tagged, AsyncLifecycle<R> lifecycle);
}
