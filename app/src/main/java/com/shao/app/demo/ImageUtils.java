package com.shao.app.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageUtils {


    /**
     * 将bitmap图片转换成base64
     *
     * @param bitmap
     * @return String base64字符串
     */
    public static String compressBitmap2Base64(Bitmap bitmap) {
        if (bitmap == null) {
            return "";
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }

    }

    public static Bitmap base64StrToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap MyCompressImage(String srcPath, Context context) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int screenWidthPixels = displayMetrics.widthPixels;
        int screenHeightPixels = displayMetrics.heightPixels;

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);


        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(srcPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int bitmapWidth = exifInterface.getAttributeInt(
                ExifInterface.TAG_IMAGE_WIDTH, 0);
        int bitmapHeight = exifInterface.getAttributeInt(
                ExifInterface.TAG_FOCAL_LENGTH, 0);
        // 为了更精确使用四舍五入算法 获得 宽度和高度各自的比例值
        int widthSimple = (int) (bitmapWidth * 1f / screenWidthPixels + 0.5f);
        int HeightSimple = (int) (bitmapHeight * 1f / screenHeightPixels + 0.5f);
        // 11.获得宽高比例值的其中一个的比例，通用方法是开平方
        int sqrt = (int) Math.sqrt(widthSimple * widthSimple + bitmapHeight
                * bitmapHeight + 0.5f);
        newOpts.inSampleSize = sqrt;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
        newOpts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(srcPath, newOpts);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static Bitmap compressImage(Bitmap org, int kb) {
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        org.compress(CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length / 1024 > kb && quality >= 30) {
            baos.reset();
            quality -= 10;
            org.compress(CompressFormat.JPEG, quality, baos);
        }
        if (!org.isRecycled()) {
            org.recycle();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try {
            baos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(bais);

    }

    // 计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            if (inSampleSize <= 1) {
                inSampleSize = 1;
            }
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        // compressImage(BitmapFactory.decodeFile(filePath, options), 50);
        return BitmapFactory.decodeFile(filePath, options);
//		return compressImage(BitmapFactory.decodeFile(filePath, options), 60);
    }

    public static ByteArrayOutputStream compressImage2Stream(Bitmap org, int kb) {
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        org.compress(CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length / 1024 > kb) {
            baos.reset();
            quality -= 10;
            org.compress(CompressFormat.JPEG, quality, baos);
        }
        if (!org.isRecycled()) {
            org.recycle();
        }
        return baos;
    }

    public static Bitmap compressImage(String filePath, int kb) {
        File temp = new File(filePath);
        if (!temp.exists()) {
            return null;
        }
        return compressImage(BitmapFactory.decodeFile(filePath), kb);
    }

    public static ByteArrayOutputStream compressImage2Stream(String filePath, int kb) {
        File temp = new File(filePath);
        if (!temp.exists()) {
            return null;
        }
        return compressImage2Stream(BitmapFactory.decodeFile(filePath), kb);
    }

    public static Bitmap scaleImage(Bitmap bp, int pW, int pH) {
        Matrix matrix = new Matrix();
        int oW = bp.getWidth();
        int oH = bp.getHeight();
        float scaleSize;
        float multW = pW / (float) oW;
        float multH = pH / (float) oH;
        if (oW > pW || oH > pW) {
            scaleSize = multW < multH ? multW : multH;
        } else if (oW < pW && oH < pW) {
            scaleSize = multW > multH ? multW : multH;
        } else {
            scaleSize = 1;
        }
        matrix.postScale(scaleSize, scaleSize);
        bp = Bitmap.createBitmap(bp, 0, 0, oW, oH, matrix, true);
        // if (!bp.isRecycled()) {
        // bp.recycle();
        // }
        return bp;
    }

    public static Bitmap scaleImage(String path, int pW, int pH) {
        Bitmap temp = BitmapFactory.decodeFile(path);
        return scaleImage(temp, pW, pH);
    }

    public static Bitmap compressImage(int width, int height, String pathTakePhoto) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(pathTakePhoto,
                bmpFactoryOptions);
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
                / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
                / (float) width);
        // 压缩显示
        if (heightRatio > 1 && widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio * 2;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio * 2;
            }
        }
        // 图像真正解码
        bmpFactoryOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathTakePhoto, bmpFactoryOptions);

    }

    /**
     * 图片保存到文件
     *
     * @param bitmap     位图
     * @param targetPath 文件地址
     * @return true保存成功 false保存失败
     */
    public static boolean save2File(Bitmap bitmap, String targetPath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return save2File(baos, targetPath);
    }

    /**
     * 图片保存到文件
     *
     * @param os         字节输出流
     * @param targetPath 文件地址
     * @return true保存成功 false保存失败
     */
    public static boolean save2File(ByteArrayOutputStream os, String targetPath) {
        if (TextUtils.isEmpty(targetPath)) {
            return false;
        }
        File file = new File(targetPath);
        if (file.exists()) {
            file.delete();
        }
        file.getParentFile().mkdirs();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(os.toByteArray());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                os.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return true;
    }
}
