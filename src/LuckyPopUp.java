import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import javafx.scene.control.SelectionModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import sun.jvm.hotspot.ui.Editor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LuckyPopUp extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        //setting the layout
        JPanel popPanel = new JPanel();
        String selectedText = e.getData(PlatformDataKeys.EDITOR).getSelectionModel().getSelectedText();
        String textToDisplay = "Nothing to see here :(";
        try {
            textToDisplay = getWebData(selectedText);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        JTextArea popLabel = new JTextArea(textToDisplay, 25, 50);
        JScrollPane scrollPane = new JScrollPane(popLabel);
        scrollPane.setSize(new Dimension(500,500));
        popPanel.add(scrollPane);

        //creating a popup
        JBPopup jbPopup = JBPopupFactory.getInstance().createComponentPopupBuilder(popPanel, popPanel)
                .createPopup();
        jbPopup.setSize(new Dimension(500, 500));
        jbPopup.showInFocusCenter();
        jbPopup.moveToFitScreen();


    }

    public String getWebData(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements answers = doc.getElementsByAttributeValue("class", "answer accepted-answer");

        return answers.text();
    }
}
