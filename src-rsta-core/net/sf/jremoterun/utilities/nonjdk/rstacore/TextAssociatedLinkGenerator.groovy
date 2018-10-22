package net.sf.jremoterun.utilities.nonjdk.rstacore

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.fife.ui.rsyntaxtextarea.LinkGenerator
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.util.logging.Logger;
import org.fife.ui.rsyntaxtextarea.Token;

@CompileStatic
abstract class TextAssociatedLinkGenerator implements LinkGenerator{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

   // public HashMap<String,LinkGeneratorResult> links = new HashMap<>()


    @Override
    LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea, int offs) {
        Token token = findToken(textArea, offs)
        int start = token.getOffset()
        int end = token.getEndOffset()
        int length = end - start
        if(length == 0){
            log.info "length is zero at off = ${offs}"
            return null
        }
        String text = textArea.getText(start,length)
        return isLinkAtOffset2(textArea,offs,text)
    }

    abstract LinkGeneratorResult isLinkAtOffset2(RSyntaxTextArea textArea3, int offs,String text)
//        text = text.trim()
//        LinkGeneratorResult generatorResult = links.get(text)
//        return generatorResult



    Token findToken( RSyntaxTextArea textArea2,int offset){
        int line = textArea2.getLineOfOffset(offset)
        Token token = textArea2.getTokenListForLine(line)
        return findTokenNext(token,offset)
    }

    Token findTokenNext(Token startToken,int offset){
        Token token1 = getNearestPaintableToken(startToken)
        if(token1==null){
            log.info "failed find token at offset ${offset}"
            return null
        }
        if(token1.containsPosition(offset)){
            return token1
        }
        return findTokenNext(startToken.getNextToken(),offset)
    }

    static Token getNearestPaintableToken(Token token){
        if(token==null){
            return null
        }
        if(token.isPaintable()){
            return token
        }
        Token nextToken = token.getNextToken()
        if(nextToken==null){
            return null
        }
//        if(nextToken.isPaintable()){
//            return nextToken
//        }
        return getNearestPaintableToken(nextToken)
    }
}
