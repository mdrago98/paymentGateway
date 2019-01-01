package com.cps3230.assignment.payment.gateway.helpers;

import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.invocation.InvocationOnMock;

public class CallsRealMethodWithDelay extends CallsRealMethods {

  private final long delay;

  public CallsRealMethodWithDelay(long delay) {
    this.delay = delay;
  }

  public Object answer(InvocationOnMock invocation) throws Throwable {
    Thread.sleep(delay);
    return super.answer(invocation);
  }

}
