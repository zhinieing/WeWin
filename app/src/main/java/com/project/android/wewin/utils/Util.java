package com.project.android.wewin.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by pengming on 18/12/2017.
 */

public class Util {

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static void loadCircleImage(Uri imageUri, ImageView imageView){
        if (imageUri == null) {
            imageUri = Uri.parse("");
        }

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_person_img)
                .error(R.drawable.ic_person_img);

        Glide.with(MyApplication.getContext())
                .load(imageUri)
                .apply(requestOptions)
                .apply(RequestOptions.bitmapTransform(new CropCircleTransformation()))
                .into(imageView);
    }

    // todo don't use MyApplication.getContext()

    public static void loadNormalImage(Uri imageUri, ImageView imageView){
        if (imageUri == null) {
            imageUri = Uri.parse("");
        }

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_login_icon)
                .error(R.drawable.ic_login_icon);

        Glide.with(MyApplication.getContext())
                .load(imageUri)
                .apply(requestOptions)
                .into(imageView);
    }




    public static String getPath(Context context, Uri uri) {
        String data = null;

        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public static String ShowLongFileSzie(Long length) {
        if (length >= 1048576) {
            return (length / 1048576) + "MB";
        } else if (length >= 1024) {
            return (length / 1024) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }


    public static String fileType(String end) {
        if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("jpeg")
                || end.equals("gif") || end.equals("png") || end.equals("bmp")) {
            return "image";
        } else if (end.equals("mp4") || end.equals("avi") || end.equals("mkv")
                || end.equals("flv") || end.equals("rmvb")) {
            return "video";
        } else if (end.equals("mp3") || end.equals("wav") || end.equals("aac")) {
            return "music";
        } else if (end.equals("pdf")) {
            return "pdf";
        } else if (end.equals("doc") || end.equals("docx")) {
            return "doc";
        } else if (end.equals("ppt") || end.equals("pptx")) {
            return "ppt";
        } else if (end.equals("xls") || end.equals("xlsx")) {
            return "xls";
        }
        return "unknown";
    }


    public static int fileIcon(String kind) {
        switch (kind) {
            case "music": return R.drawable.ic_music;
            case "pdf": return R.drawable.ic_pdf;
            case "doc": return R.drawable.ic_word;
            case "ppt": return R.drawable.ic_ppt;
            case "xls": return R.drawable.ic_excel;

            default:
                return R.drawable.ic_file_default;
        }
    }



    public static String queryImageThumbnailByPath(Context context, String path) {
        Uri uri = Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { Images.Media._ID };
        String selection = Images.Media.DATA + " = ? ";
        String[] selectionArgs = new String[] { path };

        Cursor cursor = query(context, uri, projection, selection,
                selectionArgs);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(Images.Media._ID));
        }
        cursor.close();
        if (id == -1) {
            return null;
        }

        uri = Images.Thumbnails.EXTERNAL_CONTENT_URI;
        projection = new String[] { Images.Thumbnails.DATA };
        selection = Images.Thumbnails.IMAGE_ID + " = ? ";
        selectionArgs = new String[] { String.valueOf(id) };

        cursor = query(context, uri, projection, selection, selectionArgs);
        String thumbnail = null;
        if (cursor.moveToFirst()) {
            int idxData = cursor.getColumnIndex(Images.Thumbnails.DATA);
            thumbnail = cursor.getString(idxData);
        }
        cursor.close();
        return thumbnail;
    }

    public static String queryVideoThumbnailByPath(Context context, String path) {
        Uri uri = Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { Video.Media._ID };
        String selection = Video.Media.DATA + " = ? ";
        String[] selectionArgs = new String[] { path };

        Cursor cursor = query(context, uri, projection, selection,
                selectionArgs);
        int mediaId = -1;
        if (cursor.moveToFirst()) {
            int idxId = cursor.getColumnIndex(Video.Media._ID);
            mediaId = cursor.getInt(idxId);
        }
        cursor.close();
        if (mediaId == -1) {
            return null;
        }

        uri = Video.Thumbnails.EXTERNAL_CONTENT_URI;
        projection = new String[] { Video.Thumbnails.DATA };
        selection = Video.Thumbnails.VIDEO_ID + " =  ? ";
        selectionArgs = new String[] { String.valueOf(mediaId) };

        cursor = query(context, uri, projection, selection, selectionArgs);
        String thumbnail = null;
        if (cursor.moveToFirst()) {
            int idxData = cursor.getColumnIndex(Video.Thumbnails.DATA);
            thumbnail = cursor.getString(idxData);
        }
        cursor.close();
        return thumbnail;
    }

    private static Cursor query(Context context, Uri uri, String[] projection,
                                String selection, String[] selectionArgs) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, projection, selection, selectionArgs,
                null);
        return cursor;
    }




    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getResponse(String fullUrl){
        try {
            HttpClient client = getNewHttpClient();
            HttpGet getMethod = new HttpGet(fullUrl);
            HttpResponse response = client.execute(getMethod);
            int res = response.getStatusLine().getStatusCode();
            if (res == 200) {
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                for (String s = reader.readLine(); s != null; s = reader
                        .readLine()) {
                    builder.append(s);
                }
                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * GET请求方式访问api接口
     *
     * @param urlString
     * @param params
     * @return
     */
    public static String getRequest(String urlString, Map<String, String> params) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(urlString);
            if (null != params) {
                urlBuilder.append("?");
                Iterator<Entry<String, String>> iterator = params.entrySet()
                        .iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> param = iterator.next();
                    urlBuilder
                            .append(URLEncoder.encode(param.getKey(), "UTF-8"))
                            .append('=')
                            .append(URLEncoder.encode(param.getValue(), "UTF-8"));
                    if (iterator.hasNext()) {
                        urlBuilder.append('&');
                    }
                }
            }
            getResponse(urlBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * POST请求方式访问api
     *
     * @param urlString
     * @param params
     * @return
     */
    public static String postRequest(String urlString,List<BasicNameValuePair> params) {
        try {
            HttpClient client = getNewHttpClient();
            HttpPost postMethod = new HttpPost(urlString);
            postMethod.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = client.execute(postMethod);
            int statueCode = response.getStatusLine().getStatusCode();
            if (statueCode == 200) {
                System.out.println(statueCode);
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static long expires(String second) {
        Long l = Long.valueOf(second);
        return l * 1000L + System.currentTimeMillis();
    }

    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

}
