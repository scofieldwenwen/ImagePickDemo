package com.scofield.imagepickdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.scofield.imagepickdemo.activity.ImageCropActivity;
import com.scofield.imagepickdemo.activity.ImageGridActivity;
import com.scofield.imagepickdemo.bean.ImageItem;
import com.scofield.imagepickdemo.imageloader.GlideImageLoader;
import com.scofield.imagepickdemo.uitls.ImagePicker;
import com.scofield.imagepickdemo.widget.CropImageView;
import com.scofield.imagepickdemo.widget.PickPicturePopupWindow;

import java.util.ArrayList;
import java.util.List;

import static com.scofield.imagepickdemo.R.id.gridview;
import static com.scofield.imagepickdemo.R.id.iv_avatar;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private ImageView mAvatarIv;
    private static final int IMAGE_PICKER = 1000;

    private ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(gridview);
        mAvatarIv = (ImageView) findViewById(iv_avatar);

        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
//        imagePicker.setShowCamera(true);  //显示拍照按钮
//        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
//        imagePicker.setSelectLimit(9);    //选中数量限制

    }

    public void onShowPopupWindowClick(View view) {

        imagePicker.setShowCamera(false);  //不显示拍照按钮
        imagePicker.setMultiMode(false);   //单选
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状

        PickPicturePopupWindow.getInstance(this, new PickPicturePopupWindow.OnPopuClickListener() {
            @Override
            public void onCamera() {
                imagePicker.takePicture(MainActivity.this,imagePicker.REQUEST_CODE_TAKE);
            }

            @Override
            public void onGallery() {
                Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
            }

            @Override
            public void onCancel() {}
        }).init();
    }


    public void onMultipleClick(View view) {
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setMultiMode(true);
        imagePicker.setSelectLimit(9);    //选中数量限制

        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER);
    }
    public void onWeiXinClick(View view) {
        Intent intent = new Intent(this, WxDemoActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                if (imagePicker.isCrop()) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    imagePicker.getImageLoader().displayImage(MainActivity.this, images.get(0).path, mAvatarIv, 60, 60);
                } else {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    MyAdapter adapter = new MyAdapter(images);
                    gridView.setAdapter(adapter);
                }
            } else if (requestCode == imagePicker.REQUEST_CODE_CROP) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imagePicker.getImageLoader().displayImage(MainActivity.this, images.get(0).path, mAvatarIv, 60, 60);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }

        //如果是裁剪，因为裁剪指定了存储的Uri，所以返回的data一定为null
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_TAKE) {
            //发送广播通知图片增加了
            ImagePicker.galleryAddPic(this, imagePicker.getTakeImageFile());
            ImageItem imageItem = new ImageItem();
            imageItem.path = imagePicker.getTakeImageFile().getAbsolutePath();
            imagePicker.clearSelectedImages();
            imagePicker.addSelectedImageItem(0, imageItem, true);
            if (imagePicker.isCrop()) {
                Intent intent = new Intent(MainActivity.this, ImageCropActivity.class);
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                finish();
            }
        }



    }




    private class MyAdapter extends BaseAdapter {

        private List<ImageItem> items;

        public MyAdapter(List<ImageItem> items) {
            this.items = items;
        }

        public void setData(List<ImageItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ImageItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            int size = gridView.getWidth() / 3;
            if (convertView == null) {
                imageView = new ImageView(MainActivity.this);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size);
                imageView.setLayoutParams(params);
                imageView.setBackgroundColor(Color.parseColor("#88888888"));
            } else {
                imageView = (ImageView) convertView;
            }

            imagePicker.getImageLoader().displayImage(MainActivity.this, getItem(position).path, imageView, size, size);
            return imageView;
        }
    }


}
