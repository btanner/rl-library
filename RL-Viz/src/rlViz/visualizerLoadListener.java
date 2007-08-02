package rlViz;

import rlVizLib.visualization.AbstractVisualizer;

public interface visualizerLoadListener {
	
	public void notifyVisualizerLoaded(AbstractVisualizer theNewVisualizer);

	public void notifyVisualizerUnLoaded();

}
