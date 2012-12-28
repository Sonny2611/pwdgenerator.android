/*
 * Copyright (c) 2010-2012, Petr Panteleyev
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.panteleyev.passwdgen;

import java.util.Random;

/**
 *
 * @author Petr Panteleyev <petr@panteleyev.org>
 */
class Generator {
    private static final char[] upperCaseChars = {
        'A','B','C','D','E','F','G','H','I','J','K','L','M',
        'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };

    private static final char[] lowerCaseChars = {
        'a','b','c','d','e','f','g','h','i','j','k','l','m',
        'n','o','p','q','r','s','t','u','v','w','x','y','z'
    };

    private static final char[] digits = {
        '0','1','2','3','4','5','6','7','8','9'
    };

    private static final char[] symbols = {
        '@','#','$','%','&','*','(',')','-','+','=','^','.',','
    };
    
    private static final char[] badLetters = {
        'I', 'l', 'O', '0'
    };    

    private enum Bucket {
        UPPER_CASE(upperCaseChars),
        LOWER_CASE(lowerCaseChars),
        DIGITS(digits),
        SYMBOLS(symbols);

        private final char[] chars;
        private boolean used;

        private Bucket(char[] chars) {
            this.chars = chars;
        }

        boolean isUsed() {
            return used;
        }

        void setUsed(boolean use) {
            this.used = use;
        }

        char getChar(int index) {
            return chars[index];
        }

        int getSize() {
            return chars.length;
        }

        boolean check(String pwd) {
            for (char ch : pwd.toCharArray()) {
                for (char c : chars) {
                    if (ch == c) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private Bucket[] buckets = new Bucket[] {
        Bucket.UPPER_CASE, Bucket.LOWER_CASE, Bucket.DIGITS, Bucket.SYMBOLS
    };

    private Random random = new Random(System.currentTimeMillis());

    Generator() {
    }

    public void setDigits(boolean digits) {
        Bucket.DIGITS.setUsed(digits);
    }

    public void setLowerCase(boolean lowerCase) {
        Bucket.LOWER_CASE.setUsed(lowerCase);
    }

    public void setSymbols(boolean symbols) {
        Bucket.SYMBOLS.setUsed(symbols);
    }

    public void setUpperCase(boolean upperCase) {
        Bucket.UPPER_CASE.setUsed(upperCase);
    }

    public String generate(int len, boolean avoid) {
        String pwd = null;
        
        // Check if at least one bucket is used
        boolean used = false;
        for (Bucket b : buckets) {
        	used |= b.used;
        }
        
        if (used) {
            boolean all;
            do {
                StringBuilder res = new StringBuilder();

                for (int i = 0; i < len; ++i) {
                    // Select bucket
                    Bucket bucket;
                    do {
                        bucket = buckets[random.nextInt(buckets.length)];
                    } while (!bucket.isUsed());

                    char sym = ' ';
                    boolean symOk;
                    do {
                        symOk = true;
                        sym = bucket.getChar(random.nextInt(bucket.getSize()));
                        if (avoid) {
                            for (char badC : badLetters) {
                                if (sym == badC) {
                                    symOk = false;
                                    break;
                                }
                            }
                        }
                    } while (!symOk);
                    
                    res.append(sym);
                }

                pwd = res.toString();

                all = true;
                for (Bucket b : buckets) {
                    if (b.used) {
                        all &= b.check(pwd);
                    }
                }
            } while (!all);
        }
        return pwd;
    }
}
