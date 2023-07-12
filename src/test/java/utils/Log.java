package utils;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;


import org.apache.log4j.Logger;


public class Log {
    public static Logger logger;

    @BeforeAll
    public static void setUp() {
        DOMConfigurator.configure("log4j.xml");
        logger = Logger.getLogger(Log.class.getName());

    }
}
