
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sedak
 */
package com.mycompany.orderservicetest;

import java.util.UUID;

public class OrderService {
    // Sabit Parametreler
    private static final double STANDART_KDV = 0.20;
    private static final double GIDA_KDV = 0.10;
    private static final double KARGO_UCRETI = 75.0;
    private static final double UCRETSIZ_KARGO_LIMITI = 2000.0;

    /**
     * Kurumsal Sipariş Yönetim Sistemi
     * @param category Ürün kategorisi (Elektronik, Gıda vb.)
     * @param unitPrice Birim fiyat (KDV hariç)
     * @param stock Mevcut depo stoğu
     * @param quantity Talep edilen miktar
     * @param customerBalance Müşteri cüzdan bakiyesi
     * @param isPremiumMember Premium üyelik durumu
     */
    public String processComplexOrder(String category, double unitPrice, int stock, int quantity, 
                                     double customerBalance, boolean isPremiumMember) {
        
        // 1. Temel Doğrulama
        if (quantity <= 0 || unitPrice <= 0) return "HATA_KODU_400: Gecersiz veri girisi.";
        
        // 2. Stok ve Depo Kontrolü
        if (quantity > stock) {
            return String.format("HATA_KODU_404: Stok yetersiz. Talep: %d, Mevcut: %d", quantity, stock);
        }

        // 3. Dinamik Vergi Hesaplama
        double taxRate = category.equalsIgnoreCase("Gıda") ? GIDA_KDV : STANDART_KDV;
        double subTotal = unitPrice * quantity;
        
        // 4. Katmanlı İndirim Mantığı
        double discount = 0.0;
        if (isPremiumMember) {
            discount = subTotal * 0.20; // Premium üyeye %20 indirim
        } else if (subTotal > 10000) {
            discount = subTotal * 0.10; // 10.000 TL üzerine herkese %10 indirim
        }

        double totalAfterDiscount = subTotal - discount;
        double totalTax = totalAfterDiscount * taxRate;
        double priceWithTax = totalAfterDiscount + totalTax;

        // 5. Kargo ve Lojistik Maliyeti
        double shippingFee = (priceWithTax >= UCRETSIZ_KARGO_LIMITI || isPremiumMember) ? 0 : KARGO_UCRETI;
        double grandTotal = priceWithTax + shippingFee;

        // 6. Ödeme ve Bakiye Onayı
        if (customerBalance < grandTotal) {
            return String.format("HATA_KODU_402: Bakiye yetersiz. Eksik tutar: %.2f", (grandTotal - customerBalance));
        }

        // 7. Sipariş Takip Numarası Oluşturma (Kapsamlılık için)
        String orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return String.format("SIPARIS_ONAYLANDI|ID:%s|Toplam:%.2f|KDV:%.2f|Indirim:%.2f|Kargo:%.2f", 
                             orderId, grandTotal, totalTax, discount, shippingFee);
    }
}