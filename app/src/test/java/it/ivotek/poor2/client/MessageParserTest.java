package it.ivotek.poor2.client;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageParserTest {

    @Test
    public void feedData() throws Exception {
        MessageParser p = new MessageParser();
        p.feedData("[");
        p.feedData("45|34|23");
        p.feedData("[781|192");
        p.feedData("]");
        assertEquals(Integer.valueOf(781), p.getUltrasoundLeft());
        assertEquals(Integer.valueOf(192), p.getUltrasoundCenter());
        assertNull(p.getUltrasoundRight());
        p.feedData("[");
        p.feedData("45|34|23]");
        assertEquals(Integer.valueOf(45), p.getUltrasoundLeft());
        assertEquals(Integer.valueOf(34), p.getUltrasoundCenter());
        assertEquals(Integer.valueOf(23), p.getUltrasoundRight());
        assertNull(p.getCompass());
    }

}
