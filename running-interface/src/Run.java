import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Run {

    private static java.util.logging.Logger logger = LoggerCustom.getLogger(Run.class.getName());

    public static void main(String[] args) {
        var allServicesImplementations = ServiceLoader.load(MsgInterface.class).stream().map(Provider::get).toList();
        logger.info(() -> "Services.count() = %s".formatted(allServicesImplementations.size()));
        allServicesImplementations.forEach((msg) ->  logger.info(msg.getCurrentPluginMsg()) );
    }

    public static class LoggerCustom {

        private LoggerCustom() {
            throw new IllegalStateException("Class to be used on static method invokings");
        }

        private static final String LOG_FORMART_ENV = "LOG_FORMART";
        private static final String LOG_LEVEL_ENV = "LOG_LEVEL";

        public static java.util.logging.Logger getLogger(String name){
            var logger = java.util.logging.Logger.getLogger(name);

            var format = System.getenv(LOG_FORMART_ENV) == null ? "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s %n" :  System.getenv(LOG_FORMART_ENV);
            var level = System.getenv(LOG_LEVEL_ENV) == null ? "INFO" : System.getenv(LOG_LEVEL_ENV);

            System.setProperty("java.util.logging.SimpleFormatter.format",format);
            Stream.of(logger.getParent().getHandlers()).forEach(h -> h.setLevel(Level.parse(level)));
            logger.setLevel(Level.parse(level));

            logger.setFilter(logRecord-> !logRecord.getMessage().isBlank());
            return logger;

        }

      
    }
    
}
