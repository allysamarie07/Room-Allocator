/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TextFieldAutoSuggestion;

import java.awt.Color;
import java.awt.Font;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;

public class TextFieldSuggestion extends JTextField {

    private TextFieldSuggestionUI textUI;

    public TextFieldSuggestion() {
        textUI = new TextFieldSuggestionUI(this);
        setUI(textUI);
        setFont(new Font("DialogInput", Font.PLAIN, 18)); // Set font to Dubai 18 Plain
        setBackground(Color.WHITE); 
        setForeground(Color.BLACK); 
    }

    public void addItemSuggestion(String text) {
        textUI.getItems().add(text);
    }

    public void removeItemSuggestion(String text) {
        textUI.getItems().remove(text);
    }

    public void clearItemSuggestion() {
        textUI.getItems().clear();
    }

    public void setRound(int round) {
        textUI.setRound(round);
    }

    public int getRound() {
        return textUI.getRound();
    }

    public void setModel(DefaultComboBoxModel<String> buildingNameModel) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
