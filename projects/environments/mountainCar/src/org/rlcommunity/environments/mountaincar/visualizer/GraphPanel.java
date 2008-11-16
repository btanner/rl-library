/*
 * Copyright 2008 Brian Tanner
 * http://bt-recordbook.googlecode.com/
 * brian@tannerpages.com
 * http://brian.tannerpages.com
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.rlcommunity.environments.mountaincar.visualizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import org.rlcommunity.rlglue.codec.types.Observation_action;
import org.rlcommunity.rlglue.codec.types.Reward_observation_action_terminal;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.VizComponentChangeListener;

/**
 * Brian Tanner is refactoring this useful piece of code from Sam Sarjant's Tetris Workshop.
 * 
 * @author Sam Sarjant
 * 
 */
// TODO Modify this code so the numbers on the axes are better visible.
public class GraphPanel implements SelfUpdatingVizComponent, Observer {
    private VizComponentChangeListener theChangeListener;
    private PerformanceTracker WT = new PerformanceTracker();
    private boolean somethingNew = false;

    public GraphPanel(MountainCarVisualizer parent) {
        this.parent_ = parent;
        WT.init();
        this.parent_.getTheGlueState().addObserver(this);
    }

    private int getWidth() {
        return 1000;
    }

    private int getHeight() {
        return 1000;
    }

    public void render(Graphics2D g2D) {
        somethingNew = false;
        AffineTransform saveAT = g2D.getTransform();
        g2D.scale(.001, .001);

        // Sorting out what type of graph is showing
        int steps = parent_.getSubdivSteps();
        if (graphType_ != 0) {
            steps = graphType_;
        }
        boolean showPerformances = (steps < 0) ? true : false;
        // Calculating the number of xSubdivisions in the graph
        int xSubdivisions = REDUCED_X_AXIS * (HORIZONTAL_GRAPH_SUBDIVISIONS * getWidth()) / getHeight();

        // If only showing total performance, just get the total performance
        Double[] values = null;
//		PerformanceData[] otherValues = null;
        if (showPerformances) {
            values = WT.getPerformanceValues(steps);
        //Brian commented out, shoudl fix.
//			otherValues = WT.getOtherPerformances();
        } else {
//			values = WT.getPerformanceValues();
            values = WT.getPerformanceValues(steps);
        }

        // If values is empty, use minimal axes and values.
        if ((values == null) || (values.length == 0)) {
            values = new Double[1];
            values[0] = 0.0;
        }

        // Finding the max/min values
        double maxVal = -Double.MAX_VALUE;
        double minVal = Double.MAX_VALUE;
        Double[] baseVals = values;
        double baseScale = values[0];
//		if (showPerformances) {
//			if (basePerformance_ >= 0) {
//				if (steps == -1)
//					baseVals = otherValues[basePerformance_].getData();
//				if (steps == -2)
//					baseVals = otherValues[basePerformance_].getEpisodeData();
//				baseScale = otherValues[basePerformance_].getScale();
//			}
//		}
        for (int i = (baseVals == values) ? 1 : 0; i < baseVals.length; i++) {
            double val = baseVals[i];
            if (val > maxVal) {
                maxVal = val;
            }
            if (val < minVal) {
                minVal = val;
            }
        }
        maxVal = Math.max(maxVal, 0);
        minVal = Math.min(minVal, 0);

        paintBackGround(g2D);
        // Paint the axes
        paintAxes(g2D, maxVal, minVal, xSubdivisions, steps);

        double scale = baseVals.length - 1;
        // Paint the other performances
//		if (showPerformances) {
//			// The number of xSubdivisions depends on what line is used as a
//			// base.
//			for (PerformanceData perf : otherValues) {
//				// The scale depends on what line is used as a base
//				Double[] data = null;
//
//				xSubdivisions = baseVals.length - 2;
//				if (steps == -2) {
//					data = perf.getEpisodeData();
//				}
//				if (steps == -1) {
//					data = perf.getData();
//					scale = perf.getScale() / baseScale;
//				}
//				
//
//				paintGraphLine(g2D, data, maxVal, minVal, xSubdivisions, steps,
//						perf.getColor(), scale);
//			}
//		}
        // The scale here depends on what view is being shown or what line is
        // the base scale.
        int valNum = baseVals.length - 1;
        if (steps == -1) {
            scale = values[0] / baseScale;
            xSubdivisions = baseVals.length - 2;
            valNum = (baseVals.length - 1) * (int) baseScale;
        }

        // Paint the base line
        paintGraphLine(g2D, values, maxVal, minVal, xSubdivisions, steps,
                LINE_COLOR, scale);

        paintText(g2D, maxVal, minVal, steps, valNum);

        g2D.setTransform(saveAT);
    }
    /** The number of horizontal graph subdivisions. */
    private static final int HORIZONTAL_GRAPH_SUBDIVISIONS = 4;
    /** The colour of the line. */
    private static final Color LINE_COLOR = Color.GREEN;
    /** The colour of the axes. */
    private static final Color AXES_COLOR = Color.DARK_GRAY;
    /** The colour of the wording. */
    private static final Color TEXT_COLOR = Color.WHITE;
    /** The value for centering the grid size text. */
    private static final int CENTERING_VAL = 25;
    /** The value of the text size. */
    private static final int TEXT_SIZE = 10;
    /** The y offset of the text. */
    private static final int TEXT_Y_OFFSET = 10;
    /** The factor that the x axis is relative to the y axis. */
    private static final int REDUCED_X_AXIS = 4;
    /** The parent of this JPanel. */
    private MountainCarVisualizer parent_;
    /** If this graph panel only shows the total performance. */
    private int graphType_ = 0;
    /** The performance line to base performance off. */
    private int basePerformance_;

    /**
     * The constructor which takes in the parent of this JPanel.
     * 
     * @param parent
     *            The container parent of this JPanel.
     */
//	public GraphPanel(MountainCarVisualizer parent) {
//		super();
//		parent_ = parent;
//		basePerformance_ = -1;
//	}
//
//	public GraphPanel(MountainCarVisualizer parent, int graphType) {
//		this(parent);
//		graphType_ = graphType;
//	}
    /**
     * Paints the component.
     */
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		Graphics2D g2D = (Graphics2D) g;
//
//		// Sorting out what type of graph is showing
//		int steps= parent_.getSubdivSteps();
//		if (graphType_ != 0)
//			steps = graphType_;
//		boolean showPerformances = (steps < 0) ? true : false;
//
//		// Calculating the number of xSubdivisions in the graph
//		int xSubdivisions = REDUCED_X_AXIS
//				* (HORIZONTAL_GRAPH_SUBDIVISIONS * this.getWidth())
//				/ this.getHeight();
//
//		// If only showing total performance, just get the total performance
//		Double[] values = null;
//		PerformanceData[] otherValues = null;
//		if (showPerformances) {
//			values = parent_.getPerformanceValues(steps);
//			otherValues = parent_.getOtherPerformances();
//		} else {
//			values = parent_.getPerformanceValues();
//		}
//
//		// If values is empty, use minimal axes and values.
//		if ((values == null) || (values.length == 0)) {
//			values = new Double[1];
//			values[0] = 0.0;
//		}
//
//		// Finding the max/min values
//		double maxVal = -Double.MAX_VALUE;
//		double minVal = Double.MAX_VALUE;
//		Double[] baseVals = values;
//		double baseScale = values[0];
//		if (showPerformances) {
//			if (basePerformance_ >= 0) {
//				if (steps == -1)
//					baseVals = otherValues[basePerformance_].getData();
//				if (steps == -2)
//					baseVals = otherValues[basePerformance_].getEpisodeData();
//				baseScale = otherValues[basePerformance_].getScale();
//			}
//		}
//		for (int i = (baseVals == values) ? 1 : 0; i < baseVals.length; i++) {
//			double val = baseVals[i];
//			if (val > maxVal)
//				maxVal = val;
//			if (val < minVal)
//				minVal = val;
//		}
//		maxVal = Math.max(maxVal, 0);
//		minVal = Math.min(minVal, 0);
//
//		// Paint the axes
//		paintAxes(g2D, maxVal, minVal, xSubdivisions, steps);
//
//		double scale = baseVals.length - 1;
//		// Paint the other performances
//		if (showPerformances) {
//			// The number of xSubdivisions depends on what line is used as a
//			// base.
//			for (PerformanceData perf : otherValues) {
//				// The scale depends on what line is used as a base
//				Double[] data = null;
//
//				xSubdivisions = baseVals.length - 2;
//				if (steps == -2) {
//					data = perf.getEpisodeData();
//				}
//				if (steps == -1) {
//					data = perf.getData();
//					scale = perf.getScale() / baseScale;
//				}
//				
//
//				paintGraphLine(g2D, data, maxVal, minVal, xSubdivisions, steps,
//						perf.getColor(), scale);
//			}
//		}
//		// The scale here depends on what view is being shown or what line is
//		// the base scale.
//		int valNum = baseVals.length - 1; 
//		if (steps == -1) {
//			scale = values[0] / baseScale;
//			xSubdivisions = baseVals.length - 2;
//			valNum = (baseVals.length - 1) * (int) baseScale;
//		}
//
//		// Paint the base line
//		paintGraphLine(g2D, values, maxVal, minVal, xSubdivisions, steps,
//				LINE_COLOR, scale);
//
//		paintText(g2D, maxVal, minVal, steps, valNum);
//	}
    /**
     * Paints the subdivision guide lines into the graph as well as axis number
     * labels.
     * 
     * @param g2d
     *            The graphics drawing class.
     * @param maxVal
     *            The maximum value in the graph.
     * @param minVal
     *            The minimum value in the graph.
     * @param subdivisions
     *            The number of subdivisions in the graph.
     */
    private void paintAxes(Graphics2D g2d, double maxVal, double minVal,
                           int subdivisions, int graphType) {
        g2d.setColor(AXES_COLOR);
        // Drawing the horizontal lines
        for (int y = 1; y < HORIZONTAL_GRAPH_SUBDIVISIONS; y++) {
            int yHeight = (int) (this.getHeight() * ((1.0 * y) / HORIZONTAL_GRAPH_SUBDIVISIONS));
            g2d.drawLine(0, yHeight, this.getWidth(), yHeight);
        }

        if (graphType >= 0) {
            // Drawing the vertical lines
            for (int x = REDUCED_X_AXIS; x < subdivisions; x += REDUCED_X_AXIS) {
                int xWidth = (int) (this.getWidth() * ((1.0 * x) / subdivisions));
                g2d.drawLine(xWidth, 0, xWidth, this.getHeight());
            }
        }
    }

    private void paintBackGround(Graphics2D g2d) {
        g2d.setColor(Color.black);
        g2d.fill(new Rectangle(0,0,getWidth(),getHeight()));
        
    }

    /**
     * Draws the text in on the graph
     * 
     * @param g2d
     *            The graphics device.
     * @param maxVal
     *            The maximum value.
     * @param minVal
     *            The minimum value.
     * @param isTotalPerformance
     *            If this is a total performance graph
     * @param steps
     *            The scale of the graph.
     */
    private void paintText(Graphics2D g2d, double maxVal, double minVal,
                           int steps, int valSize) {
        // Drawing in the text
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Serif", Font.PLAIN, TEXT_SIZE));
        g2d.drawString("" + maxVal, 1, TEXT_Y_OFFSET);
        g2d.drawString("" + minVal, 1, this.getHeight() - 1);

        String txt = "";
        switch (steps) {
            case -1:
                txt = "Steps: " + valSize;
                break;
            case -2:
                txt = "Episodes " + valSize;
                break;
            default:
                txt = "Grid: " + REDUCED_X_AXIS * steps + " steps";
        }
        g2d.drawString(txt, this.getWidth() / 2 - CENTERING_VAL, this.getHeight() - 1);
    }

    /**
     * Draws the data (values) in as a graph line, where the line will be
     * right-aligned if a full window of data isn't present. The line is drawn
     * in given the 3 dimensions of the graph (maxVal, minVal and
     * xSubdivisions). If the data is smaller than the number of xSubdivisions,
     * then the line starts mid-way through the window. If the data is larger
     * than the number of subdivisions, only the latest portion is drawn.
     * 
     * @param g2D
     *            The graphics drawing class.
     * @param values
     *            The data being drawn in.
     * @param maxVal
     *            The largest value in the data or 0 - whichever is larger.
     * @param minVal
     *            The smallest value in the data or 0 - whichever is smaller.
     * @param xSubdivisions
     *            The number of subdivisions along the x axis.
     * @param isTotalPerformance
     *            If the values are the total performance and should take up the
     *            entire graph, regardless of size.
     * @param color
     *            The colour of the line being drawn.
     * @param scale
     *            The number of steps between values in the array. Also used in
     *            the episode view as the number of values to be shown
     */
    private void paintGraphLine(Graphics2D g2D, Double[] values, double maxVal,
                                double minVal, int xSubdivisions, int steps, Color color,
                                double scale) {
        g2D.setColor(color);

        // TODO Change episode graph to be like the total graph.
        // Calculating which parts of the graph to draw and where
        int xStart = 0;
        int indexStart = 0;
        if (steps >= 0) {
            scale = 1;
            if (values.length <= xSubdivisions + 1) {
                xStart = xSubdivisions + 1 - values.length;
            } else {
                indexStart = values.length - xSubdivisions - 1;
            }
        } else {
            if (steps == -2) {
                xSubdivisions = (int) scale;
                scale = 1;
            }
            if (values.length <= 1) {
                return;
            }
            if (values[0] != 0) {
                indexStart = 1;
            }
        }

        try {
            // Calculating the start positions
            int xStartPos = (int) (getWidth() * (1.0 * xStart) / xSubdivisions);
            int yStartPos = getHeight() - (int) (getHeight() * (values[indexStart] - minVal) / (maxVal - minVal));
            // Moving the start position slightly so that it is visible.
            if (yStartPos == 0) {
                yStartPos += 2;
            }
            if (yStartPos == getHeight()) {
                yStartPos -= 2;            // While there are subdivisions left and there are indices left
            }
            double sub = xStart;
            while ((sub < xSubdivisions) && (indexStart < values.length - 1)) {
                int xEndPos = (int) (getWidth() * ((1.0 * (sub + scale)) / xSubdivisions));
                int yEndPos = getHeight() - (int) (getHeight() * (values[indexStart + 1] - minVal) / (maxVal - minVal));
                // Moving the end position slightly so that it is visible.
                if (yEndPos == 0) {
                    yEndPos += 2;
                }
                if (yEndPos == getHeight()) {
                    yEndPos -= 2;
                }
                g2D.drawLine(xStartPos, yStartPos, xEndPos, yEndPos);

                // Move the positions along.
                xStartPos = xEndPos;
                yStartPos = yEndPos;
                indexStart++;
                sub += scale;
            }
        } catch (Exception e) {
            System.err.println("----------Graph mess-up.-----------");
            e.printStackTrace();
        }
    }

    /**
     * Sets the total performance scale of the graph.
     * 
     * @param val
     *            The scale value. If -1, uses the current performance. If
     *            greater, uses that as an index for otherPerformances.
     */
    public void setBasePerformance(int val) {
        basePerformance_ = val;
    }

    public void setGraphType(int graphType) {
        graphType_ = graphType;
    }

    public void update(Observable o, Object theEvent) {
        if (theEvent instanceof Observation_action) {
            WT.start((Observation_action) theEvent);
        }
        if (theEvent instanceof Reward_observation_action_terminal) {
            WT.step((Reward_observation_action_terminal) theEvent);
        }
        if(theChangeListener!=null){
            theChangeListener.vizComponentChanged(this);
        }
    }

    public void setVizComponentChangeListener(VizComponentChangeListener theChangeListener) {
        this.theChangeListener=theChangeListener;
    }
}


