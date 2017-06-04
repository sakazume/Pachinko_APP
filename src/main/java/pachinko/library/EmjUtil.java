package pachinko.library;


import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class EmjUtil {
    private static final String MAX_PUA = new String(EmjUtil.makeString("EFA3BF", "UTF-8"));
    private static final String MIN_PUA = EmjUtil.makeString("EE8080", "UTF-8");

    /**
     * 16進数文字列で表現した文字列から文字列への変換
     * @param src 16進数文字列で表現した文字列
     * @param encoding エンコーディング
     * @return　変換後の文字列
     */
    public static String makeString(String src, String encoding) {
        if (null == src) {
            throw new RuntimeException();
        }
        int length = src.length();
        if (1 == length % 2) {
            throw new RuntimeException();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < length; i += 2) {
            baos.write(Integer.parseInt(src.substring(i, i + 2), 16));
        }
        try {
            return baos.toString(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }

    /**
     * 絵文字があるインデックスを取得する。
     * @param src 判定する文字列
     * @param index 開始するインデックス
     * @return 発見した位置、無い場合は-1
     */
    public static int indexEmj(String src, int index) {
        if (src.length() <= index) {
            return -1;
        }
        for (int i = index; i < src.length();  i++) {
            String target = src.substring(i, i+1);
            if (
                    (0 <= target.compareTo(MIN_PUA)) &&
                            (0 >= target.compareTo(MAX_PUA)))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * 絵文字を削除する
     * @param src　絵文字があるかもしれない文字列
     * @return 絵文字を削除した文字列
     */
    public static String cutEmj(String src) {
        int index = indexEmj(src, 0);
        if (-1 == index) {
            return src;
        }
        StringBuilder sb = new StringBuilder();
        int oldIndex = 0;
        while (-1 != index) {
            sb.append(src.substring(oldIndex, index));
            oldIndex = index + 1;
            index = indexEmj(src, index + 1);
        }
        sb.append(src.substring(oldIndex, src.length()));
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        byte[][] mojiAry = {
                {},
                {(byte)0x61},
                {(byte)0xEE, (byte)0x80, (byte)0x82},
                {(byte)0x61, (byte)0x62},
                {(byte)0x61, (byte)0xEE, (byte)0x80, (byte)0x82},
                {(byte)0xEE, (byte)0x80, (byte)0x82, (byte)0x61},
                {(byte)0x61, (byte)0xEE, (byte)0x80, (byte)0x82, (byte)0x62},
                {(byte)0xEE, (byte)0x80, (byte)0x82, (byte)0xEE, (byte)0x80, (byte)0x83},
                {(byte)0x61, (byte)0xEE, (byte)0x80, (byte)0x82, (byte)0xEE, (byte)0x80, (byte)0x83},
                {(byte)0xEE, (byte)0x80, (byte)0x82, (byte)0xEE, (byte)0x80, (byte)0x83, (byte)0x61},
                {(byte)0xEE, (byte)0x80, (byte)0x82, (byte)0x61, (byte)0xEE, (byte)0x80, (byte)0x83},
                {(byte)0x61, (byte)0xEE, (byte)0x80, (byte)0x82, (byte)0xEE, (byte)0x80, (byte)0x83, (byte)0x62},
        };

        for (byte[] moji : mojiAry) {
            String str1 = new String(moji, "UTF-8");
            String str2 = EmjUtil.cutEmj(str1);
            System.out.println(str1 + " : " + str2);
        }
    }
}