package com.frontwit.barcodeapp.administration.route.planning;

import com.frontwit.barcodeapp.administration.route.planning.dto.RouteInfoDto;
import com.itextpdf.text.DocumentException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RouteController {

    private final RouteGenerator routeGenerator;

    public RouteController(RouteGenerator routeGenerator) {
        this.routeGenerator = routeGenerator;
    }

    @PostMapping(value = "/route", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] generateRouteSummary(@RequestBody RouteInfoDto routeInfoDto) throws DocumentException {
        var routeReportDetails = RouteDetails.of(routeInfoDto);
        return routeGenerator.generateRouteSummary(routeReportDetails);
    }
}