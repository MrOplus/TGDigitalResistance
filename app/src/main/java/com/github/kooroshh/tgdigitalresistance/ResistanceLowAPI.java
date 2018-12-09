package com.github.kooroshh.tgdigitalresistance;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Created by Oplus on 2018/05/03.
 */

public class ResistanceLowAPI {
    private static final String daemon_CONFIG = "{\"server\": \"%s\", \"server_port\": %s, \"local_port\": %s, \"password\": \"%s\", \"method\":\"%s\", \"timeout\": %d}";
    private static final String TAG = "DAEMON-LOG";
    public static void Run(Context context,String server, String password, String encryption, String port, String localPort){
        String dPath = context.getFilesDir() + "/daemon";
        String conf = String.format(Locale.ENGLISH,
                daemon_CONFIG
                ,server, port, localPort,
                password, encryption, 10);
        PrintWriter printWriter = printToFile(new File(context.getFilesDir() + "/daemon.conf"));
        printWriter.println(conf);
        printWriter.close();

        String[] cmd = {
                context.getFilesDir() + "/daemon", "-u",
                "-b", "127.0.0.1",
                "-t", "600", "-c",
                context.getFilesDir() + "/daemon.conf", "-f ",
                context.getFilesDir() + "/daemon.pid"
        };
        Log.d(TAG, Console.mkCMD(cmd));
        Console.runCommand(Console.mkCMD(cmd));
        Log.v(TAG,"[+] ShadowSocks Has been Started.");
    }
    public static void Kill(Context context){
        String[] tasks = {"/daemon"};
        for (String task : tasks) {
            try {
                File f = new File(context.getFilesDir() + task + ".pid");
                BufferedReader br = new BufferedReader(new FileReader(f));
                Integer pid = Integer.valueOf(br.readLine());
                if(pid!=null)
                    android.os.Process.killProcess(pid);
                Console.runCommand("pkill -9 " + task.substring(1));
                f.delete();
            }catch(Exception e){

            }
        }
    }
    private static PrintWriter printToFile(File f) {
        PrintWriter p = null;
        try {
            p = new PrintWriter(f);
            return p;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
