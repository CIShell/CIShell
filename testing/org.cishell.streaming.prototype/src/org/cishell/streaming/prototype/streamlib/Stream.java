package org.cishell.streaming.prototype.streamlib;

public interface Stream<T> {

	public abstract int getCurrentEndpoint();

	public abstract T getValueAtTimestep(int timestep);

	public abstract boolean isFinalEndpoint(int candidateEndpoint);

}
