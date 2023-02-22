package com.increff.pos.spring;

import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;

@Configuration
@ComponentScan(
        basePackages = {"com.increff.pos"},
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {SpringConfig.class})//
)
@PropertySources({
        @PropertySource(value = "classpath:./com/increff/pos/test.properties", ignoreResourceNotFound = true) //
})
public class QaConfig {
}
