package com.scriptella.server.core.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Test;

public class SimpleJobRunnerTest {

	@Test
	public void test() throws Exception {
		AbstractJobRunner<String> tst = new SimpleJobRunner<String>() {
			@Override
			protected String execute() {
				getExecutionCallback().setJobProgress(1);
				return "1";
			}
		};
		TestExecutionCallback callback = new TestExecutionCallback();
		tst.setExecutionCallback(callback);

		Assert.assertEquals("1", tst.call());
		Assert.assertEquals(JobExecutionCallback.CompletionStatus.SUCCESSFUL, callback.status);
		Assert.assertEquals(1.0, callback.progress, 0.00001);
	}

	@Test(timeout=100)
	public void testCancellationSleep() throws Exception {
		final AbstractJobRunner<String> tst = new SimpleJobRunner<String>() {
			@Override
			protected String execute() throws InterruptedException {
				getExecutionCallback().setJobProgress(.1);
				Thread.sleep(150);
				getExecutionCallback().setJobProgress(.2);
				System.out.println("ppp");
				return "1";
			}
		};
		TestExecutionCallback callback = new TestExecutionCallback();
		tst.setExecutionCallback(callback);
		final List<Throwable> errors = new ArrayList<Throwable>();
		Executors.newSingleThreadExecutor().execute(new Runnable() {

			@Override
			public void run() {
				try {
					tst.call();
				} catch (Exception e) {
					errors.add(e);
				}

			}
		});
		Thread.sleep(1); // simply wait for tst job to start
		tst.cancel();
		Thread.sleep(1); // simply wait for cancellation to end

		Assert.assertTrue("Interrupted exception should be propagated", errors.size() == 1
				&& errors.get(0) instanceof InterruptedException);
		Assert.assertEquals(0.1, callback.progress, 0.00001);
		Assert.assertEquals(JobExecutionCallback.CompletionStatus.CANCELLED, callback.status);
		Assert.assertEquals(1, errors.size());

	}
	
	@Test()
	public void testCancellationSync() throws Exception {
		final CountDownLatch signalStart = new CountDownLatch(1);
		final CountDownLatch signalEnd = new CountDownLatch(1);
		final AbstractJobRunner<String> tst = new SimpleJobRunner<String>() {
			@Override
			protected String execute() throws InterruptedException {
				getExecutionCallback().setJobProgress(.1);
				signalStart.await();
				for (int i=0;i<1000;i++) {
					getExecutionCallback().setJobProgress(i);
				}
				signalEnd.countDown();
				return "1";
			}
		};
		TestExecutionCallback callback = new TestExecutionCallback();
		tst.setExecutionCallback(callback);
		final List<Throwable> errors = new ArrayList<Throwable>();
		Executors.newSingleThreadExecutor().execute(new Runnable() {

			@Override
			public void run() {
				try {
					tst.call();
				} catch (Exception e) {
					errors.add(e);
				}

			}
		});
		signalStart.countDown();
		
		tst.cancel();
		signalEnd.await();
		
		Assert.assertTrue("Exception occurred while executing a job", errors.isEmpty());
		Assert.assertEquals(999, callback.progress, 0.00001);
		Assert.assertEquals(JobExecutionCallback.CompletionStatus.CANCELLED, callback.status);
		Assert.assertEquals(1, errors.size());
		//TODO Fix the test
	}


}
