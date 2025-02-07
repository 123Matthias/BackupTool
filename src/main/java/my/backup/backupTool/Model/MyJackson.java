package my.backup.backupTool.Model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;

public class MyJackson {

    public static class SecretKeySerializer extends JsonSerializer<SecretKey> {
        @Override
        public void serialize(SecretKey key, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (key == null) {
                gen.writeNull();
                return;
            }
            gen.writeString(Base64.getEncoder().encodeToString(key.getEncoded()));
        }
    }

    public static class SecretKeyDeserializer extends JsonDeserializer<SecretKey> {
        @Override
        public SecretKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String encodedKey = p.getText();
            if (encodedKey == null || encodedKey.isEmpty()) {
                throw new IOException("SecretKey ist leer!");
            }
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(decodedKey, "AES");
        }
    }

    public static class IvParameterSpecSerializer extends JsonSerializer<IvParameterSpec> {
        @Override
        public void serialize(IvParameterSpec iv, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(Base64.getEncoder().encodeToString(iv.getIV()));
        }
    }

    public static class IvParameterSpecDeserializer extends JsonDeserializer<IvParameterSpec> {
        @Override
        public IvParameterSpec deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            byte[] ivBytes = Base64.getDecoder().decode(p.getText());
            return new IvParameterSpec(ivBytes);
        }
    }
}

