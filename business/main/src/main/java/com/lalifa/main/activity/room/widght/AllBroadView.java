package com.lalifa.main.activity.room.widght;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.adapters.ViewGroupBindingAdapter;

import com.drake.logcat.LogCat;
import com.lalifa.main.R;
import com.lalifa.main.activity.room.message.AllBroadcastManager;
import com.lalifa.main.activity.room.message.RCAllBroadcastMessage;
import com.lalifa.utils.SystemUtil;


/**
 * @author gyn
 * @date 2021/10/19
 */
public class AllBroadView extends AppCompatTextView implements AllBroadcastManager.OnObtainMessage {

    private OnClickBroadcast onClickBroadcast;
    private boolean supportJump = true;

    public AllBroadView(@NonNull Context context) {
        this(context, null);
    }

    public AllBroadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllBroadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setAlpha(0);
        setVisibility(INVISIBLE);
        setBackgroundResource(R.color.bg_broadcast);
        setTextSize(12);
        setTextColor(getResources().getColor(R.color.white));
        setMarqueeRepeatLimit(-1);
        setHorizontallyScrolling(true);
        setHorizontalFadingEdgeEnabled(true);
        int left = getResources().getDimensionPixelOffset(R.dimen.dimen_room_padding);
        int top = getResources().getDimensionPixelOffset(R.dimen.dimen_5dp);
        setPadding(left, top, left, top);
        setBroadcastListener();
    }

    public void setBroadcastListener() {
        AllBroadcastManager.getInstance().setListener(this);
    }

    public void setOnClickBroadcast(OnClickBroadcast onClickBroadcast) {
        this.onClickBroadcast = onClickBroadcast;
    }

    public void setSupportJump(boolean supportJump) {
        this.supportJump = supportJump;
    }

    public void showMessage(RCAllBroadcastMessage message) {
        if (message == null) {
            animation(false);
        } else {
            animation(true);
            setText(buildMessage(message));
            setMovementMethod(LinkMovementMethod.getInstance());
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
            setSingleLine();
            setSelected(true);
            setFocusableInTouchMode(true);
            setFocusable(true);
        }
    }

    private void animation(boolean show) {
        float alpha = 0.9f;
        if (show) {
            alpha = 1;
        }

        animate().alpha(alpha).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                LogCat.e(animation.getDuration()+"==2=="+ System.currentTimeMillis());
                if (!show) {
                    setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                LogCat.e(animation.getDuration()+"==1=="+ System.currentTimeMillis());
                if (show) {
                    setVisibility(VISIBLE);
                }
            }
        });
    }

    private SpannableStringBuilder buildMessage(RCAllBroadcastMessage message) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (message != null) {
            int start = 0, end = 0;
                String name = message.getUserName()+" 发布广播：";
                builder.append(name);
                end = name.length();
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(message.getInfo());
                start = end + message.getInfo().length();
            if (!supportJump) {
                return builder;
            }
            String clickStr = " 点击进入房间围观";
            builder.append(clickStr);
            end = start + clickStr.length();
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (onClickBroadcast != null) {
                        onClickBroadcast.clickBroadcast(message);
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#FFEB61"));
                    ds.setUnderlineText(false);
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    @Override
    protected void onDetachedFromWindow() {
        AllBroadcastManager.getInstance().removeListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onMessage(RCAllBroadcastMessage message) {
        showMessage(message);
    }

    public interface OnClickBroadcast {
        void clickBroadcast(RCAllBroadcastMessage message);
    }
}
