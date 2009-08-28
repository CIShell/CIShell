package org.cishell.streaming.prototype.streamcore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DefaultStream<T> implements Stream<T> {
	private static final long DEFAULT_SLEEP_TIME = 1000;
	
	private List<T> dataList =
		Collections.synchronizedList(new ArrayList<T>());
	private Thread streamingThread;

	private boolean paused;

	private boolean stopped;
	
	
	public DefaultStream() {
		this(DEFAULT_SLEEP_TIME);
	}
	
	public DefaultStream(final long sleepTime) {
		this.streamingThread = new Thread(new Runnable() {
			public void run() {
				while(!isStopped() && !isFinished()) {
					while (isPaused()) {
						// TODO Debug only
						System.out.println("Still paused!");
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {}
					}
					
					dataList.add(next());
					
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {}
				}
			}
		});
		
		streamingThread.start();
	}
	
	public void pause() {
		setPaused(true);
	}
	public void unpause() {
		setPaused(false);
	}
	public boolean isPaused() {
		return paused;
	}
	public void setPaused(boolean paused) {
		this.paused = paused;
	}	
	
	public void stop() {
		setStopped(true);
	}
	public boolean isStopped() {
		return stopped;
	}
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public abstract boolean isFinished();
	
	
	public int getCurrentEndpoint() {
		return dataList.size();
	}

	public T getValueAtTimestep(int timestep) {
		return dataList.get(timestep);
	}

	public boolean isFinalEndpoint(int candidateEndpoint) {
		return ((!streamingThread.isAlive())
				&& (candidateEndpoint == getCurrentEndpoint()));
	}
}
