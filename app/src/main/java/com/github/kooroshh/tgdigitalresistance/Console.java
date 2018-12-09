package com.github.kooroshh.tgdigitalresistance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Oplus on 2018/05/03.
 */

public class Console {
    static public String mkCMD(String[] cmd){
        String c = "";
        for(int i = 0;i<cmd.length;i++)
            c+=cmd[i]+" ";
        return c;
    }

    static public void runCommand(String command) {
        tg.digitalresistance.jni.System.exec(command);
    }
    public static void CopyAssets(Context context){
        try {
            String Arch = Build.CPU_ABI;
            File LocalDirectory = context.getFilesDir();
            copyFile(context, Arch + "/daemon", new File(LocalDirectory, "daemon"));
            copyFile(context, Arch + "/tunnel", new File(LocalDirectory, "tunnel"));
            SetExecutable(new File(LocalDirectory, "daemon"));
            SetExecutable(new File(LocalDirectory, "tunnel"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void copyFile(Context context,String AssetFile , File localFile) {
        try {
            InputStream is = context.getAssets().open(AssetFile);
            OutputStream os = new FileOutputStream(localFile);

            byte[] buffer = new byte[1024];
            while (is.read(buffer) > 0) {
                os.write(buffer);
            }

            os.flush();
            os.close();
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void SetExecutable(File F){
        F.setExecutable(true);
    }
    public static void setProxy(Context context){
        String innerUrl = "https://t.me/socks?server=127.0.0.1&port=5080";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(innerUrl));
        context.startActivity(i);
    }
}
