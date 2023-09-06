package com.capacitorcommunity.CapacitorOcr


import android.util.NoSuchPropertyException
import com.getcapacitor.JSObject
import com.getcapacitor.PluginCall

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import org.json.JSONArray


class TextDetector {
  fun detectText(call: PluginCall, image: InputImage) {

    try {

      val textDetector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

      textDetector.processImage(image)
        .addOnSuccessListener { detectedBlocks ->
          for (block in detectedBlocks.textBlocks) {
            for (line in block.lines) {
              // Gets the four corner points in clockwise direction starting with top-left.
              val cornerPoints = line.cornerPoints ?: throw NoSuchPropertyException("FirebaseVisionTextRecognizer.processImage: could not get bounding coordinates")
              val topLeft = cornerPoints[0]
              val topRight = cornerPoints[1]
              val bottomRight = cornerPoints[2]
              val bottomLeft = cornerPoints[3]

              val textDetection = mapOf(
                // normalizing coordinates
                "topLeft" to listOf<Double?>((topLeft.x).toDouble()/width, (height - topLeft.y).toDouble()/height),
                "topRight" to listOf<Double?>((topRight.x).toDouble()/width, (height - topRight.y).toDouble()/height),
                "bottomLeft" to listOf<Double?>((bottomLeft.x).toDouble()/width, (height - bottomLeft.y).toDouble()/height),
                "bottomRight" to listOf<Double?>((bottomRight.x).toDouble()/width, (height - bottomRight.y).toDouble()/height),
                "text" to line.text
              )
              detectedText.add(textDetection)
            }
          }
          call.success(JSObject().put("textDetections", JSONArray(detectedText)))
        }
        .addOnFailureListener { e ->
          call.reject("FirebaseVisionTextRecognizer couldn't process the given image", e)
        }
    } catch (e: Exception) {
      e.printStackTrace();
      call.reject(e.localizedMessage, e)
    }
  }
}
