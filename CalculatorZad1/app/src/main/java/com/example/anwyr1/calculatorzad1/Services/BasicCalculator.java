package com.example.anwyr1.calculatorzad1.Services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Queue;

import com.example.anwyr1.calculatorzad1.Interfaces.IRPNSCharacter;

import static com.example.anwyr1.calculatorzad1.Services.MathematicalNamesUtils.*;

public class BasicCalculator {
    final static String ERROR_ALERT_TITLE = "Error";
    final static String ERROR_ALERT_DEFAULT_CONTENT = "Faulty operation requested";
    private final static String EMPTY = "";
    private final static String WRONG_FORMAT_ALERT_MESSAGE = "Wrong format";
    final TextView textView;
    boolean resultPrinted;
    private Activity activity;
    private ReversePolishNotationConverter reversePolishNotationConverter;
    private ReversePolishNotationCounter reversePolishNotationCounter;

    public BasicCalculator(View v, Activity activity) {
        textView = (TextView) v;
        resultPrinted = false;
        this.activity = activity;
        reversePolishNotationConverter = new ReversePolishNotationConverter();
        reversePolishNotationCounter = new ReversePolishNotationCounter();
    }

    public void handleNumber(String inputted) {
        if (resultPrinted) {
            clearInput();
            resultPrinted = false;
        }
        textView.append(inputted);
    }

    public void clearInput() {
        resultPrinted = false;
        ReversePolishNotationCounter.lastOperator = null;
        ReversePolishNotationCounter.lastNumber = 0;
        textView.setText(EMPTY);
    }

    public void handleDotInput() {
        String[] inputs = getInputTextSplit();
        if (inputs == null) {
            showAlert(ERROR_ALERT_TITLE, ERROR_ALERT_DEFAULT_CONTENT);
            return;
        }
        String lastNumber = inputs[inputs.length - 1];
        if (!lastNumber.contains(DECIMAL_SEPARATOR)) {
            if (Character.isDigit(lastNumber.charAt(lastNumber.length() - 1)))
                textView.append(DECIMAL_SEPARATOR);
            else
                showAlert(ERROR_ALERT_TITLE, ERROR_ALERT_DEFAULT_CONTENT);
        } else {
            showAlert(ERROR_ALERT_TITLE, ERROR_ALERT_DEFAULT_CONTENT);
        }
    }

    private String[] getInputTextSplit() {
        String inputted = textView.getText().toString();
        final String isOperatorRegex = "[^0-9.%]";
        if(inputted.length() > 0) {
            return inputted.split(isOperatorRegex);
        }
        return null;
    }

    public void summarize() {
        if (textView.getText().length() == 0) {
            showAlert(ERROR_ALERT_TITLE, ERROR_ALERT_DEFAULT_CONTENT);
            return;
        } else if (textView.getText().charAt(0) == '+') {
            textView.setText(textView.getText().toString().substring(1));
        }

        if (resultPrinted && ReversePolishNotationCounter.lastOperator != null) {
            String inputted = textView.getText().toString();
            inputted += ReversePolishNotationCounter.lastOperator;
            inputted += ReversePolishNotationCounter.lastNumber;
            textView.setText(inputted);
        }

        reversePolishNotationConverter.setInput(textView.getText().toString());
        reversePolishNotationConverter.convertToReversePolishNotationSequence();

        Queue<IRPNSCharacter> characters = reversePolishNotationConverter.getRPNSSequence();
        double result = reversePolishNotationCounter.countResult(characters);
        if (result == 9.223372036854776E16) {
            showAlert("MAX", "The number is too big. Clearing to 0");
            result = 0;
        }
        String resultString = new BigDecimal(result).toPlainString();
        if (resultString.startsWith("-")) {
            resultString = "(" + resultString + ")";
        }
        textView.setText(resultString);
        resultPrinted = true;
    }

    public void handleChangSignOperator() {
        String input = textView.getText().toString();
        final String isPositiveOrNegativeNumber = "[^(\\-0-9.%)]";
        if (input.length() == 0) {
            showAlert(ERROR_ALERT_TITLE, ERROR_ALERT_DEFAULT_CONTENT);
            return;
        }
        input = input.replace(String.valueOf(MINUS_CHARACTER), SPACE_CHARACTER + String.valueOf(MINUS_CHARACTER));
        input = input.replace(String.valueOf(OPENING_BRACKET) + SPACE_CHARACTER, String.valueOf(OPENING_BRACKET));
        String[] split = input.split(isPositiveOrNegativeNumber);
        int index = split.length;
        if (index-- == 0 || !Character.isDigit(input.charAt(input.length() - 1)) &&
                input.charAt(input.length() - 1) != CLOSING_BRACKET) {
            showAlert(ERROR_ALERT_TITLE, WRONG_FORMAT_ALERT_MESSAGE);
            return;
        }

        String number = split[index];
        if(number.contains(String.valueOf(MINUS_CHARACTER))) {
            changeIntoPositive(input, number);
        } else {
            changeIntoNegative(input, number);
        }

    }

    private void changeIntoNegative(String input, String split) {
        textView.setText(input.subSequence(0, input.length() - split.length()));
        split = OPENING_BRACKET + String.valueOf(MINUS_CHARACTER)
                + split + CLOSING_BRACKET;
        textView.append(split);
    }

    private void changeIntoPositive(String input, String split) {
        textView.setText(input.subSequence(0, input.length() - split.length()));
        if (split.contains(OPENING_BRACKET + String.valueOf(MINUS_CHARACTER))) {
            split = split.substring((OPENING_BRACKET + String.valueOf(MINUS_CHARACTER)).length(),
                    split.length() - String.valueOf(CLOSING_BRACKET).length());
            textView.append(split);
        } else {
            split = PLUS_CHARACTER + split.substring(1);
            textView.append(split);
        }
    }

    public void handleOperator(String operator) {
        if (resultPrinted)
            resultPrinted = false;
        String input = textView.getText().toString();
        if (input.length() == 0) {
            showAlert(ERROR_ALERT_TITLE, ERROR_ALERT_DEFAULT_CONTENT);
            return;
        }
        char lastCharacter = input.charAt(input.length() - 1);
        if (Character.isDigit(lastCharacter) || lastCharacter == PERCENT_CHARACTER &&
                !operator.equals(String.valueOf(PERCENT_CHARACTER)) || lastCharacter == CLOSING_BRACKET)
            textView.append(operator);
    }

    public void handleBackspace() {
        String inputtedText = textView.getText().toString();
        int length = inputtedText.length();
        if (length > 0 && inputtedText.endsWith(String.valueOf(CLOSING_BRACKET))) {
            textView.setText(inputtedText.subSequence(0, inputtedText.lastIndexOf(OPENING_BRACKET)));
        } else {
            textView.setText(inputtedText.subSequence(0, length - 1));
        }
    }

    void showAlert(String title, String content) {
        final String okButtonText = "OK";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(content)
                .setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, okButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}