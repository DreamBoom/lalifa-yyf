package cn.rongcloud.roomkit.ui.room.fragment.gift;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;


import com.lalifa.widget.dialog.BaseDialog;

import cn.rongcloud.roomkit.R;

public class VideoGiftFragment extends BaseDialog {

    public VideoGiftFragment(Context context) {
        super(context, R.layout.fragment_video_gift, true);
    }
    @Override
    public void initView() {
        VideoView videoView = findViewById(R.id.video);
        //获取文件对象
      //  File file = new File("file:///android_asset/lite.mp4");
        /**
         * 控制视频的播放 主要通过MediaController控制视频的播放
         */
        //创建MediaController对象
        MediaController mediaController = new MediaController(getContext());
        mediaController.setVisibility(View.INVISIBLE);
        String uri = "android.resource://" + getContext().getPackageName() + "/" + R.raw.lite;
       // Log.e("======",uri);
        videoView.setVideoURI(Uri.parse(uri));
//http://vjs.zencdn.net/v/oceans.mp4
        videoView.suspend();
        videoView.setMediaController(mediaController); //让videoView 和 MediaController相关联
        videoView.setFocusable(true); //让VideoView获得焦点
        videoView.start(); //开始播放视频
        //给videoView添加完成事件监听器，监听视频是否播放完毕
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                dismiss();
              //  Toast.makeText(context, "该视频播放完毕！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void initListener() {

    }
}
