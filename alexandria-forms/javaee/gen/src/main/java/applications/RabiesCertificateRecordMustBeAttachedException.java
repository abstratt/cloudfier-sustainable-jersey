package applications;

public class RabiesCertificateRecordMustBeAttachedException extends alexandria_forms.ConstraintViolationException {
    private static final long serialVersionUID = 1L;
    public RabiesCertificateRecordMustBeAttachedException() {
        //TODO support for message bundles 
        super("Rabies certificate record must be attached");
    }
}
