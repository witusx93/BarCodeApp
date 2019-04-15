package com.frontwit.barcodeapp.domain.order.processing;

import com.frontwit.barcodeapp.domain.order.processing.dto.ProcessCommand;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class OrderTest {

    private static int MAX_COMPONENTS_AMOUNT = 100;

    @Test
    public void shouldUpdateStageAndMarkComplete() {
        //  given
        Long extId = 1L;
        int stage = 1;
        Order order = createOrder(extId, 1);
        ProcessCommand process =
                new ProcessCommand(extId + MAX_COMPONENTS_AMOUNT, stage, LocalDateTime.now());
        //  when
        order.update(process);
        //  then
        assertEquals(Stage.valueOf(stage), order.getStage());
        assertTrue(order.isComplete());
    }

    @Test
    public void shouldUpdateStageAndMarkIncompleteWhenNotAllComponentsProcessed() {
        //  given
        Long extId = 1L;
        int stage = 1;
        Order order = createOrder(extId, 2);
        ProcessCommand process =
                new ProcessCommand(extId + MAX_COMPONENTS_AMOUNT, stage, LocalDateTime.now());
        //  when
        order.update(process);
        //  then
        assertEquals(Stage.valueOf(stage), order.getStage());
        assertFalse(order.isComplete());
    }


    private Order createOrder(long id, int componentsNr) {
        Set<Component> components = IntStream.range(1, componentsNr + 1)
                .mapToObj(i -> Component
                        .builder()
                        .processingHistory(new ArrayList<>())
                        .barcode((long) (MAX_COMPONENTS_AMOUNT + i))
                        .quantity(1)
                        .build())
                .collect(Collectors.toSet());
        return new Order(id, "", components);
    }
}