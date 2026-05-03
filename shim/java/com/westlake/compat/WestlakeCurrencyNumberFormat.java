package com.westlake.compat;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Currency;

/**
 * Minimal currency formatter used when the portable runtime cannot safely enter
 * Android's ICU-backed DecimalFormat bootstrap path yet.
 */
public final class WestlakeCurrencyNumberFormat extends NumberFormat {
    private Currency currency;

    public WestlakeCurrencyNumberFormat() {
        setMinimumFractionDigits(2);
        setMaximumFractionDigits(2);
    }

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        if (toAppendTo == null || pos == null) {
            throw new NullPointerException();
        }
        if (Double.isNaN(number) || Double.isInfinite(number)) {
            int start = toAppendTo.length();
            toAppendTo.append('$').append(number);
            pos.setBeginIndex(start + 1);
            pos.setEndIndex(toAppendTo.length());
            return toAppendTo;
        }
        boolean negative = Double.doubleToRawLongBits(number) < 0 && number != 0.0d;
        double abs = Math.abs(number);
        long cents = Math.round(abs * 100.0d);
        appendCents(cents, negative, toAppendTo, pos);
        return toAppendTo;
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        if (toAppendTo == null || pos == null) {
            throw new NullPointerException();
        }
        if (number == Long.MIN_VALUE) {
            appendCurrencyDigits("9223372036854775808", true, toAppendTo, pos);
            toAppendTo.append(".00");
            return toAppendTo;
        }
        boolean negative = number < 0;
        appendCurrencyDigits(Long.toString(Math.abs(number)), negative, toAppendTo, pos);
        toAppendTo.append(".00");
        return toAppendTo;
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        if (source == null || parsePosition == null) {
            throw new NullPointerException();
        }
        int index = parsePosition.getIndex();
        int length = source.length();
        while (index < length && Character.isWhitespace(source.charAt(index))) {
            index++;
        }
        if (index < length && source.charAt(index) == '$') {
            index++;
        }
        boolean negative = false;
        if (index < length && (source.charAt(index) == '-' || source.charAt(index) == '+')) {
            negative = source.charAt(index) == '-';
            index++;
        }

        long whole = 0L;
        int digits = 0;
        while (index < length) {
            char c = source.charAt(index);
            if (c == ',') {
                index++;
                continue;
            }
            if (c < '0' || c > '9') {
                break;
            }
            if (whole <= (Long.MAX_VALUE - 9L) / 10L) {
                whole = whole * 10L + (long) (c - '0');
            }
            digits++;
            index++;
        }

        int fracDigits = 0;
        double fraction = 0.0d;
        double scale = 10.0d;
        if (index < length && source.charAt(index) == '.') {
            index++;
            while (index < length) {
                char c = source.charAt(index);
                if (c < '0' || c > '9') {
                    break;
                }
                if (fracDigits < 6) {
                    fraction += (double) (c - '0') / scale;
                    scale *= 10.0d;
                }
                fracDigits++;
                index++;
            }
        }

        if (digits == 0 && fracDigits == 0) {
            parsePosition.setErrorIndex(index);
            return null;
        }
        parsePosition.setIndex(index);
        double value = (double) whole + fraction;
        return Double.valueOf(negative ? -value : value);
    }

    @Override
    public Currency getCurrency() {
        Currency current = currency;
        if (current != null) {
            return current;
        }
        try {
            current = Currency.getInstance("USD");
            currency = current;
        } catch (Throwable ignored) {
            return null;
        }
        return current;
    }

    @Override
    public void setCurrency(Currency currency) {
        if (currency == null) {
            throw new NullPointerException("currency");
        }
        this.currency = currency;
    }

    private static void appendCents(long cents, boolean negative, StringBuffer out,
            FieldPosition pos) {
        long dollars = cents / 100L;
        long fraction = cents % 100L;
        appendCurrencyDigits(Long.toString(dollars), negative, out, pos);
        out.append('.');
        if (fraction < 10L) {
            out.append('0');
        }
        out.append(fraction);
    }

    private static void appendCurrencyDigits(String digits, boolean negative, StringBuffer out,
            FieldPosition pos) {
        int start = out.length();
        if (negative) {
            out.append('-');
        }
        out.append('$');
        int integerStart = out.length();
        int firstGroup = digits.length() % 3;
        if (firstGroup == 0) {
            firstGroup = 3;
        }
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && (i - firstGroup) % 3 == 0) {
                out.append(',');
            }
            out.append(digits.charAt(i));
        }
        pos.setBeginIndex(integerStart);
        pos.setEndIndex(out.length());
        if (digits.length() == 0) {
            pos.setBeginIndex(start);
            pos.setEndIndex(start);
        }
    }
}
