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
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.util.ChatUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dexing on 2017/5/10.
 * No1
 */

public class ChatAdapter extends RecyclerView.Adapter {

    private String TAG = ChatAdapter.class.getSimpleName();
    private List<BeanChat> listChat = new ArrayList<>();
    private int VIEW_PARENT = 0;
    private int VIEW_TEACHER = 1;
    private ParentAdapterDelegate parentAdapterDelegate;
    private TeacherAdapterDelegate teacherAdapterDelegate;
    private ContactParent currentParent;

    public ChatAdapter(Context context) {
        parentAdapterDelegate = new ParentAdapterDelegate(context, VIEW_PARENT);
        teacherAdapterDelegate = new TeacherAdapterDelegate(context, VIEW_TEACHER);
        SoundPlayHelper.getInstance().setPlaySoundViews(null);
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

    public void setCurrentParent(ContactParent currentParent) {
        this.currentParent = currentParent;
    }

    public void appendData(List<BeanChat> chats) {
        if (listChat.size() == 0) {
            listChat = chats;
        } else {
            List<BeanChat> newList = new ArrayList<BeanChat>();
            for (Iterator<BeanChat> it = chats.iterator(); it.hasNext(); ) {
                newList.add(it.next());
            }
            listChat.addAll(0, newList);
        }
        notifyDataSetChanged();
    }

    //  添加数据
    public void addData(BeanChat chat) {
        listChat.add(listChat.size(), chat);
        notifyItemInserted(listChat.size());
    }

    public void updateData(BeanChat chat) {
        for (int i = 0; i < listChat.size(); i++) {
            if (listChat.get(i).getChatId().equals(chat.getChatId())) {
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

    public class OnItemResendListener {
        void onResend(BeanChat chat, int position) {
//            removeData(position);
            chat.setSendStatus(ChatUtil.STATUS_SENDING);
            updateData(chat);
            GreenDaoHelper.getInstance().updateChat(chat);
            chat.onReSendChatToMessage();
        }
    }

}