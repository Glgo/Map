package com.example.guo.map.activity.erweima;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 生成二维码
 * Created by Guo on 2016/9/3.
 */
public class CreateActivity extends Activity{

    // 整体布局
    private LinearLayout cLinearLayout;
    // 生成二维码按钮
    private Button cButton;
    // 二维码数据输入框
    private EditText cEditText;
    // 二维码生成图片
    private ImageView cImageView;
    // 二维码图片
    private Bitmap cBitmap;
    // 分享二维码按钮
    private Button cButtonShare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 整体布局设置
        cLinearLayout = new LinearLayout(this);
        cLinearLayout.setOrientation(LinearLayout.VERTICAL);
        // 输入框设置
        cEditText = new EditText(this);
        cEditText.setHint("请输入需要生成的二维码数据");
//        cEditText.setBackgroundResource(R.drawable.shape);//android4.0版本会出现黑色背景bug，所以取消
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        cLinearLayout.addView(cEditText, layoutParams);
        // 生成按钮设置
        cButton = new Button(this);
        cButton.setText("生成二维码");
        cButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cEditText.getText().toString().trim().equals("")) {
                    try {
                        // 生成带CF的二维码
//                        cBitmap = createArtwork(cEditText.getText().toString()
//                                .trim());
//                         生成纯红色的二维码
                         cBitmap = Create2DCode(cEditText.getText()
                         .toString().trim());
                        if (cBitmap != null) {
                            cImageView.setImageBitmap(cBitmap);
                            cImageView.invalidate();
                            cImageView.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CreateActivity.this, "输入不能为空",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        cLinearLayout.addView(cButton);
        // 分享二维码设置
        cButtonShare = new Button(this);
        cButtonShare.setText("分享我的二维码");
        cButtonShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cBitmap == null) {
                    Toast.makeText(CreateActivity.this, "请先生成二维码!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    myshare("分享一下我的二维码!");
                }

            }
        });
        cLinearLayout.addView(cButtonShare);
        // 生成的二维码图片设置
        cImageView = new ImageView(this);
        LayoutParams paramsImage = new LayoutParams(300, 300);
        cImageView.setBackgroundColor(0xffffffff);
        cImageView.setVisibility(View.GONE);
        cLinearLayout.addView(cImageView, paramsImage);
        setContentView(cLinearLayout);
    }

    /**
     * 字符串生成二维码
     */
    public Bitmap Create2DCode(String str) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        final int WHITE = 0xFFFFFFFF;
        // 整体为黑色
         final int BLACK = 0xFF000000;
//        final int RED = 0xFFFF0000;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
//                    pixels[y * width + x] = RED;
                     pixels[y * width + x] = BLACK ;
                }else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixel(0, 0, WHITE);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生成带有其他装饰的二维码图片
     */
    public Bitmap createArtwork(String str) throws WriterException {
        Bitmap res = Bitmap.createBitmap(300, 300, Config.ARGB_8888);

        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;
        final int RED = 0xFFFF0000;
        final int BLUE = 0xFF0000FF;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    // 绘制一个CF样式
                    if ((x > 60 && x < 120 && y > 73 && y < 77)
                            || (x > 58 && x < 62 && y >= 75 && y <= 225)
                            || (x > 60 && x < 120 && y > 223 && y < 227)) {
                        pixels[y * width + x] = RED;
                        // pixels[y * width + x-2] = BLACK ;
                    } else if ((x > 180 && x < 240 && y > 73 && y < 77)
                            || (x > 178 && x < 182 && y >= 75 && y <= 225)
                            || (x > 180 && x < 240 && y > 148 && y < 152)) {
                        pixels[y * width + x] = BLUE;
                    } else {
                        pixels[y * width + x] = BLACK;
                    }
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        bitmap.setPixel(0, 0, WHITE);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        // 绘制二维码图片
        Canvas canvas = new Canvas(res);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(bitmap, 0, 0, null);

        // 二维码上添加图片
        // Paint paint = new Paint();
        // paint.setARGB(128, 128, 0, 0);
        // Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),
        // R.drawable.ic_launcher);
        // canvas.drawBitmap(bitmap2, 0, 0, paint);
        return res;
    }

    /**
     * 执行分享
     */
    private void myshare(String content) {
        // 获取图片所在位置的uri
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
                getContentResolver(), cBitmap, null, null));
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) {
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            // 当用户选择短信时使用sms_body取得文字
            // shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        // 自定义选择框的标题
        // startActivity(Intent.createChooser(shareIntent, "邀请好友"));
        // 系统默认标题
        startActivity(shareIntent);
    }
}
