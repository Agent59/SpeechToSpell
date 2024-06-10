package net.agent59.speech;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import java.io.IOException;

// Based on the following fix:
// https://sourceforge.net/p/cmusphinx/discussion/sphinx4/thread/3875fc39/
// by Łukasz Strzelecki posted 2019-07-06

public class CustomStreamSpeechRecognizer extends StreamSpeechRecognizer {

    public CustomStreamSpeechRecognizer(Configuration configuration) throws IOException {
        super(configuration);
    }

    public void cancelRecognition() {
        context.setSpeechSource(null);
    }
}

