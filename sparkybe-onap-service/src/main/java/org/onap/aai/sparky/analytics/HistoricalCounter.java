/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.sparky.analytics;

/**
 * A simple class to model a historical counter. A set of values will be tracked and basic
 * statistics will be calculated in real time (n, min, max, avg).
 * 
 * @author davea
 */
public class HistoricalCounter {

  private double min;

  private double max;

  private double totalOfSamples;

  private long numSamples;

  private double value;

  private boolean maintainSingleValue;

  /**
   * Instantiates a new historical counter.
   *
   * @param trackSingleValue the track single value
   */
  public HistoricalCounter(boolean trackSingleValue) {
    min = -1;
    max = 0;
    totalOfSamples = 0;
    value = 0.0;
    numSamples = 0;
    this.maintainSingleValue = trackSingleValue;
  }

  public boolean isSingleValue() {
    return maintainSingleValue;
  }

  /**
   * Update.
   *
   * @param value the value
   */
  public synchronized void update(double value) {

    if (value < 0) {
      return;
    }

    if (maintainSingleValue) {

      this.value = value;

    } else {

      if (Double.valueOf(min).equals(-1.0)) {
        min = value;
      }

      if (value < min) {
        min = value;
      }

      if (value > max) {
        max = value;
      }

      totalOfSamples += value;
      numSamples++;
    }
  }

  public double getValue() {
    return value;
  }

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  public long getNumSamples() {
    return numSamples;
  }

  public double getAvg() {
    if (numSamples == 0) {
      return 0;
    }

    return (totalOfSamples / numSamples);
  }
  public void setMin(double min) {
		this.min = min;
	}
  
  public void setMax(double max) {
		this.max = max;
	}
  
  public double getTotalOfSamples() {
		return totalOfSamples;
	}

	public void setTotalOfSamples(double totalOfSamples) {
		this.totalOfSamples = totalOfSamples;
	}

	public void setNumSamples(long numSamples) {
		this.numSamples = numSamples;
	}
	
	public void setMaintainSingleValue(boolean maintainSingleValue) {
		this.maintainSingleValue = maintainSingleValue;
	}


  /**
   * Reset.
   */
  public synchronized void reset() {
    min = -1;
    max = 0;
    numSamples = 0;
    totalOfSamples = 0;
    value = 0.0;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(32);

    if (maintainSingleValue) {
      sb.append("[ Val=").append(value).append(" ]");
    } else {
      sb.append("[ NumSamples=").append(numSamples).append(",");
      sb.append(" Min=").append(min).append(",");
      sb.append(" Max=").append(max).append(",");
      sb.append(" Avg=").append(getAvg()).append(" ]");
    }

    return sb.toString();
  }

}
