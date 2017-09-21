package uk.ac.manchester.cs.jfact.visitors;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;

@SuppressWarnings("javadoc")
public abstract class DLExpressionVisitorExAdapter<A> implements DLExpressionVisitorEx<A> {
    @Override
    public A visit(ConceptTop expr) {
        return null;
    }

    @Override
    public A visit(ConceptBottom expr) {
        return null;
    }

    @Override
    public A visit(ConceptName expr) {
        return null;
    }

    @Override
    public A visit(ConceptNot expr) {
        return null;
    }

    @Override
    public A visit(ConceptAnd expr) {
        return null;
    }

    @Override
    public A visit(ConceptOr expr) {
        return null;
    }

    @Override
    public A visit(ConceptOneOf expr) {
        return null;
    }

    @Override
    public A visit(ConceptObjectSelf expr) {
        return null;
    }

    @Override
    public A visit(ConceptObjectValue expr) {
        return null;
    }

    @Override
    public A visit(ConceptObjectExists expr) {
        return null;
    }

    @Override
    public A visit(ConceptObjectForall expr) {
        return null;
    }

    @Override
    public A visit(ConceptObjectMinCardinality expr) {
        return null;
    }

    @Override
    public A visit(ConceptObjectMaxCardinality expr) {
        return null;
    }

    @Override
    public A visit(ConceptObjectExactCardinality expr) {
        return null;
    }

    @Override
    public A visit(ConceptDataValue expr) {
        return null;
    }

    @Override
    public A visit(ConceptDataExists expr) {
        return null;
    }

    @Override
    public A visit(ConceptDataForall expr) {
        return null;
    }

    @Override
    public A visit(ConceptDataMinCardinality expr) {
        return null;
    }

    @Override
    public A visit(ConceptDataMaxCardinality expr) {
        return null;
    }

    @Override
    public A visit(ConceptDataExactCardinality expr) {
        return null;
    }

    @Override
    public A visit(IndividualName expr) {
        return null;
    }

    @Override
    public A visit(ObjectRoleTop expr) {
        return null;
    }

    @Override
    public A visit(ObjectRoleBottom expr) {
        return null;
    }

    @Override
    public A visit(ObjectRoleName expr) {
        return null;
    }

    @Override
    public A visit(ObjectRoleInverse expr) {
        return null;
    }

    @Override
    public A visit(ObjectRoleChain expr) {
        return null;
    }

    @Override
    public A visit(ObjectRoleProjectionFrom expr) {
        return null;
    }

    @Override
    public A visit(ObjectRoleProjectionInto expr) {
        return null;
    }

    @Override
    public A visit(DataRoleTop expr) {
        return null;
    }

    @Override
    public A visit(DataRoleBottom expr) {
        return null;
    }

    @Override
    public A visit(DataRoleName expr) {
        return null;
    }

    @Override
    public A visit(DataTop expr) {
        return null;
    }

    @Override
    public A visit(DataBottom expr) {
        return null;
    }

    @Override
    public A visit(Datatype<?> expr) {
        return null;
    }

    @Override
    public A visit(Literal<?> expr) {
        return null;
    }

    @Override
    public A visit(DataNot expr) {
        return null;
    }

    @Override
    public A visit(DataAnd expr) {
        return null;
    }

    @Override
    public A visit(DataOr expr) {
        return null;
    }

    @Override
    public A visit(DataOneOf expr) {
        return null;
    }
}
