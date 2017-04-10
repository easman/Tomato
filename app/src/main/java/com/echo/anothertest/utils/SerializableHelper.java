package com.echo.anothertest.utils;

import android.util.Base64;

import com.echo.anothertest.bean.Tomato;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Echo on 2017/4/3.
 */

public class SerializableHelper {
    //序列化Tomato的方法
    public static String setTomatoToShare(Tomato tomato) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(tomato);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tomatoStr = new String(Base64.encode(baos.toByteArray(),
                Base64.DEFAULT));
        try {
            baos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tomatoStr;
    }

    //反序列化Tomato的方法
    public static Tomato getTomatoFromShare(String tomatoBase64){
        try {
            // 将base64格式字符串还原成byte数组
            if (tomatoBase64 == null || tomatoBase64.equals("")) { // 不可少，否则在下面会报java.io.StreamCorruptedException
                return null;
            }
            byte[] tomatoBytes = Base64.decode(tomatoBase64.getBytes(),
                    Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(tomatoBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 将byte数组转换成Tomato对象
            Object tomato = ois.readObject();
            bais.close();
            ois.close();
            return (Tomato) tomato;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
