package com;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Clase auxiliar que realiza todas las tareas de la encriptación del ejercicio
 */
public class Encrypter {
    String seed;
    String textToEncrypt;
    String textEncrypted;

    SecretKey key;

    // --- Constructor ---
    public Encrypter(){
        this.seed = "";
        this.textToEncrypt = "";
        this.textEncrypted = "";
    }

    /**
     * Función que devuelve la SecretKey que genera la clave de cifrado a partir de la semilla proporcionada por
     * parámetro
     * En este caso, se utiliza una clave AES simétrica de 128 bits
     * @param seed semilla a utilizar
     * @return SecretKey con la clave
     */
    public SecretKey generateKey(String seed){
        // SecureRandom genera claves para números aleatorios con semilla
        SecureRandom random = new SecureRandom(seed.getBytes());

        //KeyGenerator genera la clave AES de 128 bits
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128, random); //128 bits + numero aleatorio con semilla
        return keyGen.generateKey();
    }


}
