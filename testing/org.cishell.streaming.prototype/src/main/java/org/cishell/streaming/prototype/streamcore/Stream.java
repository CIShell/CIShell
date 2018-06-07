package org.cishell.streaming.prototype.streamcore;

public interface Stream<T> {
	public T next();
	public void pause();
	public void unpause();
	public void stop();
	
	public int getCurrentEndpoint();	
	public T getValueAtTimestep(int timestep);
	public boolean isFinalEndpoint(int candidateEndpoint);
}
