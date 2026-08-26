package mx.edu.utez.DesarrolloAcademico.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase utilitaria con funciones de hasheo (SHA-256) usadas para almacenar y verificar contraseñas de forma segura.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-08-19
 */
public class HashUtils {

    /**
     * Hashea un string usando SHA-256 y lo devuelve en formato hexadecimal.
     * @param text Texto en crudo a hashear.
     * @return String con el hash en formato hexadecimal.
     */
    public static String hashSHA256(String text) {
        if (text == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: No se encontró el algoritmo SHA-256", e);
        }
    }

    /**
     * Convierte un arreglo de bytes a su representación en texto hexadecimal.
     * @param hash Arreglo de bytes con el hash a convertir.
     * @return Cadena de texto resultante.
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
