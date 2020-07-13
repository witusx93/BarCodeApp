package com.frontwit.barcodeapp.administration.statistics.application

import com.frontwit.barcodeapp.administration.statistics.domain.order.Meters
import com.frontwit.barcodeapp.administration.statistics.domain.order.OrderStatistics
import com.frontwit.barcodeapp.administration.statistics.domain.order.OrderStatisticsRepository
import com.frontwit.barcodeapp.administration.statistics.domain.shared.StatisticsPeriod
import spock.lang.Specification

import java.time.Month
import java.time.Year

import static org.hamcrest.Matchers.containsInAnyOrder
import static spock.util.matcher.HamcrestSupport.expect

class OrderStatisticsCreatorTest extends Specification {
    OrderStatisticsRepository repository = Mock()
    OrderStatisticsCreator orderStatisticsCreator = new OrderStatisticsCreator(repository)

    def "should create all statistics for given day"() {
        given:
        def statisticsPeriod1 = new StatisticsPeriod(19, Month.JUNE as Month, Year.of(2020))
        def statisticsPeriod2 = new StatisticsPeriod(16, Month.JUNE as Month, Year.of(2020))
        def statisticsPeriod3 = new StatisticsPeriod(10, Month.JUNE as Month, Year.of(2020))
        def statisticsPeriod4 = new StatisticsPeriod(2, Month.MAY as Month, Year.of(2020))
        def statisticsPeriod5 = new StatisticsPeriod(7, Month.FEBRUARY as Month, Year.of(2020))

        def statistics = [
                OrderStatistics.of(statisticsPeriod1, Meters.of(1), Meters.of(1)),
                OrderStatistics.of(statisticsPeriod2, Meters.of(1), Meters.of(1)),
                OrderStatistics.of(statisticsPeriod3, Meters.of(1), Meters.of(1)),
                OrderStatistics.of(statisticsPeriod4, Meters.of(1), Meters.of(1)),
                OrderStatistics.of(statisticsPeriod5, Meters.of(1), Meters.of(1)),
        ]
        repository.findForYearUntil(statisticsPeriod1) >> statistics
        when:
        def result = orderStatisticsCreator.statisticsFor(statisticsPeriod1)

        then:
        expect result.getPeriods(), containsInAnyOrder(
                periodDto(PeriodType.TODAY, 1, 1),
                periodDto(PeriodType.WEEK, 2, 2),
                periodDto(PeriodType.MONTH, 3, 3),
                periodDto(PeriodType.QUARTER, 4, 4),
                periodDto(PeriodType.YEAR, 5, 5)
        )
    }

    private static OrderStatisticsDto.PeriodDto periodDto(PeriodType type, Double orders, Double complaints) {
        new OrderStatisticsDto.PeriodDto(type, orders, complaints)
    }
}
