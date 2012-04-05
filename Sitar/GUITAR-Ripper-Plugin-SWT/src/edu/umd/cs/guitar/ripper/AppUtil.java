package edu.umd.cs.guitar.ripper;
/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil
{
   /**
    * Hand-crafted regex for matching AUT-specific strings
    */
   private static final List<String> PATTERNS =
   Arrays.asList("Rachota .*",
                 "OmegaT-.*",
                 "Buddi.*",
                 "Open:.*",
                 "JabRef.*",
                 "GanttProject.*",
                 ".*Pauker.*",
                 ".*FreeMind.*",
                 ".* - ArgoUML.*",
                 "Save Project .*");


   /**
    * Lookup app specific regex for given string.
    *
    * Check if there exists a regex matching the input string.
    * The regex is manually added on a need-basis for AUTs. Adding
    * the regex must be done very carefully. It must be hand-crafted
    * to match specific needs of an AUT.
    *
    * @param sInputString    String for which regex is being looked up
    * @return                Returns regex if it exists, returns input
    *                          string otherwise.
    */
   public String
   findRegexForString(String sInputString)
   {
      for (String sPattern : PATTERNS) {
         if (matchRegex(sInputString, sPattern)) {
            return sPattern;
         }
      }

      return sInputString;
   }

   /**
    * Determine if the input string matches the input regex pattern.
    *
    * Attempt to match the pattern 'sPattern' with the string 'sInputString'.
    
    * @param sInputString    Input string to match with pattern
    * @param sPattern        Regex pattern to match with string
    * @return                True if match, false otherwise
    */
   private static boolean
   matchRegex(String sInputString,
              String sPattern)
   {
      Pattern pattern;
      Matcher matcher;

      pattern = Pattern.compile(sPattern);
      matcher = pattern.matcher(sInputString);
      if (matcher.matches()) {
         return true;
      }

      return false;
   }

} // End of class
