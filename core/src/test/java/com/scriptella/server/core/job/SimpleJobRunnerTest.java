package com.scriptella.server.core.job;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author <a href="mailto:scriptella@gmail.com">Fyodor Kupolov</a>
 *
 */
public class SimpleJobRunnerTest {

	@Test
	public void test() throws Exception {
		TestJobRunner tst = new TestJobRunner() {
			@Override
			protected String execute() {
				getExecutionCallback().setJobProgress(1);
				return "1";
			}
		};

		Assert.assertEquals("1", tst.call());
		Assert.assertEquals(JobExecutionCallback.CompletionStatus.SUCCESSFUL, tst.executionCallback.status);
		Assert.assertEquals(1.0, tst.executionCallback.progress, 0.00001);
	}

	@Test(timeout = 100)
	public void testCancellationSleep() throws Exception {
		final CountDownLatch startSignal = new CountDownLatch(1);
		final TestJobRunner tst = new TestJobRunner() {
			@Override
			protected String execute() throws InterruptedException {
				System.out.println(Thread.currentThread().getName() + " sending start signal");
				startSignal.countDown();
				getExecutionCallback().setJobProgress(.1);
				System.out.println(Thread.currentThread().getName() + " start sleep");
				Thread.sleep(150);
				System.out.println(Thread.currentThread().getName() + " end sleep");
				getExecutionCallback().setJobProgress(.2);
				return "1";
			}
		};
		Executors.newSingleThreadExecutor().submit(tst);

		System.out.println(Thread.currentThread().getName() + " waiting for start signal");
		startSignal.await();
		tst.cancel();
		System.out.println(Thread.currentThread().getName() + " cancel called");
		Thread.sleep(20); // simply wait for cancellation to end

		Assert.assertTrue("Interrupted exception should be propagated: " + tst.errors, tst.errors.size() == 1
				&& tst.errors.get(0) instanceof InterruptedException);
		Assert.assertEquals(0.1, tst.executionCallback.progress, 0.00001);
		Assert.assertEquals(JobExecutionCallback.CompletionStatus.CANCELLED, tst.executionCallback.status);
	}

	@Test(timeout = 100)
	public void testCancellationSync() throws Exception {

		final CountDownLatch signalStart = new CountDownLatch(1);
		final CountDownLatch signalReadyToCancel = new CountDownLatch(1);
		final CountDownLatch signalEnd = new CountDownLatch(1);
		final TestJobRunner tst = new TestJobRunner() {
			@Override
			protected String execute() throws InterruptedException {
				getExecutionCallback().setJobProgress(.1);
				System.out.println(Thread.currentThread().getName() + " Sending readyToCancel signal");
				signalReadyToCancel.countDown();
				System.out.println(Thread.currentThread().getName()
						+ " Emulate non-interruptible wait until cancel method is called");
				do {
					getExecutionCallback().setJobProgress(1);
				} while (!cancelCalled);
				System.out.println(Thread.currentThread().getName()
						+ " End non-interruptible wait - cancel method is called");
				return "1";
			}
		};
		// Second thread cancels the job
		Thread t = new Thread() {

			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " Waiting for start signal");
				try {
					signalStart.await(); // synchronize on start
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				System.out.println(Thread.currentThread().getName() + " Calling job");
				try {
					tst.call();
					System.out.println(Thread.currentThread().getName() + " Job completed");
				} catch (Exception e) {
					// Exception should be captured by the TestJobRunner class
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " Sending end signal");
				signalEnd.countDown();
			}
		};
		t.setDaemon(true);
		t.start();
		System.out.println("Sending start signal");
		signalStart.countDown();
		System.out.println("Waiting for readyToCancel signal");
		signalReadyToCancel.await();
		System.out.println("Calling cancel");
		tst.cancel();
		System.out.println("Completed cancel ");

		System.out.println("Waiting for end signal");
		signalEnd.await();

		Assert.assertTrue("Exception occurred while executing a job", tst.errors.isEmpty());
		Assert.assertEquals(1, tst.executionCallback.progress, 0.00001);
		Assert.assertEquals(JobExecutionCallback.CompletionStatus.CANCELLED, tst.executionCallback.status);
	}

	@Test(timeout = 1000)
	public void testname() throws Exception {
		for (int i = 0; i < 100; i++) {
			testCancellationSync();
		}
	}

	@Test()
	public void testFailures() throws Exception {
		final TestJobRunner tst = new TestJobRunner() {
			@Override
			protected String execute() throws Exception {
				getExecutionCallback().setJobProgress(.1);
				throw new IllegalStateException("test");
			}
		};
		try {
			tst.call();
			fail("Exception expected");
		} catch (IllegalStateException e) {
			// OK
		}

		Assert.assertEquals(.1, tst.executionCallback.progress, 0.00001);
		Assert.assertEquals(JobExecutionCallback.CompletionStatus.FAILED, tst.executionCallback.status);
	}

	static abstract class TestJobRunner extends SimpleJobRunner<String> {
		volatile boolean cancelCalled;
		final List<Throwable> errors = new ArrayList<Throwable>();
		final TestExecutionCallback executionCallback = new TestExecutionCallback();

		public TestJobRunner() {
			setExecutionCallback(executionCallback);
		}

		@Override
		public void cancel() {
			try {
				super.cancel();
				cancelCalled = true;
			} catch (RuntimeException e) {
				e.printStackTrace();
				errors.add(e);
			}
		}

		@Override
		public String call() throws Exception {
			try {
				return super.call();
			} catch (Exception e) {
				errors.add(e);
				throw e;
			}
		}
	};

}
