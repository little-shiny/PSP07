package com;

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
        final String message = "This is the secret message for this task";
        final String fileName = "encryptedFile";

        Encrypter encrypter = new Encrypter();


        try{
            // Se genera la clave de cifrado con la cadena que se pide (usuario + contraseña)
            encrypter.generateKey(user + password);
            System.out.println("Clave generada correctamente");

            // Cifrado del mensaje
            encrypter.encryptMessage(message);
        }

    }
}