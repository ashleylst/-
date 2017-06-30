package com.bitlove.fetlife.view.screen.resource.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.view.screen.BaseActivity;

public class ActivityFeedFragment extends ProfileFragment implements FeedRecyclerAdapter.OnFeedItemClickListener {

    public static ActivityFeedFragment newInstance(String memberId) {
        ActivityFeedFragment friendsFragment = new ActivityFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_ID, memberId);
        friendsFragment.setArguments(args);
        return friendsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        FeedRecyclerAdapter adapter = new FeedRecyclerAdapter(getFetLifeApplication(),this,getArguments().getString(ARG_MEMBER_ID));
        adapter.setUseSwipe(false);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_FEED;
    }

    @Override
    public void onMemberClick(Member member) {
        //TODO(feed): Remove this mergeSave after Feed is stored in local db
        member.mergeSave();
        ProfileActivity.startActivity((BaseActivity) getActivity(), member.getId());
    }

    @Override
    public void onFeedInnerItemClick(Story.FeedStoryType feedStoryType, String url, Member targetMember) {
        if (targetMember != null) {
            if (feedStoryType == Story.FeedStoryType.FOLLOW_CREATED || feedStoryType == Story.FeedStoryType.FRIEND_CREATED) {
                //TODO(feed): Remove this mergeSave after Feed is stored in local db
                targetMember.mergeSave();
                ProfileActivity.startActivity((BaseActivity) getActivity(), targetMember.getId());
                return;
            }
        }
        UrlUtil.openUrl(getActivity(),url);
    }

    @Override
    public void onFeedImageClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
        if (targetMember != null) {
            if (feedStoryType == Story.FeedStoryType.FOLLOW_CREATED || feedStoryType == Story.FeedStoryType.FRIEND_CREATED) {
                //TODO(feed): Remove this mergeSave after Feed is stored in local db
                targetMember.mergeSave();
                ProfileActivity.startActivity((BaseActivity) getActivity(), targetMember.getId());
                return;
            }
        }
        if (feedStoryType == Story.FeedStoryType.LIKE_CREATED && feedEvent.getSecondaryTarget().getVideo() != null) {
            String videoUrl = feedEvent.getSecondaryTarget().getVideo().getVideoUrl();
            if (videoUrl == null || videoUrl.endsWith("null")) {
                return;
            }
            Uri uri = Uri.parse(videoUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
            return;
        }
        UrlUtil.openUrl(getActivity(),url);
    }

    @Override
    public void onFeedImageLongClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
        UrlUtil.openUrl(getActivity(),url);
    }

    @Override
    public void onVisitItem(Object object, String url) {
        UrlUtil.openUrl(getActivity(),url);
    }

    public void refreshUi() {
        if (recyclerView != null) {
            FeedRecyclerAdapter recyclerViewAdapter = (FeedRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getActivity().getApplication();
    }

    private void openProfileScreen(Member member) {
        ProfileActivity.startActivity((BaseActivity) getActivity(),member.getId());
    }
}
