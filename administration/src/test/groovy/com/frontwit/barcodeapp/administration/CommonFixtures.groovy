package com.frontwit.barcodeapp.administration

import com.frontwit.barcodeapp.api.shared.Barcode
import com.frontwit.barcodeapp.api.shared.OrderId

class CommonFixtures {

    static OrderId anOrderId() {
        new OrderId(1L);
    }

    static Barcode aBarcode(frontId = 1L) {
        Barcode.valueOf(anOrderId(), frontId)
    }
}
