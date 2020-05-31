package de.lukweb.discordbeam.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldLimit extends PlainDocument {

    private final int limit;

    public JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }

        if ((getLength() + str.length()) > limit) {
            int allowed = limit - getLength();
            String subString = str.substring(0, allowed);

            super.insertString(offs, subString, a);
            return;
        }

        super.insertString(offs, str, a);
    }
}
