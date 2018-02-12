/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base64decoder;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64.*;
import java.util.Base64;

/**
 *
 * @author Girija Singh
 */
public class Base64Decoder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Base64Decoder bd = new Base64Decoder();
        bd.decodebase64();
    }
    
    public void decodebase64() {
        
        Path originalPath = Paths.get("c:/temp", "encodedImage.txt");
        Path targetPath = Paths.get("c:/temp", "decodedImage.pdf");
        
        Decoder decoder = Base64.getDecoder();
        
        OutputStream output = null;
        InputStream inStream = null;
        
        try {
         inStream = Files.newInputStream(originalPath);
         Files.copy(decoder.wrap(inStream), targetPath);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Conversion completed");;
        
    }
    
}
