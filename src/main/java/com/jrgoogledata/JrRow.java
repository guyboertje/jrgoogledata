package com.jrgoogledata;

import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 *
 * @author guy
 */
@JRubyClass(name = "JrGoogleData::Row", parent = "Object")
public class JrRow extends RubyObject {
    ListEntry _row;

    public static final ObjectAllocator JRROW_ALLOCATOR = new ObjectAllocator() {
        @Override
        public IRubyObject allocate(Ruby ruby, RubyClass rc) {
            return new JrRow(ruby, rc);
        }
    };

    public JrRow(final Ruby runtime, RubyClass rubyClass) {
        super(runtime, rubyClass);
    }

    public JrRow(final Ruby runtime, ListEntry row) {
        super(runtime,
                runtime.getModule("JrGoogleData").getClass("Row"));
        _row = row;
    }

    @JRubyMethod(name = "new", meta = true)
    public static JrRow newInstance(IRubyObject self) {
        JrRow jr = (JrRow)((RubyClass) self).allocate();
        jr.callInit(Block.NULL_BLOCK);
        return jr;
    }




    @JRubyMethod(required = 1)
    public JrRow update(ThreadContext context) {
        String errorMessage = "Failed to update Row, reason unknown";
        try {
            _row.update();
            return this;
        }
        catch(IOException | ServiceException e) {
            errorMessage = e.getMessage();
        }
        throw JrReadError.newError(context.runtime, errorMessage);
    }
}
