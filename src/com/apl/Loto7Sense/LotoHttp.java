package com.apl.Loto7Sense;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

public class LotoHttp {

    //HTTP通信 → 文字列
    public static String http2str(Context context,
                                  String path) throws Exception {
        byte[] w=http2data(context,path);
        return new String(w);
    }

    //HTTP通信
    public static byte[] http2data(Context context,String path) throws Exception {

        int size;
        byte[] w=new byte[1024];
        HttpURLConnection c=null;
        InputStream in=null;
        ByteArrayOutputStream out=null;

        try {
            //HTTP接続のオープン
            URL url=new URL(path);
            c=(HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            in=c.getInputStream();

            //バイト配列の読み込み
            out=new ByteArrayOutputStream();
            while (true) {
                size=in.read(w);
                if (size<=0) break;
                out.write(w,0,size);
            }
            out.close();

            //HTTP接続のクローズ
            in.close();
            c.disconnect();

            return out.toByteArray();

        } catch (Exception e) {
            try {
                if (c!=null){
                    c.disconnect();
                }
                if (in!=null){
                    in.close();
                }
                if (out!=null){
                    out.close();
                }
            } catch (Exception e2) {
            }
            throw e;
        }
    }
}
