package com;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Clase principal del ejercicio PSP06
 * Se encarga de generar una cadena de texto que será almacenada en un fichero encriptado en la raíz del proyecto
 * Para comprobar el funcionamiento debe poder leer el archivo e imprimir por pantalla el contenido del fichero
 * desencriptado.
 */
public class Main {
    public static void main(String[] args) {
        final String user = "myUser";
        final String password = "MyPassword";
        final String message = "Este es un mensaje secreto que quiero que no se pueda leer sin una clave de cifrado ";
        final String fileName = "encryptedFile";

        Encrypter encrypter = new Encrypter();

        try{
            // ---- Se genera la clave de cifrado con la cadena que se pide (usuario + contraseña)
            encrypter.generateKey(user + password);
            System.out.println("Clave generada correctamente");

            // ---- Cifrado del mensaje
            encrypter.encryptMessage(message);

            // ---- Guardado en fichero
            FileOutputStream fos = new FileOutputStream(fileName);
            // Se escribe en el fichero el mensaje cifrado
            fos.write(encrypter.textEncrypted);
            fos.close();
            System.out.println("Fichero cifrado guardado: " + fileName);

            // ---- Leer y descifrar
            FileInputStream fis = new FileInputStream(fileName);
            byte[] encryptedFileContent = fis.readAllBytes();
            fis.close();

            // ---- Mostrar contenido del fichero descifrado
            System.out.println("Mensaje descifrado: " + new String(encrypter.decryptMessage(encryptedFileContent), StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algoritmo no encontrado: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.err.println("Padding no soportado: " + e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println("Clave inválida: " + e.getMessage());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Error en el cifrado/descifrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de fichero: " + e.getMessage());
        }
    }
}