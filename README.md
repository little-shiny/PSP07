# Ejercicio 1 — Cifrado y descifrado de ficheros con AES 

**Módulo:** Programación de Servicios y Procesos (PSP)  
**Algoritmo utilizado:** AES / ECB / PKCS5Padding (Rijndael)  
**Lenguaje:** Java

---

## Índice

1. [Descripción del ejercicio](#1-descripción-del-ejercicio)
2. [Estructura del proyecto](#2-estructura-del-proyecto)
3. [Fundamento teórico](#3-fundamento-teórico)
4. [Decisiones de diseño justificadas](#4-decisiones-de-diseño-justificadas)
5. [Explicación del código](#5-explicación-del-código)
6. [Flujo de ejecución](#6-flujo-de-ejecución)
7. [Tratamiento de excepciones](#7-tratamiento-de-excepciones)
8. [Resultado esperado](#8-resultado-esperado)

---

## 1. Descripción del ejercicio

El ejercicio consiste en desarrollar un programa Java que genere una **cadena de texto** y la almacene en un 
**fichero cifrado** llamado `fichero.cifrado` en la raíz del 
proyecto, utilizando el algoritmo **Rijndael (AES)** con la configuración `AES/ECB/PKCS5Padding`.
Se pide tambíen que genere la **clave de cifrado** a partir de un número aleatorio cuya semilla es la concatenación del 
nombre de usuario y la contraseña, con una longitud de **128 bits** y que como comprobación pueda leer el fichero 
cifrado,
lo **descifre** e imprima el contenido original por pantalla.

---

## 2. Estructura del proyecto

```
proyecto/
│
├── src/
│   └── com/
│       ├── Main.java         ← Clase principal: orquesta el flujo completo
│       └── Encrypter.java    ← Clase auxiliar: encapsula toda la lógica criptográfica
│
└── encryptedFile             ← Fichero cifrado generado al ejecutar el programa
```

Se ha separado la lógica en dos clases siguiendo el principio de **responsabilidad única**:
- `Main` se encarga del flujo general y del manejo de ficheros.
- `Encrypter` encapsula toda la lógica criptográfica, facilitando su reutilización y mantenimiento.

---

## 3. Fundamento teórico

### 3.1 Criptografía simétrica

El programa utiliza **criptografía de clave privada o simétrica**, donde la misma clave se usa tanto para cifrar 
como para descifrar. Es el tipo adecuado para este ejercicio porque no se necesita comunicar la clave a un tercero 
ya que 
(el mismo programa cifra y descifra), 
y es significativamente más rápida que la criptografía asimétrica, además de que AES es el estándar mundial actual para 
este tipo de cifrado.

### 3.2 El algoritmo AES (Rijndael)

AES (Advanced Encryption Standard) es el sucesor del DES y el estándar de cifrado simétrico más utilizado en la actualidad. Opera sobre **bloques de 128 bits (16 bytes)** y admite claves de 128, 192 o 256 bits. Fue diseñado originalmente bajo el nombre **Rijndael**, de ahí que el enunciado los mencione como equivalentes.

### 3.3 API criptográfica de Java (JCA / JCE)

Java proporciona dos arquitecturas para trabajar con criptografía:

- **JCA** (Java Cryptography Architecture): núcleo del sistema, paquete `java.security`. Gestiona claves, números aleatorios seguros y resúmenes de mensajes.
- **JCE** (Java Cryptography Extension): extensión complementaria, paquete `javax.crypto`. Proporciona las clases para cifrado y descifrado como `Cipher`, `KeyGenerator` y `SecretKey`.

Ambas arquitecturas se basan en el modelo de **proveedores de servicios criptográficos (PSC)**, que permite utilizar diferentes implementaciones de algoritmos sin cambiar el código de la aplicación.


## 4. Decisiones de diseño justificadas

### 4.1 ¿Por qué AES y no DES u otro algoritmo?

El enunciado lo especifica explícitamente. Además, AES es el nuevo estándar mundial que ha reemplazado a DES y 3DES, ofreciendo mayor seguridad. La seguridad de un sistema criptográfico depende del diseño del algoritmo y de la longitud de la clave; AES con 128 bits cumple ambos requisitos actuales.

### 4.2 ¿Por qué 128 bits para la clave?

Según los principios criptográficos estudiados en la unidad, la longitud mínima recomendada actualmente es **128 bits**. Con menos bits el sistema sería vulnerable a ataques de fuerza bruta. Se elige 128 (y no 192 o 256) porque el enunciado lo indica explícitamente y es suficiente para este caso de uso.

### 4.3 ¿Por qué modo ECB?

**ECB (Electronic Code Book)** es el modo de cifrado de bloques más sencillo: cada bloque de 16 bytes se cifra de 
manera independiente con la misma clave. Se elige porque el enunciado lo indica expresamente y es el modo más directo 
de entender el funcionamiento del cifrado por bloques.


### 4.4 ¿Por qué PKCS5Padding?

AES trabaja con bloques fijos de 16 bytes. Si el texto a cifrar no es múltiplo exacto de 16 bytes, es necesario completar el último bloque con bytes de relleno. PKCS5Padding es el esquema estándar para esto: añade al final tantos bytes como falten, y cada byte de relleno tiene como valor el número de bytes añadidos. 

### 4.5 ¿Por qué `SecureRandom` con semilla y no `Random`?

La clase `SecureRandom` del paquete `java.security` genera números aleatorios criptográficamente seguros, a diferencia de `java.util.Random` que es predecible. El enunciado pide generar la clave "a partir de un número aleatorio con semilla la cadena usuario + password", lo que se traduce directamente en:

```java
SecureRandom random = new SecureRandom(semilla.getBytes());
```

Usando la misma semilla siempre se obtiene la misma secuencia de números, lo que garantiza que la clave generada sea **determinista y reproducible** dentro del mismo programa.

### 4.6 ¿Por qué separar la lógica en la clase `Encrypter`?

Se ha creado la clase `Encrypter` para encapsular toda la lógica criptográfica, siguiendo buenas prácticas de programación orientada a objetos. Esto hace que `Main` sea más limpio y legible, y que la clase `Encrypter` sea reutilizable en otros contextos.

---

## 5. Explicación del código

### `Encrypter.java`

#### Atributos

```java
String seed;          // Semilla para el número aleatorio
String textToEncrypt; // Texto original
byte[] textEncrypted; // Texto cifrado (en bytes)
SecretKey key;        // Clave simétrica generada
```

Los datos cifrados se almacenan como array de bytes (`byte[]`) porque el resultado del cifrado AES son bytes binarios, no texto legible.

#### `generateKey(String seed)`

```java
SecureRandom random = new SecureRandom(seed.getBytes());
KeyGenerator keyGen = KeyGenerator.getInstance("AES");
keyGen.init(128, random);
key = keyGen.generateKey();
```

- `SecureRandom` recibe la semilla como array de bytes.
- `KeyGenerator.getInstance("AES")` solicita al proveedor criptográfico de Java un generador para AES.
- `keyGen.init(128, random)` establece el tamaño de 128 bits y el número aleatorio seguro.
- `generateKey()` devuelve un objeto `SecretKey` que es la clave simétrica.

#### `encryptMessage(String msg)`

```java
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
cipher.init(Cipher.ENCRYPT_MODE, key);
textEncrypted = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
```

- `Cipher.getInstance(...)` obtiene el motor de cifrado con el algoritmo, modo y padding especificados.
- `cipher.init(Cipher.ENCRYPT_MODE, key)` configura el objeto para **cifrar** con nuestra clave.
- `doFinal()` ejecuta el cifrado y devuelve los bytes cifrados.
- Se usa `StandardCharsets.UTF_8` para garantizar la correcta codificación del texto en cualquier sistema operativo.

#### `decryptMessage(byte[] textEncrypted)`

```java
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
cipher.init(Cipher.DECRYPT_MODE, key);
return cipher.doFinal(textEncrypted);
```

Proceso inverso: se usa exactamente el mismo algoritmo y la misma clave, pero se inicializa en modo `DECRYPT_MODE`. El padding añadido durante el cifrado se elimina automáticamente.

---

### `Main.java`

```java
encrypter.generateKey(user + password);   // 1. Genera clave con semilla usuario+password
encrypter.encryptMessage(message);         // 2. Cifra el mensaje

FileOutputStream fos = new FileOutputStream(fileName);
fos.write(encrypter.textEncrypted);        // 3. Escribe bytes cifrados en fichero
fos.close();

FileInputStream fis = new FileInputStream(fileName);
byte[] encryptedFileContent = fis.readAllBytes(); // 4. Lee el fichero cifrado
fis.close();

System.out.println(new String(                    // 5. Descifra e imprime
    encrypter.decryptMessage(encryptedFileContent),
    StandardCharsets.UTF_8));
```

---

## 6. Flujo de ejecución

```
usuario + password
        │
        ▼
 SecureRandom(semilla)
        │
        ▼
 KeyGenerator → SecretKey (AES, 128 bits)
        │
        ▼
 Cipher (AES/ECB/PKCS5Padding)
        │
        ├─── ENCRYPT_MODE ──► bytes cifrados ──► fichero.cifrado
        │
        └─── DECRYPT_MODE ◄── bytes del fichero ◄── FileInputStream
                │
                ▼
        Texto original impreso por pantalla
```

---

## 7. Tratamiento de excepciones

El enunciado valora explícitamente el tratamiento adecuado de excepciones. Se capturan de forma individual para ofrecer mensajes de error descriptivos:

| Excepción | Cuándo ocurre |
|---|---|
| `NoSuchAlgorithmException` | El algoritmo "AES" no está disponible en el proveedor |
| `NoSuchPaddingException` | El padding "PKCS5Padding" no está soportado |
| `InvalidKeyException` | La clave generada no es válida para el algoritmo |
| `IllegalBlockSizeException` | El bloque de datos no tiene el tamaño correcto |
| `BadPaddingException` | Error al eliminar el padding en el descifrado |
| `IOException` | Error de lectura o escritura del fichero |

Se ha optado por capturar cada excepción por separado (en lugar de un único `catch (Exception e)`) para facilitar la depuración y ofrecer mensajes de error precisos, lo que es una buena práctica en el uso del API criptográfico de Java.



## 8. Resultado esperado

Al ejecutar el programa, la salida por consola debe ser similar a:

```
Clave generada correctamente
Fichero cifrado guardado: fichero.cifrado
Mensaje descifrado: Este es un mensaje secreto que quiero que no se pueda leer sin una clave de cifrado
```

El fichero `fichero.cifrado` generado en la raíz del proyecto contendrá bytes binarios ilegibles (el criptograma), que 
solo pueden recuperarse aplicando el descifrado con la misma clave.