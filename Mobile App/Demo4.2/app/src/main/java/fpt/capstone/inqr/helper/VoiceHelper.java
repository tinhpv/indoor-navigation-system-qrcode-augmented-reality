package fpt.capstone.inqr.helper;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class VoiceHelper {
    private TextToSpeech mTTS;

    public VoiceHelper() {
    }

    public void generateSpeechLine(Activity activity, String startPoint, String endPoint, int orientation) {
        mTTS = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(new Locale("vi_VN"));

                    if (result == mTTS.LANG_NOT_SUPPORTED || result == mTTS.LANG_MISSING_DATA) {
                        Log.e("TTS", "Not supported!");
                    } else {

                        String line = "";
                        mTTS.setLanguage(new Locale("vi_VN"));
                        line += "Bạn đang ở " + startPoint;
                        switch (orientation) {
                            case 0:
                                line += ", điểm đến đang ở gần bạn, hãy kiểm tra bản đồ.";
                                break;
                            case 1:
                                line += ", quẹo trái tới " + endPoint;
                                break;
                            case 2:
                                line += ", quẹo phải tới " + endPoint;
                                break;
                            case 3:
                                line += ", hãy đi lên tới " + endPoint;
                                break;
                            case 4:
                                line += ", hãy đi xuống tới " + endPoint;
                                break;
                        }
                        mTTS.speak(line, mTTS.QUEUE_FLUSH, null);
                    }
                } else {
                    Log.e("TTS", "Init failed!");
                }
            }
        });

    }
}

