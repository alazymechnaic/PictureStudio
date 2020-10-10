package com.example.hp.picturestudio;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hp.picturestudio.HandleImage.*;
import com.example.hp.picturestudio.PermisionUtils.*;
import com.example.hp.picturestudio.Handle.*;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class Encode extends AppCompatActivity {
    public static void savePNG_After(Bitmap bitmap, String name) {
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //定义线程：
    public class Mythread extends Thread{
        private String message;
        public Mythread(String mes){
            this.message=mes;
        }
        @Override
        public void run(){
            ans=Handle.HS_Hide_Message(message,Con_Image);
        }
    }
    Ima_str ans=null;
    private static  final String TAG="Debug_pic";
    private int covMat2bm(Mat mat,Bitmap bm)
    {
        Utils.matToBitmap(mat, bm);
        return 1;
    }
    //存储目录saveImage
    public boolean saveImage(Bitmap bitmap, String path) {//path是传过来的文件名，时间+.png
        File directory_pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.e(TAG, "directory_pictures="+directory_pictures);
        File pic_file=new File(directory_pictures ,path);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(directory_pictures+ path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(pic_file);
        intent.setData(uri);
        sendBroadcast(intent, null);
        return true;
    }
    public final int REQ_CODE_CONT_IMG = 10;
    public final int REQ_CODE_MES_IMG  = 20;
    Mat Con_Image=null;
    Mat Mes_Image=null;
    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    //openCV4Android 需要加载用到
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java3");
        PermisionUtils.verifyStoragePermissions(this);
        if(Con_Image==null){
            final TextView tv1 = (TextView)findViewById(R.id.contain);
            tv1.setText("Container Not chosen!");
        }
        if(Mes_Image==null){
            final TextView tv2 = (TextView)findViewById(R.id.message);
            tv2.setText("Message Not chosen!");
        }
        Button contain_bt = (Button)findViewById(R.id.PIC1);
        Button Message_bt = (Button)findViewById(R.id.PIC2);
        Button Hide_bt = (Button)findViewById(R.id.hide);
        Button Hs_Hide=(Button)findViewById(R.id.HS_Hide);
        Hs_Hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Con_Image!=null){
                    EditText Secret_Mes=(EditText)findViewById(R.id.SecretMes);
                    String mes=Secret_Mes.getText().toString();
                    if(!mes.equals("")){
                        Log.d(TAG,"Start Processing Thread!");
                        Thread thread=new Mythread(mes);
                        thread.start();
                    }else {
                        Toast ts = Toast.makeText(getBaseContext(),"Input Something!", Toast.LENGTH_LONG);
                        ts.show();
                    }
                    while(ans==null){
                    }
                    Log.d(TAG,"Processing Image! Please wait!");
                    if(ans!=null){
                        Log.d(TAG,"Start Save Thread!");
                        Mat Result=ans.Get_image();
                        String Secret_Key=ans.Get_String();
                        Bitmap mBitmap = null;
                        mBitmap = Bitmap.createBitmap(Result.cols(), Result.rows(), Bitmap.Config.ARGB_8888);
                        //生成Bitmap类型对象
                        Utils.matToBitmap(Result, mBitmap);
                        Date date = new Date();
                        String time = date.toLocaleString();
                        //使用当前时间来标识文件名
                        saveImage(mBitmap,"Encode:"+time+".png");
                        Toast ts = Toast.makeText(getBaseContext(),"SECRET_KEY:"+Secret_Key+"Saved as "+time, Toast.LENGTH_LONG);
                        ts.show();
                    }else{
                        Toast ts = Toast.makeText(getBaseContext(),"Input String Length out of range!", Toast.LENGTH_LONG);
                        ts.show();
                    }
                }else{
                    Toast ts = Toast.makeText(getBaseContext(),"Please Choose Container Image First!", Toast.LENGTH_LONG);
                    ts.show();
                }
            }
        });
        Hide_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Mes_Image!=null && Con_Image!=null){
                    Mat Result=HandleImage.Image_Encode(Con_Image,Mes_Image);
                    Bitmap mBitmap = null;
                    mBitmap = Bitmap.createBitmap(Result.cols(), Result.rows(), Bitmap.Config.ARGB_8888);
                    //生成Bitmap类型对象
                    Utils.matToBitmap(Result, mBitmap);
                    Date date = new Date();
                    String time = date.toLocaleString();
                    //使用当前时间来标识文件名
                    saveImage(mBitmap,"Encode:"+time+".png");
                    Toast ts = Toast.makeText(getBaseContext(),"Saved as "+time, Toast.LENGTH_LONG);
                    ts.show();
                }else{
                    Toast ts = Toast.makeText(getBaseContext(),"Please Choose some Images First!", Toast.LENGTH_LONG);
                    ts.show();
                }
            }
        });
        contain_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imgPickerIntent = new Intent(Intent.ACTION_PICK);
                imgPickerIntent.setType("image/*");
                //以包含者的身份去打开图库
                startActivityForResult(imgPickerIntent, REQ_CODE_CONT_IMG);
            }
        });
        Message_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imgPickerIntent = new Intent(Intent.ACTION_PICK);
                imgPickerIntent.setType("image/*");
                //以隐藏信息的部分去打开
                startActivityForResult(imgPickerIntent, REQ_CODE_MES_IMG);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //接收的返回值，防止空指针
        //在图库选择图片后，设置重新加载当前Activity的信息接收函数
        ImageView mImageView = (ImageView)findViewById(R.id.imageView2);
        super.onActivityResult(requestCode, resultCode, data);
        //判断当前是否是从图库返回
        if (requestCode == REQ_CODE_CONT_IMG) {
            if (resultCode == RESULT_OK) {   //RESULT_OK为预定义的的返回结果，此处表示执行成功
                try {
                    //获取图库选择的图像，Android上可用的每种资源 (图像、视频片段、网页等) 都可以用Uri来表示
                    final Uri imgUri = data.getData(); //返回的data为资源定位符
                    //根据Uri构造出InputStream对象
                    final InputStream inputStream = getContentResolver().openInputStream(imgUri);
                    //图像输入流解析为bitmap对象
                    final Bitmap originImg = BitmapFactory.decodeStream(inputStream);
                    //根据bitmap信息初始化mat对象
                    Mat src = new Mat(originImg.getHeight(), originImg.getWidth(), CvType.CV_8UC4);
                    //将bitmap转换为mat对象
                    Utils.bitmapToMat(originImg, src);
                    //以上部分为获取到的图像并且转化为Mat对象
                    Con_Image=src;
                    if(Con_Image!=null){
                        final TextView tv = (TextView)findViewById(R.id.contain);
                        tv.setText("Ready!");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == REQ_CODE_MES_IMG){
            if (resultCode == RESULT_OK) {   //RESULT_OK为预定义的的返回结果，此处表示执行成功
                try {
                    //获取图库选择的图像
                    final Uri imgUri = data.getData(); //返回的data为资源定位符
                    //根据Uri构造出InputStream对象
                    final InputStream inputStream = getContentResolver().openInputStream(imgUri);
                    //图像输入流解析为bitmap对象
                    final Bitmap originImg = BitmapFactory.decodeStream(inputStream);
                    //根据bitmap信息初始化mat对象
                    Mat src = new Mat(originImg.getHeight(), originImg.getWidth(), CvType.CV_8UC4);
                    //将bitmap转换为mat对象
                    Utils.bitmapToMat(originImg, src);
                    //以上部分为获取到的图像并且转化为Mat对象
                    Mes_Image= src;//将获得的图片赋值给全局变量
                    if(Mes_Image!=null){
                        final TextView tv = (TextView)findViewById(R.id.message);
                        tv.setText("Ready!");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
