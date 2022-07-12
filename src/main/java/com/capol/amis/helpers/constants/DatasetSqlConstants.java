package com.capol.amis.helpers.constants;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 19:56
 * desc: 数据集sql常数
 */
public class DatasetSqlConstants {

    public static final String SPACE = " ";
    public static final String BLANK = "";
    public static final String COMMA = ",";
    public static final String DOT = ".";

    public static final String SELECT = getDoubleBlankStr("SELECT");
    public static final String FROM = getDoubleBlankStr("FROM");
    public static final String WHERE = getDoubleBlankStr("WHERE");
    public static final String GROUP_BY = getDoubleBlankStr("GROUP BY");
    public static final String LEFT_JOIN = getDoubleBlankStr("LEFT JOIN");

    public static final String AS = getDoubleBlankStr("AS");
    public static final String IF = getDoubleBlankStr(" IF ");
    public static final String AND = getDoubleBlankStr("AND");
    public static final String OR = getDoubleBlankStr("OR");


    private static  String getDoubleBlankStr(String source) {
        return BLANK + source + BLANK;
    }




}
