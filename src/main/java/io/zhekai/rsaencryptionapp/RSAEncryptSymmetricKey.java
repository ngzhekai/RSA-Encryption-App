package io.zhekai.rsaencryptionapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSAEncryptSymmetricKey extends Application {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKey secretKey;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RSAEncryptSymmetricKey.class.getResource("app-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 680, 725);
        stage.setTitle("RSA Encryption App");
        stage.setScene(scene);
        stage.show();
    }

    public void showErrorMessage(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("RSA Encryption App Dialog");
        alert.setHeaderText("Thrown Exception");
        alert.setContentText("RSA Encryption App has thrown an exception.");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    public RSAEncryptSymmetricKey() {

        try {
            // Generate a symmetric key for AES encryption
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // Use a 256-bit key
            secretKey = keyGenerator.generateKey();

            // Generate an RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024); // Use a 1024-bit key
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

        } catch (Exception e) {
            showErrorMessage(e);
        }
    }

    public String encrypt(String message) throws Exception {

        // Encrypt the message with the symmetric key
        byte[] messageToBytes = message.getBytes();
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[aesCipher.getBlockSize()];
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        byte[] encryptedMessage = aesCipher.doFinal(messageToBytes);

        // Store the IV and encode the encrypted message as a base64 string
        return encode(iv) + ":" + encode(encryptedMessage);
    }

    public String keyEncrypt() throws Exception {
        // Encrypt the symmetric key with the RSA public key
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = rsaCipher.doFinal(secretKey.getEncoded());
        // Encode the encrypted key as base64 strings
        return encode(encryptedKey);
    }

    private String encode(byte[] data) {

        // Encode the encrypted message and encrypted key as base64 strings
        return Base64.getEncoder().encodeToString(data);
    }

    public String keyDecrypt(String encryptedKey, String encryptedMessage) throws Exception {

        // Decrypt the symmetric key with the RSA private key
        byte[] encryptedBytes = decode(encryptedKey);
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKey = rsaCipher.doFinal(encryptedBytes);
        SecretKey originalKey = new SecretKeySpec(decryptedKey, "AES");

        // Extract the IV and encrypted message from the input string
        String[] parts = encryptedMessage.split(":");
        byte[] iv = decode(parts[0]);
        return decrypt(parts[1], originalKey, iv);
    }

    public String decrypt(String encryptedMessage, SecretKey originalKey, byte[] iv) throws Exception {

        // Decrypt the message with the symmetric key
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        aesCipher.init(Cipher.DECRYPT_MODE, originalKey, ivParams);
        byte[] decryptedMessage = aesCipher.doFinal(encryptedBytes);
        return new String(decryptedMessage, StandardCharsets.UTF_8);

    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static void main(String[] args) {
        launch();
    }
}