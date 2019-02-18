package zg.dove.utils;


import org.apache.commons.cli.*;

/*
 * 命令行参数解析
 */
public final class CliParse {
    private Options options = new Options();
    private CommandLine cmdline;

    /*
     * 添加一个参数选项
     *
     * @param shortOpt    短标记
     * @param longOpt     长标记
     * @param hasArg
     * @param description 意义描述
     * @return
     */
    public CliParse addOption(String shortOpt, String longOpt, boolean hasArg, String description) {
        options.addOption(shortOpt, longOpt, hasArg, description);
        return this;
    }

    /*
     * 解析命令行参数
     *
     * @param arguments 命令行参数
     * @return
     */
    public CliParse parse(String[] arguments) {
        CommandLineParser parser = new DefaultParser();
        try {
            cmdline = parser.parse(options, arguments);
        } catch (ParseException e) {
            printHelpAndExit();
            throw new RuntimeException(e);
        }

        return this;
    }

    public boolean hasOption(String key) {
        return cmdline.hasOption(key);
    }

    public String getOptionValue(String key) {
        if (!cmdline.hasOption(key)) {
            return "";
        }

        return cmdline.getOptionValue(key);
    }

    public void printHelpAndExit() {
        HelpFormatter hf = new HelpFormatter();
        //TODO:不是期望的输出
        hf.printHelp("java -cp ${CLASSPATH} ${Gateway} [Options]", options);
    }
}

