package QcArtifact.Qctool;


    public class ConversionException extends HandleException {

        public ConversionException(String message) {
            super(message);
        }

        public ConversionException(String message, Throwable cause) {
            super(message, cause);
        }

        public ConversionException(Throwable cause) {
            super("Issue with aspose conversion: ", cause);
        }
    }

