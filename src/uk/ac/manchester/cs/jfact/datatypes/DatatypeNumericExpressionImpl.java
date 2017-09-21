package uk.ac.manchester.cs.jfact.datatypes;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.ABSTRACT_NUMERIC_DATATYPE;

class DatatypeNumericExpressionImpl<O extends Comparable<O>> extends
        ABSTRACT_NUMERIC_DATATYPE<O> implements DatatypeExpression<O> {
    // TODO handle all value space restrictions in the delegations
    private final Datatype<O> host;

    public DatatypeNumericExpressionImpl(Datatype<O> b) {
        super(b.getDatatypeURI() + "_" + DatatypeFactory.getIndex(), b.getFacets());
        if (b.isExpression()) {
            this.host = b.asExpression().getHostType();
        } else {
            this.host = b;
        }
        ancestors = Utils.generateAncestors(this.host);
        knownNumericFacetValues.putAll(b.getKnownNumericFacetValues());
        knownNonNumericFacetValues.putAll(b.getKnownNonNumericFacetValues());
    }

    @Override
    public O parseValue(String s) {
        return this.host.parseValue(s);
    }

    @Override
    public Datatype<O> getHostType() {
        return this.host;
    }

    @Override
    public DatatypeExpression<O> addNumericFacet(Facet f,
 Comparable value) {
        if (!facets.contains(f)) {
            throw new IllegalArgumentException("Facet " + f
                    + " not allowed tor datatype " + this.getHostType());
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (value instanceof Literal && !this.host.isCompatible((Literal<?>) value)) {
            throw new IllegalArgumentException("Not a valid value for this expression: "
                    + f + "\t" + value + " for: " + this);
        }
        DatatypeNumericExpressionImpl<O> toReturn = new DatatypeNumericExpressionImpl<O>(
                this.host);
        toReturn.knownNumericFacetValues.putAll(knownNumericFacetValues);
        toReturn.knownNonNumericFacetValues.putAll(knownNonNumericFacetValues);
        // cannot have noth min/maxInclusive and min/maxExclusive values, so
        // remove them if the feature is min/max
        if (f.equals(Facets.minExclusive) || f.equals(Facets.minInclusive)) {
            toReturn.knownNumericFacetValues.remove(Facets.minExclusive);
            toReturn.knownNumericFacetValues.remove(Facets.minInclusive);
        }
        if (f.equals(Facets.maxExclusive) || f.equals(Facets.maxInclusive)) {
            toReturn.knownNumericFacetValues.remove(Facets.maxExclusive);
            toReturn.knownNumericFacetValues.remove(Facets.maxInclusive);
        }
        toReturn.knownNumericFacetValues.put(f, value);
        return toReturn;
    }

    @Override
    public DatatypeExpression<O> addNonNumericFacet(Facet f, Comparable value) {
        if (!facets.contains(f)) {
            throw new IllegalArgumentException("Facet " + f
                    + " not allowed tor datatype " + this.getHostType());
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (value instanceof Literal && !this.host.isCompatible((Literal<?>) value)) {
            throw new IllegalArgumentException("Not a valid value for this expression: "
                    + f + "\t" + value + " for: " + this);
        }
        DatatypeNumericExpressionImpl<O> toReturn = new DatatypeNumericExpressionImpl<O>(
                this.host);
        toReturn.knownNumericFacetValues.putAll(knownNumericFacetValues);
        toReturn.knownNonNumericFacetValues.putAll(knownNonNumericFacetValues);
        toReturn.knownNonNumericFacetValues.put(f, value);
        return toReturn;
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public boolean emptyValueSpace() {
        // TODO not checking string type value spaces; looks like the only
        // sensible way would be to check for 0 length constraints
        if (getNumeric()) {
            // remember whether it's inclusive or exclusive - needed to know if
            // the two extremes can be the same or not
            int excluded = 0;
            Comparable min = getNumericFacetValue(Facets.minInclusive);
            if (min == null) {
                min = getNumericFacetValue(Facets.minExclusive);
                excluded++;
            }
            Comparable max = getNumericFacetValue(Facets.maxInclusive);
            if (max == null) {
                max = getNumericFacetValue(Facets.maxExclusive);
                excluded++;
            }
            return !DatatypeFactory.nonEmptyInterval(min, max, excluded);
        }
        return false;
    }

    @Override
    public boolean isNumericDatatype() {
        return this.host.isNumericDatatype();
    }

    @Override
    public NumericDatatype<O> asNumericDatatype() {
        return this;
    }

    @Override
    public boolean isOrderedDatatype() {
        return this.host.isOrderedDatatype();
    }

    @Override
    public OrderedDatatype<O> asOrderedDatatype() {
        return this;
    }

    @Override
    public String toString() {
        return "numeric(" + this.host.toString() + "(extra facets:" + getMin() + " "
                + getMax() + "))";
    }
}
