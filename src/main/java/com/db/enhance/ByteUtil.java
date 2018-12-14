package com.db.enhance;

public class ByteUtil {
    public static String byte2String(byte[] bs,int si,int length){
        return null;
    }

    public static byte[] byte2byte(byte[] bs,int si,int length){
        byte[] rb=new byte[length];
        for(int i=0;i<length;i++){
            rb[i]=bs[si+i];
        }
        return rb;
    }
}
