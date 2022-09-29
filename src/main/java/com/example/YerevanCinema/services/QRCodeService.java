package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class QRCodeService {

    public void generateQRCodeImage(Customer customer) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        String info = String.format("Customer %s1 %s2 bought ticket to movie for session with" +
                        " email %s3 and account registered with %s4 username",customer.getCustomerName(),
                customer.getCustomerSurname(),customer.getCustomerEmail(),customer.getCustomerUsername());
        BitMatrix bitMatrix =
                barcodeWriter.encode(info, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        File file = new File("E:\\Java\\YerevanCinema\\src\\main\\resources\\qr\\"
                + customer.getCustomerID() + "." + customer.getCustomerEmail() + ".png");
        ImageIO.write(bufferedImage, "png", file);
        file.createNewFile();
    }
}
