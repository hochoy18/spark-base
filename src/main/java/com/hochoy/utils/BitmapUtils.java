package com.hochoy.utils;

import org.roaringbitmap.RoaringBitmap;

import java.io.*;

/**
 */
public class BitmapUtils implements Serializable{

    /**
     * serialize  a roaringbitmap to ByteArrary
     *
     * @param mrb
     * @return
     */
    public static byte[] serializeBitMapToByteArray(RoaringBitmap mrb) throws IOException{
        final byte[] array = new byte[mrb.serializedSizeInBytes()];
        try {
            mrb.serialize(new DataOutputStream(new OutputStream() {
                int c = 0;

                @Override
                public void close() {
                    // Do nothing
                }

                @Override
                public void flush() {
                    // Do nothing
                }

                @Override
                public void write(int b) {
                    array[c++] = (byte) b;
                }

                @Override
                public void write(byte[] b) {
                    write(b, 0, b.length);
                }

                @Override
                public void write(byte[] b, int off, int l) {
                    System.arraycopy(b, off, array, c, l);
                    c += l;
                }
            }));
        } catch (Exception e) {
            // should never happen because we read from a byte array
            throw new IOException(e);
        }
        return array;
    }

    /**
     * deserialize  a ByteArrary to a bitmap
     *
     * @param array
     * @return
     */
    public static RoaringBitmap deSerializeByteArrayToBitMap(final byte[] array) throws IOException{
        RoaringBitmap ret = new RoaringBitmap();
        try {
            ret.deserialize(new DataInputStream(new InputStream() {
                int c = 0;

                @Override
                public int read() {
                    return array[c++] & 0xff;
                }

                @Override
                public int read(byte[] b) {
                    return read(b, 0, b.length);
                }

                @Override
                public int read(byte[] b, int off, int l) {
                    System.arraycopy(array, c, b, off, l);
                    c += l;
                    return l;
                }
            }));
        } catch (Exception e) {
            // should never happen because we read from a byte array
            throw new IOException(e);
        }
        return ret;
    }
}
