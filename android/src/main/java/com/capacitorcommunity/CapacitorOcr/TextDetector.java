package com.capacitorcommunity.CapacitorOcr;

import androidx.annotation.NonNull;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutionException;

public class TextDetector {
    /** there must be exactly 6 digits inside the element */
    private final int thresholdDigitAmount = 6;
    /** each of the six digits must have confidence over 55% */
    private final float thresholdConfidence = 0.55f;

    public void detectText(PluginCall call, InputImage image) {
        try {

            TextRecognizer textDetector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            textDetector.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            call.success(new JSObject().put("result", extractText(visionText)));
                        }
                    })
                    .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                call.reject("error, could not process image");
                            }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            call.reject(e.localizedMessage, e);
        }
    }

    public String extractText(Text visionText){
        String resultText = "";

        List<Text.TextBlock> blocks = textResult.getTextBlocks();
        if (blocks.size() > 0) {
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
                                // elementString is filtered result for JSON
                                resultText = elementString;
                            }
                        }
                    }
                }
            }
        }
        return resultText;
    }

}