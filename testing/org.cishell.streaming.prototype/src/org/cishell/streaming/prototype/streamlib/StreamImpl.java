package org.cishell.streaming.prototype.streamlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class StreamImpl<T> implements Stream<T> {

	private static final long DEFAULT_SLEEP_TIME = 1000;
	
	private List<T> streamingList = Collections.synchronizedList(new ArrayList<T>());
	private Thread streamingThread;
	
	public StreamImpl() {
		this(DEFAULT_SLEEP_TIME);
	}
	
	public StreamImpl(final long sleepTime) {
		this.streamingThread = new Thread(new Runnable(){
			public void run() {
				while(! StreamImpl.this.isFinished()) {
				StreamImpl.this.streamingList.add(StreamImpl.this.yield());
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {}
				}
			}
		});
		
		streamingThread.start();
	}
	
	
	public abstract boolean isFinished();
	public abstract T yield();
	
	/* (non-Javadoc)
	 * @see org.cishell.streaming.prototype.streamlib.Streamo#getCurrentEndpoint()
	 */
	public int getCurrentEndpoint() {
		return streamingList.size();
	}

	/* (non-Javadoc)
	 * @see org.cishell.streaming.prototype.streamlib.Streamo#getValueAtTimestep(int)
	 */
	public T getValueAtTimestep(int timestep) {
		return streamingList.get(timestep);
	}

	/* (non-Javadoc)
	 * @see org.cishell.streaming.prototype.streamlib.Streamo#isFinalEndpoint(int)
	 */
	public boolean isFinalEndpoint(int candidateEndpoint) {
		return !streamingThread.isAlive() && candidateEndpoint == getCurrentEndpoint();
	}
}
