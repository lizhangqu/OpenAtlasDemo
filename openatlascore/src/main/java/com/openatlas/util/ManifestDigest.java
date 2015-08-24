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

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

import java.util.Arrays;
import java.util.jar.Attributes;

/**
 * Represents the manifest digest for a package. This is suitable for comparison
 * of two packages to know whether the manifests are identical.
 *
 */
public class ManifestDigest {
    /** The digest of the manifest in our preferred order. */
    private final byte[] mDigest;

    /** Digest field names to look for in preferred order. */
    private static final String[] DIGEST_TYPES = {
            "SHA1-Digest", "SHA-Digest", "MD5-Digest",
    };

    /** What we print out first when toString() is called. */
    private static final String TO_STRING_PREFIX = "ManifestDigest {mDigest=";

    ManifestDigest(byte[] digest) {
        mDigest = digest;
    }


    @TargetApi(Build.VERSION_CODES.FROYO)
    static ManifestDigest fromAttributes(Attributes attributes) {
        if (attributes == null) {
            return null;
        }

        String encodedDigest = null;

        for (int i = 0; i < DIGEST_TYPES.length; i++) {
            final String value = attributes.getValue(DIGEST_TYPES[i]);
            if (value != null) {
                encodedDigest = value;
                break;
            }
        }

        if (encodedDigest == null) {
            return null;
        }

        final byte[] digest = Base64.decode(encodedDigest, Base64.DEFAULT);
        return new ManifestDigest(digest);
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ManifestDigest)) {
            return false;
        }

        final ManifestDigest other = (ManifestDigest) o;

        return this == other || Arrays.equals(mDigest, other.mDigest);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mDigest);
    }


}