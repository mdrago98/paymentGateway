package com.cps3230.assignment.payment.webapp;

import com.cps3230.assignment.payment.gateway.CcInfo;
import com.cps3230.assignment.payment.gateway.PaymentProcessor;
import com.cps3230.assignment.payment.gateway.enums.CardBrands;
import com.cps3230.assignment.payment.gateway.enums.CardValidationStatuses;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

@Controller
public class PaymentProcessorController {

  private static Logger LOGGER = LogManager.getRootLogger();

  private PaymentProcessor processor = new PaymentProcessor();

  private List<String> cardBrands = Stream.of(CardBrands.values())
      .map(CardBrands::name)
        .filter(brand -> !brand.equals(String.valueOf(CardBrands.INVALID)))
      .collect(Collectors.toList());

  /**
   * A setter that sets the payment processor.
   * @param processor A payment processor instance
   */
  void setPaymentProcessor(PaymentProcessor processor) {
    this.processor = processor;
  }

  /**
   * A get endpoint that returns a webpage.
   * @param model A generic Spring Model
   * @return A payment webpage
   */
  @GetMapping("/")
  public String paymentForm(Model model) {
    model.addAttribute("paymentModel", new PaymentModel());
    model.addAttribute("cardBrands", cardBrands);
    return "payment";
  }

  /**
   * A post endpoint that processes card details received from a form
   * @param payment A payment model
   * @param model A generic Spring model
   * @return A payment webpage
   */
  @PostMapping("/")
  public String paymentSubmit(PaymentModel payment, Model model) {
    CcInfo info = new CcInfo(payment.getCustomerName(), payment.getCustomerAddress(), payment.getCardType(), payment.getCardNumber(), String.valueOf(payment.getCardExpiryDate()), payment.getCardCvv());
    try {
      long amount;
      int paymentResult = processor.processPayment(info, Long.parseLong(payment.getAmount()), new Date());
      if (paymentResult == 0) {
        payment = new PaymentModel();
        payment.setErrorMsg("Payment successful");
      } else if (paymentResult == 1) {
        CardValidationStatuses validationStatus = processor.verifyOfflineEnum(info);
        switch (validationStatus) {
          case EMPTY_FIELDS: {
            payment.setErrorMsg("Empty fields " + String.join(", ", payment.getEmptyFields()));
            break;
          }
          case CARD_EXPIRED: {
            payment.setErrorMsg("Date is invalid");
            break;
          }
          case LUHN_FAILURE: {
            payment.setErrorMsg("Card is invalid");
            break;
          }
          case PREFIX_NOT_VALID: {
            payment.setErrorMsg("The card number's prefix does not match the card type");
            break;
          }
          case DATE_PARSE_FAILURE: {
            payment.setErrorMsg("Date is invalid");
            break;
          }
          default: {
            payment.setErrorMsg("An unexpected error occurred");
          }
        }
      } else {
        payment.setErrorMsg("An unexpected error occurred");
      }
    } catch (ExecutionException | InterruptedException | IllegalAccessException e) {
      LOGGER.error(e.getLocalizedMessage());
      payment.setErrorMsg("Error");
    } catch (NumberFormatException e) {
      payment.setErrorMsg("Empty fields amount");
    }
    model.addAttribute("paymentModel", payment);
    model.addAttribute("cardBrands", cardBrands);
    return "payment";
  }

}
