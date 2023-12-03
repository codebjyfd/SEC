package org.example;

import org.apache.kerby.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class DEFLATE {
    /**
     *
     * @param inputByte
     *            待解压缩的字节数组
     * @return 解压缩后的字节数组
     * @throws IOException
     */
    public static byte[] uncompress(byte[] inputByte) throws IOException {
        int len = 0;
        Inflater infl = new Inflater();
        infl.setInput(inputByte);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] outByte = new byte[1024];
        try {
            while (!infl.finished()) {
                // 解压缩并将解压缩后的内容输出到字节输出流bos中
                len = infl.inflate(outByte);
                if (len == 0) {
                    break;
                }
                bos.write(outByte, 0, len);
            }
            infl.end();
        } catch (Exception e) {
            //
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    /**
     * 压缩.
     *
     * @param inputByte
     *            待压缩的字节数组
     * @return 压缩后的数据
     * @throws IOException
     */
    public static byte[] compress(byte[] inputByte) throws IOException {
        int len = 0;
        Deflater defl = new Deflater();
        defl.setInput(inputByte);
        defl.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] outputByte = new byte[1024];
        try {
            while (!defl.finished()) {
                // 压缩并将压缩后的内容输出到字节输出流bos中
                len = defl.deflate(outputByte);
                bos.write(outputByte, 0, len);
            }
            defl.end();
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("D:\\idea_code\\xaa10");
            int len = fis.available();
            byte[] b = new byte[len];
            fis.read(b);
            byte[] bd = compress(b);
            // 为了压缩后的内容能够在网络上传输，一般采用Base64编码
            String encodestr = Base64.encodeBase64String(bd);
            byte[] bi = uncompress(Base64.decodeBase64(encodestr));
            FileOutputStream fos = new FileOutputStream("D:\\idea_code\\xaa101");
            FileOutputStream fos2 = new FileOutputStream("D:\\idea_code\\xaa10.def");
            fos.write(bi);
            fos2.write(bd);
            fos.flush();
            fos2.flush();
            fos2.close();
            fos.close();
            fis.close();
        } catch (Exception e) {
            //
        }
    }
}