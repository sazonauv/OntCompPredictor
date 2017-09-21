package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import conformance.PortedFrom;

/** individual in a query */
@PortedFrom(file = "QR.h", name = "QRIndividual")
class QRIndividual extends QRiObject {
    /** original individual from Expression Manager */
    @PortedFrom(file = "QR.h", name = "Ind")
    IndividualName Ind;

    /** init c'tor */
    QRIndividual(IndividualName ind) {
        Ind = ind;
    }

    /** get the name */
    @PortedFrom(file = "QR.h", name = "getIndividual")
    IndividualName getIndividual() {
        return Ind;
    }
}
