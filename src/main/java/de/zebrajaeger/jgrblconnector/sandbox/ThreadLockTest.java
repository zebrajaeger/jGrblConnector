package de.zebrajaeger.jgrblconnector.sandbox;

/**
 * Created by lars on 03.09.2016.
 */
public class ThreadLockTest {
  private Object x = null;

  public void put(Object toStore) throws InterruptedException {
    synchronized (this) {
      while (x != null) {
        wait();
      }
      x = toStore;
    }
  }

  public Object get() {
    Object result = x;
    if (x != null) {
      x = null;
      synchronized (this) {
        notifyAll();
      }
    }
    return result;
  }

  public static void main(String[] args) throws InterruptedException {
    final ThreadLockTest test = new ThreadLockTest();

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("T1.put");
          test.put("T1");
          System.out.println("T1.ok");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("T2.put");
          test.put("T2");
          System.out.println("T2.ok");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("T3.put");
          test.put("T3");
          System.out.println("T3.ok");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

    Thread.sleep(1000);
    test.get();
    Thread.sleep(1000);
    test.get();
  }
}
