package com.cheappartsguy.app.cpg;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;

public class StringEncoder {

    public static String Encode(String StringEncode) throws UnsupportedEncodingException {
        try {
            // one easy string, one that's a little bit harder
            String encodedString = "";

            String[] testStrings = {StringEncode};
            for (String s : testStrings) {
                encodedString = URLEncoder.encode(s, "UTF-8");
            }
            return encodedString;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String Decode(String StringEncode) throws UnsupportedEncodingException {
        try {
            // one easy string, one that's a little bit harder
            String encodedString = "";

            String[] testStrings = {StringEncode};
            for (String s : testStrings) {
                encodedString = URLDecoder.decode(s, "UTF-8");
            }
            return encodedString;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
