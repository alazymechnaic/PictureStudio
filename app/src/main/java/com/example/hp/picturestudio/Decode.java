package com.example.hp.picturestudio;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.hp.picturestudio.Handle.*;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import com.example.hp.picturestudio.PermisionUtils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class Decode extends AppCompatActivity {
    private static  final String TAG="Debug_pic";
    public final int REQ_CODE_PICK_IMG = 1;
    public final int REQ_MES_CODE = 2;
    private static final String SDCARD_CACHE_IMG_PATH = "/storage/emulated/0/";
    String Mes=null;
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
    private int covMat2bm(Mat mat,Bitmap bm)
    {
        Utils.matToBitmap(mat, bm);
        return 1;
    }
    private boolean Ifwords(String s){
        char[] Astr=s.toCharArray();
        for(int i=0;i<Astr.length;i++){
            if(Astr[i]<33||Astr[i]>126){
                return false;
            }
        }
        return true;
    }
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
        setContentView(R.layout.activity_decode);
        Button btnLoadImg = (Button)findViewById(R.id.choose);
        Button GetMes=(Button)findViewById(R.id.secretMes);
        Button set_num=(Button)findViewById(R.id.textbutton);
        //申请读写权限
        OpenCVLoader.initDebug();
        PermisionUtils.verifyStoragePermissions(this);
        //加载OpenCV库
        System.loadLibrary("opencv_java3");

        set_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText Sec_num=(EditText)findViewById(R.id.editText2);
                Mes=Sec_num.getText().toString();
                Toast ts = Toast.makeText(getBaseContext(),"Input Success!", Toast.LENGTH_LONG);
                ts.show();
            }
        });
        GetMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imgPickerIntent = new Intent(Intent.ACTION_PICK);
                imgPickerIntent.setType("image/*");
                startActivityForResult(imgPickerIntent, REQ_MES_CODE);
            }
        });
        btnLoadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //启动图库
                Intent imgPickerIntent = new Intent(Intent.ACTION_PICK);
                imgPickerIntent.setType("image/*");
                startActivityForResult(imgPickerIntent, REQ_CODE_PICK_IMG);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //接收的返回值，防止空指针
        //在图库选择图片后，设置重新加载当前Activity的信息接收函数
        ImageView mImageView = (ImageView)findViewById(R.id.imageView2);
        super.onActivityResult(requestCode, resultCode, data);
        //判断当前是否是从图库返回
        if (requestCode == REQ_CODE_PICK_IMG) {
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
                    Mat Image= HandleImage.Image_Decode(src);//获得解密后的图片
                    Bitmap mBitmap = null;
                    //Imgproc.cvtColor(seedsImage, rgba, Imgproc.COLOR_GRAY2RGBA, 4); //转换通道
                    mBitmap = Bitmap.createBitmap(Image.cols(), Image.rows(), Bitmap.Config.ARGB_8888);
                    //生成Bitmap类型对象
                    Utils.matToBitmap(Image, mBitmap);
                    //将图片显示到当前页面
                    mImageView.setImageBitmap( mBitmap );
                    mImageView.invalidate();
                    mImageView.setVisibility(View.VISIBLE);
                    Date date = new Date();
                    String time = date.toLocaleString();
                    //使用当前时间来标识文件名
                    saveImage(mBitmap,"Encode:"+time+".png");
                    Toast ts = Toast.makeText(getBaseContext(),"Result Saved as "+time, Toast.LENGTH_LONG);
                    ts.show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == REQ_MES_CODE){
            if(resultCode==RESULT_OK){
                EditText Sec_num=(EditText)findViewById(R.id.editText2);
                Mes=Sec_num.getText().toString();
                if(Mes!=null){
                    Integer secnum=Integer.parseInt(Mes);
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
                        Handle.Ima_str result;
                        result=Handle.HS_Generate_Message(src,secnum);
                        Mat Image= result.Get_image();
                        String Mesresult=null;
                        Mesresult=result.Get_String();
                        if(Mesresult!=null){
                            TextView tv = (TextView)findViewById(R.id.textView);
                            tv.setText("Get Result:"+Mesresult);
                            Bitmap mBitmap = null;
                            //Imgproc.cvtColor(seedsImage, rgba, Imgproc.COLOR_GRAY2RGBA, 4); //转换通道
                            mBitmap = Bitmap.createBitmap(Image.cols(), Image.rows(), Bitmap.Config.ARGB_8888);
                            //生成Bitmap类型对象
                            Utils.matToBitmap(Image, mBitmap);
                            //将图片显示到当前页面
                            mImageView.setImageBitmap( mBitmap );
                            mImageView.invalidate();
                            mImageView.setVisibility(View.VISIBLE);
                            Date date = new Date();
                            String time = date.toLocaleString();
                            //使用当前时间来标识文件名
                            saveImage(mBitmap,"Encode:"+time+".png");
                            Toast ts = Toast.makeText(getBaseContext(),"Container Image Saved as "+time, Toast.LENGTH_LONG);
                            ts.show();
                        }else{
                            final TextView tv = (TextView)findViewById(R.id.textView);
                            tv.setText("There Must be something Wrong");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast ts=Toast.makeText(getBaseContext(),"No Secret Key imputed!",Toast.LENGTH_LONG);
                    ts.show();
                }
            }
        }
    }
}
