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

@JRubyClass(name = "JrGoogleData::CredentialError", parent = "RuntimeError")
public class JrCredentialError {

    public static RaiseException newError(Ruby ruby, String message) {
        RubyClass errorClass = ruby.getModule("JrGoogleData").getClass("CredentialError");
        return new RaiseException(RubyException.newException(ruby, errorClass, message), true);
    }
}
