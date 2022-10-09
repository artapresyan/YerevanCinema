package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.services.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Value("${qr.path}")
    private String qrPath;

    @Override
    public void generateQRCodeImage(Customer customer, Long ticketID, MovieSession movieSession) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        String info = String.format("%1$s %2$s bought ticket for %3$s movie at %4$s time." +
                        " Registered  with %5$s username", customer.getCustomerName(),
                customer.getCustomerSurname(),movieSession.getMovie().getMovieName(),movieSession.getMovieSessionStart(),
                 customer.getCustomerUsername());
        BitMatrix bitMatrix =
                barcodeWriter.encode(info, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        File file = new File(String.format(qrPath, customer.getCustomerID(), customer.getCustomerEmail(), ticketID));
        ImageIO.write(bufferedImage, "PNG", file);
        file.createNewFile();
    }
}
