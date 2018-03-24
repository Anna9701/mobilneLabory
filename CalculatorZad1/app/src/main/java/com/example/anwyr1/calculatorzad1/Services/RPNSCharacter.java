package com.example.anwyr1.calculatorzad1.Services;

import com.example.anwyr1.calculatorzad1.Interfaces.Action;
import com.example.anwyr1.calculatorzad1.Interfaces.IRPNSCharacter;

/**
 * Created by anwyr1 on 22/03/2018.
 */

public class RPNSCharacter implements IRPNSCharacter {
    private double number;
    private Action operator;
    private boolean isOperator = false;
    private boolean isNumber = false;

    public RPNSCharacter(double value) {
        this.number = value;
        isNumber = true;
    }

    public RPNSCharacter(Action operator) {
        this.operator = operator;
        isOperator = true;
    }

    public boolean isNumber() {
        return isNumber;
    }

    public double getNumber() {
        return number;
    }

    public boolean isOperator() {
        return isOperator;
    }

    public Action getOperator() {
        return operator;
    }
}
