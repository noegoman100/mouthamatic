package mouthamatic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordParserTest {
    @Test
    void wordParserTest(){
        try {
            Main.db.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SentenceData sentenceData = new SentenceData("Test. One, Two; THREE");
        SentenceParser.parseSentence(sentenceData);
        WordParser.parseWords(sentenceData, 999);
        //The word TEST should have symbols: 70, 31, 68, 70
        assertEquals(999, sentenceData.getParsedSentenceWordsList().get(0).getPhonemes().get(0)); //Blank Symbol
        assertEquals(70, sentenceData.getParsedSentenceWordsList().get(0).getPhonemes().get(1));
        assertEquals(31, sentenceData.getParsedSentenceWordsList().get(0).getPhonemes().get(2));
        assertEquals(68, sentenceData.getParsedSentenceWordsList().get(0).getPhonemes().get(3));
        assertEquals(70, sentenceData.getParsedSentenceWordsList().get(0).getPhonemes().get(4));
        //The word ONE should have symbols: 81, 11, 56
        assertEquals(999, sentenceData.getParsedSentenceWordsList().get(1).getPhonemes().get(0)); //Blank Symbol
        assertEquals(81, sentenceData.getParsedSentenceWordsList().get(1).getPhonemes().get(1));
        assertEquals(11, sentenceData.getParsedSentenceWordsList().get(1).getPhonemes().get(2));
        assertEquals(56, sentenceData.getParsedSentenceWordsList().get(1).getPhonemes().get(3));
        //The word TWO should have symbols: 70, 78
        assertEquals(999, sentenceData.getParsedSentenceWordsList().get(2).getPhonemes().get(0)); //Blank Symbol
        assertEquals(70, sentenceData.getParsedSentenceWordsList().get(2).getPhonemes().get(1));
        assertEquals(78, sentenceData.getParsedSentenceWordsList().get(2).getPhonemes().get(2));
        //The word THREE should have symbols: 71, 67, 50
        assertEquals(999, sentenceData.getParsedSentenceWordsList().get(3).getPhonemes().get(0)); //Blank Symbol
        assertEquals(71, sentenceData.getParsedSentenceWordsList().get(3).getPhonemes().get(1));
        assertEquals(67, sentenceData.getParsedSentenceWordsList().get(3).getPhonemes().get(2));
        assertEquals(50, sentenceData.getParsedSentenceWordsList().get(3).getPhonemes().get(3));
    }


}