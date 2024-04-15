package org.sumit.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    boolean equalPressed = false;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_land);
        } else {
            setContentView(R.layout.activity_main);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_land);
        } else {
            setContentView(R.layout.activity_main);
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.btn_click);
    }
    public static double evaluate(String expression) {
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();
        expression = expression.replaceAll("\\s", "");
        int i = 0;
        while (i < expression.length()) {
            char ch = expression.charAt(i);
            if (Character.isDigit(ch) || ch == '.' || (ch == '-' && (i == 0 || !Character.isDigit(expression.charAt(i - 1))))) {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.' || expression.charAt(i) == 'E' || expression.charAt(i) == 'e' || (expression.charAt(i) == '-' && i == 0))) {
                    num.append(expression.charAt(i++));
                }
                operands.push(Double.parseDouble(num.toString()));
            } else if (ch == '+' || ch == '-' || ch == 'x' || ch == '*' || ch == '/') {
                operators.push(ch);
                i++;
            } else {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
        }
        while (!operators.isEmpty()) {
            processOperation(operands, operators);
        }
        return operands.pop();
    }

    public static double evaluateSci(String expression) {
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();
        expression = expression.replaceAll("\\s", "");
        int i = 1;
        while (i < expression.length()-1) {
            char ch = expression.charAt(i);
            if(ch=='π'&& (Character.isDigit(expression.charAt(i - 1))||Character.isDigit(expression.charAt(i + 1)))){
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
            i++;
        }
        expression = expression.replaceAll("π", "3.1415926");

        i = 0;
        while (i < expression.length()) {
            char ch = expression.charAt(i);
            if (Character.isDigit(ch) || ch == '.' || (ch == '-' && (i == 0 || !Character.isDigit(expression.charAt(i - 1))))) {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.' || expression.charAt(i) == 'E' || expression.charAt(i) == 'e' || (expression.charAt(i) == '-' && i == 0))) {
                    num.append(expression.charAt(i++));
                }
                operands.push(Double.parseDouble(num.toString()));
            } else if (ch == '+' || ch == '-' || ch == 'x' || ch == '*' || ch == '/' || ch == '^') {
                operators.push(ch);
                i++;
            } else if (ch == 's' || ch == 'c' || ch == 't' || ch == 'l' || ch == 'r' || ch == '!') {
                // Handle trigonometric, logarithmic, square root, and factorial functions
                StringBuilder func = new StringBuilder();
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    func.append(expression.charAt(i++));
                }
                String funcName = func.toString();
                if (funcName.equals("sin")) {
                    double operand = operands.pop();
                    operands.push(Math.sin(Math.toRadians(operand)));
                } else if (funcName.equals("cos")) {
                    double operand = operands.pop();
                    operands.push(Math.cos(Math.toRadians(operand)));
                } else if (funcName.equals("tan")) {
                    double operand = operands.pop();
                    operands.push(Math.tan(Math.toRadians(operand)));
                } else if (funcName.equals("ln")) {
                    double operand = operands.pop();
                    operands.push(Math.log(operand));
                } else if (funcName.equals("lg")) {
                    double operand = operands.pop();
                    operands.push(Math.log10(operand));
                } else if (funcName.equals("sqrt")) {
                    double operand = operands.pop();
                    operands.push(Math.sqrt(operand));
                } else if (funcName.equals("!")) {
                    double operand = operands.pop();
                    operands.push((double) factorial((int) operand));
                } else {
                    throw new IllegalArgumentException("Invalid function: " + funcName);
                }
            } else if (ch == '(') {
                operators.push(ch);
                i++;
            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    processOperation(operands, operators);
                }
                operators.pop(); // Pop '('
                i++;
            } else {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
        }
        while (!operators.isEmpty()) {
            processOperation(operands, operators);
        }
        return operands.pop();
    }

    private static void processOperation(Stack<Double> operands, Stack<Character> operators) {
        char operator = operators.pop();
        if (operator == '(' || operator == ')') return;
        double operand2 = operands.pop();
        double operand1 = operands.pop();
        switch (operator) {
            case '+':
                operands.push(operand1 + operand2);
                break;
            case '-':
                operands.push(operand1 - operand2);
                break;
            case 'x':
            case '*':
                operands.push(operand1 * operand2);
                break;
            case '/':
                operands.push(operand1 / operand2);
                break;
            case '^':
                operands.push(Math.pow(operand1, operand2));
                break;
        }
    }

    private static int factorial(int n) {
        if (n == 0) return 1;
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public void appendNum(View view){
        Button btn = (Button)view;
        String tag = btn.getTag().toString();
        char ch = tag.charAt(0);
        TextView status = findViewById(R.id.status);
        String str = status.getText().toString();
        if(str=="Invalid input") str="";
        if(ch == 'd'){
            if(equalPressed){
                status.setText("");
                equalPressed = false;
            }
            else if(str != null && !str.isEmpty()) {
                status.setText(str.substring(0, str.length() - 1));
            }

        } else if (ch == 'c'){
            status.setText("");
        } else if(ch == '='){
            equalPressed=true;
            try {
                double ans = 0.0;
                ans = evaluateSci(str);
                int ansInt = (int) ans;
                if (ans == (double) ansInt) {
                    status.setText(String.valueOf(ansInt));
                } else {
                    if (ans > -1 && ans < 1000000000) {
                        DecimalFormat df = new DecimalFormat("#.#########");
                        df.setRoundingMode(RoundingMode.HALF_UP);
                        String formattedAns = df.format(ans);
                        status.setText(formattedAns);
                    } else {
                        // Format the double value with 8 decimal places and scientific notation
                        DecimalFormat df = new DecimalFormat("0.########E0");
                        df.setRoundingMode(RoundingMode.HALF_UP); // or any other rounding mode you prefer
                        String formattedAns = df.format(ans);
                        status.setText(formattedAns);
                    }
                }
            } catch (Exception ex) {
                status.setText("Invalid input");
            }
        } else {
            if(equalPressed&&(Character.isDigit(ch) || ch == '.')){
                str="";
            }
            else if(!str.isEmpty()) {
                char chLast = str.charAt(str.length() - 1);
                if(!Character.isDigit(chLast) && chLast != '.' && !Character.isDigit(ch) && ch != '.') {
                    str = str.substring(0, str.length() - 1);
                }
            }
            str = str + ch;
            status.setText(str);
            equalPressed=false;
        }
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    public void appendSci(View view) {
        Button btn = (Button) view;
        String tag = btn.getTag().toString();
        TextView status = findViewById(R.id.status);
        String str = status.getText().toString();
        if (str.equals("Invalid input")) str = "";
        str += tag;
        status.setText(str);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    public void inverseTrigno(View view){
        Button btn = (Button) view;
        String tag = btn.getTag().toString();
        //Todo : setup inverseTrigno method
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }


}