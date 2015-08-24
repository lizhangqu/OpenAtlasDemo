/**
 * OpenAtlasForAndroid Project
 * The MIT License (MIT) Copyright (OpenAtlasForAndroid) 2015 Bunny Blue,achellies
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author BunnyBlue
 **/
package com.openatlas.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.openatlas.hack.AssertionArrayException;
import com.openatlas.hack.OpenAtlasHacks;
import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;
import com.openatlas.runtime.PackageLite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ApkUtils {
    static final int SYSTEM_ROOT_STATE_DISABLE = 0;
    static final int SYSTEM_ROOT_STATE_ENABLE = 1;
    static final int SYSTEM_ROOT_STATE_UNKNOW = -1;
    static final Logger log;
    private static int systemRootState;

    static {
        log = LoggerFactory.getInstance("ApkUtils");
        systemRootState = SYSTEM_ROOT_STATE_UNKNOW;
    }

    private static boolean assertAtlasHacks() {
        try {
            return OpenAtlasHacks.defineAndVerify();
        } catch (AssertionArrayException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final PackageInfo parsePackageInfo(Context context, String str) {
        if (!assertAtlasHacks()) {
            return null;
        }
        try {
            PackageLite parse = PackageLite.parse(new File(str));
            PackageInfo packageInfo = new PackageInfo();
            packageInfo.packageName = parse.packageName;
            packageInfo.versionCode = parse.versionCode;
            packageInfo.versionName = parse.versionName;
            packageInfo.applicationInfo = new ApplicationInfo();
            packageInfo.applicationInfo.metaData = parse.metaData;
            return packageInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String[] getApkPublicKey(String apkPath) {
        JarFile jarFile = null;

        try {
            jarFile = new JarFile(apkPath);
            JarEntry jarEntry = jarFile.getJarEntry("classes.dex");
            if (jarEntry != null) {
                Certificate[] loadCertificates = PackageValidate.loadCertificates(jarFile, jarEntry, new byte[4096]);
                if (loadCertificates != null) {
                    String[] strArr = new String[loadCertificates.length];
                    for (int i = SYSTEM_ROOT_STATE_DISABLE; i < loadCertificates.length; i += SYSTEM_ROOT_STATE_ENABLE) {
                        strArr[i] = bytesToHexString(loadCertificates[i].getPublicKey().getEncoded());
                    }

                }
            }

        } catch (IOException e) {

            jarFile = null;
            log.warn("Exception reading public key from apk file " + apkPath, e);

            return null;
        } catch (Throwable e) {
            e.printStackTrace();
            jarFile = null;

        } finally {

            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    private static final String bytesToHexString(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (int i = SYSTEM_ROOT_STATE_DISABLE; i < bArr.length; i += SYSTEM_ROOT_STATE_ENABLE) {
            String toHexString = Integer.toHexString(bArr[i] & 255);// TODO 255
            if (toHexString.length() < 2) {
                stringBuilder.append(SYSTEM_ROOT_STATE_DISABLE);
            }
            stringBuilder.append(toHexString);
        }
        return stringBuilder.toString();
    }

    public static final void chmod(File file) {
        if (file != null && !file.exists()) {
            file.mkdirs();
            try {
                Runtime.getRuntime().exec("chmod 555 " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isRootSystem() {
        if (systemRootState == SYSTEM_ROOT_STATE_UNKNOW) {
            String[] strArr = new String[]{"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
            int length = strArr.length;
            for (int i = SYSTEM_ROOT_STATE_DISABLE; i < length; i += SYSTEM_ROOT_STATE_ENABLE) {
                if (new File(strArr[i], "su").exists()) {
                    systemRootState = SYSTEM_ROOT_STATE_ENABLE;
                    return true;
                }
            }
            return false;
        } else return systemRootState == SYSTEM_ROOT_STATE_ENABLE;
    }

    public static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannel = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileChannel = fileOutputStream.getChannel();
            byte[] bArr = new byte[4096];
            while (true) {
                int read = inputStream.read(bArr);
                if (read <= 0) {
                    break;
                }
                fileChannel.write(ByteBuffer.wrap(bArr, SYSTEM_ROOT_STATE_DISABLE, read));
            }


        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e22) {
                    e22.printStackTrace();
                }
            }
        }
    }

    /**
     *Valid plugin  md5
     * ***/
    public static boolean validFileMD5(String path, String md5Sum) {


        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            File mFile = new File(path);
            if (mFile == null || !mFile.exists() || !mFile.isFile()) {
                return false;
            }
            FileInputStream in = new FileInputStream(mFile);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, mFile.length());
            messageDigest.update(byteBuffer);
            byte[] bytes = messageDigest.digest();
            final String HEX = "0123456789abcdef";
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(HEX.charAt((b >> 4) & 0x0f));
                sb.append(HEX.charAt(b & 0x0f));
            }
            return md5Sum.equals(sb.toString());
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {


        }
        return false;
    }
}
