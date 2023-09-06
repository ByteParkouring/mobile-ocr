package com.capacitorcommunity.CapacitorOcr;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

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

import org.json.JSONArray;

public class TextDetector {

    public void detectText(call: PluginCall, image: InputImage) {
        try {

            TextRecognizer textDetector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            textDetector.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {

                        /** set the currently detected text visible,
                         * also filter the detected text */
                        @Override
                        public void onSuccess(Text visionText) {
                            extractText(visionText);

                        }
                    });
                    /*.addOnSuccessListener { detectedBlocks ->
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
            }*/
        } catch (e: Exception) {
            e.printStackTrace();
            call.reject(e.localizedMessage, e)
        }
    }

    public void extractText(Text visionText){
        List<Text.TextBlock> blocks = textResult.getTextBlocks();
        if (blocks.size() == 0) {
            return;
        }
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) { //&& (elements.get(k).getConfidence() > thresholdConfidence)
                    Text.Element element = elements.get(k);
                    List<Text.Symbol> symbols = element.getSymbols();

                    ArrayList<String> symbolStringList = new ArrayList<>();

                    boolean valid_element = true;
                    if ((element.getConfidence() > thresholdConfidence) && (symbols.size() == thresholdDigitAmount)) {
                        for (int m = 0; m < symbols.size(); m++) {
                            Text.Symbol symbol = symbols.get(m);

                            String symbolString = symbol.getText();
                            if((symbolString.charAt(0) == 'O') || (symbolString.charAt(0) == 'U')){
                                symbolString = "0";
                            }
                            symbolStringList.add(symbolString);

                            if ((symbol.getConfidence() < thresholdConfidence) || (!Character.isDigit(symbolString.charAt(0)))){
                                valid_element = false;
                            }
                        }
                        if (valid_element){
                            String elementString = "";
                            for(int m = 0; m < symbolStringList.size(); m++){
                                elementString += symbolStringList.get(m);
                            }
                            listOfNumbersWith6Digits.add(elementString);
                            // display confidence percentage for test purpose
                            resultString += "\nConfidence" + element.getConfidence() * 100 + "%";
                            return;
                        }
                    }
                }
            }
        }
    }

}