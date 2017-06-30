package com.bitlove.fetlife.view.screen.resource.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.BaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class ProfileFragment extends BaseFragment {

    public static final int PAGE_COUNT = 25;

    protected static final String ARG_MEMBER_ID = "ARG_MEMBER_ID";
    protected static final String ARG_RELATION_TYPE = "ARG_RELATION_TYPE";

    protected RecyclerView recyclerView;

    protected int requestedItems = 0;
    protected int requestedPage = 1;

    //TODO: replace this with a more sophisticated solution od checking queue of FetLife Intent service
    private boolean onCreateCallInProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh();
        onCreateCallInProgress = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!onCreateCallInProgress) {
            refresh();
        } else {
            onCreateCallInProgress = false;
        }
    }

    public void refresh() {
        requestedPage = 1;
        requestedItems = getPageCount();
        startResourceCall(getPageCount(),requestedPage);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Workaround for Android OS crash issue
        if (recyclerView != null) {
            recyclerView.stopScroll();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                        int lastVisiblePosition = visibleItemCount + pastVisibleItems;

                        if (lastVisiblePosition >= requestedItems) {
                            requestedItems += getPageCount();
                            startResourceCall(getPageCount(), ++requestedPage);
                        }
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(getApiCallAction())) {
            int countIncrease = serviceCallFinishedEvent.getItemCount();
            if (countIncrease != Integer.MIN_VALUE) {
                //One Item we already expected at the call
                requestedItems += countIncrease - getPageCount();
            }
            refreshUi();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction().equals(getApiCallAction())) {
            refreshUi();
        }
    }

    private ProfileActivity getProfileActivity() {
        return (ProfileActivity) getActivity();
    }

    protected int getPageCount() {
        return PAGE_COUNT;
    }

    protected int getRelationTypeReference() {
        return getArguments().getInt(ARG_RELATION_TYPE,Integer.MIN_VALUE);
    }

    public abstract String getApiCallAction();

    public String getMemberId() {
        return getArguments().getString(ARG_MEMBER_ID);
    }

    public void startResourceCall(int pageCount, int requestedPage) {
        int relationTypeReference = getRelationTypeReference();
        if (relationTypeReference != Integer.MIN_VALUE) {
            FetLifeApiIntentService.startApiCall(getActivity(),getApiCallAction(),getMemberId(),Integer.toString(relationTypeReference),Integer.toString(pageCount),Integer.toString(requestedPage));
        } else {
            FetLifeApiIntentService.startApiCall(getActivity(),getApiCallAction(),getMemberId(),Integer.toString(pageCount),Integer.toString(requestedPage));
        }
    }

    public abstract void refreshUi();


}
