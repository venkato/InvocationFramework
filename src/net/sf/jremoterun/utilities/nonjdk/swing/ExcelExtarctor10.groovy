package net.sf.jremoterun.utilities.nonjdk.swing

import com.steadystate.css.dom.CSSRuleListImpl
import com.steadystate.css.dom.CSSStyleDeclarationImpl
import com.steadystate.css.dom.CSSStyleRuleImpl
import com.steadystate.css.dom.CSSValueImpl
import com.steadystate.css.dom.Property
import com.steadystate.css.dom.RGBColorImpl
import com.steadystate.css.parser.CSSOMParser
import com.steadystate.css.parser.SACParserCSS3
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.TwoResult
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.w3c.css.sac.InputSource
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.css.CSSValue

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.util.logging.Logger

@CompileStatic
class ExcelExtarctor10 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static String convertHtmlTableToJiraFormat(boolean firstLineHeader) {
        String clipboard = getPureHtmlFromClipboard();
        // new Html(clipboard).dumpc()
        TwoResult<Elements, CSSStyleSheet> els8 = findTrElemsts(clipboard);
        List<List<String>> rows1 = convertHtmlTableToJira1(els8);
        //rows1.dumpc()
        String result = convertHtmlTableToJIra2(rows1, firstLineHeader)
        // result.dumpc()
        ClipboardUtils.setClipboardContent(result)
        return result
    }



// https://msdn.microsoft.com/ru-ru/library/windows/desktop/ms649015(v=vs.85).aspx
    static String getPureHtmlFromClipboard() {
        String string = ClipboardUtils.getHtmlFromClipboard()
        if (string == null || string.trim().length() == 0) {
            throw new Exception("no html in clipboard")
        }

        String res = getHtml(string);
        return res
    }

    static List<Integer> parseRawSpan(Elements els) {
        return els.collect {
            String s3 = it.attr('rowspan');
            if (s3 == null || s3.length() == 0) {
                return null;
            }
            return s3 as Integer
        }
    }


    static TwoResult<Elements, CSSStyleSheet> findTrElemsts(String s) {
        Document d = Jsoup.parse(s); ;
        Element el = d;
        Elements table = el.select("table")
        assert table.size() == 1
        Elements els = table.select("tr");
        if (els.size() == 0) {
            throw new Exception("not tr els in ${s}")
        }
        Element e2 = d.select("style").first() as Element
        assert e2 != null
        String data = e2.data()
        data = data.replace('<!--', '')
        data = data.replace('-->', '')
        InputSource source = new InputSource(new StringReader(data));
        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
        CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

        return new TwoResult<Elements, CSSStyleSheet>(els, sheet)

    }


    static String convertHtmlToJiraOneElement(Element el, CSSStyleSheet styles) {
        String elText = el.text()
        elText = removeNonAsciiSymbols(elText)
        String className = el.className()
        if (className == null || className.length() == 0) {
            return elText
        }
            CSSStyleRuleImpl style;
            className = '.' + className.trim()
//        log.info "className = ${className}"
            CSSRuleListImpl rules = styles.getCssRules() as CSSRuleListImpl;
            List<CSSRule> rules1 = rules.getRules()
            List<CSSStyleRuleImpl> rules2 = rules1.findAll { it instanceof CSSStyleRuleImpl } as List<CSSStyleRuleImpl>
            style = rules2.find { it.selectorText == className }
        try {


            if (style == null) {
                throw new Exception("style not found for ${className}")
//            return elText
            }
            CSSStyleDeclarationImpl style1 = style.getStyle() as CSSStyleDeclarationImpl


            String backGroudColor
            String color = style1.getColor()
            if (!isGoodColorSet(color)) {
                color = null
            }
            backGroudColor = style1.getBackground()
            if (!isGoodColorSet(backGroudColor)) {
                backGroudColor = null
            }
            if (backGroudColor != null || color != null) {
                Property property = style1.getProperties().findAll { it.name in ['background', 'color'] }.find {
                    it.toString().contains('rgb(')
                }
                if (property != null) {
                    CSSValueImpl value = property.value as CSSValueImpl
                    RGBColorImpl value3 = value.value as RGBColorImpl
                    BigInteger red = value3.red.cssText as BigInteger
                    BigInteger green = value3.green.cssText as BigInteger
                    BigInteger blue = value3.blue.cssText as BigInteger
                    BigInteger summ = red*256*256+green*256+blue as BigInteger
                    backGroudColor = summ.toString(16)
                    if(backGroudColor.length()<6){
                        backGroudColor = org.apache.commons.lang3.StringUtils.leftPad(backGroudColor,6,'0')
                    }
                    backGroudColor = '#'+backGroudColor
                    color =null

                }
            }
//        log.info "${backGroudColor} ${color}"
            if (color == null && backGroudColor == null) {
                return elText
            }

//        if (color != null && backGroudColor != null) {
//            throw new Exception("Both background and foreground colors set  ${backGroudColor} and ${color}")
//        }
            String collllor = color == null ? backGroudColor : color
            if(collllor.contains('rgb(')){
                throw new Exception("failed parse ${style}")
            }
            return "{color:${collllor}}${elText}{color}"
        }catch (Exception e){
            throw new Exception("failed parse ${el.className()} ${style}",e)
        }
    }

    static boolean isGoodColorSet(String color) {
        boolean good = color != null && color.length() > 1 && color != 'windowtext'
        return good
    }


    static List<List<String>> convertHtmlTableToJira1(TwoResult<Elements, CSSStyleSheet> els8) {
        int rowNum = 0;
        int columnSize
        //List<String> classToNum
        //Map<String,Integer> classToNum = [:]
        List<Integer> skiprow = []
        List<String> rowBefore = null
        List<List<String>> rows1 = (List) els8.first.collect { tr ->
            try {
                rowNum++;
                Elements children3 = tr.select("td");
                if (children3.size() == 0) {
                    //throw new Exception("no td els in ${tr}")
                    return null
                }
                List<String> row = children3.collect { convertHtmlToJiraOneElement(it, els8.second) };
                //row = row.collect { removeNonAsciiSymbols(it) }
                boolean hasData = row.find { it.length() > 0 }
                if (!hasData) {
                    return null
                }
                if (rowNum == 1) {
                    columnSize = row.size()
                    rowBefore = row
                    skiprow = parseRawSpan(children3)
                    return row;
                } else {
                    if (columnSize == row.size()) {
                        rowBefore = row
                        skiprow = parseRawSpan(children3)
                        return row;
                    } else {
                        if (children3.size() < columnSize) {
                            Element elWitColSpan = children3.find { it.attr('colspan') }
                            if (elWitColSpan != null) {
                                throw new Exception("Col span not supported at row ${rowNum} : ${elWitColSpan}")
                            }
                            try {
                                int realColumn = -1
                                int j = -1
                                skiprow = (List) skiprow.collect { k ->
                                    realColumn++
                                    if (k == null || k == 0) {
                                        j++
                                        List<Element> childern4 = (List) children3
                                        Element el = childern4.get(j)
                                        String valueBefore = rowBefore.get(realColumn)
                                        valueBefore += '\n' + removeNonAsciiSymbols(el.text())
                                        rowBefore.set(realColumn, valueBefore)
                                        return null
                                    } else {
                                        k--;
                                        return k;
                                    }

                                }
                            } catch (Exception e5) {
                                throw new Exception("failed parse row ${rowNum} : ${tr} ${e5}", e5)
                            }
                            return null
                        } else {
                            throw new Exception("column size mismatched for row = ${rowNum}, size = ${row.size()},  before =${columnSize}")
                        }
                        return null
                    }
                }
                throw new IllegalStateException("column size mismatched for row = ${rowNum}, size = ${row.size()},  before =${columnSize}")
            } catch (Throwable e) {
                throw new Exception("failed parse ${rowNum} ${e}", e);
            }

        }
        rows1 = rows1.findAll { it != null }
        return rows1;
    }

    static String removeNonAsciiSymbols(String s) {
        s = StringUtils.replaceChars(s, '\t[]', '')
//    s= s.replace('\t', '')
//    s= s.replace('[', '')
//    s= s.replace(']', '')
        s = s.trim()
        List<Character> row3 = (List) s.toCharArray().toList().collect { Character c ->
            char c2 = (char) c;
            byte v = (byte) c2
            if (v >= 32) {
                return c
            }
            return null
        }
        row3 = row3.findAll { it != null }
        StringBuilder sb = new StringBuilder()
        row3.each { sb.append(it) }
        return sb.toString().trim();
    }


    static String convertHtmlTableToJIra2(List<List<String>> rows1, boolean firstLineHeader) {
        boolean first3 = true;
        List<String> rows2 = rows1.collect {
            String separator = firstLineHeader && first3 ? '||' : '|';
            first3 = false;
//            separator += ' '
            return separator + it.join(' ' + separator) + ' ' + separator
        }
        return rows2.join('\n')
    }

    static String getHtml(String s) {
        def i = s.indexOf("<html");
        if (i == -1) {
            throw new Exception("Seems not MS content ${s}")
        }
        return s.substring(i);
    }


}
