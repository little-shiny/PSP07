package com;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Clase auxiliar que realiza todas las tareas de la encriptación del ejercicio
 */
public class Encrypter {
    String seed;
    String textToEncrypt;
    byte[] textEncrypted;
    SecretKey key;

    // --- Constructor ---
    public Encrypter(){
        this.seed = "";
        this.textToEncrypt = "";
        this.textEncrypted = "".getBytes();
    }

    /**
     * Función que devuelve la SecretKey que genera la clave de cifrado a partir de la semilla proporcionada por
     * parámetro
     * En este caso, se utiliza una clave AES simétrica de 128 bits
     * @param seed semilla a utilizar
     */
    public void generateKey(String seed) throws NoSuchAlgorithmException {
        // SecureRandom genera claves para números aleatorios con semilla
        SecureRandom random = new SecureRandom(seed.getBytes());

        //KeyGenerator genera la clave AES de 128 bits
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128, random); //128 bits + numero aleatorio con semilla
        key = keyGen.generateKey(); //Almacenamos la clave
    }


    /**
     * Método que cifra un mensaje mediante el cifrado AES de 128 bits (16 bytes) y lo almacena en las propiedades de
     * la clase.
     *
     * @param msg mensaje a cifrar
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public void encryptMessage(String msg) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // se añade el throws en el método para manejar las excepciones posteriormente

        // Se obtiene el objeto cypher con el algoritmo que se pide
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        // Se inicia en modo cifrado con la clave que se ha definido
        cipher.init(Cipher.ENCRYPT_MODE, key);

        //Se cifra el mensaje
        textEncrypted = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Función que devuelve una lista de bytes con el mensaje descifrado con la clave proporcionada.
     * @param textEncrypted texto encriptado que se desea traducir
     * @return texto traducido
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     */
    public byte[] decryptMessage(byte[] textEncrypted) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        // Se inicia cipher en modo descifrado
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(textEncrypted);
    }


}
