package example.java.actor;

import com.thesong.antlr.ListenerRewrite;
import com.thesong.antlr.learnAntlrLexer;
import com.thesong.antlr.learnAntlrParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @Author thesong
 * @Date 2020/11/13 13:48
 * @Version 1.0
 * @Describe
 */
public class Test {
    public static void main(String[] args) throws IOException {
        final ANTLRInputStream antlrInputStream = new ANTLRInputStream("hello world");
        learnAntlrLexer learnantlrexer = new learnAntlrLexer(antlrInputStream);
        final CommonTokenStream tokenStream = new CommonTokenStream(learnantlrexer);
        final learnAntlrParser learnantlrParser = new learnAntlrParser(tokenStream);
        final learnAntlrParser.RContext rContext = learnantlrParser.r();
        ListenerRewrite listenerRewrite = new ListenerRewrite();
        ParseTreeWalker.DEFAULT.walk(listenerRewrite, rContext);


    }
}
