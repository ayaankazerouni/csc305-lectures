package demo;

import java.util.concurrent.Semaphore;

class LoginQueue {

  private Semaphore semaphore;

  public LoginQueue(int slotLimit) {
      semaphore = new Semaphore(slotLimit);
  }

  boolean tryLogin() {
      return semaphore.tryAcquire();
  }

  void logout() {
      semaphore.release();
  }

  int availableSlots() {
      return semaphore.availablePermits();
  }

}
