/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netcompss.ffmpeg4android;

import android.app.Activity;
import android.widget.TextView;
import android.os.Bundle;


public class LicenseCheckJNI
{
   
    
    public int licenseCheck(String path) {
    	String rcStr = licenseCheckJNI(path);
    	int rc =Integer.decode(rcStr);
    	return rc;
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String licenseCheckJNI(String path);

    

    
    static {
        System.loadLibrary("license-jni");
    }
}
