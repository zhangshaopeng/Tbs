package com.open.file;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * p>@Describe:文件预览页面 调用此方法{@link #show(Context, String)} {上下文，文件地址}
 * p>@Author:zhangshaop
 * p>@Data:2021/1/19.
 */
public class FileDisplayActivity extends AppCompatActivity {
    private String TAG = FileDisplayActivity.class.getName();
    SuperFileView mSuperFileView;
    String filePath;


    /**
     * @param context
     * @param url     文件地址  本地或者网络地址
     */
    public static void show(Context context, String url) {
        Intent intent = new Intent(context, FileDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", url);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);
        StatusBarUtil.setStatusBar(this);
        init();
    }


    public void init() {
        TextView contentTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout layout = findViewById(R.id.appBarLayout);
        mSuperFileView = (SuperFileView) findViewById(R.id.mSuperFileView);
        mSuperFileView.setOnGetFilePathListener(mSuperFileView -> {
            getFilePathAndShowFile(mSuperFileView);
        });
        contentTitle.setText("附件预览");
        toolbar.setTitle("");
        if (OpenFileManager.backgroundColor != -1 && OpenFileManager.backgroundColor != R.color.wihte) {
            toolbar.setBackgroundColor(getResources().getColor(OpenFileManager.backgroundColor));
            toolbar.setNavigationIcon(R.drawable.ic_back);
            contentTitle.setTextColor(getResources().getColor(R.color.wihte));
            layout.setBackgroundColor(getResources().getColor(OpenFileManager.backgroundColor));
            StatusBarUtil.setStatusBarLight(this);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_back_black);
            StatusBarUtil.setStatusBarDark(this);
        }
        toolbar.getNavigationIcon();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = this.getIntent();
        String path = (String) intent.getSerializableExtra("path");
        if (!TextUtils.isEmpty(path)) {
            LogUtils.d(TAG, "文件path:" + path);
            setFilePath(path);
        }
        mSuperFileView.show();
    }

    /**
     * 地址判断
     *
     * @param mSuperFileView2
     */
    private void getFilePathAndShowFile(SuperFileView mSuperFileView2) {
        //网络地址要先下载
        if (getFilePath().contains("http")) {
            downLoadFromNet(getFilePath(), mSuperFileView2);
        } else {
            mSuperFileView2.displayFile(new File(getFilePath()));
        }
    }


    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private String getFilePath() {
        return filePath;
    }

    private void downLoadFromNet(final String url, final SuperFileView mSuperFileView2) {

        //1.网络下载、存储路径、
        File cacheFile = getCacheFile(url);
        if (cacheFile.exists()) {
            if (cacheFile.length() <= 0) {
                LogUtils.d(TAG, "删除空文件！！");
                cacheFile.delete();
                return;
            }
        }


        LoadFileModel.loadPdfFile(url, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                LogUtils.d(TAG, "下载文件-->onResponse");
                boolean flag;
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    ResponseBody responseBody = response.body();
                    is = responseBody.byteStream();
                    long total = responseBody.contentLength();

                    File file1 = getCacheDir(url);
                    if (!file1.exists()) {
                        file1.mkdirs();
                        LogUtils.d(TAG, "创建缓存目录： " + file1.toString());
                    }


                    //fileN : /storage/emulated/0/pdf/kauibao20170821040512.pdf
                    File fileN = getCacheFile(url);//new File(getCacheDir(url), getFileName(url))

                    LogUtils.d(TAG, "创建缓存文件： " + fileN.toString());
                    if (!fileN.exists()) {
                        boolean mkdir = fileN.createNewFile();
                    }
                    fos = new FileOutputStream(fileN);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        LogUtils.d(TAG, "写入缓存文件" + fileN.getName() + "进度: " + progress);
                    }
                    fos.flush();
                    LogUtils.d(TAG, "文件下载成功,准备展示文件。");
                    //2.ACache记录文件的有效期
                    mSuperFileView2.displayFile(fileN);
                } catch (Exception e) {
                    LogUtils.d(TAG, "文件下载异常 = " + e.toString());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.d(TAG, "文件下载失败");
                File file = getCacheFile(url);
                if (!file.exists()) {
                    LogUtils.d(TAG, "删除下载失败文件");
                    file.delete();
                }
            }
        });


    }

    /***
     * 获取缓存目录
     *
     * @param url
     * @return
     */
    private File getCacheDir(String url) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/007/");
    }

    /***
     * 绝对路径获取缓存文件
     *
     * @param url
     * @return
     */
    private File getCacheFile(String url) {
        File cacheFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/007/"
                + getFileName(url));
        LogUtils.d(TAG, "缓存文件 = " + cacheFile.toString());
        return cacheFile;
    }

    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        String fileName = Md5Tool.hashKey(url) + "." + getFileType(url);
        return fileName;
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }
        str = paramString.substring(i + 1);
        LogUtils.d(TAG, "getFileType------>" + str);
        return str;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSuperFileView != null) {
            mSuperFileView.onStopDisplay();
        }
    }
}