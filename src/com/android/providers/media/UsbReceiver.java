/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.providers.media;

import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.hardware.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


public class UsbReceiver extends BroadcastReceiver
{
    private final static String TAG = "UsbReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        // do nothing if MTP is not supported in the kernel
        if (!UsbManager.USB_FUNCTION_ENABLED.equals(
                extras.getString(UsbManager.USB_FUNCTION_MTP))) {
            return;
        }

        if (extras.getBoolean(UsbManager.USB_CONNECTED)) {
            context.startService(new Intent(context, MtpService.class));
            // tell MediaProvider MTP is connected so it can bind to the service
            context.getContentResolver().insert(Uri.parse(
                    "content://media/none/mtp_connected"), null);
        } else {
            context.stopService(new Intent(context, MtpService.class));
            // tell MediaProvider MTP is disconnected so it can unbind from the service
            context.getContentResolver().delete(Uri.parse(
                    "content://media/none/mtp_connected"), null, null);
        }
    }
}

