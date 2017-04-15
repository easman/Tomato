package com.echo.anothertest.utils;

import android.util.Base64;

import com.echo.anothertest.bean.Pomodori;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Echo on 2017/4/3.
 */

public class SerializableHelper {

    //序列化Pomodori的方法
    public static String setPomodoriToShare(Pomodori pomodori) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(pomodori);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pomodoriStr = new String(Base64.encode(baos.toByteArray(),
                Base64.DEFAULT));
        try {
            baos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pomodoriStr;
    }

    //反序列化Pomodori的方法
    public static Pomodori getPomodoriFromShare(String pomodoriBase64){
        try {

            // 将base64格式字符串还原成byte数组
            if (pomodoriBase64 == null || pomodoriBase64.equals("")) {                              // 不可少，否则在下面会报java.io.StreamCorruptedException
                return null;
            }
            byte[] pomodoriBytes = Base64.decode(pomodoriBase64.getBytes(),
                    Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(pomodoriBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);

            // 将byte数组转换成Pomodori对象
            Object pomodori = ois.readObject();
            bais.close();
            ois.close();
            return (Pomodori) pomodori;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
