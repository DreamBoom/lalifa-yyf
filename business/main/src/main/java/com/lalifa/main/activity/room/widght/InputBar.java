package com.lalifa.main.activity.room.widght;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lalifa.main.R;
import com.lalifa.utils.UiUtils;
import com.lalifa.widget.SoftKeyboardUtils;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

/**
 * Created by gyn on 2021/11/12
 */
public class InputBar extends LinearLayout {
    private final static String TAG = InputBar.class.getSimpleName();
    private EmojiEditText etInput;
    private Space space1;
    private ImageView ivEmoji;
    private Space space2;
    private TextView tvSend;
    private String info;
    private InputBarListener inputBarListener;
    /**
     * emoji选择框
     */
    private EmojiPopup mEmojiPopup;

    public InputBar(Context context,String info) {
        this(context, info,null);
    }

    public InputBar(Context context,String info, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.info = info;
        LayoutInflater.from(context).inflate(R.layout.view_inputbar, this);
        initView();
    }


    public void initView() {
        // init view
        this.setOrientation(HORIZONTAL);
        this.setBackgroundResource(R.drawable.bg_input_bar);
        this.setMinimumHeight(UiUtils.dp2px(50));
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setPadding(UiUtils.dp2px(12), UiUtils.dp2px(7), UiUtils.dp2px(12), UiUtils.dp2px(7));
        etInput = (EmojiEditText) findViewById(R.id.et_input);
        etInput.setText(info);
        space1 = (Space) findViewById(R.id.space1);
        ivEmoji = (ImageView) findViewById(R.id.iv_emoji);
        space2 = (Space) findViewById(R.id.space2);
        tvSend = (TextView) findViewById(R.id.tv_send);
        mEmojiPopup = EmojiPopup
                .Builder
                .fromRootView(this)
                .setKeyboardAnimationStyle(com.vanniktech.emoji.R.style.emoji_fade_animation_style)
                .setOnEmojiPopupShownListener(() -> {
                    ivEmoji.setImageResource(R.drawable.ic_voice_room_keybroad);
                })
                .setOnEmojiPopupDismissListener(() -> {
                    ivEmoji.setImageResource(R.drawable.ic_voice_room_emoji);
                }).build(etInput);
        ivEmoji.setOnClickListener(v -> {
            if (inputBarListener != null) {
                boolean intercept = inputBarListener.onClickEmoji();
                if (intercept) {

                } else {
                    mEmojiPopup.toggle();
                }
            }
        });
        tvSend.setOnClickListener(v -> {
            send();
        });
        etInput.setOnEditorActionListener((v, actionId, event) -> {
            send();
            return false;
        });
    }

    public void send() {
        String message = "";
        if (etInput.getText() != null) {
            message = etInput.getText().toString().trim();
        }
        etInput.setText("");

        if (inputBarListener != null) {
            inputBarListener.onClickSend(message);
        }
    }

    public void showInputBar() {
        this.setVisibility(VISIBLE);
        etInput.requestFocus();
        SoftKeyboardUtils.showSoftKeyboard(etInput, 200);
    }

    public void hideInputBar() {
        mEmojiPopup.dismiss();
        this.setVisibility(GONE);
        etInput.clearFocus();
        SoftKeyboardUtils.hideSoftKeyboard(etInput);
    }

    public void setInputBarListener(InputBarListener inputBarListener) {
        this.inputBarListener = inputBarListener;
    }

    public interface InputBarListener {

        void onClickSend(String message);

        boolean onClickEmoji();
    }
}
