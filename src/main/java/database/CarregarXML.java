package database;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CarregarXML {

    public CarregarXML(){

    }
    public List<List<String>> XML(Context context,String xml,String no) throws IOException, ParserConfigurationException, SAXException {
        final AssetManager manager = context.getAssets();
        final InputStream is = manager.open(xml);
        final List<List<String>> listPai = new ArrayList<>();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = factory.newDocumentBuilder();
        final InputSource inputSource = new InputSource(is);
        final Document document = db.parse(inputSource);
        final NodeList nodeList = document.getElementsByTagName(no);
        final int nodeListLength = nodeList.getLength();
        for (int i = 0; i < nodeListLength; i++) {
            List<String> listFilho = new ArrayList<>();
            final Node nos = nodeList.item(i);
            final NodeList filhos = nos.getChildNodes();
            final int filhosLength = filhos.getLength();
            for (int j = 0; j < filhosLength; j++) {
                if (filhos.item(j).getTextContent().charAt(0)!='\n')
                listFilho.add(filhos.item(j).getTextContent());
            }
            listPai.add(listFilho);

        }
        return listPai;
    }
}
