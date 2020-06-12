package mouthamatic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SymbolMapperTest {
    @Test
    void symbolMapperTest(){
        try {
            Main.db.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SentenceData sentenceData = new SentenceData("Test. One, Two; THREE");
        SentenceParser.parseSentence(sentenceData);
        WordParser.parseWords(sentenceData, 999);
        SymbolMapper.mapSymbolsTo(sentenceData, 4, 999); //mouth_pair_id of 4 is selected.

        //TEST = (0) 999 -> Set7, (1) 70 -> Set8, (2) 31 -> Set1, (3) 68 -> Set5, (4) 70 -> Set8
        assertEquals("Mike-LipSet-Layers-Alpha_07.png", sentenceData.parsedImageSequence.get(0));
        assertEquals("Mike-LipSet-Layers-Alpha_08.png", sentenceData.parsedImageSequence.get(1));
        assertEquals("Mike-LipSet-Layers-Alpha_01.png", sentenceData.parsedImageSequence.get(2));
        assertEquals("Mike-LipSet-Layers-Alpha_05.png", sentenceData.parsedImageSequence.get(3));
        assertEquals("Mike-LipSet-Layers-Alpha_08.png", sentenceData.parsedImageSequence.get(4));
        //ONE =  (5) 999 -> Set7, (6) 81 -> Set6, (7) 11 -> Set3, (8) 56 -> Set9
        assertEquals("Mike-LipSet-Layers-Alpha_07.png", sentenceData.parsedImageSequence.get(5));
        assertEquals("Mike-LipSet-Layers-Alpha_06.png", sentenceData.parsedImageSequence.get(6));
        assertEquals("Mike-LipSet-Layers-Alpha_03.png", sentenceData.parsedImageSequence.get(7));
        assertEquals("Mike-LipSet-Layers-Alpha_09.png", sentenceData.parsedImageSequence.get(8));
        //TWO = (9) 999 -> Set7, (10) 70 -> Set8, (11) 78 -> Set2
        assertEquals("Mike-LipSet-Layers-Alpha_07.png", sentenceData.parsedImageSequence.get(9));
        assertEquals("Mike-LipSet-Layers-Alpha_08.png", sentenceData.parsedImageSequence.get(10));
        assertEquals("Mike-LipSet-Layers-Alpha_02.png", sentenceData.parsedImageSequence.get(11));
        //THREE = (12) 999 -> Set7, (13) 71 -> Set10, (14) 67 -> Set2, (15) 50 -> Set4
        assertEquals("Mike-LipSet-Layers-Alpha_07.png", sentenceData.parsedImageSequence.get(12));
        assertEquals("Mike-LipSet-Layers-Alpha_10.png", sentenceData.parsedImageSequence.get(13));
        assertEquals("Mike-LipSet-Layers-Alpha_02.png", sentenceData.parsedImageSequence.get(14));
        assertEquals("Mike-LipSet-Layers-Alpha_04.png", sentenceData.parsedImageSequence.get(15));
    }


}