package com.meson.imageplayer;

import android.graphics.Bitmap;

public class MyBitmapFactory {
    /*
     * 获取位图的RGB数据
     */
    public static byte[]  getRGBByBitmap(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return null;
        }
        
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        int size = width * height;
        
        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        
        byte[]data = convertColorToByte(pixels);
        
        
        return data;
    }
    
    
    /*
     * 像素数组转化为RGB数组
     */
    public static byte[] convertColorToByte(int color[])
    {
        if (color == null)
        {
            return null;
        }
        
        byte[] data = new byte[color.length * 3];
        for(int i = 0; i < color.length; i++)
        {
            data[i * 3] = (byte) (color[i] >> 16 & 0xff);
            data[i * 3 + 1] = (byte) (color[i] >> 8 & 0xff);
            data[i * 3 + 2] =  (byte) (color[i] & 0xff);
        }
        
        return data;
        
    }
    
    /*
     * byte[] data保存的是纯RGB的数据，而非完整的图片文件数据
     */
    static public Bitmap createMyBitmap(byte[] data, int width, int height){    
        int []colors = convertByteToColor(data);
        if (colors == null){
            return null;
        }
            
        Bitmap bmp = null;

        try {
            bmp = Bitmap.createBitmap(colors, 0, width, width, height, 
                    Bitmap.Config.ARGB_8888);
        } catch (Exception e) {
            // TODO: handle exception
    
            return null;
        }
                        
        return bmp;
    }

    
    /*
     * 将RGB数组转化为像素数组
     */
    private static int[] convertByteToColor(byte[] data){
        int size = data.length;
        if (size == 0){
            return null;
        }
        
        
        // 理论上data的长度应该是3的倍数，这里做个兼容
        int arg = 0;
        if (size % 3 != 0){
            arg = 1;
        }
        
        int []color = new int[size / 3 + arg];
        int red, green, blue;
        
        
        if (arg == 0){                                  //  正好是3的倍数
            for(int i = 0; i < color.length; ++i){
        
                color[i] = (data[i * 3] << 16 & 0x00FF0000) | 
                           (data[i * 3 + 1] << 8 & 0x0000FF00 ) | 
                           (data[i * 3 + 2] & 0x000000FF ) | 
                            0xFF000000;
            }
        }else{                                      // 不是3的倍数
            for(int i = 0; i < color.length - 1; ++i){
                color[i] = (data[i * 3] << 16 & 0x00FF0000) | 
                   (data[i * 3 + 1] << 8 & 0x0000FF00 ) | 
                   (data[i * 3 + 2] & 0x000000FF ) | 
                    0xFF000000;
            }
            
            color[color.length - 1] = 0xFF000000;                   // 最后一个像素用黑色填充
        }
    
        return color;
    }
}
