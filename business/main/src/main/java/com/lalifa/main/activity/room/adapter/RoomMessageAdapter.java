package com.lalifa.main.activity.room.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lalifa.main.activity.room.ext.UserManager;
import com.lalifa.main.R;
import com.lalifa.main.activity.room.message.RCChatroomAdmin;
import com.lalifa.main.activity.room.message.RCChatroomBarrage;
import com.lalifa.main.activity.room.message.RCChatroomEnter;
import com.lalifa.main.activity.room.message.RCChatroomGiftAll;
import com.lalifa.main.activity.room.message.RCChatroomKickOut;
import com.lalifa.main.activity.room.message.RCChatroomLocationMessage;
import com.lalifa.main.activity.room.widght.CenterAlignImageSpan;
import com.lalifa.utils.UiUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * @author gyn
 * @date 2021/9/23
 */
public class RoomMessageAdapter extends RcyAdapter<MessageContent, RcyHolder> {
    OnClickMessageUserListener mOnClickMessageUserListener;
    private String mRoomCreateId = "";
    private int iconSize = 0;
    public RCRefreshBuffer<MessageContent> refreshBuffer;
    WeakReference<RecyclerView> recyclerView;

    public void release() {
        if (null != refreshBuffer) {
            refreshBuffer.release();
            refreshBuffer = null;
        }
    }

    public RoomMessageAdapter(Context context, RecyclerView recyclerView,
                              OnClickMessageUserListener onClickMessageUserListener) {
        this(context, R.layout.item_message_system,
                R.layout.item_message_normal);
        this.mOnClickMessageUserListener = onClickMessageUserListener;
        iconSize = UiUtils.dp2px(11);
        this.recyclerView = new WeakReference<>(recyclerView);
    }

    public RoomMessageAdapter(Context context, int... itemLayoutId) {
        super(context, itemLayoutId);
        refreshBuffer = new RCRefreshBuffer<>(-1);
        refreshBuffer.setOnOutflowListener(data -> {
            setData(data, false);
            // ??????????????????????????????
            boolean inBottom = null != recyclerView && null != recyclerView.get() && !recyclerView.get().canScrollVertically(1);
            // ?????????????????????????????????
            boolean isMyselfMessage = false;
            if (data != null && data.size() > 0) {
                MessageContent messageContent = data.get(data.size() - 1);
                if (messageContent instanceof RCChatroomBarrage) {
                    isMyselfMessage = TextUtils.equals(((RCChatroomBarrage) messageContent).getUserId(), UserManager.get().getUserId());
                }
            }
            if (inBottom || isMyselfMessage) {
                int count = getItemCount();
                if (count > 0 && null != recyclerView.get()) {
                    recyclerView.get().smoothScrollToPosition(count - 1);
                }
            }
        });
    }

    public void setRoomCreateId(String roomCreateId) {
        this.mRoomCreateId = roomCreateId;
    }

    public synchronized void interMessage(MessageContent content) {
        if (null != refreshBuffer) refreshBuffer.apply(content);
    }

    /**
     * ???recyclerview???????????? ????????????????????????????????????????????????????????????????????????
     *
     * @param list
     * @param refresh
     */
    public synchronized void setMessages(List<MessageContent> list, boolean refresh) {
        // ??????????????????????????????
        boolean inBottom = null != recyclerView && null != recyclerView.get() && !recyclerView.get().canScrollVertically(1);
        // ????????????
        super.setData(list, refresh);
        // ?????????????????????????????????
        boolean isMyselfMessage = false;
        if (list != null && list.size() > 0) {
            MessageContent messageContent = list.get(list.size() - 1);
            if (messageContent instanceof RCChatroomBarrage) {
                isMyselfMessage = TextUtils.equals(((RCChatroomBarrage) messageContent).getUserId(),
                        UserManager.get().getUserId());
            }
        }
        if (refresh || inBottom || isMyselfMessage) {
            int count = getItemCount();
            if (count > 0 && null != recyclerView.get()) {
                recyclerView.get().smoothScrollToPosition(count - 1);
            }
        }
    }

    @Override
    public int getItemLayoutId(MessageContent item, int position) {
        if (item instanceof RCChatroomLocationMessage || item instanceof TextMessage) {
            return R.layout.item_message_system;
        } else {
            return R.layout.item_message_normal;
        }
    }

    @Override
    public void convert(RcyHolder holder, MessageContent messageContent, int position, int layoutId) {
        if (messageContent instanceof RCChatroomLocationMessage || messageContent instanceof TextMessage) {
            setSystemMessage(holder, messageContent);
        } else {
            setNormalMessage(holder, messageContent);
        }
    }

    RcyHolder lastHolder;

    public View getLastMessageView() {
        if (getData() == null || getData().size() < 1) {
            return null;
        }
        MessageContent messageContent = getData().get(getData().size() - 1);
        int id = getItemLayoutId(messageContent, 0);
        lastHolder = new RcyHolder(View.inflate(context, id, null));
        convert(lastHolder, messageContent, 0, id);
        TextView textView;
        if (messageContent instanceof RCChatroomLocationMessage || messageContent instanceof TextMessage) {
            textView = lastHolder.getView(R.id.tv_message_system);
        } else {
            textView = lastHolder.getView(R.id.tv_message_normal);
        }
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine();
        textView.setTextSize(12);
        textView.setPadding(UiUtils.dp2px(10), UiUtils.dp2px(8), UiUtils.dp2px(10), UiUtils.dp2px(8));
        return lastHolder.itemView;
    }

    /**
     * ????????????????????????
     *
     * @param holder         holder
     * @param messageContent messageContent
     */
    private void setSystemMessage(RcyHolder holder, MessageContent messageContent) {
        if (messageContent instanceof RCChatroomLocationMessage) {
            holder.setTextColor(R.id.tv_message_system, Color.parseColor("#6A9FFF"));
            holder.setText(R.id.tv_message_system, ((RCChatroomLocationMessage) messageContent).getContent());
        } else if (messageContent instanceof TextMessage) {
            if (!TextUtils.isEmpty(messageContent.getExtra()) && messageContent.getExtra().equals("mixTypeChange")) {
                holder.setTextColor(R.id.tv_message_system, Color.parseColor("#EF499A"));
            } else {
                holder.setTextColor(R.id.tv_message_system, Color.parseColor("#6A9FFF"));
            }
            holder.setText(R.id.tv_message_system, ((TextMessage) messageContent).getContent());
        }

    }

    /**
     * ????????????????????????
     *
     * @param holder  holder
     * @param message messageContent
     */
    private void setNormalMessage(RcyHolder holder, MessageContent message) {
        EmojiTextView messageTextView = holder.getView(R.id.tv_message_normal);
        List<MsgInfo> list = new ArrayList<>(4);
        messageTextView.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                0
        );
        messageTextView.setCompoundDrawablePadding(0);
        messageTextView.setBackgroundResource(R.drawable.bg_voice_room_message_item);
        if (message instanceof RCChatroomBarrage) {
            list.add(new MsgInfo(String.format("%s: ", ((RCChatroomBarrage) message).getUserName()), ((RCChatroomBarrage) message).getUserId(), true, 0, 0));
            list.add(new MsgInfo(((RCChatroomBarrage) message).getContent(), "", false, 0, 0));
        } else if (message instanceof RCChatroomEnter) {
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomEnter) message).getUserName()), ((RCChatroomEnter) message).getUserId(), true, 0, 0));
            list.add(new MsgInfo("?????????", "", false, 0, 0));
        } else if (message instanceof RCChatroomKickOut) {
            list.add(new MsgInfo(String.format("%s ??? ", ((RCChatroomKickOut) message).getTargetName()), "", false, 0, 0));
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomKickOut) message).getUserName()), ((RCChatroomKickOut) message).getUserId(), true, 0, 0));
            list.add(new MsgInfo(" ????????????", "", false, 0, 0));
        } else if (message instanceof RCChatroomGiftAll) {
            if(((RCChatroomGiftAll) message).getTargetId()==""){
                list.add(new MsgInfo(String.format("%s ", ((RCChatroomGiftAll) message).getUserName()), ((RCChatroomGiftAll) message).getUserId(), true, 0, 0));
                list.add(new MsgInfo(String.format("???????????? %s x%s", ((RCChatroomGiftAll) message).getGiftName(), ((RCChatroomGiftAll) message).getNumber()), "", false, 0, 0));
            }else {
                list.add(new MsgInfo(String.format("%s ", ((RCChatroomGiftAll) message).getUserName()), ((RCChatroomGiftAll) message).getUserId(), true, 0, 0));
                list.add(new MsgInfo(" ?????? ", "", false, 0, 0));
                list.add(new MsgInfo(String.format("%s ", ((RCChatroomGiftAll) message).getTargetName()), ((RCChatroomGiftAll) message).getTargetId(), true, 0, 0));
                list.add(new MsgInfo(String.format(" %s x%s", ((RCChatroomGiftAll) message).getGiftName(), ((RCChatroomGiftAll) message).getNumber()), "", false, 0, 0));
            }
        }else if (message instanceof RCChatroomAdmin) {
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomAdmin) message).getUserName()), ((RCChatroomAdmin) message).getUserId(), true, 0, 0));
            if (((RCChatroomAdmin) message).isAdmin()) {
                list.add(new MsgInfo(" ???????????????", "", false, 0, 0));
            } else {
                list.add(new MsgInfo(" ??????????????????", "", false, 0, 0));
            }
        } else if (message instanceof RCChatroomLocationMessage) {
            list.add(new MsgInfo(((RCChatroomLocationMessage) message).getContent(), "", false, 0, 0));
        }
        messageTextView.setText(styleBuilder(list));
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private SpannableStringBuilder styleBuilder(List<MsgInfo> infos) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        int start = 0;
        MsgInfo info;
        for (int i = 0; i < infos.size(); i++) {
            info = infos.get(i);
            if (!TextUtils.isEmpty(info.getClickId())) {
                if (TextUtils.equals(info.getClickId(), mRoomCreateId)) {
                    SpannableString icon = new SpannableString(" ");
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_creator);
                    drawable.setBounds(0, 0, iconSize, iconSize);
                    icon.setSpan(new CenterAlignImageSpan(drawable), 0, icon.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    style.append(icon).append(" ");
                    info.start = start;
                    start += 2;
                    info.end = start;
                }
            }
            info.start = start;
            start += info.getContent().length();
            info.end = start;
            style.append(info.getContent());
            if (info.isClicked()) {
                MsgInfo finalInfo = info;
                style.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        mOnClickMessageUserListener.clickMessageUser(finalInfo.getClickId());
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(Color.parseColor("#78FFFFFF"));
                    }
                }, info.start, info.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }

    public interface OnClickMessageUserListener {
        void clickMessageUser(String userId);
    }

    class MsgInfo {
        private String content = "";
        private String clickId = "";
        private boolean clicked = false;
        private int start = 0;
        private int end = 0;

        public MsgInfo(String content, String clickId, boolean clicked, int start, int end) {
            this.content = content;
            this.clickId = clickId;
            this.clicked = clicked;
            this.start = start;
            this.end = end;
        }

        public String getContent() {
            return content;
        }

        public String getClickId() {
            return clickId;
        }

        public boolean isClicked() {
            return clicked;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
