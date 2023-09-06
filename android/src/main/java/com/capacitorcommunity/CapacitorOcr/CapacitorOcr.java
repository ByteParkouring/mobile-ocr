package com.capacitorcommunity.CapacitorOcr;

import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.util.concurrent.ExecutionException;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import java.io.File;
import java.io.IOException;

@NativePlugin
public class CapacitorOcr extends Plugin {

    @PluginMethod
    public void detectText(PluginCall call) throws IOException {
        String filename = call.getString("filename");
        if (filename == null) {
            call.reject("filename not specified");
            return;
        }
        String orientation = call.getString("orientation");
        if (orientation == null) {
            orientation = "UP";
        }

        int rotation = this.orientationToRotation(orientation);

        InputImage image =
                InputImage.fromMediaImage(Uri.parse(filename), rotation);

        if (image == null) {
            call.reject("Could not load image from path");
            return;
        } else {
            TextDetector td = new TextDetector();
            td.detectText(call, image);
        }
    }

    private int orientationToRotation(String orientation) {
        switch (orientation) {
            case "UP":
                return 0;
            case "RIGHT":
                return 90;
            case "DOWN":
                return 180;
            case "LEFT":
                return 270;
            default:
                return 0;
        }
    }
}
