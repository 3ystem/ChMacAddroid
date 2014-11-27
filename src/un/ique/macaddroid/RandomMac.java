/*
 * MacAddroid - Android app that changes a network devices MAC address
 * Copyright (C) 2014 Matthew Finkel <Matthew.Finkel@gmail.com>
 *
 * This file is part of MacAddroid
 *
 * MacAddroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MacAddroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MacAddroid, in the COPYING file.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package un.ique.macaddroid;

import android.app.Activity;
import android.os.Bundle;
import un.ique.macaddroid.Layer2Address;
import un.ique.macaddroid.NativeIOCtller;
import un.ique.macaddroid.FileStuff;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import java.lang.Process;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

public class RandomMac extends Activity {
    // Let's hardcode wlan0, for now
    private String dev = "wlan0";
    private Layer2Address mNewNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.randommac);

        Layer2Address newNet = new Layer2Address();
        newNet.setInterfaceName(dev);
        NativeIOCtller ctller = new NativeIOCtller(newNet);
        newNet.setAddress(ctller.getCurrentMacAddr());
        String addr = newNet.formatAddress();
        TextView macField = (TextView)
            findViewById(R.id.randommac_macaddress);
        if (macField != null) {
            macField.setText(addr);
        }
        byte[] nextAddr = newNet.generateNewAddress();
        mNewNet = new Layer2Address(nextAddr, dev);
        TextView nextMacField = (TextView)
            findViewById(R.id.randommac_nextmacaddress);
        if (nextMacField != null) {
            nextMacField.setText(mNewNet.formatAddress());
        }
    }

    public void showNewAddress(View view) {
        byte[] nextAddr = mNewNet.generateNewAddress();
        mNewNet = new Layer2Address(nextAddr, dev);
        TextView nextMacField = (TextView)
            findViewById(R.id.randommac_nextmacaddress);
        if (nextMacField != null) {
            nextMacField.setText(mNewNet.formatAddress());
        }
    }

    public void applyNewAddress(View view) {
        String pathToBinary = "";
        NativeIOCtller ctller = new NativeIOCtller(mNewNet);
        int err = 11;
        String uid = Integer.toString(ctller.getCurrentUID());
        FileStuff fs = new FileStuff(this);
        File exe = fs.copyBinaryFile();
        if (exe == null) {
            // TODO Show a useful message like "Please restart this app.
            // We're broken?"
        } else {
            pathToBinary = exe.getAbsolutePath();
        }
        try {
            String[] args = {"su", "0",
                             pathToBinary, dev,
                             mNewNet.formatAddress(), uid};
            Process root_shell = Runtime.getRuntime().exec(args);
            try {
                 root_shell.waitFor();
                 err = root_shell.exitValue();
            } catch (InterruptedException e) {
            }
        } catch (IOException e) {
            return;
        }
        //int err = ctller.setMacAddr(mNewNet.getAddress());
        String errorcode = ctller.getErrorString(err);
        mNewNet.setAddress(ctller.getCurrentMacAddr());
        String addr = mNewNet.formatAddress();
        TextView macField = (TextView)
            findViewById(R.id.randommac_macaddress);
        if (macField != null) {
            macField.setText(addr);
        }
    }
}
