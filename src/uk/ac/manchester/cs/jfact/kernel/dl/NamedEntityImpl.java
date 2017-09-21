package uk.ac.manchester.cs.jfact.kernel.dl;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import conformance.PortedFrom;

@PortedFrom(file = "tDLExpression.h", name = "TNamedEntity")
public abstract class NamedEntityImpl implements NamedEntity {
    /** name of the entity */
    protected String name;
    private NamedEntry entry;

    @Override
    public NamedEntry getEntry() {
        return entry;
    }

    @Override
    public void setEntry(NamedEntry e) {
        entry = e;
    }

    public NamedEntityImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + name + ")";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof NamedEntity) {
            return name.equals(((NamedEntity) obj).getName())
                    && obj.getClass().equals(this.getClass());
        }
        return false;
    }
}
