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
        p.feedData("[5[45[87");
        p.feedData("|22|180|23.00]");
        assertEquals(Integer.valueOf(87), p.getUltrasoundLeft());
        assertEquals(Integer.valueOf(22), p.getUltrasoundCenter());
        assertEquals(Integer.valueOf(180), p.getUltrasoundRight());
        assertEquals(Float.valueOf(23), p.getCompass());
        p.feedData("[2[5[57|11|13|24.00]");
        assertEquals(Integer.valueOf(57), p.getUltrasoundLeft());
        assertEquals(Integer.valueOf(11), p.getUltrasoundCenter());
        assertEquals(Integer.valueOf(13), p.getUltrasoundRight());
        assertEquals(Float.valueOf(24), p.getCompass());
    }

}
