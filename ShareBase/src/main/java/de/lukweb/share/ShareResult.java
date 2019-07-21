package de.lukweb.share;

public interface ShareResult {

    default void onSuccess() {

    }

    void onFailure(Throwable ex);

}
