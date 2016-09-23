package com.scofield.imagepickdemo.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

import com.scofield.imagepickdemo.R;


/**
 * 选取图片底部弹框
 */
public class PickPicturePopupWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private PopupWindow popupWindow;
    private View popupWindowView;
    private LinearLayout popup_ll;
    private Button cameraBtn;
    private Button galleryBtn;
    private Button cancelBtn;
    private OnPopuClickListener clickListener;

    /**
     * 点击相关item后的操作
     */
    public interface OnPopuClickListener {
        void onCamera();

        void onGallery();

        void onCancel();
    }

    private PickPicturePopupWindow() {
    }

    private PickPicturePopupWindow(Context context, OnPopuClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;

        popupWindowView = View.inflate(context, R.layout.layout_popupwindow_pick_picture, null);
        popup_ll = (LinearLayout) popupWindowView.findViewById(R.id.ll_popup);
        cameraBtn = (Button) popupWindowView.findViewById(R.id.item_pop_camera);
        galleryBtn = (Button) popupWindowView.findViewById(R.id.item_pop_Photo);
        cancelBtn = (Button) popupWindowView.findViewById(R.id.item_pop_cancel);

    }

    /**
     * 用于创建对象
     *
     * @param context
     * @param clickListener 回调点击item事件接口
     * @return 返回对象
     */
    public static PickPicturePopupWindow getInstance(Context context, OnPopuClickListener clickListener) {
        PickPicturePopupWindow selectPicturePopupWindow = new PickPicturePopupWindow(context, clickListener);
        return selectPicturePopupWindow;
    }

    //启用popup
    public void init() {
        if (popupWindow != null && popupWindow.isShowing()) {
            dismissWindow();
        } else {
            showWindow();
        }
    }

    //设置相机按钮文本
    public void setCameraText(String text) {
        cameraBtn.setText(text);
    }

    //设置相册按钮文本
    public void setGalleryText(String text) {
        cameraBtn.setText(text);
    }

    //隐藏popup
    private void dismissWindow() {
        popup_ll.clearAnimation();
        popup_ll.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bottom_out));
        popupWindow.dismiss();
        popupWindow = null;
    }

    //显示popup
    private void showWindow() {
        popupWindow = new PopupWindow(popupWindowView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);

        popup_ll.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bottom_in));
        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        ColorDrawable dw = new ColorDrawable(0x80000000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAtLocation(popup_ll, Gravity.BOTTOM, 0, 0);
        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = popupWindowView.findViewById(R.id.ll_popup).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismissWindow();
                    }
                }
                return true;
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (clickListener == null) {
            dismissWindow();
            return;
        }

        switch (view.getId()) {
            case R.id.item_pop_camera:
                clickListener.onCamera();
                dismissWindow();
                break;

            case R.id.item_pop_Photo:
                clickListener.onGallery();
                dismissWindow();
                break;

            case R.id.item_pop_cancel:
                dismissWindow();
                break;

            default:
                break;
        }
    }

}
