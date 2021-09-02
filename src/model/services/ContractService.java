package model.services;

import java.util.Calendar;
import java.util.Date;

import model.entities.Contract;
import model.entities.Installment;

public class ContractService {

	private OnlinePaymentService onlinePaymentService;

	public ContractService(OnlinePaymentService onlinePaymentService) {
		this.onlinePaymentService = onlinePaymentService;
	}

	public void processContract(Contract contract, int month) {

		/*
		 * example: total value = 600;
		 * 
		 * months = 3
		 * 
		 * basicQUOTA = 200 (600/3)
		 */
		double basicQUOTA = contract.getTotalValue() / month;

		for (int cont = 1; cont <= month; cont++) {

			/*
			 * interest:
			 * 
			 * first month = 2 (200 * 0.01) 
			 * updated quota = 202 (200 + 2)
			 * 
			 * second month = 4 (200 * 0.02) 
			 * updated quota = 204 (200 + 4)
			 * 
			 * third month = 6 (200 * 0.03) 
			 * updated quota = 206 (200 + 6)
			 */
			double updatedQUOTA = basicQUOTA + onlinePaymentService.interest(basicQUOTA, cont);

			/*
			 * Payment fee:
			 * 
			 * first month = 4,04 (202 * 0,02) 
			 * full quota = 206,04 (202 + 4,04)
			 * 
			 * second month = 4,08 (204 * 0,02) 
			 * full quota = 208,08 (204 + 4,08)
			 * 
			 * third month = 4,12 (206 * 0,02) 
			 * full quota = 210,12 (206 + 4,12)
			 */
			double fullQUOTA = updatedQUOTA + onlinePaymentService.paymentFee(updatedQUOTA);

			Date dueDate = addMonths(contract.getDate(), cont);

			contract.addInstallment(new Installment(dueDate, fullQUOTA));
		}
	}

	// Auxiliary function
	private Date addMonths(Date date, int N) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, N);
		return cal.getTime();
	}
}
