import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBScrollPane;
import gherkin.formatter.model.Builder;
import org.apache.commons.lang.ObjectUtils;
import org.jetbrains.annotations.Nullable;
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
        DialogWrapper popPanel = new DialogWrapper(null) {
            {
                init();
            }
            @Nullable

            @Override
            protected JComponent createCenterPanel() {
                //generating the text to display
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
                JTextArea popLabel = new JTextArea(textToDisplay, 25, 50);
                popLabel.setEditable(false);
                JScrollPane scrollPane = new JBScrollPane(popLabel);
                scrollPane.setSize(new Dimension(500,500));
                return scrollPane;
            }

            @Override
            protected Action[] createActions() {
                Action helpAction = this.getHelpAction();
                Action[] var10000 = helpAction == this.myHelpAction && this.getHelpId() == null ? new Action[]{this.getOKAction()} : new Action[]{this.getOKAction(), helpAction};

                return var10000;
            }
        };
        popPanel.show();

    }

    private String getBestAnswer(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        //getting the question title
        Elements titles = doc.getElementsByAttributeValue("class", "question-hyperlink");
        Element  title = titles.first();
        //getting either the best answer or the first answer
        Elements answers = doc.getElementsByAttributeValue("class", "answer accepted-answer");
        if (answers.size() == 0)
        {
            answers = doc.getElementsByAttributeValue("class", "answer");

        }
        answers = answers.attr("class", "post-text");
        answers = answers.attr("itemprop", "text");
        Element answer = answers.first();
        Elements children = answer.children();
        answer = children.get(0);
        children = answer.children();
        answer = children.get(1);
        children = answer.children();
        answer = children.get(0);

        return title.text()+"\n\n"+answer.text();
    }


    //adds \n's to make the text fit the popup size (it forces you to scroll horizontally otherwise)
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
        String searchURL = "https://stackoverflow.com/search?q="+selectedText;
        return searchURL;
    }

    private String getQuestionURL(String searchURL) throws IOException {
        Document doc = Jsoup.connect(searchURL).get();
        Elements h3Tags = doc.select("h3");
        Element firstAnswer = h3Tags.select("a[href]").get(2);
        return "https://stackoverflow.com"+firstAnswer.attr("href");
    }
}
