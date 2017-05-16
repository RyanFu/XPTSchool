package com.xptschool.teacher.ui.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.teacher.R;
import com.xptschool.teacher.model.BeanChat;
import com.xptschool.teacher.model.ContactParent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dexing on 2017/5/10.
 * No1
 */

public class ChatAdapter extends RecyclerView.Adapter {

    private String TAG = ChatAdapter.class.getSimpleName();
    private List<BeanChat> listChat;
    private int VIEW_PARENT = 0;
    private int VIEW_TEACHER = 1;
    private ParentAdapterDelegate parentAdapterDelegate;
    private TeacherAdapterDelegate teacherAdapterDelegate;
    private ContactParent currentParent;

    public ChatAdapter(Context context) {
        parentAdapterDelegate = new ParentAdapterDelegate(context, VIEW_PARENT);
        teacherAdapterDelegate = new TeacherAdapterDelegate(context, VIEW_TEACHER);
    }

    @Override
    public int getItemViewType(int position) {
        return listChat.get(position).isSend() ? teacherAdapterDelegate.getViewType() : parentAdapterDelegate.getViewType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == parentAdapterDelegate.getViewType()) {
            return parentAdapterDelegate.onCreateViewHolder(parent);
        } else {
            return teacherAdapterDelegate.onCreateViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        Log.i(TAG, "onBindViewHolder position:" + position + " viewType:" + viewType);
        if (viewType == parentAdapterDelegate.getViewType()) {
            parentAdapterDelegate.onBindViewHolder(currentParent, listChat, position, holder);
        } else {
            teacherAdapterDelegate.onBindViewHolder(listChat, position, holder, new OnItemResendListener());
        }
    }

    @Override
    public int getItemCount() {
        return listChat == null ? 0 : listChat.size();
    }

    public void loadData(List<BeanChat> chats, ContactParent parent) {
        listChat = chats;
        currentParent = parent;
        notifyDataSetChanged();
    }

    //  添加数据
    public void addData(BeanChat chat) {
        Log.i(TAG, "addData: " + chat.getChatId() + " content:" + chat.getContent());
        listChat.add(listChat.size(), chat);
        notifyItemInserted(listChat.size());
    }

    public void updateData(BeanChat chat) {
        Log.i(TAG, "updateData: ");
        for (int i = 0; i < listChat.size(); i++) {
            if (listChat.get(i).getChatId().equals(chat.getChatId())) {
                Log.i(TAG, "updateData chatId : " + chat.getChatId() + "  position:" + i);
                listChat.set(i, chat);
                notifyItemChanged(i);
                break;
            }
        }
    }

    //  删除数据
    public void removeData(int position) {
        listChat.remove(position);
        notifyItemRemoved(listChat.size());
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (teacherAdapterDelegate != null) {
            teacherAdapterDelegate.onDetachedFromRecyclerView();
        }
        if (parentAdapterDelegate != null) {
            parentAdapterDelegate.onDetachedFromRecyclerView();
        }
    }

    public class OnItemResendListener {
        void onResend(BeanChat chat, int position) {
            removeData(position);
            chat.onReSendChatToMessage();
        }
    }

}
