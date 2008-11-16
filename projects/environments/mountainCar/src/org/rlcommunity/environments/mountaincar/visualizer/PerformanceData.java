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
import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing data gleaned from a performance file and displaying it on
 * the graph.
 * 
 * @author Sam Sarjant
 */
public class PerformanceData {
	/** The data of the agent at a particular scale. */
	private Double[] data_;

	/** The scale of the data (the number of steps between indices). */
	private int scale_;

	/** The name given to the agent that created this data. */
	private String agentName_;

	/** The colour of the line on the graph. */
	private Color colour_;

	/** The reward/episode data. */
	private Double[] episodeData_;

	public PerformanceData(String agentName, int scale, Double[] data,
			Double[] episodeData, Color colour) {
		agentName_ = agentName;
		scale_ = scale;
		data_ = data;
		episodeData_ = episodeData;
		colour_ = colour;
	}

	/**
	 * Averages the performance of several performance data files into a single
	 * performance data object. If the input data is of different scales, then
	 * the outcome is unpredictable.
	 * 
	 * @param datas
	 *            The data being averaged. They must be of the same scale.
	 * @return A PerformanceData object being the average of a group of
	 *         performances. Note that the episode data is not averaged.
	 */
	public static PerformanceData averagePerformances(PerformanceData[] datas,
			Color colour) {
		Double[] avData = new Double[datas[0].data_.length];
		for (int i = 0; i < datas[0].data_.length; i++) {
			avData[i] = 0.0;
			for (PerformanceData data : datas) {
				// If for some reason the data is messed up, throw an exception
				if (i < data.getData().length) {
					avData[i] += data.getData()[i];
				} else {
					System.err.println("Error averaging performances.");
					for (PerformanceData errorData : datas)
						System.err.println(errorData);
					avData[i] += data.getData()[data.getData().length - 1];
				}
			}
			avData[i] /= datas.length;
		}
		PerformanceData average = new PerformanceData(datas[0].getAgentName(),
				datas[0].getScale(), avData, new Double[0], colour);

		return average;
	}

	/**
	 * Concatenates the performances of several performance data files into a
	 * single performance data object. This means that the first performance
	 * will be as per usual, but each following one will add the final
	 * performance value of the last.
	 * 
	 * @param datas
	 *            The datas being concatenated
	 * @param col
	 *            The colour of the performance data
	 * @return A concatenated PerformanceData object showing all performances.
	 */
	public static PerformanceData concatenatePerformances(
			PerformanceData[] datas, Color colour) {
		List<Double> concatData = new ArrayList<Double>();
		// For every data set
		double startVal = 0;
		for (int d = 0; d < datas.length; d++) {
			// For all elements of data in the dataset
			int i = 0;
			for (i = 0; i < datas[d].data_.length; i++) {
				// Check that the scale is equal
				if (datas[d].scale_ == datas[0].scale_) {
					concatData.add(datas[d].getData()[i] + startVal);
				} else {
					System.err.println("Error concatenating performances.");
					for (PerformanceData errorData : datas)
						System.err.println(errorData);
				}
			}
			startVal += datas[d].getData()[i - 1];
		}

		PerformanceData average = new PerformanceData(datas[0].getAgentName(),
				datas[0].getScale(), concatData.toArray(new Double[concatData
						.size()]), new Double[0], colour);

		return average;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(agentName_ + " performance data: ");
		buffer.append("Scale: " + scale_);
		buffer.append(", Data (" + data_.length + "): ");
		for (Double item : data_) {
			buffer.append(item + ",");
		}
		buffer.append(" Episode Data (" + episodeData_.length + "): ");
		for (Double item : episodeData_) {
			buffer.append(item + ",");
		}
		return buffer.toString();
	}

	public Double[] getData() {
		return data_;
	}

	public int getScale() {
		return scale_;
	}

	public String getAgentName() {
		return agentName_;
	}

	public Color getColor() {
		return colour_;
	}

	public Double[] getEpisodeData() {
		return episodeData_;
	}
}
