package com.jrgoogledata;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyException;
import org.jruby.anno.JRubyClass;
import org.jruby.exceptions.RaiseException;

/**
 *
 * @author guy
 */

@JRubyClass(name = "JrGoogleData::ReadError", parent = "RuntimeError")
public class JrReadError {

    public static RaiseException newError(Ruby ruby, String message) {
        RubyClass errorClass = ruby.getModule("JrGoogleData").getClass("ReadError");
        return new RaiseException(RubyException.newException(ruby, errorClass, message), true);
    }
}
