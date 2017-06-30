package com.bitlove.fetlife.view.screen.resource.profile;

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
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.adapter.RelationsRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.view.screen.BaseActivity;

public class RelationsFragment extends ProfileFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Member> {

    public static RelationsFragment newInstance(String memberId, int relationType) {
        RelationsFragment friendsFragment = new RelationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_ID, memberId);
        args.putInt(ARG_RELATION_TYPE, relationType);
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
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        RelationsRecyclerAdapter adapter = new RelationsRecyclerAdapter(getArguments().getString(ARG_MEMBER_ID), getArguments().getInt(ARG_RELATION_TYPE),getFetLifeApplication());
        adapter.setOnItemClickListener(this);
        adapter.setUseSwipe(false);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            RelationsRecyclerAdapter recyclerViewAdapter = (RelationsRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getActivity().getApplication();
    }

    @Override
    public void onItemClick(Member member) {
        openProfileScreen(member);
    }

    @Override
    public void onAvatarClick(Member member) {
        openProfileScreen(member);
    }

    private void openProfileScreen(Member member) {
        ProfileActivity.startActivity((BaseActivity) getActivity(),member.getId());
    }
}
