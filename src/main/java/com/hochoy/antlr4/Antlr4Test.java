package com.hochoy.antlr4;


import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.apache.spark.sql.catalyst.parser.SqlBaseLexer;
import org.apache.spark.sql.catalyst.parser.SqlBaseParser;
import org.apache.spark.sql.catalyst.parser.UpperCaseCharStream;

import java.io.IOException;

public class Antlr4Test {

    public static void main(String[] args) throws IOException {


        String command = "select action , day, actionAttache ,count(1) from parquetTmpTable where appkey = '11111' and dan = '20200101' group by action,day,actionAttache order by action ";
        CodePointCharStream codePointCharStream = CharStreams.fromString(command);
        SqlBaseLexer lexer = new SqlBaseLexer(new UpperCaseCharStream(codePointCharStream));
        System.out.println(lexer);

        lexer.removeErrorListeners();
//        lexer.addErrorListener(ParseErrorListener);
//        lexer.legacy_setops_precedence_enbled = conf.setOpsPrecedenceEnforced
        TokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);

//        String sql= "Select 'abc' as a, `hahah` as c  From a aS table;";
//        ANTLRInputStream input = new ANTLRInputStream(sql);  //将输入转成antlr的input流
//        DslLexer lexer = new DslLexer(input);  //词法分析
//        CommonTokenStream tokens = new CommonTokenStream(lexer);  //转成token流
//        DslParser parser = new DslParser(tokens); // 语法分析
//        DslParser.StaContext tree = parser.sta();  //获取某一个规则树，这里获取的是最外层的规则，也可以通过sql()获取sql规则树......
//        System.out.println(tree.toStringTree(parser)); //打印规则数
    }
}
