package org.example.cinema_reservation_system.service.payment;

import java.io.UnsupportedEncodingException;

public interface VNPayService {
    String createPaymentUrl(Integer idHoaDon, String ip) throws UnsupportedEncodingException;
}
