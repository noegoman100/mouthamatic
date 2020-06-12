package mouthamatic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SentenceParserTest {
    @Test
    void sentenceParserTest(){
        SentenceData sentenceData = new SentenceData("Test. One, Two; THREE");
        SentenceParser.parseSentence(sentenceData);
        assertEquals("TEST", sentenceData.getParsedSentenceWordsList().get(0).getWord_name());
        assertEquals("ONE", sentenceData.getParsedSentenceWordsList().get(1).getWord_name());
        assertEquals("TWO", sentenceData.getParsedSentenceWordsList().get(2).getWord_name());
        assertEquals("THREE", sentenceData.getParsedSentenceWordsList().get(3).getWord_name());

    }


}