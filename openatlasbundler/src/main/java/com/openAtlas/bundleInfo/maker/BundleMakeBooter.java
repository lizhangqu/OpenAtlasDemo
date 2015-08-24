/**OpenAtlasForAndroid Project

The MIT License (MIT) 
Copyright (c) 2015 Bunny Blue

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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * @author BunnyBlue
 *
 */
public class BundleMakeBooter {
	public static void main(String[] args) throws JSONException, IOException {
		//if(args.length!=2){
		//	throw new  IOException(" args to less , usage plugin_dir out_put_json_path");
		//}

        args=new String[2];
        args[0]="C:\\Users\\kltz\\Desktop\\AtlasDemo\\plugin";
        args[1]="C:\\Users\\kltz\\Desktop\\AtlasDemo\\plugin\\bundle-info.json";

		String path=args[0];
		ApkPreProcess.preProcess(path);
		String targetFile=args[1];
		File dirFile=new File(path);
		JSONArray jsonArray=new JSONArray();
		File[]files=	dirFile.listFiles();
		for (File file : files) {
			if (file.getAbsolutePath().contains("libcom")) {
				PackageLite packageLit=PackageLite.parse(file.getAbsolutePath());
				jsonArray.put(packageLit.getBundleInfo());
//				try {
//					 packageLit.getBundleInfo().toString();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}

		}
		org.apache.commons.io.FileUtils.writeStringToFile(new File(targetFile), jsonArray.toString());
		System.out.println(jsonArray.toString());
	}

}
