package com.galvan.inventarios.servicio;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalServicio {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    private PayPalHttpClient client;

    @PostConstruct
    public void init() {
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        this.client = new PayPalHttpClient(environment);
    }

    // 1. CREAR ORDEN DE PAGO EN PAYPAL
    public Order crearOrdenPaypal(Double total, Long pedidoId) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // Configurar montos y moneda (EUR)
        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode("EUR")
                .value(String.format(java.util.Locale.US, "%.2f", total));

        // Configuración compatible con el modelo del SDK de PayPal
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest();
        purchaseUnitRequest.referenceId(pedidoId.toString());
        purchaseUnitRequest.amountWithBreakdown(amount); // <-- Corregido al setter oficial

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(purchaseUnitRequest);
        orderRequest.purchaseUnits(purchaseUnits);

        // URLs de redirección para el Frontend (Angular)
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("http://localhost:4200/pago-exito?pedidoId=" + pedidoId)
                .cancelUrl("http://localhost:4200/pago-cancelado");
        orderRequest.applicationContext(applicationContext);

        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        return client.execute(request).result();
    }

    // 2. CAPTURAR EL PAGO (Confirmación final)
    public Order capturarPago(String paypalOrderId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(paypalOrderId);
        request.requestBody(new OrderRequest());
        return client.execute(request).result();
    }
}