package com.lalifa.yyf.weight.code;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.lalifa.yyf.R;

public class EditTextSendCodeBtn extends LinearLayout {
    EditText code;
    TextView send;
    private CountDownTimer timer;
    SendListener sendListener;
    Context context;
    public void setSendListener(SendListener sendListener) {
        this.sendListener = sendListener;
    }
    public interface SendListener {
        void sendCode(String phone);
    }

    public EditTextSendCodeBtn(Context context) {
        super(context);
        this.context = context;
        init();
    }


    public EditTextSendCodeBtn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        setBackgroundColor(getResources().getColor(R.color.transparent));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.edt_send_code, this, true);
        code = view.findViewById(R.id.et_code);
        send = view.findViewById(R.id.send);
        send.setOnClickListener(v -> {
            isCountdown();
        });
    }

    public void isCountdown() {
        sendListener.sendCode(getCode());
         Long aLong = 0L;
//        if (aLong == 0) {
//            closeTimer();
//            setEnabled(true);
//            send.setText("获取验证码");
//            return;
//        }
        Long time = System.currentTimeMillis();
        Long difference = time - aLong;
        if (difference < 60000) {
            countDown((60000 - difference));
        }
    }

    public String getCode() {
        if (code != null) {
            return code.getText().toString().trim();
        } else {
            return "";
        }
    }

    /**
     * 倒计时显示
     */
    public void countDown(long alltime) {

        timer = new CountDownTimer(alltime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                send.setEnabled(false);
                send.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                send.setEnabled(true);
                send.setText("重新发送");
            }
        }.start();

    }

    public void closeTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
    public void reFresh(){
        if (timer != null) {
            timer.cancel();
            timer.onFinish();
        }
    }
    public void setSendEnable(boolean b) {
        send.setEnabled(b);
        if (b) {
            send.setEnabled(true);
            send.setTextColor(ContextCompat.getColor(context,R.color.zs));
        } else {
            send.setEnabled(false);
            send.setTextColor(ContextCompat.getColor(context,R.color.white));
        }

    }
}
