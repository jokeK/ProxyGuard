package com.mik.proxy_guard_tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2018/1/22 0022.
 */

public class Main {

    public static void main(String[] args) throws Exception {
        /**
         * 1、制作只包含解密代码的dex 文件
         */
        //1.1 解压aar 获得classes.jar
        File aarFile = new File("proxy-guard-core/build/outputs/aar/proxy-guard-core-debug.aar");
        File aarTemp = new File("proxy-guard-tools/temp");
        Zip.unZip(aarFile, aarTemp);
        File classesJar = new File(aarTemp, "classes.jar");
        //1.2 执行dx命令 将jar变成dex文件
        File classesDex = new File(aarTemp, "classes.dex");
        //执行命令  windows:cmd /c  linux/mac不需要（cmd /c）
        Process process = Runtime.getRuntime().exec("cmd /c dx --dex --output " + classesDex
                .getAbsolutePath() + " " +
                classesJar.getAbsolutePath());
        process.waitFor();
        //失败
        if (process.exitValue() != 0) {
            throw new RuntimeException("dex error");
        }

        /**
         * 2、加密apk中所有dex文件
         */
        //2.1 解压apk 获得所有的dex文件
        File apkFile = new File("app/build/outputs/apk/debug/app-debug.apk");
        File apkTemp = new File("app/build/outputs/apk/debug/temp");
        Zip.unZip(apkFile, apkTemp);
        //获得所有的dex
        File[] dexFiles = apkTemp.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".dex");
            }
        });
        //初始化aes
        AES.init(AES.DEFAULT_PWD);
        for (File dex : dexFiles) {
            //读取文件数据
            byte[] bytes = getBytes(dex);
            //加密
            byte[] encrypt = AES.encrypt(bytes);
            //写到指定目录
            FileOutputStream fos = new FileOutputStream(new File(apkTemp, "secret-"
                    + dex.getName()));
            fos.write(encrypt);
            fos.flush();
            fos.close();
            dex.delete();
        }

        /**
         * 3、把classes.dex 放入 apk解压目录 在压缩成apk
         */
        classesDex.renameTo(new File(apkTemp, "classes.dex"));
        File unSignedApk = new File("app/build/outputs/apk/debug/app-unsigned.apk");
        Zip.zip(apkTemp, unSignedApk);

        /**
         * 4、对齐与签名
         */
        //4.1 对齐
//       26.0.2不认识-p参数 zipalign -v -p 4 my-app-unsigned.apk my-app-unsigned-aligned.apk
        File alignedApk = new File("app/build/outputs/apk/debug/app-unsigned-aligned.apk");
        process = Runtime.getRuntime().exec("cmd /c zipalign -f 4 " + unSignedApk
                .getAbsolutePath() + " " +
                alignedApk.getAbsolutePath());
        process.waitFor();
        //失败
        if (process.exitValue() != 0) {
            throw new RuntimeException("zipalign error");
        }

        //4.2 签名
//        apksigner sign  --ks jks文件地址 --ks-key-alias 别名 --ks-pass pass:jsk密码 --key-pass
// pass:别名密码 --out  out.apk in.apk
        //官方文档没有 --ks-key-alias等参数 有点坑爹啊
        File signedApk = new File("app/build/outputs/apk/debug/app-signed-aligned.apk");
        File jks = new File("proxy-guard-tools/proxyDex.jks");
        process = Runtime.getRuntime().exec("cmd /c apksigner sign  --ks " + jks.getAbsolutePath
                () + " --ks-key-alias hz --ks-pass pass:123456 --key-pass  pass:123456 --out" +
                " " + signedApk.getAbsolutePath() + " " + alignedApk.getAbsolutePath());
        process.waitFor();
        //失败
        if (process.exitValue() != 0) {
            throw new RuntimeException("apksigner error");
        }

    }

    public static byte[] getBytes(File file) throws Exception {
        RandomAccessFile r = new RandomAccessFile(file, "r");
        byte[] buffer = new byte[(int) r.length()];
        r.readFully(buffer);
        r.close();
        return buffer;
    }
}
