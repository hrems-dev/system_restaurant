package pe.edu.upeu.api_restaurant.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GeneradorQR {
    public String generarBase64(String contenido) {
        if (!StringUtils.hasText(contenido)) {
            throw new IllegalArgumentException("El contenido del QR no puede estar vacio");
        }
        String contenidoNormalizado = contenido.trim();
        try {
            BitMatrix matrix = new QRCodeWriter().encode(contenidoNormalizado, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el codigo QR", ex);
        }
    }
}
