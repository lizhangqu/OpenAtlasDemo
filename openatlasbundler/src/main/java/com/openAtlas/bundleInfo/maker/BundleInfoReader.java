/*
 * Copyright 2008 Android4ME
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.openAtlas.bundleInfo.maker;

import org.json.JSONException;

/**
 * This is example usage of AXMLParser class.
 * 
 * Prints xml document from Android's binary xml file.
 */
@Deprecated
public class BundleInfoReader {
	private static final String DEFAULT_XML = "AndroidManifest.xml";
	private static final String ACTIVITY="activity";
	private static final String SERVICE="service";//receiver//Provider
	private static final String RECEIVER="receiver";
	private static final String PROVIDER="provider";
	public static void main(String[] arguments) {
//		if (arguments.length<1) {
//			System.out.println("Usage: BundleInfoReader <APK FILE PATH>");
//			return;
//		}
//		String apkPath = "E:/apk_file/201008300227127991.apk";
		String apkPath = "/Users/BunnyBlue/Documents/openSource/AtlasForAndroid/sample/AtlasLauncher/libs/armeabi/libcom_openatlas_qrcode.so";
		System.out.println(getManifestXMLFromAPK(apkPath));
	}
	
	public static String getManifestXMLFromAPK(String apkPath) {
//		libcom_openatlas_homelauncher.so
//		libcom_openatlas_qrcode.so
//		libcom_taobao_android_game20x7a.so
//		libcom_taobao_android_gamecenter.so
//		libcom_taobao_universalimageloader_sample0x6a.so
		
		//PackageLite packageLite=new PackageLite();
		PackageLite packageLit=PackageLite.parse(apkPath);
		try {
			return packageLit.getBundleInfo().toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		ZipFile file = null;
//		StringBuilder xmlSb = new StringBuilder(100);
//		try {
//			File apkFile = new File(apkPath);
//			file = new ZipFile(apkFile, ZipFile.OPEN_READ);
//			ZipEntry entry = file.getEntry(DEFAULT_XML);
//			
//			AXmlResourceParser parser=new AXmlResourceParser();
//			parser.open(file.getInputStream(entry));
//			PackageLite packageLite=PackageLite.parse(parser);
//			System.err.println(packageLite.getBundleInfo().toString());
//			file.close();
//			//parser.close();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		return xmlSb.toString();
		return apkPath;
	}
	

	
	private static String getPackage(int id) {
		if (id>>>24==1) {
			return "android:";
		}
		return "";
	}


	
	
	
	/////////////////////////////////// ILLEGAL STUFF, DONT LOOK :)
	
	public static float complexToFloat(int complex) {
		return (complex & 0xFFFFFF00)*RADIX_MULTS[(complex>>4) & 3];
	}
	
	private static final float RADIX_MULTS[]={
		0.00390625F,3.051758E-005F,1.192093E-007F,4.656613E-010F
	};
	private static final String DIMENSION_UNITS[]={
		"px","dip","sp","pt","in","mm","",""
	};
	private static final String FRACTION_UNITS[]={
		"%","%p","","","","","",""
	};
}