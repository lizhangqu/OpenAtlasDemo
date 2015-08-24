package com.lizhangqu.zxing.encode;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public class CodeCreator {

	/**
	 * 生成QRCode（二维码）
	 * 
	 * @param url
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap createQRCode(String url,int w,int h) throws WriterException {

		if (url == null || url.equals("")) {
			return null;
		}
		Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(url,
				BarcodeFormat.QR_CODE, w, h, hints);

		int width = matrix.getWidth();
		int height = matrix.getHeight();

		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					//pixels[y * width + x] = 0xff000000;
					pixels[y * width + x] = 0xff2a64ca;
				}

			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

}
