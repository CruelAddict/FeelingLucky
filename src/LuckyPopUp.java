import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LuckyPopUp extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        //creating output data
        String selectedText = e.getData(PlatformDataKeys.EDITOR).getSelectionModel().getSelectedText();
        String textToDisplay = "Nothing to see here :(";
        try {
            textToDisplay = generateSearchURL(selectedText);
            textToDisplay = getQuestionURL(textToDisplay);
            textToDisplay = getBestAnswer(textToDisplay);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        textToDisplay = beautify(textToDisplay);
        //setting the layout
        JPanel popPanel = new JPanel();
        JTextArea popLabel = new JTextArea(textToDisplay, 25, 50);
        JScrollPane scrollPane = new JScrollPane(popLabel);
        scrollPane.setSize(new Dimension(500,500));
        popPanel.add(scrollPane);

        //creating a popup
        JBPopup jbPopup = JBPopupFactory.getInstance().createComponentPopupBuilder(popPanel, popPanel)
                .createPopup();
        //jbPopup.setSize(new Dimension(500, 500));
        jbPopup.showInFocusCenter();
        jbPopup.moveToFitScreen();


    }

    private String getBestAnswer(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements answers = doc.getElementsByAttributeValue("class", "answer accepted-answer");
        if (answers.size() == 0)
        {
            answers = doc.getElementsByAttributeValue("class", "answer");

        }
        answers = answers.attr("class", "post-text");
        //answers = answers.attr("class", "answercell post-layout--right");
        answers = answers.attr("itemprop", "text");
        Element answer = answers.first();
        Elements children = answer.children();
        answer = children.get(0);
        children = answer.children();
        answer = children.get(1);
        children = answer.children();
        answer = children.get(0);

        return answer.text();
    }

    private String beautify(String text){
        int counter = 0;
        int lineLength = 40;
        for(int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == '\n'){
                counter = -1;
            }
            if ( text.charAt(i) == ' ' && counter >= lineLength){
                text = text.substring(0, i)+"\n"+text.substring(i+1, text.length());
                counter = -1;
            };
            counter++;
        }
        return text;
    }
    private String generateSearchURL(String selectedText){
        for(int i = 0; i < selectedText.length()-1; i++)
        {
            if (selectedText.charAt(i) == ' '){
                selectedText = selectedText.substring(0, i)+"+"+selectedText.substring(i+1, selectedText.length());
            };
            if (selectedText.charAt(i) == '\n'){
                selectedText = selectedText.substring(0, i)+"+"+selectedText.substring(i+1, selectedText.length());
            }
        }
        String searchURL = "https://ru.stackoverflow.com/search?q="+selectedText;
        return searchURL;
    }
    private String getQuestionURL(String searchURL) throws IOException {
        Document doc = Jsoup.connect(searchURL).get();
        Elements h3Tags = doc.select("h3");
        Element firstAnswer = h3Tags.select("a[href]").get(2);
        return "https://ru.stackoverflow.com"+firstAnswer.attr("href");
    }
}
