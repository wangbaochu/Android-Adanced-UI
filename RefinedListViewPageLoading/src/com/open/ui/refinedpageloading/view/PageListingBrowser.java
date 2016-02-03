package com.open.ui.refinedpageloading.view;

import java.util.ArrayList;
import java.util.List;

import com.open.ui.refinedpageloading.net.MService;
import com.open.ui.refinedpageloading.test.MainApplication;

public abstract class PageListingBrowser<T> {
    
    protected final int FINISH_SUCCESS   = 0;
    private final int FINISH_ERROR     = 1;
    private final int FINISH_CANCELLED = 2;
    public final static int UNKNOWN_COUNT = Integer.MAX_VALUE;
    
    /**
     * The number of elements retrieved by one service call.
     * Implementations of <CODE>startPageRequest</CODE> should use this value to
     * retrieve the right number of elements.
     */
    protected final int pageSize;
    
    private boolean isPageListingEnd = false;

    /**
     * The number of elements at the bottom of the list that will trigger the next
     * page to be fetched. For instance if nextPageTriggerCount is 3, the last, the
     * (PageSize - 3) element in the current page will cause the next page to be fetched.
     */
    protected final int nextPageTriggerCount;

    /**
     * When this item with this index is reached in the list, start to request the next page.
     */
    protected int nextTriggerObjectIndex = -1;

    /**
     * All the objects that were retrieved by the service calls so far.
     */
    protected final List<T> listingObjects = new ArrayList<T>();
    
    /**
     * All the objects' indexes in the server's response, and the objects were retrieved by the service calls so far. 
     *
     * The object and its corresponding image have the same index in the server's response, here added this list to
     * save these index. And used them to support that we can delete items from client.
     * 
     * Since when we delete an item in the list view from UI, the item may only have received the search result data,
     * and hasn't received its corresponding image, thus we need record which search result has been deleted, as the 
     * image is being received, we either add it to the listingImages or discard it according to our records.
     */
    protected List<Integer> listingIndices = new ArrayList<Integer>();

    /**
     * All the images that were retrieved by the service calls so far.
     */
    protected final List<byte[]> listingImages = new ArrayList<byte[]>();

    /**
     * One subscriber (usually the listing screen).
     */
    protected PageListingSubscriber mPageListingSubscriber;

    /**
     * <CODE>true</CODE> if the current service call was cancelled and we
     * are waiting to be notified that the cancellation is complete.
     */
    private boolean hasPendingCancellation;

    /**
     * The index of the page that is being retrieved by the current service call.
     * Relevant only if <CODE>currentRequest</CODE> is not <CODE>null</CODE>.
     */
    protected int currentRequestPageIndex  = -1;

    /**
     * The index of the page that will be retrieved by the next service call.
     * The next service call will be started as soon as the current one is done.
     * Used only if <CODE>currentRequest</CODE> is not <CODE>null</CODE>.
     */
    protected int pendingRequestPageIndex  = -1;

    /**
     * The index of the last page that was successfully and completely retrieved by
     * a service call.
     */
    protected int lastRequestPageIndex     = -1;

    /**
     * The cursor position in the list. This is typically linked with the focus in
     * the list view.
     */
    protected int currentIndex;

    /**
     * Number of items we can actually retrieve using the request.
     */
    private int availableCount = UNKNOWN_COUNT;

    /**
     * The service call that is currently running to retrieve one page of data.
     */
    protected MService currentRequest;
    
    public MService getCurrentServiceCall(){
        return currentRequest;
    }
    
    public boolean hasPendingCancellation(){
        return hasPendingCancellation;
    }
    
    public PageListingSubscriber getmPageListingSubscriber(){
        return mPageListingSubscriber;
    }

    /**
     * Constructor for a browser with 2 final parameters.
     *
     * @param pageSize int the number of elements in a page fetched from the server.
     * @param nextPageTriggerCount int the number of elements at the bottom of the list that
     *   will trigger the next page to be fetched. For instance if nextPageTriggerCount is 3,
     *   the last, the penultimate and the antepenultimate elements in the list will cause the
     *   next page to be fetched.
     */
    public PageListingBrowser (final int pageSize, final int nextPageTriggerCount)
    {
        this.pageSize = pageSize;
        this.nextPageTriggerCount = nextPageTriggerCount;
    }

    /**
     * To be invoked from the UI thread only.
     *
     * @param subscriber
     */
    public void setmPageListingSubscriber (final PageListingSubscriber subscriber)
    {
        mPageListingSubscriber = subscriber;
    }

    //----------------------------------------------------------------------
    // Lists accessors and cursor manipulation methods (UI thread)
    //----------------------------------------------------------------------

    /**
     * To be invoked from the UI thread only.
     *
     * @return int
     */
    public int getAvailableCount ()
    {
        return availableCount;
    }
    
    public void setAvailableCount (int count)
    {
        availableCount = count;
    }

    public int getReceivedCount() {
        return listingObjects.size();
    }


    /**
     * To be invoked from the UI thread only.
     *
     * @param list Vector the list to retrieve the object from.
     * @return Object the currently selected / focused item in the list.
     */
    private <E> E getCurrentObject (final List<E> list)
    {
        // We allow the index to be one past the end of listingObjects.
        // Secondary lists like listingImages can be shorter than listingObjects, in
        // this case we return null for indexes in-between
        if (currentIndex >= list.size() && currentIndex <= listingObjects.size())
            return null;

        return list.get(currentIndex);
    }

    /**
     * To be invoked from the UI thread only.
     *
     * @return Object the currently selected / focused item.
     */
    public T getCurrentObject ()
    {
        return getCurrentObject(listingObjects);
    }

    /**
     * To be invoked from the UI thread only.
     *
     * @return Object at the index or null
     */
    public T getObjectAtIndex (final int index)
    {
        if (index >= listingObjects.size())
            return null;

        return listingObjects.get(index);
    }

    /**
     * To be invoked from the UI thread only.
     *
     * @return int index of the selected / focused item.
     */
    public int getCurrentIndex ()
    {
        return currentIndex;
    }

    /**
     * Called by the <CODE>PagedListingScreen</CODE> when an item in the list is focused.
     * You can go one past the number of downloaded objects but not more.
     * To be invoked from the UI thread only.
     *
     * @param currentIndex int the index of the selected / focused item.
     */
    public void setCurrentIndex (int currentIndex)
    {
        if ((currentIndex < 0) || (currentIndex > listingObjects.size()))
        {
            throw new IndexOutOfBoundsException("PagesListingBrowser.setCurrentIndex " + currentIndex +
                                                " is not in (0, " + listingObjects.size() + ")");
        }
        this.currentIndex = currentIndex;
        
        //Here modified to support that we can delete items in the list view from UI, and not 
        //affect the client keep on retrieving data from server. 
        int trigger = adjustRequestTriggerIndex(currentIndex);
        if ((nextTriggerObjectIndex >= 0) && trigger >= nextTriggerObjectIndex && !isPageListingEnd) {
            startNextPageRequest();
            nextTriggerObjectIndex += pageSize;
        }

    }
        
    /**
     * If the list view support delete behavior from UI, this method can be overrided in the sub class.
     * @param currentIndex
     * @return
     */
    protected int adjustRequestTriggerIndex(int currentIndex){
        return currentIndex;
    }
        
    /**
     * You can go one past the number of downloaded objects but not more.
     * To be invoked from the UI thread only.
     *
     * @param forward <CODE>true</CODE> to go forward, <CODE>false</CODE> to go backward.
     * @return boolean <CODE>false</CODE> if we could not move because the limit was reached.
     */
    public boolean moveCurrentIndex (boolean forward)
    {
        if (forward)
        {
            // Going to the right (towards the end of the list)
            if (currentIndex < listingObjects.size())
                setCurrentIndex(currentIndex + 1);
            else
                return false;
        }
        else
        {
            // Going to the left (towards the beginning of the list)
            if (currentIndex > 0)
                setCurrentIndex(currentIndex - 1);
            else
                return false;
        }

        return true;
    }

    //----------------------------------------------------------------------
    // Service call related logic (UI thread)
    //----------------------------------------------------------------------
    /**
     * Will call <CODE>startPageRequest</CODE> when there is no pending
     * service call anymore.
     * This is a way to "reset" the browser if we are starting a list
     * based on a different request for instance.
     * To be invoked from the UI thread only.
     *
     * @param availableCount int the number of results in this list (when known ahead of time).
     *   Pass <CODE>ObjectSubscriber.UNKNOWN_COUNT</CODE> if the count is not known ahead of time.
     * @return boolean <CODE>true</CODE> if there was a request that we cancelled.
     */
    public boolean startFirstPageRequest (final int availableCount)
    {
        this.availableCount = availableCount;
        currentIndex = 0;
        lastRequestPageIndex = -1;

        // Note: availableCount == ObjectSubscriber.UNKNOWN_COUNT means we
        // don't know yet, in that case we assume we should do the request.
        if (availableCount == 0) {
            return hasPendingCancellation;
        }
        nextTriggerObjectIndex = pageSize - nextPageTriggerCount;

        if (currentRequest != null) {
            pendingRequestPageIndex = 0;
            hasPendingCancellation  = true;

            listingObjects.clear();
            listingImages.clear();
            listingIndices.clear();

            currentRequest.cancel();
        } else {
            listingObjects.clear();
            listingImages.clear();
            listingIndices.clear();
            pendingRequestPageIndex = -1;
            currentRequestPageIndex = 0;
            currentRequest = startPageRequest(currentRequestPageIndex);
        }
        return hasPendingCancellation;
    }

    /**
     * Will call <CODE>startPageRequest</CODE> when there is no pending
     * service call anymore.
     * To be invoked from the UI thread only.
     *
     * @return boolean <CODE>true</CODE> if a request was really started,
     *   <CODE>false</CODE> if the end of the list was reached.
     */
    public boolean startNextPageRequest () {
        if (currentRequest != null) {
            if ((currentRequestPageIndex + 1) * pageSize >= availableCount) {
                return false;
            }
            // This may override an existing pendingRequestPageIndex which is OK
            pendingRequestPageIndex = currentRequestPageIndex + 1;
        } else {
            if ((lastRequestPageIndex + 1) * pageSize >= availableCount)
                return false;

            pendingRequestPageIndex = -1;

            currentRequestPageIndex = lastRequestPageIndex + 1;
            currentRequest = startPageRequest(currentRequestPageIndex);

        }

        return true;
    }
    
    /**
     * Re-send the page request for current page
     */
    public void replayPageRequest() {
        currentRequestPageIndex = lastRequestPageIndex + 1;       
        if (currentRequestPageIndex > 0) {
            currentRequest = startPageRequest(currentRequestPageIndex);
        } else {
            currentRequest = null;
            startFirstPageRequest(UNKNOWN_COUNT);
        }
    }

    /**
     * Will call <CODE>startPageRequest</CODE> if there is more to load and
     * there was no error.
     */
    protected void requestFinished (final int finishState, final String errorMsg) {
        MainApplication.getInstance().invokeLater(new Runnable() {
            @Override
            public void run () {
                if (hasPendingCancellation) {
                    hasPendingCancellation = false;
 
                    if (mPageListingSubscriber != null) {
                        mPageListingSubscriber.onCancelled();
                    }

                    listingObjects.clear();
                    listingImages.clear();
                    listingIndices.clear();
                }

                if (finishState == FINISH_SUCCESS) {
                    lastRequestPageIndex = currentRequestPageIndex;
                }

                currentRequestPageIndex = -1;
                currentRequest = null;

                if (pendingRequestPageIndex >= 0) {
                    if (finishState != FINISH_ERROR) {
                        currentRequestPageIndex = pendingRequestPageIndex;
                        currentRequest = startPageRequest(currentRequestPageIndex);
                    }
                    pendingRequestPageIndex = -1;
                }

                if (finishState == FINISH_ERROR) {
                    if (mPageListingSubscriber != null) {
                        mPageListingSubscriber.onError(errorMsg);
                    }
                } else if (finishState == FINISH_SUCCESS) {
                    if (mPageListingSubscriber != null) {
                        mPageListingSubscriber.onPageComplete(isPageListingEnd);
                    }
                }
            }
        });
    }

    /**
     * Cancel the current service call.
     * To be invoked from the UI thread only.
     */
    public boolean cancel () {
        pendingRequestPageIndex = -1;
        if ((currentRequest != null) && (! hasPendingCancellation)) {
            hasPendingCancellation = true;
            currentRequest.cancel();
        }
        return hasPendingCancellation;
    }

    //----------------------------------------------------------------------
    // Abstract methods to be implemented by sub-classes (UI thread)
    //----------------------------------------------------------------------
    /**
     * To be implemented by concrete subclasses.
     * Invoked from the UI thread.
     *
     * @param pageNumber int zero based index of the page.
     * @return MService a service call that can be cancelled.
     */
    protected abstract MService startPageRequest (int pageNumber);
    
    //----------------------------------------------------------------------
    // ResponseListener interface partial implementation (worker thread)
    //----------------------------------------------------------------------
    /**
     * Called when the service call finished successfully.
     * Invoked from the worker thread.
     *
     */
    protected void completed(final List<T> objects, final boolean pageListingEnd) {
        if (!hasPendingCancellation) {
            listingObjects.addAll(objects);
            isPageListingEnd = pageListingEnd;
            requestFinished(FINISH_SUCCESS, null);
        }
    }
    
    /**
     * Called when the service call finished with an error.
     * Invoked from the worker thread.
     *
     * @param e Exception
     * @param sc MService
     */
    public void error (final String errorMsg) {
        requestFinished(FINISH_ERROR, errorMsg);
    }

    /**
     * Called when the service call finished after being cancelled.
     * Invoked from the worker thread.
     *
     * @param sc MService
     */
    public void cancelled () {
        requestFinished(FINISH_CANCELLED, null);
    }
    
}

