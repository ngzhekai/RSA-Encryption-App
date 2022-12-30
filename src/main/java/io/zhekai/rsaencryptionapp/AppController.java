package io.zhekai.rsaencryptionapp;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AppController {
    RSAEncryptSymmetricKey rsa = new RSAEncryptSymmetricKey();
    @FXML
    private TextArea plaintext;
    @FXML
    private TextField EncryptedText;
    @FXML
    private TextField EncryptedKey;
    @FXML
    private TextField ciphertext;
    @FXML
    private TextField encryptedSymmetricKey;
    @FXML
    private TextArea DecryptedText;

    @FXML
    protected void onEncryptButtonClick() {
        try {
            if (plaintext.getText().isBlank()) {
                EncryptedText.clear();
                EncryptedKey.clear();
                throw new Exception("No message is entered in the Text Box!"); // throw exception

            } else {

                // Encrypt the message with the symmetric key
                String encryptedMessage = rsa.encrypt(plaintext.getText());
                EncryptedText.setText(encryptedMessage);

                // Encrypt the symmetric key with the RSA
                String encryptedKey = rsa.keyEncrypt();
                EncryptedKey.setText(encryptedKey);
            }
        } catch (Exception e) {
            rsa.showErrorMessage(e);
        }
    }

    @FXML
    protected void onDecryptButtonClick() {
        try {
            if (ciphertext.getText().isBlank() || encryptedSymmetricKey.getText().isBlank()) {
                DecryptedText.clear();
                throw new Exception("No encrypted message/encrypted key is pasted!"); // throw exception

            } else {

                // Decrypt the symmetric key with RSA followed by the message
                String decryptedMessage = rsa.keyDecrypt(encryptedSymmetricKey.getText(), ciphertext.getText());
                DecryptedText.setText(decryptedMessage);
            }

        } catch (Exception e) {
            rsa.showErrorMessage(e);
        }
    }
}