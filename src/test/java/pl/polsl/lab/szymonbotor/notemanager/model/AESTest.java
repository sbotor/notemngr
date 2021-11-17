package pl.polsl.lab.szymonbotor.notemanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AESTest {

    AES aes;

    @ParameterizedTest
    @ValueSource(strings = {
            "", "TEST STRING", "test string",
            "\\teststring\"\t  ~12340",
    })
    void testEncryptionAndDecryption(String testValue) {

    }
}