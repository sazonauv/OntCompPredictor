package uk.ac.manchester.cs.jfact.kernel;

import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
@PortedFrom(file = "mergableLabel.h", name = "mergableLabel")
public class MergableLabel {
    /** sample for all equivalent labels */
    @PortedFrom(file = "mergableLabel.h", name = "pSample")
    private MergableLabel pSample;

    public MergableLabel() {
        pSample = this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof MergableLabel) {
            MergableLabel p = (MergableLabel) obj;
            return pSample.equals(p.pSample);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (pSample != null) {
            return pSample.hashCode();
        }
        return super.hashCode();
    }

    /** make 2 labels equal */
    @PortedFrom(file = "mergableLabel.h", name = "merge")
    public void merge(MergableLabel p) {
        MergableLabel sample = p.resolve();
        resolve();
        if (pSample != sample) {
            pSample.pSample = sample;
        }
    }

    /** make label's depth <= 2; @return sample of the label */
    @PortedFrom(file = "mergableLabel.h", name = "resolve")
    public MergableLabel resolve() {
        // check if current node is itself sample
        if (!isSample()) {
            pSample = pSample.resolve();
        }
        return pSample;
    }

    /** is given lable a sample label */
    @PortedFrom(file = "mergableLabel.h", name = "isSample")
    public boolean isSample() {
        return pSample.equals(this);
    }
}
