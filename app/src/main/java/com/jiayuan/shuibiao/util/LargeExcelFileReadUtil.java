package com.jiayuan.shuibiao.util;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据量比较大(8万条以上)的excel文件解析，将excel文件解析为 行列坐标-值的形式存入map中，此方式速度快，内存耗损小 但只能读取excle文件
 * 提供处理单个sheet方法 processOneSheet(String  filename) 以及处理多个sheet方法 processAllSheets(String  filename)
 * 只需传入文件路径+文件名即可  调用处理方法结束后，只需 接收LargeExcelFileReadUtil.getRowContents()返回值即可获得解析后的数据
 */
public class LargeExcelFileReadUtil extends DefaultHandler {
    private LinkedHashMap<String, String> rowContents = new LinkedHashMap<String, String>();
    private SheetHandler sheetHandler;

    public LinkedHashMap<String, String> getRowContents() {
        return rowContents;
    }

    public void setRowContents(LinkedHashMap<String, String> rowContents) {
        this.rowContents = rowContents;
    }

    public SheetHandler getSheetHandler() {
        return sheetHandler;
    }

    public void setSheetHandler(SheetHandler sheetHandler) {
        this.sheetHandler = sheetHandler;
    } //处理一个sheet

    public void processOneSheet(InputStream is) throws Exception {
        InputStream sheet2 = null;
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(is);
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();
            XMLReader parser = fetchSheetParser(sst);
            sheet2 = r.getSheet("sheet1");
            InputSource sheetSource = new InputSource(sheet2);
            parser.parse(sheetSource);
            setRowContents(sheetHandler.getRowContents());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (pkg != null) {
                pkg.close();
            }
            if (sheet2 != null) {
                sheet2.close();
            }
        }
    }

    //处理多个sheet
    public void processAllSheets(InputStream is) throws Exception {
        OPCPackage pkg = null;
        InputStream sheet = null;
        try {
            pkg = OPCPackage.open(is);
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();
            XMLReader parser = fetchSheetParser(sst);
            Iterator<InputStream> sheets = r.getSheetsData();
            while (sheets.hasNext()) {
                System.out.println("Processing new sheet:\n");
                sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (pkg != null) {
                pkg.close();
            }
            if (sheet != null) {
                sheet.close();
            }
        }
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        setSheetHandler(new SheetHandler(sst));
        ContentHandler handler = (ContentHandler) sheetHandler;
        parser.setContentHandler(handler);
        return parser;
    }

    public void test() throws Exception {
        Long time = System.currentTimeMillis();
        LargeExcelFileReadUtil example = new LargeExcelFileReadUtil();
        File file = new File("C:/Users/Desktop/2018041310024112.xlsx");
        InputStream inputStream = new FileInputStream(file);
        example.processOneSheet(inputStream);
        Long endtime = System.currentTimeMillis();
        LinkedHashMap<String, String> map = example.getRowContents();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        int count = 0;
        String prePos = "";
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            String pos = entry.getKey();
            if (!pos.substring(1).equals(prePos)) {
                prePos = pos.substring(1);
                count++;
            }
            System.out.println(pos + ";" + entry.getValue());
        }
        System.out.println("解析数据" + count + "条;耗时" + (endtime - time) / 1000 + "秒");
    }
}