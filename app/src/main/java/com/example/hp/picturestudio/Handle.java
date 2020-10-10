package com.example.hp.picturestudio;

import org.opencv.core.Mat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Handle {
    public  static class Ima_str{
        public String message;
        public Mat Image;
        public Ima_str(){
        }
        Ima_str(Mat image,String mes){
            message=mes;
            Image=image;
        }
        public Mat Get_image(){
            return Image;
        }
        public String Get_String(){
            return message;
        }
    }
    //遍历数组获取最大值


    public  static int Get_Max(int[] L){
        int res=0;
        for(int i=0;i<L.length;i++){
            if(L[i]>L[res]){
                res=i;
            }
        }
        return res;
    }
    public  static int[] Init(int[] L){
        for(int i=0;i<L.length;i++){
            L[i]=0;
        }
        return L;
    }
    public  static int[] Get_Count(int[] list,Mat image){
        for(int i=0;i<image.rows();i++){
            for (int j=0;j<image.cols();j++){
                //此处仅获取第一个通道的像素值进行统计
                list[(int)image.get(i,j)[0]]+=1;
            }
        }
        return list;
    }
    public  static int Get_image_capacity(Mat image){
        int[] list=new int[256];
        list=Get_Count(list,image);
        return list[Get_Max(list)];
    }

    //String转换为二进制字符串
    public static String toBinary(String str){
        //把字符串转成字符数组
        char[] strChar=str.toCharArray();
        String result="";
        for(int i=0;i<strChar.length;i++){
            String answer=Integer.toBinaryString(strChar[i]);
            while (answer.length()<8)
                answer='0'+answer;
            result +=answer;
        }
        return result;
    }
    //二进制字符串转化为String
    public static String toString(String binary) {
        char[] temp=binary.toCharArray();
        int count=0;
        int k=0;
        String res="";
        while (k<temp.length){
            res=res+temp[k];
            count++;
            if(count==8){
                res=res+" ";
                count=0;
            }
            k+=1;
        }
        String[] tempStr=res.split(" ");
        char[] tempChar=new char[tempStr.length];
        for(int i=0;i<tempStr.length;i++) {
            tempChar[i]=BinstrToChar(tempStr[i]);
        }
        return String.valueOf(tempChar);
    }
    //将二进制字符串转换成int数组
    public static int[] BinstrToIntArray(String binStr) {
        char[] temp=binStr.toCharArray();
        int[] result=new int[temp.length];
        for(int i=0;i<temp.length;i++) {
            result[i]=temp[i]-48;
        }
        return result;
    }
    //将二进制转换成字符
    public static char BinstrToChar(String binStr){
        int[] temp=BinstrToIntArray(binStr);
        int sum=0;
        for(int i=0; i<temp.length;i++){
            sum +=temp[temp.length-1-i]<<i;
        }
        return (char)sum;
    }

    //向图片的第一个通道位置写东西
    public  static Mat cut_pixel_in_first_channel(Mat image,int i,int j){
        double data0=image.get(i,j)[0];
        double data1=image.get(i,j)[1];
        double data2=image.get(i,j)[2];
        data0=data0-1;
        image.put(i,j,data0,data1,data2);
        return image;
    }

    public  static Mat add_pixel_in_first_channel(Mat image,int i,int j){
        double data0=image.get(i,j)[0];
        double data1=image.get(i,j)[1];
        double data2=image.get(i,j)[2];
        data0=data0+1;
        image.put(i,j,data0,data1,data2);
        return image;
    }

    public  static Mat Move_pixel(Mat image,int Max){
        if(0<Max){
            for(int i=0;i<image.rows();i++){
                for (int j=0;j<image.cols();j++){
                    //任何在零点和最大值点之间的像素的值都减一
                    if((int)image.get(i,j)[0]>0&&(int)image.get(i,j)[0]<Max){
                        image=cut_pixel_in_first_channel(image,i,j);
                    }
                }
            }
            return image;
        }else{
            return null;
        }
    }
    public  static Mat unMove_pixel(Mat image,int Max){
        if(0<Max){
            for(int i=0;i<image.rows();i++){
                for (int j=0;j<image.cols();j++){
                    //任何在零点和最大值点之间的像素的值都加一
                    if((int)image.get(i,j)[0]>0&&(int)image.get(i,j)[0]<Max){
                        image=add_pixel_in_first_channel(image,i,j);
                    }
                }
            }
            return image;
        }else{
            return null;
        }
    }
    public  static Mat Move_Pixel_right(Mat image,int mid){
        if(mid<255){
            for(int i=0;i<image.rows();i++){
                for (int j=0;j<image.cols();j++){
                    //任何在中间值和最大值点之间的像素的值都加一，整体向右移动
                    if((int)image.get(i,j)[0]>mid&&(int)image.get(i,j)[0]<255){
                        image=add_pixel_in_first_channel(image,i,j);
                    }
                }
            }
            return image;
        }else {
            return null;
        }
    }
    public  static Mat unMove_Pixel_right(Mat image,int mid){
        if(mid<255){
            for(int i=0;i<image.rows();i++){
                for (int j=0;j<image.cols();j++){
                    //任何在中间值和最大值点之间的像素的值都加一，整体向右移动
                    if((int)image.get(i,j)[0]>mid&&(int)image.get(i,j)[0]<255){
                        image=cut_pixel_in_first_channel(image,i,j);
                    }
                }
            }
            return image;
        }else {
            return null;
        }
    }

    public  static Mat Hs_Hide(Mat image,String bin_str,int Max){
        char[] binChar=bin_str.toCharArray();
        int count=0;
        for(int i=0;i<image.rows();i++){
            for (int j=0;j<image.cols();j++){
                if((int)image.get(i,j)[0]==Max) {
                    if (binChar[count] == '1') {
                        cut_pixel_in_first_channel(image,i,j);
                    }else if(binChar[count]=='0'){
                        add_pixel_in_first_channel(image,i,j);
                    }
                    if(count<binChar.length-1) {
                        count += 1;
                    }else {
                        return image;
                    }
                }
            }
        }
        return image;
    }


    //--------------------------------------------------------------------------------------------------
    public static Ima_str HS_Hide_Message(String mes,Mat image){
        int[] list=new int[256];
        //初始化列表
        list=Init(list);
        //获取图片对应列表
        list=Get_Count(list,image);
        //获得图片最大值，和零值
        int Max=Get_Max(list);
        //将零值和最大值之间的像素进行移动
        image=Move_pixel(image,Max);
        image=Move_Pixel_right(image,Max);
        //获得信息的二进制字符串
        String bin_str=toBinary(mes);
        //将二进制信息写入像素中
        if(bin_str.length()<=list[Max]){
            image=Hs_Hide(image,bin_str,Max);
        }else{
            return null;
        }
        Ima_str res=new Ima_str(image,Integer.toString(Max));
        return res;
    }


    //--------------------------------------------------------------------------------------------------
    public  static Ima_str Generate_mes(Mat image,int Max){
        String mes="";
        StringBuilder mesBuilder = new StringBuilder(mes);
        for(int i = 0; i<image.rows(); i++){
            for (int j=0;j<image.cols();j++){
                //任何在零点和最大值点之间的像素的值都减一
                if((int)image.get(i,j)[0]==Max+1){
                    image=cut_pixel_in_first_channel(image,i,j);
                    mesBuilder.append('0');
                }else if((int)image.get(i,j)[0]==Max-1){
                    image=add_pixel_in_first_channel(image,i,j);
                    mesBuilder.append('1');
                }
            }
        }
        image=unMove_pixel(image,Max);
        image=unMove_Pixel_right(image,Max);
        mes = mesBuilder.toString();
        mes=toString(mes);
        Ima_str result=new Ima_str(image,mes);
        return result;
    }


    public  static Ima_str HS_Generate_Message(Mat image,int Max){
        //创建结果对象
        Ima_str result=new Ima_str();
        //进行解密获得result，并且还原图像
        result=Generate_mes(image,Max);
        //遍历图片的
        return result;
    }

}
