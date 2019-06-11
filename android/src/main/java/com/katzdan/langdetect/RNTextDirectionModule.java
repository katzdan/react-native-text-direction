package com.katzdan.langdetect;

import android.content.Context;
import android.util.Log;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.List;


//RNTextDirectionModule is a react native module that invokes the Android OS builtin function
//to identify languages in a given string.
public class RNTextDirectionModule extends ReactContextBaseJavaModule {

  private final double PROBABILITY_THRESH_HOLD = 0.66; //The required probability for language identification
  private final String API_NOT_AVAILABLE_ON_THIS_DEVICE_ERROR_CODE = "1001";

  //Standard ctor
  public RNTextDirectionModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "RNTextDirection";
  }

  //This class only supports text language detection on Android 8 and above
  //and not on all Android versions.
  //'isAvailable' will return true only in case the OS on the user's device
  //supports this api.
  //The code in this class
  @ReactMethod
  public void isAvailable(final Promise promise) {
      boolean result;
      try {
          result = DetectionTextClassifier.isAvailable(getReactApplicationContext());
          Log.d(getName(), "DetectionTextClassifier.isAvailable returned " + result);
      } catch (Throwable throwable){
          result = false;
          //For now - print to logcat but in the future - print to stream passe from the hosting application
          throwable.printStackTrace();
          Log.e(getName(), "DetectionTextClassifier.isAvailable returned " + result);
      }
      promise.resolve(result);
  }

  //isRTL receives a string, uses a OS function to identify which languages it contains
  //and returns weather the text should be displayed as RTL ot LTR.
  //Example inputs :
  //String in English -> isRTL returns false
  //String in Hebrew/arabic -> isRTL returns true
  //String in English and Hebrew/Arabic -> isRTL returns true
  @ReactMethod
  public void isRTL(String input, final Promise promise) {
    try {
      if (DetectionTextClassifier.isAvailable(getReactApplicationContext())) {
        Log.d(getName(), "isRTL invoked with the text :" + input);
        List<DetectionTextClassifier.Language> languages = detectLangs(getReactApplicationContext(), input);

        if (languages == null || languages.size()==0){
          //If not languages are identified (e.g. the input string is numeric) - we return 'false'
          Log.d(getName(), "languages is null or zero length");
          promise.resolve(false);
          return;
        }

        if (languages != null && languages.size() > 0) {
          for (DetectionTextClassifier.Language language : languages) {
            Log.d(getName(), "isRTL languages detected :" + language.lang + ", probability : " + language.prob);

            //We regard the accuracy of the detected language acceptable only above 0.66
            if (    (language.lang.equals("iw") || //Hebrew
                    language.lang.equals("ar") || //Arabic
                    language.lang.equals("fa") || //Farsi
                    language.lang.equals("ur") || //Urdu
                    language.lang.equals("hy") || //Armenian
                    language.lang.equals("ckb")) //Kurdish
              && language.prob>= PROBABILITY_THRESH_HOLD) {

              //We found  RTL language -> we return 'true'
              Log.d(getName(), "isRTL languages found.");
              promise.resolve(true);
              return;
            }
          }
          //if we reached this code it means we haven't detected a RTL language. We return "false"
          promise.resolve(false);
          return;
        }
      } else {
          promise.reject(API_NOT_AVAILABLE_ON_THIS_DEVICE_ERROR_CODE, "OS based language detection is not available on this device, please use the '' function to check before using 'isRTL'");
      }
    }catch (Throwable throwable){
        promise.reject(throwable);
    }
  }

  //detectLangs receives an input string and involves an OS function to try and identify which languages
  //it contains. It returns a list of languages with the probability of each language (= the level
  //of certainty of language identification).
  private synchronized List<DetectionTextClassifier.Language> detectLangs(Context context, String input) {

      final DetectionTextClassifier.DetectionResult detectionResult = new DetectionTextClassifier(context).detectLanguages(input);

      if (detectionResult!=null) {
          Log.d(getName(), "DetectionTextClassifier Result is not null");
      }else{
          Log.d(getName(), "DetectionTextClassifier Result is null");
      }

    return detectionResult==null ? null : detectionResult.list;

  }
}