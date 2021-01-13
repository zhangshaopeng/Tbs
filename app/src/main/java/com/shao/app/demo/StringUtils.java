package com.shao.app.demo;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


import static android.util.Base64.NO_WRAP;

public class StringUtils {
    /**
     * 返回true如果字符串为null或者长度为0
     *
     * @param str 被检查的字符串
     * @return true 如果str为null或长度为0
     */
    public static boolean isNull(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 返回true如果密码字符串长度大于或等于0
     *
     * @param str 被检查的密码字符串
     * @return true 如果str长度大于等于6
     */
    public static boolean PWLength(String str) {
        return !isNull(str) && str.length() >= 6;
    }

    public static String file2String(String filePath) {
        String str = "";
        File file = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        int len;
        byte[] temp = new byte[1024];
        if (TextUtils.isEmpty(filePath)
                || !(file = new File(filePath)).exists()) {
            return str;
        }
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            while ((len = fis.read(temp)) != -1) {
                baos.write(temp, 0, len);
                baos.flush();
            }
            fis.close();
            baos.close();
            str = Base64.encodeToString(baos.toByteArray(), NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 长度小于3位自动部0 例如5 补完后005 在倒数第2位加. 0.05
     *
     * @param Price - 价格
     * @return - 价格
     */
    public static String formatPrice(int Price) {
        DecimalFormat format = new DecimalFormat("000");
        String PriceStr = format.format(Price);
        int priceLen = PriceStr.length();
        return PriceStr.substring(0, priceLen - 2) + "." + PriceStr.substring(priceLen - 2);

    }

    /**
     * 格式化金额(单位为分)
     *
     * @param money -价格
     * @return 价格.00
     */
    public static String formatMoney(String money) {
        money = money.trim();
        int index = money.indexOf(".");
        if (money.contains(".")) {
            if (index == 0) {
                money = "0" + money;
                index++;
            }
            money = money.substring(0, money.length() > (index + 3) ? (index + 3) : money.length());
            int length = money.length();
            for (int i = length - index - 1; i < 2; i++) {
                money += "0";
            }
        } else {
            if (money.length() != 0) {
                money = money + ".00";
            }
        }
        return money;
    }

    /**
     * 将分转换成元（参数必须是以分为单位）
     *
     * @param fen 金钱，单位‘分’
     * @return 以单位‘元’返回
     */
    public static String fenToYuan(String fen) {
        if ("0".equals(fen)) {
            fen = "0.00";
        } else {
            int lenth = fen.length();
            if (lenth > 2) {
                fen = fen.substring(0, fen.length() - 2) + "." + fen.substring(fen.length() - 2, fen.length());
            } else if (lenth == 2) {
                fen = "0." + fen;
            } else if (lenth == 1) {
                fen = "0.0" + fen;
            }
        }
        return fen;
    }

    /**
     * 将元为单位的转换为分 （乘100）
     *
     * @param amount 单位为元的金额
     * @return 单位为分的金额
     */
    public static String changeY2F(Double amount) {
        int v = (int) (amount * 100);
        return String.valueOf(v);
    }

    /**
     * 补全小数点两位
     *
     * @param number Double类型
     * @return 补全两位小数Str
     */
    public static String formatMoney(double number) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(number);
    }

    /**
     * 计算两个日期之间相差多少天
     *
     * @param smDate
     * @param bDate
     * @return
     */
    public static int daysBetween(Date smDate, Date bDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smDate = sdf.parse(sdf.format(smDate));
            bDate = sdf.parse(sdf.format(bDate));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bDate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 多色彩文字（HTML）
     *
     * @param content 编辑的字符串<br/>
     *                例如： String content = "请输入<font color = '#FE6026'>充值金额</font>：";
     * @return 返回spanned, 直接用TextView.setText()方法进行赋值
     */
    public static Spanned fromHtml(CharSequence content) {
        if (content == null) {
            content = "";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(content.toString(), Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(content.toString());
        }
    }


    /**
     * 判断字符串是否为null或全为空格
     *
     * @param string 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public static boolean isSpace(String string) {
        return (string == null || string.trim().length() == 0);
    }



    /**
     * 公钥加密
     */
    public static String encryptByPublicKey(String data, String key) {
        String encodeStr = null;
        byte[] keyBytes = Base64.decode(key, NO_WRAP);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] mi = cipher.doFinal(data.getBytes());
            encodeStr = Base64.encodeToString(mi, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | NoSuchPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return encodeStr;
    }


    public static Map<String, Object> json2map(String str_json) {
        Map<String, Object> res = new HashMap<>();
        try {
            Gson gson = new Gson();
            res = gson.fromJson(str_json, new TypeToken<Map<String, Object>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 截取字符串
     *
     * @param s         字符串
     * @param subLength 截取的长度
     * @return 设定的截取长度大于该字符串，则返回原始数据
     */
    public static String subString(String s, int subLength) {
        if (s != null) {
            if (s.length() > subLength) {
                return s.substring(0, subLength);
            } else {
                return s;
            }
        }
        return "";
    }

    /**
     * 截取字符串
     *
     * @param s         字符串
     * @param subLength 截取的长度
     * @return 设定的截取长度大于该字符串，则返回原始数据
     */
    public static String subLastString(String s, int subLength) {
        if (s != null) {
            if (s.length() > subLength) {
                return s.substring(s.length() - subLength);
            } else {
                return s;
            }
        }
        return "";
    }

    /**
     * 截取字符串
     *
     * @param list      字符串集合
     * @param subString 分隔符
     * @return 拼接好的字符串
     */
    public static String appendString(List<String> list, String subString) {
        if (list != null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i));
                if (i < list.size() - 1) {
                    builder.append(subString);
                }
            }
            return builder.toString();
        }
        return null;
    }
}
