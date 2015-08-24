/**
 *  OpenAtlasForAndroid Project
The MIT License (MIT) Copyright (OpenAtlasForAndroid) 2015 Bunny Blue,achellies

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, 
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies 
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
@author BunnyBlue
 * **/
package com.openAtlas.bundleInfo.maker;

import android.content.res.AXmlResourceParser;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;




public class PackageLite {
	private static final String XMLDISABLECOMPONENT_SSO_ALIPAY_AUTHENTICATION_SERVICE = "blue.stack.openAtlas.android.sso.internal.AlipayAuthenticationService";
	private static final String XMLDISABLECOMPONENT_SSO_AUTHENTICATION_SERVICE = "blue.stack.openAtlas.android.sso.internal.AuthenticationService";
	private static final String DEFAULT_XML = "AndroidManifest.xml";
	public String applicationClassName;
	public int applicationDescription;
	public int applicationIcon;
	public int applicationLabel;
	public final Set<String> activitys;
	public final Set<String> providers;
	public final Set<String> receivers;
	public final Set<String> services;
	public final Set<String> dependency;
	//public final Set<String> activitys;
	public final Set<String> disableComponents;

	public String packageName;
	public int versionCode;
	public String versionName;
	public String apkMD5;
	static String tag="PackageLite";
	boolean hasSO=false;
	long size;
	PackageLite() {
		this.activitys = new HashSet<String>();
		this.receivers = new HashSet<String>();
		this.providers = new HashSet<String>();
		this.services = new HashSet<String>();
		this.dependency = new HashSet<String>();
		this.disableComponents = new HashSet<String>();
	}

	//	public static PackageLite parse(File file) {
	//
	//
	//		XmlResourceParser openXmlResourceParser = null;
	//		try {
	//			AssetManager assetManager = AssetManager.class
	//					.newInstance();
	//			Method addAssetPath = AssetManager.class.getMethod("addAssetPath", new Class[]{String.class});
	//			int intValue = (Integer) addAssetPath.invoke(assetManager, file.getAbsolutePath());
	//
	//			if (intValue != 0) {
	//				openXmlResourceParser = assetManager.openXmlResourceParser(
	//						intValue, "AndroidManifest.xml");
	//			} else {
	//				openXmlResourceParser = assetManager.openXmlResourceParser(
	//						intValue, "AndroidManifest.xml");
	//			}
	//			if (openXmlResourceParser != null) {
	//				try {
	//					PackageLite parse = parse(openXmlResourceParser);
	//					if (parse == null) {
	//						parse = new PackageLite();
	//					}
	//
	//					openXmlResourceParser.close();
	//					return parse;
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//
	//					try {
	//
	//						if (openXmlResourceParser != null) {
	//							openXmlResourceParser.close();
	//						}
	//						return null;
	//					} catch (Throwable th) {
	//
	//						if (openXmlResourceParser != null) {
	//							openXmlResourceParser.close();
	//						}
	//						throw th;
	//					}
	//				}
	//			}
	//			return null;
	//		} catch (Exception e) {
	//
	//			openXmlResourceParser = null;
	//
	//			return null;
	//		} catch (Throwable eThrowable) {
	//
	//			if (openXmlResourceParser != null) {
	//				openXmlResourceParser.close();
	//			}
	//
	//		}
	//		return null;
	//	}
	private List<String> getFileList(ZipFile zipFile, String mPref, String mSuffix) {
		List<String> arrayList = new ArrayList<String>();
		try {
			Enumeration<?> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				String name = ((ZipEntry) entries.nextElement()).getName();
				if (name.startsWith(mPref) && name.endsWith(mSuffix)) {
					arrayList.add(name);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return arrayList;
	}

	private void checkNativeLibs(ZipFile apk) {


		List<String> mFileList = getFileList(apk, "lib/armeabi", ".so");
		if (mFileList != null && mFileList.size() > 0) {
			hasSO=true;
		}
	}
	public static PackageLite parse(String apkPath) {
		ZipFile file = null;

		StringBuilder xmlSb = new StringBuilder(100);
		try {
			File apkFile = new File(apkPath);
			file = new ZipFile(apkFile, ZipFile.OPEN_READ);
			ZipEntry entry = file.getEntry(DEFAULT_XML);

			AXmlResourceParser parser=new AXmlResourceParser();
			parser.open(file.getInputStream(entry));
			PackageLite packageLite=new PackageLite();
			packageLite.apkMD5=	FileUtils.getMD5(apkPath);
			packageLite.size=apkFile.length();
			packageLite.checkNativeLibs(file);
			packageLite.parse(parser);
			//System.err.println(packageLite.getBundleInfo().toString());
			file.close();
			return packageLite;
			//parser.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	protected  PackageLite parse(XmlResourceParser xmlResourceParser)throws Exception {
		int currentTag=xmlResourceParser.next();
		;
		while (currentTag!=XmlPullParser.END_DOCUMENT) {
			switch (currentTag) {
			case XmlPullParser.START_DOCUMENT:	

				break;
			case XmlPullParser.START_TAG:

				if (xmlResourceParser.getName().equals("manifest")) {
					parserManifestAttribute(xmlResourceParser, this);
				}

				if (xmlResourceParser.getName().equals("application")) {
					if (!parseApplication(this, (xmlResourceParser),
							(xmlResourceParser))) {
						return null;
					}

					return this;
				}
				break;
			case XmlPullParser.END_DOCUMENT:
				xmlResourceParser.close();
				break;

			case XmlPullParser.END_TAG:

				break;
			default:
				break;
			}

			currentTag=xmlResourceParser.next();
		}
		//TODO  if code is code  delete next version
		//	
		//		do {
		//			index = xmlResourceParser.next();
		//			if (index == startTag) {
		//				break;
		//			}
		//		} while (index != XmlPullParser.END_DOCUMENT);
		//
		//		if (index != startTag) {
		//			//PackageLite.log.error("No start tag found");
		//			mPackageLite=null;
		//		} else if (!xmlResourceParser.getName().equals("manifest")) {
		//			//PackageLite.log.error("No <manifest> tag");
		//			mPackageLite=null;
		//		} else {
		//			mPackageLite.packageName = ((AttributeSet) xmlResourceParser).getAttributeValue(null,
		//					"package");
		//			if (mPackageLite.packageName != null && mPackageLite.packageName.length() != 0) {
		//				index = 0;
		//
		//			} else {
		//				//	PackageLite.log.error("<manifest> does not specify package");
		//				return null;
		//			}
		//
		//			for (int i = 0; i <((AttributeSet) xmlResourceParser).getAttributeCount(); i++) {
		//				String value = ((AttributeSet) xmlResourceParser).getAttributeName(i);
		//				if (value.equals("versionCode")) {
		//					mPackageLite.versionCode = ((AttributeSet) xmlResourceParser)
		//							.getAttributeIntValue(i, 0);
		//
		//				} else if (value.equals("versionName")) {
		//					mPackageLite.versionName = ((AttributeSet) xmlResourceParser)
		//							.getAttributeValue(i);
		//
		//				}
		//
		//			}
		//
		//			index = xmlResourceParser.getDepth() + 1;
		//			while (true) {
		//				int v1 = xmlResourceParser.next();
		//				System.out.println(xmlResourceParser.getName());
		//				if (v1 != XmlPullParser.END_DOCUMENT) {
		//					if (xmlResourceParser.getName().equals("application")) {
		//						if (!PackageLite
		//								.parseApplication(mPackageLite, (xmlResourceParser),
		//										(xmlResourceParser))) {
		//							return null;
		//						}
		//
		//						return mPackageLite;
		//					}
		//
		//					if (v1 == endTag && xmlResourceParser.getDepth() < index) {
		//						break;
		//					}
		//
		//					if (v1 == endTag) {
		//						continue;
		//					}
		//
		//					if (v1 == 4) {
		//						continue;
		//					}
		//
		//					PackageLite.skipCurrentTag((xmlResourceParser));
		//					continue;
		//				}
		//
		//				break;
		//			}
		//
		//
		//		}

		return this;

	}

	/**
	 * parser ManifestAttribute such package name and so on
	 * @param xmlResourceParser
	 * @param mPackageLite
	 */
	private static void parserManifestAttribute(
			XmlResourceParser xmlResourceParser, PackageLite mPackageLite) {

		//			mPackageLite.packageName = ((AttributeSet) xmlResourceParser).getAttributeValue(null,"package");
		//
		//			System.out.println(mPackageLite.packageName+"pkg");
		//		if (TextUtils.isEmpty(mPackageLite.packageName)) {
		//		
		//			mPackageLite.packageName.charAt(2);
		//	}

		//		mPackageLite.versionCode = ((AttributeSet) xmlResourceParser).getAttributeIntValue(null, "versionCode", 0);
		//		mPackageLite.versionName = ((AttributeSet) xmlResourceParser).getAttributeValue(null,"versionName");
		for (int i = 0; i <((AttributeSet) xmlResourceParser).getAttributeCount(); i++) {
			String value = ((AttributeSet) xmlResourceParser).getAttributeName(i);

			if (value.equalsIgnoreCase("package")) {
				mPackageLite.packageName= ((AttributeSet) xmlResourceParser)
						.getAttributeValue(i);

			}
			if (value.equals("versionCode")) {
				mPackageLite.versionCode = ((AttributeSet) xmlResourceParser)
						.getAttributeIntValue(i, 0);

			} else if (value.equals("versionName")) {
				mPackageLite.versionName = ((AttributeSet) xmlResourceParser)
						.getAttributeValue(i);

			}

		}
	}



	private static boolean parseApplication(PackageLite packageLite,
			XmlPullParser xmlPullParser, AttributeSet attributeSet)
					throws Exception {
		int i;
		String str = packageLite.packageName;
		for (i = 0; i < attributeSet.getAttributeCount(); i++) {
			String attributeName = attributeSet.getAttributeName(i);
			if (attributeName.equals("name")) {
				packageLite.applicationClassName = buildClassName(str,
						attributeSet.getAttributeValue(i));
			} else if (attributeName.equals("icon")) {
				packageLite.applicationIcon = attributeSet
						.getAttributeResourceValue(i, 0);
			} else if (attributeName.equals("label")) {
				packageLite.applicationLabel = attributeSet
						.getAttributeResourceValue(i, 0);
			} else if (attributeName.equals("description")) {
				packageLite.applicationDescription = attributeSet
						.getAttributeResourceValue(i, 0);
			}
		}



		final int innerDepth = xmlPullParser.getDepth();

		int type;
		while ((type = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT
				&& (type != XmlPullParser.END_TAG || xmlPullParser.getDepth() > innerDepth)) {
			if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
				continue;
			}

			String tagName = xmlPullParser.getName();
			if (tagName.equals("activity")) {

				parseComponentData(packageLite, xmlPullParser,
						attributeSet, false,Component.ACTIVITY);

			} else if (tagName.equals("receiver")) {

				parseComponentData(packageLite, xmlPullParser,
						attributeSet, true,Component.RECEIVER);

			} else if (tagName.equals("service")) {

				parseComponentData(packageLite, xmlPullParser,
						attributeSet, true,Component.SERVISE);

			} else if (tagName.equals("provider")) {

				parseComponentData(packageLite, xmlPullParser,
						attributeSet, false,Component.PROVIDER);

			} else if (tagName.equals("activity-alias")) {
			} else if (xmlPullParser.getName().equals("meta-data")) {

				parseMetaData(xmlPullParser,
						attributeSet,packageLite);


			} else if (tagName.equals("uses-library")) {
			} else if (tagName.equals("uses-package")) {
			} else {
			}
		}

		return true;
	}

	private static  void parseMetaData(XmlPullParser xmlPullParser,
			AttributeSet attributeSet,PackageLite mPackageLite)
					throws XmlPullParserException, IOException {
		int i = 0;

		String mTagValue = null;
		String mTagName = null;
		int i2 = 0;
		while (i < attributeSet.getAttributeCount()) {
			String attributeName = attributeSet.getAttributeName(i);
			if (attributeName.equals("name")) {
				mTagName = attributeSet.getAttributeValue(i);
				i2++;
			} else if (attributeName.equals("value")) {
				mTagValue = attributeSet.getAttributeValue(i);
				i2++;
			}
			if (i2 >= 2) {
				break;
			}
			i++;
		}
		if (!(mTagName == null || mTagValue == null)) {
			if (mTagName.equals("dependency")) {
				String[] 	dependencys=mTagValue.split(",");
				for (String string : dependencys) {
					mPackageLite.dependency.add(string);
				}
				//	System.out.println("PackageLite.parseMetaData()"+mTagValue);
			}
			//bundle.putString(str2, str);
		}
		//return bundle;
	}

	private static String buildClassName(String str, CharSequence charSequence) {
		if (charSequence == null || charSequence.length() <= 0) {
			System.out.println("Empty class name in package " + str);
			return null;
		}
		String obj = charSequence.toString();
		char charAt = obj.charAt(0);
		if (charAt == '.') {
			return (str + obj).intern();
		}
		if (obj.indexOf(46) < 0) {
			StringBuilder stringBuilder = new StringBuilder(str);
			stringBuilder.append('.');
			stringBuilder.append(obj);
			return stringBuilder.toString().intern();
		} else if (charAt >= 'a' && charAt <= 'z') {
			return obj.intern();
		} else {
			System.out.println("Bad class name " + obj + " in package " + str);
			return null;
		}
	}
	@SuppressWarnings("unused")
	@Deprecated
	private static void skipCurrentTag(XmlPullParser xmlPullParser)
			throws XmlPullParserException, IOException {
		int depth = xmlPullParser.getDepth();
		while (true) {
			int next = xmlPullParser.next();
			if (next == XmlPullParser.END_DOCUMENT) {
				return;
			}
			if (next == XmlPullParser.END_TAG && xmlPullParser.getDepth() <= depth) {
				return;
			}
		}
	}

	private static void parseComponentData(PackageLite packageLite,
			XmlPullParser xmlPullParser, AttributeSet attributeSet, boolean isDisable,Component mComponent)
					throws XmlPullParserException {

		String pkgName = packageLite.packageName;
		for (int index = 0; index <attributeSet.getAttributeCount(); index++) {
			if (attributeSet.getAttributeName(index).equals("name")) {
				String mComponentName = attributeSet.getAttributeValue(index);
				if (mComponentName.startsWith(".")) {
					mComponentName = pkgName.concat(mComponentName);
				}
				switch (mComponent) {
				case PROVIDER:
					packageLite.providers.add(mComponentName);		
					break;

				case ACTIVITY:
					packageLite.activitys.add(mComponentName);		
					break;
				case SERVISE:
					packageLite.services.add(mComponentName);	
					break;
				case RECEIVER:
					packageLite.receivers.add(mComponentName);	
					break;
				default:
					break;
				}

				if (isDisable
						&& !(TextUtils
								.equals(mComponentName,
										XMLDISABLECOMPONENT_SSO_ALIPAY_AUTHENTICATION_SERVICE) && TextUtils
										.equals(mComponentName,
												XMLDISABLECOMPONENT_SSO_AUTHENTICATION_SERVICE))) {
					packageLite.disableComponents.add(mComponentName);
				}

			}
		}

	}
	public JSONObject getBundleInfo() throws JSONException {
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("pkgName", packageName);
		jsonObject.put("version", versionName);
		JSONArray activityArray=new JSONArray();
		for (String name:activitys) {
			activityArray.put(name);
		}
		jsonObject.put("activities", activityArray);
		JSONArray servicesArray=new JSONArray();
		for (String name:services) {
			servicesArray.put(name);
		}
		jsonObject.put("services", servicesArray);
		JSONArray receiversArray=new JSONArray();
		for (String name:receivers) {
			receiversArray.put(name);
		}
		jsonObject.put("receivers", receiversArray);

		JSONArray providersArray=new JSONArray();
		for (String name:providers) {
			providersArray.put(name);
		}
		jsonObject.put("contentProviders", providersArray);
		JSONArray dependencyArray=new JSONArray();
		for (String name:dependency) {
			dependencyArray.put(name);
		}
		jsonObject.put("dependency", dependencyArray);
		jsonObject.put("md5", apkMD5);
		jsonObject.put("size", size);
		jsonObject.put("hasSO", hasSO);

		return jsonObject;

	}
}
