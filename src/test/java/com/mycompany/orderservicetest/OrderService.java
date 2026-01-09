/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */


/**
 *
 * @author sedak
 */
package com.mycompany.orderservicetest;

public class OrderService {
    private static final double KDV_ORANI = 0.20; 
    private static final double KARGO_UCRETI = 75.0;
    private static final double UCRETSIZ_KARGO_LIMITI = 2000.0;

    public String processComplexOrder(String category, double unitPrice, int stock, int quantity, double balance, boolean isPremium) {
        if (quantity <= 0 || unitPrice <= 0) return "HATA: Gecersiz veri.";
        if (quantity > stock) return "HATA: Yetersiz stok.";

        double subTotal = unitPrice * quantity;
        double discount = isPremium ? (subTotal * 0.15) : 0; 
        double totalAfterDiscount = subTotal - discount;

        double tax = totalAfterDiscount * KDV_ORANI;
        double totalWithTax = totalAfterDiscount + tax;

        double shipping = (isPremium || totalWithTax >= UCRETSIZ_KARGO_LIMITI) ? 0 : KARGO_UCRETI;
        double grandTotal = totalWithTax + shipping;

        if (balance < grandTotal) {
            return String.format("HATA: Yetersiz bakiye. Eksik: %.2f TL", (grandTotal - balance));
        }

        return String.format("ONAYLANDI: Toplam: %.2f TL (KDV: %.2f, Indirim: %.2f, Kargo: %.2f)", 
                             grandTotal, tax, discount, shipping);
    }
}