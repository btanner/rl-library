package org.rlcommunity.environments.NonMarkovSplit.visualizer;


import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VizComponent;
import rlVizLib.visualization.GenericScoreComponent;
import rlVizLib.visualization.interfaces.GlueStateProvider;

public class NonMarkovSplitVisualizer extends AbstractVisualizer implements GlueStateProvider {

    private TinyGlue theGlueState = null;

    /**
     * Creates a new Cart Pile Visualizer
     * @param theGlueState Global glue state object
     */
    public NonMarkovSplitVisualizer(TinyGlue theGlueState) {
        super();
        this.theGlueState = theGlueState;

        VizComponent scoreComponent = new GenericScoreComponent(this);
        super.addVizComponentAtPositionWithSize(scoreComponent, 0, 0, 1.0, 1.0);
    }


    public TinyGlue getTheGlueState() {
        return theGlueState;
    }

    @Override
    public String getName() {
        return "v 1.0";
    }
}
