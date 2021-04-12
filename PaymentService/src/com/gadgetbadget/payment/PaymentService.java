package com.gadgetbadget.payment;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gadgetbadget.payment.model.Payment;


public class PaymentService {
	Payment payment = new Payment();
	//Payment End-points
	@GET
	@Path("/payments")
	@Produces(MediaType.APPLICATION_JSON)
	public String readPayments() {
		return payment.readPayment().toString();
	}

}
