/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encryption;
import Encryption.EncryptDecrypt;
import java.nio.charset.Charset;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Girija Singh
 */
public class TestEncryptDecrypt {
    public static void main(String args[]) { //arg0 = site id ; arg1 =key
        System.out.println("Inputs = " + args[0] + " , " + args[1]);
        String siteID = args[0]; // "garbageText";
        String rawKey = args[1]; //"BADC2A10ECD5C86D16232F4FFB73573189E03E7ABFC4FD10"; temp garbage key
        byte [] rawKeyBytes = DatatypeConverter.parseHexBinary(rawKey);
        byte [] siteIDArray = siteID.getBytes();
        
        EncryptDecrypt ed = new EncryptDecrypt(rawKey);
        String  encryptedEncodedString;
        String plainText = null;
        try {
            encryptedEncodedString =  ed.encrypt(siteIDArray);
            plainText = ed.decrypt(encryptedEncodedString);
            System.out.println("Plain Text : " + plainText);
        }
        catch (Exception ex) {
            ex.printStackTrace();
    }
    }    
}
