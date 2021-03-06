package com.gf.platform.gfplatform.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gf.platform.gfplatform.R;
import com.gf.platform.gfplatform.activity.MessageActivity;
import com.gf.platform.gfplatform.util.EmojiUtil;
import com.gf.platform.gfplatform.util.Global;
import com.gf.platform.gfplatform.widget.swipeback.SwipeBackActivityHelper;
import com.gf.platform.gfplatform.widget.swipelayout.SwipeLayout;
import com.gf.platform.gfplatform.widget.swipelayout.adapters.RecyclerSwipeAdapter;
import com.gf.platform.gfplatform.widget.swipelayout.implments.SwipeItemMangerImpl;

/**
 * Created by sunhaoyang on 2016/2/22.
 */
public class MessageAdapter extends RecyclerSwipeAdapter<MessageAdapter.ViewHolder> {

    private Fragment mFragment = null;
    private MsgAdapterListener mListener = null;

    public MessageAdapter(Fragment fragment, MsgAdapterListener listener) {
        mFragment = fragment;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bjmgf_message_fragment_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder view, final int position) {

        view.rlContent.setOnClickListener(v -> {
            boolean flag = false;
            for(SwipeLayout s : mItemManger.getOpenLayouts()) {
                if (s.getOpenStatus() == SwipeLayout.Status.Open) {
                    mItemManger.closeAllItems();
                    flag = true;
                }
            }
            if (flag) {
                return;
            }
            Intent intent = new Intent(mFragment.getActivity(), MessageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("index", position);
            intent.putExtras(bundle);
            SwipeBackActivityHelper.activityBuilder(mFragment.getActivity())
                    .intent(intent).needParallax(true).needBackgroundShadow(false).startActivity();
        });

        view.tvNickName.setText(Global.MESSAGES.get(position).getNickName());
        view.tvDate.setText(Global.MESSAGES.get(position).getDate());
        view.sl.setClickToClose(true);

        view.rlContent.setOnTouchListener((View v, MotionEvent event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.rlContent.setBackgroundColor(mFragment.getResources().getColor(R.color.gf_message_swipe_action_down));
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                view.rlContent.setBackgroundColor(mFragment.getResources().getColor(R.color.gf_white));
            }
            return false;
        });

        if (Global.MESSAGES.get(position).getDraft().trim().length() > 0) {
            view.tvInfo.setText(EmojiUtil.convertDraft(mFragment.getActivity(), "[草稿]" + Global.MESSAGES.get(position).getDraft()));
        } else {
            view.tvInfo.setText(Global.MESSAGES.get(position).getInfo());
        }

        if (!Global.MESSAGES.get(position).isTop()) {
            view.tvTop.setText("消息置顶");
            view.rlContent.setBackgroundColor(mFragment.getResources().getColor(R.color.gf_white));
        } else {
            view.tvTop.setText("取消置顶");
            view.rlContent.setBackgroundColor(mFragment.getResources().getColor(R.color.gf_message_top_bg));
        }

        view.rlTop.setOnClickListener(v -> {
            mListener.OnMessageTop(position);
            mItemManger.removeShownLayouts(view.sl);
            mItemManger.closeAllItems();
        });
        view.rlDel.setOnClickListener(v -> {
            mItemManger.closeItem(position);
            mItemManger.removeShownLayouts(view.sl);
            mItemManger.closeAllItems();
            mListener.OnMessageDel(position);
        });

        mItemManger.bind(view.itemView, position);
    }

    @Override
    public int getItemCount() {
        return Global.MESSAGES.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.bjmgf_message_item_sl;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rlContent = null;
        public SwipeLayout sl = null;
        public RelativeLayout rlTop = null;
        public RelativeLayout rlDel = null;
        public TextView tvNickName = null;
        public TextView tvInfo = null;
        public TextView tvDate = null;
        public TextView tvTop = null;
        public View view = null;

        public ViewHolder(View v) {
            super(v);
            rlContent = (RelativeLayout) v.findViewById(R.id.bjmgf_message_item_rl);
            sl = (SwipeLayout) v.findViewById(R.id.bjmgf_message_item_sl);
            rlTop = (RelativeLayout) v.findViewById(R.id.bjmgf_message_swipe_top_rl);
            rlDel = (RelativeLayout) v.findViewById(R.id.bjmgf_message_swipe_del_rl);
            tvNickName = (TextView) v.findViewById(R.id.bjmgf_message_top_nickname);
            tvInfo = (TextView) v.findViewById(R.id.bjmgf_message_bottom_info);
            tvDate = (TextView) v.findViewById(R.id.bjmgf_message_right_date);
            tvTop = (TextView) v.findViewById(R.id.bjmgf_message_swip_top_tv);
            view = v;
        }
    }

    public interface MsgAdapterListener {
        void OnMessageTop(int position);
        void OnMessageDel(int position);
    }

    public SwipeItemMangerImpl getItemManager() {
        return mItemManger;
    }
}
