package com.frontwit.barcodeapp.administration.processing.synchronization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontwit.barcodeapp.administration.processing.shared.Barcode;
import com.frontwit.barcodeapp.administration.processing.shared.OrderId;
import com.frontwit.barcodeapp.administration.processing.shared.Quantity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class OrderMapper {
    private static final Logger LOG = LoggerFactory.getLogger(OrderMapper.class);

    private ObjectMapper objectMapper;

    public TargetOrder map(SourceOrder source, Dictionary dictionary) {
        var orderId = new OrderId(source.getId());
        var comment = new TargetOrder.Comment(source.getDescription(), source.getAdditionalInfo());
        var orderInfo = createOrderInfo(source, dictionary);
        var fronts = createFronts(source);

        return new TargetOrder(orderId, comment, orderInfo, fronts);
    }

    private TargetOrder.Info createOrderInfo(SourceOrder source, Dictionary dictionary) {
        try {
            var features = objectMapper.readValue(source.getFeatures(), Features.class);
            var color = dictionary.getValue(features.getColor());
            var cutter = dictionary.getValue(features.getCutter());
            var size = dictionary.getValue(features.getSize());
            return new TargetOrder.Info(color, cutter, size, source.getNr(), source.getCustomer(), source.getOrderedAt());
        } catch (IOException e) {
            LOG.warn("Exception while parsing order info. Default order info set for order id=" + source.getId());
            LOG.warn(e.getMessage());
        }
        return new TargetOrder.Info("", "", "", source.getNr(), source.getCustomer(), source.getOrderedAt());
    }

    private List<TargetFront> createFronts(SourceOrder source) {
        try {
            var orderId = new OrderId(source.getId());
            var fronts = objectMapper.readValue(source.getFronts(), Element[].class);
            return Stream.of(fronts).map(element -> createFront(orderId, element)).collect(Collectors.toList());
        } catch (IOException e) {
            LOG.warn("Exception while parsing fronts collection. Empty fronts collection for order id=" + source.getId());
            LOG.warn(e.getMessage());
        }
        return new ArrayList<>();
    }

    private TargetFront createFront(OrderId orderId, Element element) {
        var barcode = Barcode.valueOf(orderId, element.getNumber());
        var dimensions = new TargetFront.Dimensions(element.getLength(), element.getWidth());
        var quantity = new Quantity(element.getQuantity());
        return new TargetFront(barcode, orderId, quantity, dimensions, element.getComment());
    }

    @Data
    @JsonIgnoreProperties(value = "do")
    private static class Features {
        @JsonProperty("cu")
        private long cutter;
        @JsonProperty("si")
        private long size;
        @JsonProperty("co")
        private long color;
    }

    @Data
    @JsonIgnoreProperties(value = {"el", "a", "cu", "si", "do", "co"})
    private static class Element {
        @JsonProperty("nr")
        private long number;
        @JsonProperty("l")
        private int length;
        @JsonProperty("w")
        private int width;
        @JsonProperty("q")
        private int quantity;
        @JsonProperty("com")
        private String comment;
    }
}
