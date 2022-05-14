package net.agent59.stp.speech;

/*
 * Copyright 2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.

Copyright 1999-2015 Carnegie Mellon University.
        Portions Copyright 2002-2008 Sun Microsystems, Inc.
        Portions Copyright 2002-2008 Mitsubishi Electric Research Laboratories.
        Portions Copyright 2013-2015 Alpha Cephei, Inc.

        All Rights Reserved.  Use is subject to license terms.

        Redistribution and use in source and binary forms, with or without
        modification, are permitted provided that the following conditions
        are met:

        1. Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.

        2. Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in
        the documentation and/or other materials provided with the
        distribution.

        3. Original authors' names are not deleted.

        4. The authors' names are not used to endorse or promote products
        derived from this software without specific prior written
        permission.

        This work was supported in part by funding from the Defense Advanced
        Research Projects Agency and the National Science Foundation of the
        United States of America, the CMU Sphinx Speech Consortium, and
        Sun Microsystems, Inc.

        CARNEGIE MELLON UNIVERSITY, SUN MICROSYSTEMS, INC., MITSUBISHI
        ELECTRONIC RESEARCH LABORATORIES AND THE CONTRIBUTORS TO THIS WORK
        DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING ALL
        IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL
        CARNEGIE MELLON UNIVERSITY, SUN MICROSYSTEMS, INC., MITSUBISHI
        ELECTRONIC RESEARCH LABORATORIES NOR THE CONTRIBUTORS BE LIABLE FOR
        ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
        WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
        ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT
        OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

// speech recognition fix after this comment from stack-overflow
// https://stackoverflow.com/questions/29121188/cant-access-microphone-while-running-dialog-demo-in-sphinx4-5prealpha#comment46488376_29121188


import java.io.IOException;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Microphone;
import edu.cmu.sphinx.frontend.util.StreamDataSource;


/**
 * High-level class for live speech recognition.
 */
public class CustomLiveSpeechRecognizer extends AbstractSpeechRecognizer {

    private final Microphone microphone;

    /**
     * Constructs new live recognition object.
     *
     * @param configuration common configuration
     * @throws IOException if model IO went wrong
     */
    public CustomLiveSpeechRecognizer(Configuration configuration) throws IOException
    {
        super(configuration);
        //microphone = speechSourceProvider.getMicrophone(); // original
        microphone = getMicrophone(); // modification
        context.getInstance(StreamDataSource.class)
                .setInputStream(microphone.getStream());
    }

    /**
     * Starts recognition process.
     *
     * @param clear clear cached microphone data
     * @see         edu.cmu.sphinx.api.LiveSpeechRecognizer#stopRecognition()
     */
    public void startRecognition(boolean clear) {
        recognizer.allocate();
        microphone.startRecording();
    }

    /**
     * Stops recognition process.
     *
     * Recognition process is paused until the next call to startRecognition.
     *
     * @see edu.cmu.sphinx.api.LiveSpeechRecognizer#startRecognition(boolean)
     */
    public void stopRecognition() {
        microphone.stopRecording();
        recognizer.deallocate();
    }


    // modification

    private static final Microphone mic = new Microphone(16000, 16, true, false);
    Microphone getMicrophone() {
        return mic;
    }
}
