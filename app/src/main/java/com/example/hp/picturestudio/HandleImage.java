package com.example.hp.picturestudio;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class HandleImage {
    private static  final String TAG="Debug_pic";
    public static Mat Generate_Double(Mat image){
        //转化为灰度图
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
        //自适应二值化，第一个参数为input，第二个为output
        Log.i(TAG,"Finish:1");
                Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 10);
        System.out.println("Finish Generating!");
        return image;
    }
    public static Mat Image_Encode(Mat image1,Mat image2){
        //将两张图片调整至相同的尺寸
        image2=Generate_Double(image2);
        Log.i(TAG,"Starting Encoding!");
        if (image1.cols() < image2.cols()) {
            Imgproc.resize(image2, image2, new Size(image1.cols()-10,image2.rows()));
            Log.i(TAG,"Resized col!");
        }
        if (image1.rows() < image2.rows()) {
            Imgproc.resize(image2, image2, new Size(image2.cols(),image1.rows()-10));
            Log.i(TAG,"Resized row!");
        }
        //白色像素点设置为奇数，黑色像素点为偶数
        for(int i=0;i<image2.rows();i++){
            for(int j=0;j<image2.cols();j++){
                double pixel=image2.get(i,j)[0];
                if(pixel>0){
                    double knw=image1.get(i,j)[2];
                    if((int)knw%2==0&&image1.get(i,j)[2]<255){
                        double data0=image1.get(i,j)[0];
                        double data1=image1.get(i,j)[1];
                        int data2=(int)image1.get(i,j)[2];
                        data2=data2+1;
                        image1.put(i,j,data0,data1,(double)data2);
                    }
                }else {
                    double knw=image1.get(i,j)[2];
                    if((int)knw%2!=0&&image1.get(i,j)[2]<255){
                        double data0=image1.get(i,j)[0];
                        double data1=image1.get(i,j)[1];
                        int data2=(int)image1.get(i,j)[2];
                        data2+=1;
                        image1.put(i,j,data0,data1,(double)data2);
                    }
                }
            }
        }
        Log.i(TAG,"Finish First processing");
        //将未隐藏的区域全部设置为无信息
        for(int i=image2.rows();i<image1.rows();i++){
            for(int j=0;j<image1.cols();j++){
                if((int)image1.get(i,j)[2]%2==0){
                    double data0=image1.get(i,j)[0];
                    double data1=image1.get(i,j)[1];
                    int data2=(int)image1.get(i,j)[2];
                    image1.put(i,j,data0,data1,(double)data2+1);
                }
            }
        }
        Log.i(TAG,"Finish Second Processing");
        for(int j=image2.cols();j<image1.cols();j++){
            for(int i=0;i<image1.rows();i++){
                if((int)image1.get(i,j)[2]%2==0){
                    System.out.println();
                    double data0=image1.get(i,j)[0];
                    double data1=image1.get(i,j)[1];
                    int data2=(int)image1.get(i,j)[2];
                    image1.put(i,j,data0,data1,(double)data2+1);
                }
            }
        }
        Log.i(TAG,"Finish Third Process");
        //返回写入成功的图片
        return image1;
    }
    public static Mat Image_Decode(Mat image){
        int high=image.rows();
        int width=image.cols();
        Log.i(TAG,"Start Decoding!");
        //创建要解密出的灰度图
        Mat result=new Mat(high,width, CvType.CV_8UC1);
        Log.i(TAG,"Finish Generating double_value Image!");
        for(int h=0;h<high;h++){
            for(int w=0;w<width;w++){
                if(image.get(h,w)[2]%2==0){//若是偶数，表明已经隐藏了信息
                    result.put(h,w,0,0,0);//将结果图像中的值改变
                }else {
                    result.put(h,w,255,255,255);//将结果图像中的值改变
                }
            }
        }
        Log.i(TAG,"Finish Decoding!");
        return result;//返回解密出的结果
    }

}
