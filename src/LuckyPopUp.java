import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import javafx.scene.control.SelectionModel;
import sun.jvm.hotspot.ui.Editor;

import javax.swing.*;
import java.awt.*;

public class LuckyPopUp extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        //setting the layout
        JPanel popPanel = new JPanel();
        String selectedText = e.getData(PlatformDataKeys.EDITOR).getSelectionModel().getSelectedText();
        JTextArea popLabel = new JTextArea(selectedText);
        popPanel.add(popLabel);

        //creating a popup
        JBPopup jbPopup = JBPopupFactory.getInstance().createComponentPopupBuilder(popPanel, popPanel)
                .createPopup();
        jbPopup.setSize(new Dimension(500, 500));
        jbPopup.showInFocusCenter();

    }
}
