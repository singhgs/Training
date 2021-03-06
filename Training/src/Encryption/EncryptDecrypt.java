/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encryption;
import java.nio.charset.Charset;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class EncryptDecrypt
{
    private byte[] key;

    private static final String ALGORITHM = "DESede/CBC/PKCS5Padding";
    private static final String KEYALGORITHM = "DESede";

    String ivStr = "12345678";
 
    public EncryptDecrypt(byte[] key)
    {
        this.key = key;
    }

    public EncryptDecrypt(String keyStr)
    {
        
        byte[] keyBytes = DatatypeConverter.parseHexBinary(keyStr);
        this.key = keyBytes;
     
    }

    /**
     * Encrypts the given plain text
     *
     * @param plainText The plain text to encrypt
     */
    public String encrypt(byte[] plainText) throws Exception
    {
        //Initialize Key
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYALGORITHM);
        SecretKey skey = factory.generateSecret(new DESedeKeySpec(key));
        System.out.println("Key is : " + skey);
       
        //Initialize Cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skey,  new IvParameterSpec(ivStr.getBytes()));

        
        //getReplayProtection : 8 bytes
        java.util.Date d = new java.util.Date();
        byte [] replayProtection = longToByteArray(d.getTime());
        
        //Add replayProtection to rawData and save in enhancedBytes
        int nextByte = 0;
        byte enhancedBytes[] = new byte[plainText.length + replayProtection.length];
        nextByte = copyBytes(enhancedBytes, nextByte, plainText);
        nextByte = copyBytes(enhancedBytes, nextByte, replayProtection);
        System.out.println("After adding replay protection : " + DatatypeConverter.printHexBinary(enhancedBytes));
        
        //Encrypt enhancedBytes
        byte encryptedData [] = cipher.doFinal(enhancedBytes);
        System.out.println("Encrypted Data : " + DatatypeConverter.printHexBinary(encryptedData));
        
        //Add IV to encryptedData
        byte[] iv = cipher.getIV();    //get IV 8 bytes        
        byte transportData[] = new byte[iv.length + encryptedData.length];
        for (int i=0; i < iv.length; i++) {
            transportData[i] = iv[i];
        }
        for (int i=0; i < encryptedData.length; i++) {
            transportData[iv.length +i] = encryptedData[i];
        }
        System.out.println("Encrypted Data : " + DatatypeConverter.printHexBinary(transportData));
        //HEX encode data and return encodedValue
        String encodedValue = encode(transportData);
//        String encodedValue = DatatypeConverter.printHexBinary(transportData);
        System.out.println("EncryptedAndEncodeValue : " + encodedValue);
        return encodedValue;
    }
    

    /**
     * Decrypts the given byte array
     *
     * @param cipherText The data to decrypt
     */
    public String decrypt(String encodedData) throws Exception
    {
        //Initialize the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYALGORITHM);
        SecretKey skey = factory.generateSecret(new DESedeKeySpec(key));
        
        //Decode encoded data
        byte decodedBytes[] = DatatypeConverter.parseHexBinary(encodedData);
  //      byte decodedBytes[] = decode(encodedData);
        //Remove IV Transport
        byte ivBytes[] = new byte[8];
        for (int i=0; i<ivBytes.length;i++) {
            ivBytes[i] = decodedBytes[i];
        }
        int dataLength = decodedBytes.length - ivBytes.length;
        byte encryptedDataBytes[] = new byte[dataLength];
        for (int i=0; i < dataLength ; i++) {
            encryptedDataBytes[i] = decodedBytes[ivBytes.length + i];
        }

        //Initialize cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivParamSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, skey , ivParamSpec);

        //Decrypt Data
        byte decryptedDataBytes[] = cipher.doFinal(encryptedDataBytes);
        dataLength = decryptedDataBytes.length;
        //Extract raw data
        long timeMessageEncrypted = byteArrayToLong(decryptedDataBytes, dataLength - 8); // TimeStamp length is 8
        java.util.Date d = new java.util.Date(timeMessageEncrypted);
        System.out.println("Time message was encrypted : " + d.toString() + " <<--Value retrieved from encrypted message after decode/decrypt of encrypted message");
        dataLength = dataLength - 8; //timestamp added is 8 bytes
        byte rawData[] = decryptedDataBytes;
        if (dataLength < decryptedDataBytes.length) {
            rawData = new byte[dataLength];
            for (int i=0; i<dataLength;i++) {
                rawData[i] = decryptedDataBytes[i];
            }
        }
        
        
        
        String decryptedString = new String(rawData, Charset.forName("UTF-8"));
        
        return decryptedString;
    }
    
    
    
     private static byte[] longToByteArray(long l) {
        byte b[] = new byte[8];
        b[7] = (byte) (l);
        l >>>= 8;
        b[6] = (byte) (l);
        l >>>= 8;
        b[5] = (byte) (l);
        l >>>= 8;
        b[4] = (byte) (l);
        l >>>= 8;
        b[3] = (byte) (l);
        l >>>= 8;
        b[2] = (byte) (l);
        l >>>= 8;
        b[1] = (byte) (l);
        l >>>= 8;
        b[0] = (byte) (l);
        return b;
    }    
     
      private static long byteArrayToLong(byte[] b, int offset){
        return ( ( (long) b[7+offset]) & 0xFF) +
            ( ( ( (long) b[6+offset]) & 0xFF) << 8) +
            ( ( ( (long) b[5+offset]) & 0xFF) << 16) +
            ( ( ( (long) b[4+offset]) & 0xFF) << 24) +
            ( ( ( (long) b[3+offset]) & 0xFF) << 32) +
            ( ( ( (long) b[2+offset]) & 0xFF) << 40) +
            ( ( ( (long) b[1+offset]) & 0xFF) << 48) +
            ( ( ( (long) b[0+offset]) & 0xFF) << 56);
    }
      
    private int copyBytes(byte[] target, int targetOffset, byte[] source) {
         
          for (int i=0; i < source.length; i++) {
              target[i + targetOffset] = source[i];
          }
          return targetOffset + source.length;
    }
    
    
    
    private static String HITS = "0123456789ABCDEF";

    /**
     * Transforms a hexadecimal string to a byte array
     *
     * @return the byte array, or null if the passed String is null
     * @exception IllegalArgumentException if the passed String is not haxadecimal 
     * encoded
     */
    public byte[] decode(String hex) {
    //public static byte[] toBytes(String hex) {

        if (hex == null) {
            return null;
        } else if ((hex.length() % 2) != 0) {
            throw new IllegalArgumentException(
                    "Hex String length must be a multiple of 2.");
        }

        int length = hex.length() / 2;
        byte[] result = new byte[length];
        String h = hex.toUpperCase();

        for (int i = 0; i < length; i++) {
            char c = h.charAt(2 * i);
            int index = HITS.indexOf(c);

            if (index == -1) {
                throw new IllegalArgumentException(
                        "Hex String can't contain '" + c + "'");
            }
            int j = 16 * index;

            c = h.charAt((2 * i) + 1);
            index = HITS.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException(
                        "Hex String can't contain '" + c + "'");
            }
            j += index;
            result[i] = (byte) (j & 0xFF);
        }
        return result;
    }
  
    /**
     * Transforms a byte array into a hexadecimal String
     *
     * @return the hexadecimal String, or null if the passed array is null
     */
    public String encode(byte[] b) {

        if (b == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < b.length; i++) {
            int j = ((int) b[i]) & 0xFF;
            char first = HITS.charAt(j / 16); 
            char second = HITS.charAt(j % 16);

            sb.append(first);
            sb.append(second);
        }
        return sb.toString();
    } 

    
    
    
    
    
}
    
