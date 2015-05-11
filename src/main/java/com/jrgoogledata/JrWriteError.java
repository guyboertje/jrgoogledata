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

@JRubyClass(name = "JrGoogleData::WriteError", parent = "RuntimeError")
public class JrWriteError {

    public static RaiseException newError(Ruby ruby, String message) {
        RubyClass errorClass = ruby.getModule("JrGoogleData").getClass("WriteError");
        return new RaiseException(RubyException.newException(ruby, errorClass, message), true);
    }
}
