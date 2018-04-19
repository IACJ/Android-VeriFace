package com.scut.veriface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.scut.veriface.aliface.FaceService;
import com.scut.veriface.baiduface.FaceDetect;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;

import com.scut.veriface.baiduface.auth.AuthService;
import com.scut.veriface.baiduface.entity.DetectResult;

public class BaiduFaceDetectActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView picture;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_face_detect);


        textView=(TextView)findViewById(R.id.text_view);
        picture=(ImageView)findViewById(R.id.picture);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* 从 intent中获取 imageUri 或 imagePath ， 然后转换为 bitmap */
        Intent intent = getIntent();
        Uri imageUri = (Uri) intent.getExtras().get("IMAGE_URI");
        String imagePath = (String) intent.getExtras().get("IMAGE_PATH");

        if (imageUri == null && imagePath== null){
            showResponse("请先放入图片~~");
            return;
        }

        if (imageUri !=null){
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (imagePath !=null){
            bitmap=BitmapFactory.decodeFile(imagePath);
        }
        /* 上面代码已拿到bitmap*/
        /* 接下来进行网络请求 */
        new Thread(networkTask).start();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.list_face:
                Intent intent=new Intent(this, ListUserActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                Intent intent1=new Intent(this, HelpActivity.class);
                startActivity(intent1);
                break;
            default:
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        /* 将原始图片先显示出来 */
        super.onWindowFocusChanged(hasFocus);
        if (bitmap !=null){
            int maxHeight = dp2px(this, 500);
            int height = (int) ((float) picture.getWidth() / bitmap.getWidth() * bitmap.getHeight());
            if (height > maxHeight) height = maxHeight;
            picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
            picture.setImageBitmap(bitmap);
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /* 处理 网络请求 之后的结果*/
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String jsonRe = data.getString("jsonRe");
            Log.i("mylog", "请求结果为-->" + jsonRe);

            Gson gson = new GsonBuilder().create();
            DetectResult baiduResult = gson.fromJson(jsonRe, DetectResult.class);
            showResponse("识别出人脸个数："+baiduResult.result_num);
            System.out.println(baiduResult);
            drawRectangles(bitmap,baiduResult.result);
        }
    };

    void showResponse(final String response) {
        /* 更新界面*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(response);
                Toast.makeText(BaiduFaceDetectActivity.this,response,Toast.LENGTH_LONG).show();
            }
        });
    }

    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            Log.i("wechat", "原始图片的大小" + (bitmap.getByteCount() / 1024)
                    + "K,宽度为" + bitmap.getWidth() + ",高度为" + bitmap.getHeight());
            /* 加速程序时，注释掉下方内容  ***********************/
//            {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//                String strImg = FaceService.getImageStr(isBm);
//                System.out.println("原始图片图片base64长度:" + strImg.length() / 1024 + "K");
//            }
            /* *************************************************/

            /* 如果照片太大，则将其压缩为 MAX_SIZE K */
            int size = bitmap.getByteCount()/ 1024;
            final int MAX_SIZE = 1800;
            if (size >MAX_SIZE){
                float scale =  (float) Math.sqrt((float) MAX_SIZE / size);
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
                Log.i("wechat", "压缩后图片的大小" + (bitmap.getByteCount() / 1024 )
                        + "K宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());
            }

            /* 将 bitmap 转为 inputStream */
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            InputStream isBm = new ByteArrayInputStream(baos.toByteArray());

            /* 将图片转为 base64 格式的字符串，并加密后发给`阿里云.人脸识别服务` */
            String strImg = FaceService.getImageStr(isBm); // 将图片转为 base64 格式的字符串
            System.out.println("发送图片base64长度:"+strImg.length()/1024+"K");
            String jsonRe = null;
            try {

                jsonRe = FaceDetect.detect(AuthService.getAuth(),strImg);

                data.putString("jsonRe", jsonRe);
                msg.setData(data);
                handler.sendMessage(msg);
            } catch (UnknownHostException e){
                showResponse("网络故障(UnknownHostException)。请检查是否联网。");
            }catch (ConnectException e){
                showResponse("网络故障(ConnectException)。请检查是否联网。");
            } catch (Exception e) {
                showResponse("网络故障。请检查是否联网。");
                e.printStackTrace();
            }

        }
    };

    private void drawRectangles(Bitmap imageBitmap, DetectResult.Result[] keywordRects) {
        /* 在 bitmap 上面画矩阵 */
        int left, top, right, bottom;
        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        if (keywordRects != null){
            for (int i = 0; i < keywordRects.length; i++) {
                left = keywordRects[i].location.left;
                top = keywordRects[i].location.top;
                right = left+keywordRects[i].location.width;
                bottom = top+keywordRects[i].location.height;
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }

        /* 将画好的图，按照一定缩放比例，展示出来 */
        int maxHeight = dp2px(this, 550);
        int height = (int) ((float) picture.getWidth()/mutableBitmap.getWidth() * mutableBitmap.getHeight());
        if (height > maxHeight) height = maxHeight;
        picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        picture.setImageBitmap(mutableBitmap);
    }

    public static int dp2px(Context context, int dp)
    {
        /* 单位转换 ： dp 转换为 px */
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
