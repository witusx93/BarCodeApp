package com.frontwit.barcodeapp.application.synchronization;

import static java.lang.String.format;

class SyncOrderNotFoundException extends RuntimeException {

    SyncOrderNotFoundException(Long id) {
        super(format("Sync order with id: %s not found", id));
    }
}