package zg.dove.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import zg.dove.utils.Assert;
import zg.dove.utils.CliParse;
import zg.dove.utils.ClzParse;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class Launch {
    public static final Logger logger = LogManager.getLogger(Launch.class);

    public static void main(String[] args) {
        try {
            // 命令行参数解析
            CliParse cli = new CliParse()
                    .addOption("c", "cfg", true, "config json file location")
                    .addOption("l", "log", true, "log4j2 file location")
                    .parse(args);
            // 初始化日志
            Launch.initLog(cli.getOptionValue("log"));
            // 开始服务器
            Launch.initStart(cli.getOptionValue("cfg").split(","));
            logger.info("server start success.");
        } catch (Throwable e) {
            logger.error("uncatch exception:", e);
        }
    }

    private static void initLog(String logFileProt) {
        if (logFileProt == null || logFileProt.isEmpty()) {
            logger.info("no log file, use default.");
            return;
        }

        logger.info("reconfig log [{}]", logFileProt);
        // 日志文件配置
        File logFile = new File(logFileProt);
        URL url;
        try {
            url = logFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        //改变系统参数
        System.setProperty("log4j.configurationFile", url.toString());
        //重新初始化Log4j2的配置上下文
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.reconfigure();
        logger.info("reconfig log [{}] success", logFileProt);
    }

    private static void initStart(String[] cfgs) throws Exception {
        for (String cfg : cfgs) {
            logger.debug("begin load config {}", cfg);
            String path = Launch.class.getClassLoader().getResource(cfg).getPath();
            Properties properties = new Properties();
            properties.load(new FileInputStream(path));

            Assert.verify(properties.getProperty("common.launch.starter") != null);
            Assert.verify(properties.getProperty("common.package") != null);


            Class<?> clz;
            Starter starter;

            String[] clznames = ClzParse.findClassesByPackage(properties.getProperty("common.package"),
                    ClzParse.EMPTY_LIST, ClzParse.EMPTY_LIST);
            for (String clzname : clznames) {
                clz = Class.forName(clzname);
                if (clz.getSuperclass() != Starter.class) {
                    continue;
                }
                starter = (Starter)clz.getDeclaredConstructor().newInstance();
                starter.start(properties);
            }

            //映射启动
            clz = Class.forName(properties.getProperty("common.launch.starter"));
            starter = (Starter)clz.getDeclaredConstructor().newInstance();
            starter.start(properties);
        }
    }
}
