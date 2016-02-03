package com.open.ui.refinedpageloading.view;

/**
 * Simple interface to receive objects that are part of a list and
 * received and decoded from a service call.
 *
 * The {@link PageListingBrowser} will always call methods of this
 * subscriber in the UI thread.
 */
public interface PageListingSubscriber {

    /**
     * The underlying page fetch service call completed successfully.
     *
     * Override this method to add custom behavior when each page fetch service call completes.
     *
     * Will receive exactly one of onCancelled, onError, or onPageComplete for each page of results.
     */
    public void onPageComplete(boolean isPageListingEnd);
    

    /**
     * The underlying service call notified that cancellation is complete.
     */
    public void onCancelled ();
    
    /**
     * Report an error that occurred while running a service call.
     */
    public void onError (final String errorMsg);
}

