package cn.rongcloud.roomkit.ui.roomlist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentActivity;

import com.lalifa.utils.ImageLoader;
import com.lalifa.utils.RealPathFromUriUtils;
import com.lalifa.utils.UiUtils;
import com.lalifa.widget.ChineseLengthFilter;
import com.lalifa.widget.dialog.BottomDialog;
import com.lalifa.widget.loading.LoadTag;

import java.util.Locale;
import java.util.Random;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.api.Office;
import cn.rongcloud.roomkit.manager.LocalDataManager;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import io.rong.imkit.picture.tools.ToastUtils;

/**
 * @author gyn
 * @date 2021/9/16
 */
public class CreateRoomDialog extends BottomDialog {
    private ImageView mCoverImage;
    private ActivityResultLauncher mLauncher;
    private String mCoverUrl;
    private String mRoomBackground;
    private String mPassword = "";

    private EditText mRoomNameEditText;
    private RadioButton mPrivateButton;

    private CreateRoomCallBack mCreateRoomCallBack;
    private InputPasswordDialog mInputPasswordDialog;
    private LoadTag mLoading;
    private Bitmap themeBitmap;

    public CreateRoomDialog(Activity activity, ActivityResultLauncher launcher,
                            CreateRoomCallBack createRoomCallBack) {
        super((FragmentActivity) activity);
        this.mLauncher = launcher;
        this.mCreateRoomCallBack = createRoomCallBack;
        setContentView(R.layout.dialog_create_room, -1, UiUtils.dp2px(590));
        initView();
    }

    public void setCoverUri(Uri coverUri) {
        this.mCoverUrl = RealPathFromUriUtils.getRealPathFromUri(mActivity, coverUri);
        ImageLoader.loadUri(mCoverImage, coverUri, R.drawable.ic_create_voice_room_default_cover);
    }

    private void initView() {
        // 关闭
        getContentView().findViewById(R.id.iv_fold).setOnClickListener(v -> {
            dismiss();
        });
        // 房间封面
        mCoverImage = getContentView().findViewById(R.id.iv_room_cover);
        mCoverImage.setOnClickListener(v -> {
            startPicSelectActivity();
        });
        mCoverImage.setImageBitmap(themeBitmap);
        // 房间背景
        mRoomBackground = LocalDataManager.getBackgroundByIndex(0);
        ImageView backgroundImage = getContentView().findViewById(R.id.iv_background);
        if (!TextUtils.isEmpty(mRoomBackground)) {
            ImageLoader.loadUrl(backgroundImage, mRoomBackground, R.drawable.bg_create_room);
        }

        // 创建房间
        getContentView().findViewById(R.id.btn_create_room).setOnClickListener(v -> {
            preCreateRoom();
        });

        mRoomNameEditText = getContentView().findViewById(R.id.et_room_name);
        mRoomNameEditText.setFilters(new InputFilter[]{new ChineseLengthFilter(20)});
        mLoading = new LoadTag(mActivity, mActivity.getString(R.string.text_creating_room));
    }

    /**
     * 创建房间前逻辑判断
     */
    private void preCreateRoom() {
        // 房间名检测
        String roomName = mRoomNameEditText.getText() == null ? "" : mRoomNameEditText.getText().toString();
        if (TextUtils.isEmpty(roomName)) {
            ToastUtils.s(mActivity, mActivity.getString(R.string.please_input_room_name));
            return;
        }
        // 私密房密码检测
        if (mPrivateButton.isChecked() && TextUtils.isEmpty(mPassword)) {
            mInputPasswordDialog = new InputPasswordDialog(mActivity, false, new InputPasswordDialog.OnClickListener() {
                @Override
                public void clickCancel() {

                }

                @Override
                public void clickConfirm(String password) {
                    if (TextUtils.isEmpty(password)) {
                        return;
                    }
                    if (password.length() < 4) {
                        ToastUtils.s(mActivity, mActivity.getString(R.string.text_please_input_four_number));
                        return;
                    }
                    mPassword = password;
                    mInputPasswordDialog.dismiss();
                    uploadThemePic(roomName);
                }
            });
            mInputPasswordDialog.show();
        } else {
            uploadThemePic(roomName);
        }
    }

    private void uploadThemePic(String roomName) {
        // 选择本地图片后，先上传本地图片
        if (!TextUtils.isEmpty(mCoverUrl)) {
            mLoading.show();
            //todo 222
//            FileBody body = new FileBody("multipart/form-data", new File(mCoverUrl));
//            OkApi.file(VRApi.FILE_UPLOAD, "file", body, new WrapperCallBack() {
//                @Override
//                public void onResult(Wrapper result) {
//                    String url = result.getBody().getAsString();
//                    if (result.ok() && !TextUtils.isEmpty(url)) {
//                        createRoom(roomName, VRApi.FILE_PATH + url);
//                    } else {
//                        ToastUtils.s(mActivity, result.getMessage());
//                        mLoading.dismiss();
//                    }
//                }
//
//                @Override
//                public void onError(int code, String msg) {
//                    super.onError(code, msg);
//                    ToastUtils.s(mActivity, msg);
//                    mLoading.dismiss();
//                }
//            });
        } else if (themeBitmap != null) {
            mLoading.show();
            //todo 222
//            BitmapBody body = new BitmapBody(null, themeBitmap);
//            OkApi.bitmap(VRApi.FILE_UPLOAD, "file", body, new WrapperCallBack() {
//                @Override
//                public void onResult(Wrapper result) {
//                    String url = result.getBody().getAsString();
//                    if (result.ok() && !TextUtils.isEmpty(url)) {
//                        createRoom(roomName, VRApi.FILE_PATH + url);
//                    } else {
//                        ToastUtils.s(mActivity, result.getMessage());
//                        mLoading.dismiss();
//                    }
//                }
//
//                @Override
//                public void onError(int code, String msg) {
//                    super.onError(code, msg);
//                    ToastUtils.s(mActivity, msg);
//                    mLoading.dismiss();
//                }
//            });
        } else {
            mLoading.show();
            createRoom(roomName, "");
        }
    }

    /**
     * 创建房间
     *
     * @param roomName
     * @param themeUrl
     */
    private void createRoom(String roomName, String themeUrl) {
        //todo 创建房间
//        Map<String, Object> params = new HashMap<>();
//        params.put("name", roomName);
//        params.put("themePictureUrl", themeUrl);
//        params.put("isPrivate", mPrivateButton.isChecked() ? 1 : 0);
//        params.put("password", mPassword);
//        params.put("backgroundUrl", mRoomBackground);
//        params.put("kv", new ArrayList());
//        params.put("roomType", mRoomType.getType());
//        OkApi.post(VRApi.ROOM_CREATE, params, new WrapperCallBack() {
//            @Override
//            public void onResult(Wrapper result) {
//                if (mCreateRoomCallBack != null) {
//                    RoomDetailBean voiceRoomBean = result.get(RoomDetailBean.class);
//                    if (result.ok() && voiceRoomBean != null) {
//                        dismiss();
//                    //    mCreateRoomCallBack.onCreateSuccess(voiceRoomBean);
//                    } else if (30016 == result.getCode() && voiceRoomBean != null) {
//                        dismiss();
//                      //  mCreateRoomCallBack.onCreateExist(voiceRoomBean);
//                    } else {
//                        ToastUtils.s(mActivity, result.getMessage());
//                    }
//                }
//                mLoading.dismiss();
//            }
//
//            @Override
//            public void onError(int code, String msg) {
//                super.onError(code, msg);
//                ToastUtils.s(mActivity, msg);
//                mLoading.dismiss();
//            }
//        });
    }

    /**
     * 选择图片
     */
    private void startPicSelectActivity() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        if (mLauncher != null) {
            mLauncher.launch(intent);
        }
    }

    public interface CreateRoomCallBack {
        void onCreateSuccess(String roomId);

        void onCreateExist(String roomId);
    }
}
