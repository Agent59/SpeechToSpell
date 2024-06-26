package net.agent59.speech;

import edu.cmu.sphinx.api.Configuration;
import net.agent59.Main;
import net.agent59.util.FileHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

public class Sphinx4Conf {
    // TODO implement custom resources https://fabricmc.net/wiki/tutorial:custom_resources

    private static final String SPEECH_DIRECTORY = FileHandler.RESOURCE_DIRECTORY + File.separatorChar + "speech";

    public static Configuration returnConf() throws MalformedURLException {
        Configuration configuration = new Configuration();

        String speechResourcesPath = SPEECH_DIRECTORY;

        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath(Paths.get(speechResourcesPath + File.separatorChar + "spells.dic").toFile().toURI().toURL().toString());
        configuration.setLanguageModelPath(Paths.get(speechResourcesPath + File.separatorChar + "spells.lm").toFile().toURI().toURL().toString());

        configuration.setUseGrammar(true);
        configuration.setGrammarName("spells");
        configuration.setGrammarPath(Paths.get(speechResourcesPath).toFile().toURI().toString());

        return(configuration);
    }

    public static void createSpeechResources() {
        try {
            FileHandler.createFolderIfNonexistent("speech", FileHandler.RESOURCE_DIRECTORY);

            String path = SPEECH_DIRECTORY;

            // dictionary
            FileWriter dictionary = new FileWriter(path + File.separatorChar + "spells.dic");
            dictionary.write("""
                    ACCIO\tAE K S IY OW
                    AGUAMENTI\tAE G W AH M EH N T IY
                    ALARTE\tAE L AH R T EH
                    ALOHOMORA\tAE L AH HH AA M AH R AH
                    ANAPNEO\tAH N AE P N IY OW
                    APERIO\tAH P IY R IY OW
                    APPARATE\tAE P AH R EY T
                    ASCENDARE\tAE S EH N D AH R EH
                    ASCENDIO\tAE S EH N D IY OW
                    AVADA\tAE V AH D AH
                    AVIFORS\tAE V AH F AH R Z
                    AVIS\tEY V IH S
                    BAUBILIOUS\tB AH B IH L IY AH S
                    BESTIO\tB IH S T AY OW
                    BOMBARDA\tB AA M B AA R D AH
                    CISTEM\tS IH S T AH M
                    COLLOPORTUS\tK AA L AH P AO R T AH S
                    CONFRINGO\tK AA N F R IH N G OW
                    CRUCIO\tK R UW S IY OW
                    DEFODIO\tD AH F OW D IY OW
                    DEPULSO\tD AH P UH L S OW
                    DIFFINDO\tD IH F IH N D OW
                    DISILLUSIO\tD AH S IH L UW S IY OW
                    DURO\tD UH R OW
                    EPISKEY\tAH P IH S IY
                    EVANESCO\tEH V AH N EH S OW
                    EXPECTO\tIH K S EH K T OW
                    EXPELLIARMUS\tIH K S EH L IY AA R M AH S
                    EXPULSO\tIH K S UH L S OW
                    FERA\tF EH R AH
                    FINITE\tF AY N AY T
                    FLIPENDO\tF L IY P AH N D UW
                    FUMOS\tF Y UW M AH Z
                    GLISSEO\tG L EH S IY OW
                    HERBIVICUS\tHH AH R B AH V IH S AH S
                    HOMENUM\tHH AA M AH N AH M
                    IMPERIO\tIH M P AH R AY OW
                    INCANTATEM\tIH N K AE N T AH T AH M
                    INCENDIO\tIH N S EH N D IY OW
                    KEDAVRA\tK AH D AE V R AH
                    LEVIOSA\tL IY V IY AH S AH
                    LUMOS\tL UW M AH Z
                    MAXIMA\tM AE K S AH M AH
                    MELOFORS\tM EH L AH F AH R Z
                    MORSMORDRE\tM AO R S M AO R D AH R
                    MULTICOLORFORS\tM AH L T AY K AH L ER F AO R Z
                    NOX\tN AA K S
                    OBLIVIATE\tAA B L IH V AY EY T
                    OBSCURO\tAA B S K Y AH R OW
                    ORCHIDEOUS\tAO R CH IH D IY AH S
                    PATRONUM\tP AE T R AH N AH M
                    PETRIFICUS\tP EH T R AH F IH S AH S
                    PORTUS\tP AO R T AH S
                    PROTEGO\tP R OW T EH G OW
                    REFULGENS\tR AH F UH L JH AH N Z
                    REVELIO\tR AH V IY L IY OW
                    STUPEFY\tS T UW P AH F AY
                    TEMPEST\tT EH M P AH S T
                    TOTALUS\tT AA T AH L AH S
                    VERTO\tV AH R T OW
                    WINGARDIUM\tW IH N G AA R D IY AH M
                    """);
            dictionary.close();

            // language model
            FileWriter languageModel = new FileWriter(path + File.separatorChar + "spells.lm");
            languageModel.write("""
                    Language model created by QuickLM on Tue Jul  5 12:51:46 EDT 2022
                    Copyright (c) 1996-2010 Carnegie Mellon University and Alexander I. Rudnicky

                    The model is in standard ARPA format, designed by Doug Paul while he was at MITRE.

                    The code that was used to produce this language model is available in Open Source.
                    Please visit http://www.speech.cs.cmu.edu/tools/ for more information

                    The (fixed) discount mass is 0.5. The backoffs are computed using the ratio method.
                    This model based on a corpus of 53 sentences and 63 words

                    \\data\\
                    ngram 1=63
                    ngram 2=114
                    ngram 3=63

                    \\1-grams:
                    -0.8046 </s> -0.3010
                    -0.8046 <s> -0.2270
                    -2.5289 ACCIO -0.2270
                    -2.5289 AGUAMENTI -0.2270
                    -2.5289 ALARTE -0.2270
                    -2.5289 ALOHOMORA -0.2270
                    -2.5289 ANAPNEO -0.2270
                    -2.5289 APERIO -0.2270
                    -2.5289 APPARATE -0.2270
                    -2.5289 ASCENDARE -0.2270
                    -2.5289 ASCENDIO -0.2270
                    -2.5289 AVADA -0.2997
                    -2.5289 AVIFORS -0.2270
                    -2.5289 AVIS -0.2270
                    -2.5289 BAUBILIOUS -0.2270
                    -2.5289 BESTIO -0.2985
                    -2.5289 BOMBARDA -0.2270
                    -2.5289 CISTEM -0.2997
                    -2.5289 COLLOPORTUS -0.2270
                    -2.5289 CONFRINGO -0.2270
                    -2.5289 CRUCIO -0.2270
                    -2.5289 DEFODIO -0.2270
                    -2.5289 DEPULSO -0.2270
                    -2.5289 DIFFINDO -0.2270
                    -2.5289 DISILLUSIO -0.2270
                    -2.5289 DURO -0.2270
                    -2.5289 EPISKEY -0.2270
                    -2.5289 EVANESCO -0.2270
                    -2.5289 EXPECTO -0.2997
                    -2.5289 EXPELLIARMUS -0.2270
                    -2.5289 EXPULSO -0.2270
                    -2.5289 FERA -0.2997
                    -2.5289 FINITE -0.2997
                    -2.5289 FLIPENDO -0.2270
                    -2.5289 FUMOS -0.2270
                    -2.5289 GLISSEO -0.2270
                    -2.5289 HERBIVICUS -0.2270
                    -2.5289 HOMENUM -0.2985
                    -2.5289 IMPERIO -0.2270
                    -2.5289 INCANTATEM -0.2270
                    -2.5289 INCENDIO -0.2270
                    -2.5289 KEDAVRA -0.2270
                    -2.5289 LEVIOSA -0.2270
                    -2.2279 LUMOS -0.2254
                    -2.5289 MAXIMA -0.2270
                    -2.5289 MELOFORS -0.2270
                    -2.5289 MORSMORDRE -0.2270
                    -2.5289 MULTICOLORFORS -0.2270
                    -2.5289 NOX -0.2270
                    -2.5289 OBLIVIATE -0.2270
                    -2.5289 OBSCURO -0.2270
                    -2.5289 ORCHIDEOUS -0.2270
                    -2.5289 PATRONUM -0.2270
                    -2.5289 PETRIFICUS -0.2997
                    -2.5289 PORTUS -0.2270
                    -2.5289 PROTEGO -0.2270
                    -2.5289 REFULGENS -0.2270
                    -2.2279 REVELIO -0.2270
                    -2.5289 STUPEFY -0.2270
                    -2.5289 TEMPEST -0.2270
                    -2.5289 TOTALUS -0.2270
                    -2.5289 VERTO -0.2270
                    -2.5289 WINGARDIUM -0.2997

                    \\2-grams:
                    -2.0253 <s> ACCIO 0.0000
                    -2.0253 <s> AGUAMENTI 0.0000
                    -2.0253 <s> ALARTE 0.0000
                    -2.0253 <s> ALOHOMORA 0.0000
                    -2.0253 <s> ANAPNEO 0.0000
                    -2.0253 <s> APPARATE 0.0000
                    -2.0253 <s> ASCENDARE 0.0000
                    -2.0253 <s> ASCENDIO 0.0000
                    -2.0253 <s> AVADA 0.0000
                    -2.0253 <s> AVIFORS 0.0000
                    -2.0253 <s> AVIS 0.0000
                    -2.0253 <s> BAUBILIOUS 0.0000
                    -2.0253 <s> BESTIO 0.0000
                    -2.0253 <s> BOMBARDA 0.0000
                    -2.0253 <s> CISTEM 0.0000
                    -2.0253 <s> COLLOPORTUS 0.0000
                    -2.0253 <s> CONFRINGO 0.0000
                    -2.0253 <s> CRUCIO 0.0000
                    -2.0253 <s> DEFODIO 0.0000
                    -2.0253 <s> DEPULSO 0.0000
                    -2.0253 <s> DIFFINDO 0.0000
                    -2.0253 <s> DISILLUSIO 0.0000
                    -2.0253 <s> DURO 0.0000
                    -2.0253 <s> EPISKEY 0.0000
                    -2.0253 <s> EVANESCO 0.0000
                    -2.0253 <s> EXPECTO 0.0000
                    -2.0253 <s> EXPELLIARMUS 0.0000
                    -2.0253 <s> EXPULSO 0.0000
                    -2.0253 <s> FERA 0.0000
                    -2.0253 <s> FINITE 0.0000
                    -2.0253 <s> FLIPENDO 0.0000
                    -2.0253 <s> FUMOS 0.0000
                    -2.0253 <s> GLISSEO 0.0000
                    -2.0253 <s> HERBIVICUS 0.0000
                    -2.0253 <s> HOMENUM 0.0000
                    -2.0253 <s> IMPERIO 0.0000
                    -2.0253 <s> INCENDIO 0.0000
                    -1.7243 <s> LUMOS 0.0000
                    -2.0253 <s> MELOFORS 0.0000
                    -2.0253 <s> MORSMORDRE 0.0000
                    -2.0253 <s> MULTICOLORFORS 0.0000
                    -2.0253 <s> NOX 0.0000
                    -2.0253 <s> OBLIVIATE 0.0000
                    -2.0253 <s> OBSCURO 0.0000
                    -2.0253 <s> ORCHIDEOUS 0.0000
                    -2.0253 <s> PETRIFICUS 0.0000
                    -2.0253 <s> PORTUS 0.0000
                    -2.0253 <s> PROTEGO 0.0000
                    -2.0253 <s> REFULGENS 0.0000
                    -2.0253 <s> STUPEFY 0.0000
                    -2.0253 <s> TEMPEST 0.0000
                    -2.0253 <s> WINGARDIUM 0.0000
                    -0.3010 ACCIO </s> -0.3010
                    -0.3010 AGUAMENTI </s> -0.3010
                    -0.3010 ALARTE </s> -0.3010
                    -0.3010 ALOHOMORA </s> -0.3010
                    -0.3010 ANAPNEO </s> -0.3010
                    -0.3010 APERIO </s> -0.3010
                    -0.3010 APPARATE </s> -0.3010
                    -0.3010 ASCENDARE </s> -0.3010
                    -0.3010 ASCENDIO </s> -0.3010
                    -0.3010 AVADA KEDAVRA 0.0000
                    -0.3010 AVIFORS </s> -0.3010
                    -0.3010 AVIS </s> -0.3010
                    -0.3010 BAUBILIOUS </s> -0.3010
                    -0.3010 BESTIO REVELIO 0.0000
                    -0.3010 BOMBARDA </s> -0.3010
                    -0.3010 CISTEM APERIO 0.0000
                    -0.3010 COLLOPORTUS </s> -0.3010
                    -0.3010 CONFRINGO </s> -0.3010
                    -0.3010 CRUCIO </s> -0.3010
                    -0.3010 DEFODIO </s> -0.3010
                    -0.3010 DEPULSO </s> -0.3010
                    -0.3010 DIFFINDO </s> -0.3010
                    -0.3010 DISILLUSIO </s> -0.3010
                    -0.3010 DURO </s> -0.3010
                    -0.3010 EPISKEY </s> -0.3010
                    -0.3010 EVANESCO </s> -0.3010
                    -0.3010 EXPECTO PATRONUM 0.0000
                    -0.3010 EXPELLIARMUS </s> -0.3010
                    -0.3010 EXPULSO </s> -0.3010
                    -0.3010 FERA VERTO 0.0000
                    -0.3010 FINITE INCANTATEM 0.0000
                    -0.3010 FLIPENDO </s> -0.3010
                    -0.3010 FUMOS </s> -0.3010
                    -0.3010 GLISSEO </s> -0.3010
                    -0.3010 HERBIVICUS </s> -0.3010
                    -0.3010 HOMENUM REVELIO 0.0000
                    -0.3010 IMPERIO </s> -0.3010
                    -0.3010 INCANTATEM </s> -0.3010
                    -0.3010 INCENDIO </s> -0.3010
                    -0.3010 KEDAVRA </s> -0.3010
                    -0.3010 LEVIOSA </s> -0.3010
                    -0.6021 LUMOS </s> -0.3010
                    -0.6021 LUMOS MAXIMA 0.0000
                    -0.3010 MAXIMA </s> -0.3010
                    -0.3010 MELOFORS </s> -0.3010
                    -0.3010 MORSMORDRE </s> -0.3010
                    -0.3010 MULTICOLORFORS </s> -0.3010
                    -0.3010 NOX </s> -0.3010
                    -0.3010 OBLIVIATE </s> -0.3010
                    -0.3010 OBSCURO </s> -0.3010
                    -0.3010 ORCHIDEOUS </s> -0.3010
                    -0.3010 PATRONUM </s> -0.3010
                    -0.3010 PETRIFICUS TOTALUS 0.0000
                    -0.3010 PORTUS </s> -0.3010
                    -0.3010 PROTEGO </s> -0.3010
                    -0.3010 REFULGENS </s> -0.3010
                    -0.3010 REVELIO </s> -0.3010
                    -0.3010 STUPEFY </s> -0.3010
                    -0.3010 TEMPEST </s> -0.3010
                    -0.3010 TOTALUS </s> -0.3010
                    -0.3010 VERTO </s> -0.3010
                    -0.3010 WINGARDIUM LEVIOSA 0.0000

                    \\3-grams:
                    -0.3010 <s> ACCIO </s>
                    -0.3010 <s> AGUAMENTI </s>
                    -0.3010 <s> ALARTE </s>
                    -0.3010 <s> ALOHOMORA </s>
                    -0.3010 <s> ANAPNEO </s>
                    -0.3010 <s> APPARATE </s>
                    -0.3010 <s> ASCENDARE </s>
                    -0.3010 <s> ASCENDIO </s>
                    -0.3010 <s> AVADA KEDAVRA
                    -0.3010 <s> AVIFORS </s>
                    -0.3010 <s> AVIS </s>
                    -0.3010 <s> BAUBILIOUS </s>
                    -0.3010 <s> BESTIO REVELIO
                    -0.3010 <s> BOMBARDA </s>
                    -0.3010 <s> CISTEM APERIO
                    -0.3010 <s> COLLOPORTUS </s>
                    -0.3010 <s> CONFRINGO </s>
                    -0.3010 <s> CRUCIO </s>
                    -0.3010 <s> DEFODIO </s>
                    -0.3010 <s> DEPULSO </s>
                    -0.3010 <s> DIFFINDO </s>
                    -0.3010 <s> DISILLUSIO </s>
                    -0.3010 <s> DURO </s>
                    -0.3010 <s> EPISKEY </s>
                    -0.3010 <s> EVANESCO </s>
                    -0.3010 <s> EXPECTO PATRONUM
                    -0.3010 <s> EXPELLIARMUS </s>
                    -0.3010 <s> EXPULSO </s>
                    -0.3010 <s> FERA VERTO
                    -0.3010 <s> FINITE INCANTATEM
                    -0.3010 <s> FLIPENDO </s>
                    -0.3010 <s> FUMOS </s>
                    -0.3010 <s> GLISSEO </s>
                    -0.3010 <s> HERBIVICUS </s>
                    -0.3010 <s> HOMENUM REVELIO
                    -0.3010 <s> IMPERIO </s>
                    -0.3010 <s> INCENDIO </s>
                    -0.6021 <s> LUMOS </s>
                    -0.6021 <s> LUMOS MAXIMA
                    -0.3010 <s> MELOFORS </s>
                    -0.3010 <s> MORSMORDRE </s>
                    -0.3010 <s> MULTICOLORFORS </s>
                    -0.3010 <s> NOX </s>
                    -0.3010 <s> OBLIVIATE </s>
                    -0.3010 <s> OBSCURO </s>
                    -0.3010 <s> ORCHIDEOUS </s>
                    -0.3010 <s> PETRIFICUS TOTALUS
                    -0.3010 <s> PORTUS </s>
                    -0.3010 <s> PROTEGO </s>
                    -0.3010 <s> REFULGENS </s>
                    -0.3010 <s> STUPEFY </s>
                    -0.3010 <s> TEMPEST </s>
                    -0.3010 <s> WINGARDIUM LEVIOSA
                    -0.3010 AVADA KEDAVRA </s>
                    -0.3010 BESTIO REVELIO </s>
                    -0.3010 CISTEM APERIO </s>
                    -0.3010 EXPECTO PATRONUM </s>
                    -0.3010 FERA VERTO </s>
                    -0.3010 FINITE INCANTATEM </s>
                    -0.3010 HOMENUM REVELIO </s>
                    -0.3010 LUMOS MAXIMA </s>
                    -0.3010 PETRIFICUS TOTALUS </s>
                    -0.3010 WINGARDIUM LEVIOSA </s>

                    \\end\\
                    """);
            languageModel.close();

            // grammar model
            FileWriter grammarModel = new FileWriter(path + File.separatorChar + "spells.gram");
            grammarModel.write("""
                    #JSGF V1.0 UTF-8 en;

                    grammar spells;
                    public <ACCIO> = ACCIO;
                    public <AGUAMENTI> = AGUAMENTI;
                    public <ALARTE_ASCENDARE> = ALARTE ASCENDARE;
                    public <ALOHOMORA> = ALOHOMORA;
                    public <ANAPNEO> = ANAPNEO;
                    public <APPARATE> = APPARATE;
                    public <ASCENDIO> = ASCENDIO;
                    public <AVADA_KEDAVRA> = AVADA KEDAVRA;
                    public <AVIFORS> = AVIFORS;
                    public <AVIS> = AVIS;
                    public <BAUBILIOUS> = BAUBILIOUS;
                    public <BESTIO_REVELIO> = BESTIO REVELIO;
                    public <BOMBARDA> = BOMBARDA;
                    public <CISTEM_APERIO> = CISTEM APERIO;
                    public <COLLOPORTUS> = COLLOPORTUS;
                    public <CONFRINGO> = CONFRINGO;
                    public <CRUCIO> = CRUCIO;
                    public <DEFODIO> = DEFODIO;
                    public <DEPULSO> = DEPULSO;
                    public <DIFFINDO> = DIFFINDO;
                    public <DISILLUSIO> = DISILLUSIO;
                    public <DURO> = DURO;
                    public <EPISKEY> = EPISKEY;
                    public <EVANESCO> = EVANESCO;
                    public <EXPECTO_PATRONUM> = EXPECTO PATRONUM;
                    public <EXPELLIARMUS> = EXPELLIARMUS;
                    public <EXPULSO> = EXPULSO;
                    public <FERA_VERTO> = FERA VERTO;
                    public <FINITE_INCANTATEM> = FINITE INCANTATEM;
                    public <FLIPENDO> = FLIPENDO;
                    public <FUMOS> = FUMOS;
                    public <GLISSEO> = GLISSEO;
                    public <HERBIVICUS> = HERBIVICUS;
                    public <HOMENUM_REVELIO> = HOMENUM REVELIO;
                    public <IMPERIO> = IMPERIO;
                    public <INCENDIO> = INCENDIO;
                    public <LUMOS> = LUMOS;
                    public <LUMOS_MAXIMA> = LUMOS MAXIMA;
                    public <MELOFORS> = MELOFORS;
                    public <MORSMORDRE> = MORSMORDRE;
                    public <MULTICOLORFORS> = MULTICOLORFORS;
                    public <NOX> = NOX;
                    public <OBLIVIATE> = OBLIVIATE;
                    public <OBSCURO> = OBSCURO;
                    public <ORCHIDEOUS> = ORCHIDEOUS;
                    public <PETRIFICUS_TOTALUS> = PETRIFICUS TOTALUS;
                    public <PORTUS> = PORTUS;
                    public <PROTEGO> = PROTEGO;
                    public <REFULGENS> = REFULGENS;
                    public <STUPEFY> = STUPEFY;
                    public <WINGARDIUM_LEVIOSA> = WINGARDIUM LEVIOSA;
                    public <TEMPEST> = TEMPEST;
                    """);
            grammarModel.close();

            Main.LOGGER.info("created speech resources");

        } catch (IOException e) {
            Main.LOGGER.error("Couldn't create speech resources");
            e.printStackTrace();
        }
    }
}
