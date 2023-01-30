package com.increff.pos.api;

import com.increff.pos.entity.InvoicePojo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional(rollbackOn = ApiException.class)
public class InvoiceApi extends AbstractApi<InvoicePojo> {

}
