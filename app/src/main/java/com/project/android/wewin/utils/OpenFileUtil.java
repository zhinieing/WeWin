package com.project.android.wewin.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.project.android.wewin.BuildConfig;

import java.io.File;
import java.util.Locale;

public class OpenFileUtil {

    public static Intent openFile(String filePath, Context context) {
        File file = new File(filePath);
        if (!file.exists())
            return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            return getAudioFileIntent(filePath, context);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(filePath, context);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(filePath, context);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath, context);
        } else if (end.equals("ppt")) {
            return getPptFileIntent(filePath, context);
        } else if (end.equals("xls")) {
            return getExcelFileIntent(filePath, context);
        } else if (end.equals("doc")) {
            return getWordFileIntent(filePath, context);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath, context);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath, context);
        } else if (end.equals("txt")) {
            return getTextFileIntent(filePath, context, false);
        } else {
            return getAllIntent(filePath, context);
        }
    }


    // Android获取一个用于打开APK文件的intent

    public static Intent getAllIntent(String param, Context context) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "*/*");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "*/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开APK文件的intent

    public static Intent getApkFileIntent(String param, Context context) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent

    public static Intent getVideoFileIntent(String param, Context context) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "video/*");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "video/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent

    public static Intent getAudioFileIntent(String param, Context context) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "audio/*");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "audio/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开Html文件的intent

    public static Intent getHtmlFileIntent(String param, Context context) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "text/html");
        } else {
            intent.setDataAndType(uri, "text/html");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        return intent;
    }

    // Android获取一个用于打开图片文件的intent

    public static Intent getImageFileIntent(String param, Context context) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent

    public static Intent getPptFileIntent(String param, Context context) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "application/vnd.ms-powerpoint");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "application/vnd.ms-powerpoint");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param, Context context) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "application/vnd.ms-excel");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "application/vnd.ms-excel");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param, Context context) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "application/msword");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "application/msword");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param, Context context) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "application/x-chm");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "application/x-chm");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开文本文件的intent

    public static Intent getTextFileIntent(String param, Context context, boolean paramBoolean) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "text/plain");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent

    public static Intent getPdfFileIntent(String param, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(param));
            intent.setDataAndType(contentUri, "application/pdf");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(param)), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

}

